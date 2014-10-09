package ca.ipredict.predictor.CPT.compressed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.CPT.Bitvector;
import ca.ipredict.predictor.CPT.CPTPredictor;
import ca.ipredict.predictor.CPT.CountTable;
import ca.ipredict.predictor.CPT.PredictionTree;
import ca.ipredict.predictor.profile.Profile;

public class CPTPredictorCompressed extends CPTPredictor {

	public Encoder encoder;
	
	protected boolean seqEncoding;
	
	public CPTPredictorCompressed() {
		super("CPTC");
		
		this.seqEncoding = true;
		
		//using the custom compressed helper
		helper = new CPTHelperCompressed(this);
	}

	@Override
	public Boolean Train(List<Sequence> trainingSequences) {
		
		Root = new PredictionTree();
		LT = new HashMap<Integer, PredictionTree>();
		II = new HashMap<Integer, Bitvector>();
		encoder = new Encoder();
		((CPTHelperCompressed) helper).setEncoded(encoder);
		nodeNumber = 0;
		
		int seqId = 0;
		PredictionTree curNode;

		//Identifying the frequent sequential itemsets
		//setting up the encoder for futur encoding tasks
		if(this.seqEncoding == true) {
//			FIF finder = new FIFPrefixSpan(); //prefixSpan is slower than the raw approach for BMS, needs to be investigated further
			FIF finder = new FIFRaw();
			List<List<Item>> itemsets = finder.findFrequentItemsets(trainingSequences, 2, 4, 2);
			
//			System.out.println("Found "+ itemsets.size() + " frequent itemsets");
			for(List<Item> itemset : itemsets) {
				encoder.addEntry(itemset);
			}
		}

		//for each training sequence
		for(Sequence seq : trainingSequences) {
			
			//slicing the sequence if needed
			if(Profile.splitMethod > 0) {
				seq = helper.keepLastItems(seq, Profile.splitLength);
			}

			//Generating the compressed version of this sequence
			Sequence seqCompressed = new Sequence(seq);
			seqCompressed = encoder.encode(seqCompressed);
			
			//resetting node pointer to root node
			curNode = Root;

			//for each item in the compressed sequence
			for(Item itemCompressed : seqCompressed.getItems()) {
				
				
				//decoding the current item the encoded sequence
				List<Item> itemset = encoder.getEntry(itemCompressed.val);
				
				//II update
				for(Item item : itemset) {
				
					//adding the item in the Inverted Index if needed
					if(II.containsKey(item.val) == false) {
						Bitvector tmpBitset = new Bitvector();
						II.put(item.val, tmpBitset);
					}
	
					//updating Inverted Index with seqId for this Item
					II.get(item.val).setBit(seqId);
				}
				
				//NOTE: the following code has yet to be proven better than the "dumb" alternative
				//PT update
				//adding the item in the Prediction Tree if needed
//				if(curNode.hasChild(itemCompressed) == false && itemset.size() > 1) {
//					
//						//identify if there is a child with a common prefix
//						//The child with the longuest prefix in common
//						List<Item> bestChildPrefix = new ArrayList<>();
//						PredictionTree bestChild = null;
//						for(PredictionTree child : curNode.getChildren()) {
//							
//							List<Item> prefix = getCommonPrefix(itemset, encoder.getEntry(child.Item.val));
//							if(prefix != null && prefix.size() > bestChildPrefix.size()) {
//								bestChild = child;
//								bestChildPrefix = prefix;
//							}
//						}
//						
//						//if there is a child with a common prefix, we can explode this child for the insertion
//						if(bestChild != null) {
//							
//							int prefixLength = bestChildPrefix.size();
//							Item prefix = new Item(encoder.getIdorAdd(bestChildPrefix));
//							Item curSuffix = new Item(encoder.getIdorAdd(itemset.subList(prefixLength, itemset.size())));
//							Item childSuffix = new Item(encoder.getIdorAdd(bestChildPrefix.subList(prefixLength, bestChildPrefix.size())));
//							
//							//Adding the new node with the common prefix
//							curNode.addChild(prefix);
//							
//							//adding a new node for the current child with the suffix only
//							bestChild.Item = childSuffix;
//							curNode.getChild(prefix).addChild(bestChild);
//							
//							//adding the current itemset's suffix
//							curNode.getChild(prefix).addChild(curSuffix);
//							
//							//removing the (old) current node
//							curNode.removeChild(bestChild.Item);
//							
//							//updating the current node pointer
//							curNode = curNode.getChild(prefix).getChild(curSuffix);
//							nodeNumber += 2;
//						}
//						//else we add the current itemset a regular child
//						else {
//							curNode.addChild(itemCompressed);
//							nodeNumber++;
//							curNode = curNode.getChild(itemCompressed);
//						}
//				}
//				//if this itemCompressed is not a child but the itemset has a size of 1
//				else if(curNode.hasChild(itemCompressed) == false) {
				if(curNode.hasChild(itemCompressed) == false) {
					curNode.addChild(itemCompressed);
					nodeNumber++;
					curNode = curNode.getChild(itemCompressed);
				}
				//if this itemCompressed is already a child of the current node
				else {
					curNode = curNode.getChild(itemCompressed);
				}
			}

			
			//adding the sequence id in the Lookup Table
			LT.put(seqId, curNode); //adding <sequence id, last node in sequence>
			seqId++; //increment sequence id number
		}
		
		//More compression9
		pathCollapse();
		
		//nodeNumber = manualCalcSize();
		
		
		return true;
	}
	
	
	@Override
	public Sequence Predict(Sequence target) {
		
		
		//remove items that were never seen before from the Target sequence before LLCT try to make a prediction
		//If set to false, those items will be still ignored later on (in updateCountTable())
		if(Profile.removeUnknownItemsForPrediction){
			target = helper.removeUnseenItems(target);
		}
	
		int maxPredictionCount = (int) (0.1 + target.size() * Profile.recursiveDividerMin * 0.75); //minimum number of required prediction to ensure the best accuracy
//		int maxPredictionCount = (int) (target.size() * 3); //minimum number of required prediction to ensure the best accuracy
		int minPredictionCount = 2; //minimum number of required prediction to ensure the best accuracy
		int predictionCount = 0; //current number of prediction done (one by default because of the CountTable being updated with the target initially) 
		
		double noiseRatio = 1.0; //Ratio of items to remove in a sequence per level (level = target.size)
		int initialTargetSize = target.size();
		
		HashSet<Sequence> seen = new HashSet<Sequence>();
		Queue<Sequence> queue = new LinkedList<Sequence>();
		queue.add(target);
		
		//Initializing the count table
		CountTable ct = new CountTable(helper);
		ct.update(target.getItems().toArray(new Item[0]), target.size());
		
		Sequence predicted = new Sequence(-1);
		
		//while the min prediction count is not reached and the target sequence is big enough
		while(queue.size() > 0 && predictionCount < maxPredictionCount) {
		
						
			//getting the sequence
			Sequence seq = queue.poll();
			
		
			//to test
			//simulate per level recursive divider
//			ct = new CountTable(helper);
//			ct.update(seq.getItems().toArray(new Item[0]), seq.size());
//			predictionCount = 0;
//			maxPredictionCount = seq.size() - 1;
			/////////////////////////////////////
			
			//if this sequence has not been seen yet
			if(seen.contains(seq) == false && seq.size() > 1) {
			
				//set this sequence to seen
				seen.add(seq);
				
				//get the sub sequences for this level while respecting the maxRatioForReduction
				List<Item> noises = getNoise(seq, noiseRatio);
//				List<Item> noises = getNoise(seq, 1 / seq.size());
				
				//generating the candidates from the list of noisy items
				for(Item noise : noises) {
					
					//clone and extract the items from the target 
					Sequence candidate = seq.clone();
					
					//remove the first noise item appearance from the target
					for(int i = 0; i < candidate.getItems().size(); i++) {
						if(candidate.getItems().get(i).equals(noise)) {
							candidate.getItems().remove(i);
							break;
						}
					}

					//add this sequence to the queue
					queue.add(candidate);
					
					//update count table with this sequence
 					Item[] candidateItems = candidate.getItems().toArray(new Item[0]);

					int branches = ct.update(candidateItems, initialTargetSize);
// 					int branches = ct.update(candidateItems, candidate.size()); //WTF on the second parameter
					
 					//do a prediction if this CountTable update did something
					if(branches > 0) {
//						predicted = ct.getBestSequence(1);
//						if(predicted.size() > 0) {
							predictionCount++;
//						}
					}
				}
			}
		}

		predicted = ct.getBestSequence(1);
		return predicted;
	}
	
	
	/**
	 * Return the list of items with the lowest support
	 * @param target Sequence to consider to find the alphabet of items
	 * @param noiseRatio Portion of the sequence to identify as noisy (it defines the number of item returned)
	 */
	protected List<Item> getNoise(Sequence target, double noiseRatio) {
		
		int noiseCount = (int) Math.floor(target.size() * noiseRatio);
		if(noiseCount <= 0) {
			noiseCount = 1;
		}
		
		//sort the items of a sequence by frequency
		//then return the last [noiseCount] items
		List<Item> noises = target.getItems().stream().sorted(
				(i1, i2) -> Integer.compare(
						II.get(i2.val).cardinality(), II.get(i1.val).
						cardinality())).collect(Collectors.toList());
				
		return noises.subList(target.size() - noiseCount, target.size());
	}
	
	
	/**
	 * This compression method can be called on a compressed prediction tree
	 * to replace direct branch with a single node.
	 * 
	 * Once one of these branch has been found, each node a decoded and concatenated
	 * to form a single itemset which is then inserted in the encoder and used to
	 * replace the branch with a single node. 
	 * 
	 * As an optimization, the leaf of the branch is the node used to replace the branch,
	 * so the Lookup Table for CPT does not have to be updated since it is already pointing
	 * to this node.
	 */
	protected void pathCollapse() {
		
		int nodeSaved = 0;
		
		//for each sequences registered in the Lookup Table (LT)
		for(Entry<Integer, PredictionTree> entry : LT.entrySet()) {
			
			PredictionTree cur = entry.getValue();
			PredictionTree leaf = cur;
			PredictionTree last = null;
			List<Item> itemset = new ArrayList<Item>();
			int pathLength = 0;
			boolean singlePath = true;
			
			//if this cur is a true leaf
			if(cur.getChildren().size() == 0) {
				
				//while the path is singular (starting from the leaf)
				while(singlePath == true) {
					
					//if the current node has multiple children
					if(cur.getChildren().size() > 1 || cur == null) {
						
						if(pathLength != 1) {
							//updating the leaf to be a child of cur
							Integer newId = encoder.getIdorAdd(itemset);
							leaf.Item = new Item(newId);
							leaf.Parent = cur;
							
							//updating cur to have the leaf has a child
							cur.removeChild(last.Item);
							cur.addChild(leaf);
							
							nodeSaved += pathLength - 1;
						}
						singlePath = false;
					}
					//this node has only one child and so it is added to the itemset 
					else {
						List<Item> curItemset = encoder.getEntry(cur.Item.val);
//						curItemset.addAll(itemset);
//						itemset = curItemset;
						
						List<Item> tmp = itemset;
						itemset = new ArrayList<Item>();
						itemset.addAll(curItemset);
						itemset.addAll(tmp);
						
						cur.getChildren().clear();
						
						pathLength++;
						
						last = cur;
						cur = cur.Parent;
					}
				}			
			}
		}
		
		nodeNumber -= nodeSaved;
//		System.out.println("Path Collapsing: "+ nodeSaved + " node saved");
	}
}
