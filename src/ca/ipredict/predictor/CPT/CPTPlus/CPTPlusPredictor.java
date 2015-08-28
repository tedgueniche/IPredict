package ca.ipredict.predictor.CPT.CPTPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.stream.Collectors;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Paramable;
import ca.ipredict.predictor.Predictor;

/**
 * CPT+ - Compact Prediction Tree Plus
 * A predictor based on CPT with three additional strategies; CCF (compression), CBS (compression) and PNR (prediction speed)
 *
 * Source: T. Gueniche, P. Fournier-Viger and V. S. Tseng, "CPT+: Decreasing the time/space complexity of the Compact Prediction Tree" In the Pacific-Asia conference on Knowledge Discovery And Data mining (PAKDD 2015).
 */
public class CPTPlusPredictor extends Predictor {

	/**
	 * Prediction Tree
	 */
	public PredictionTree Root;

	/**
	 * Lookup Table
	 */
	public Map<Integer, PredictionTree> LT;
	
	/**
	 * Inverted Index
	 */
	public Map<Integer, Bitvector> II;
	
	protected CPTHelper helper;
	
	/**
	 * number of node in the prediction tree (used for size())
	 */
	protected long nodeNumber;
	
	/**
	 * Flag for the CCF Strategy (default value)
	 */
	private boolean CCF = false;
	
	/**
	 * Flag for the CBS Strategy (default value)
	 */
	private boolean CBS = true;
	
	
	public Encoder encoder;
	
	protected boolean seqEncoding;
	
	public Paramable parameters;
	
	private String TAG = "CPT+";
	
	public CPTPlusPredictor() {
		
		Root = new PredictionTree();
		LT = new HashMap<Integer, PredictionTree>();
		II = new HashMap<Integer, Bitvector>();
		nodeNumber = 0;
		
		parameters = new Paramable();
		
		this.seqEncoding = false;
		
		//using the custom compressed helper
		helper = new CPTHelper(this);
	}
	
	public CPTPlusPredictor(String tag) {
		this();
		TAG = tag;
	}
	
	public CPTPlusPredictor(String tag, String params) {
		this(tag);
		parameters.setParameter(params);
	}
	
	@Override
	public String getTAG() {
		return TAG;
	}
	
	@Override
	public Boolean Train(List<Sequence> trainingSequences) {
		
		Root = new PredictionTree();
		LT = new HashMap<Integer, PredictionTree>();
		II = new HashMap<Integer, Bitvector>();
		encoder = new Encoder();
		((CPTHelper) helper).setEncoded(encoder);
		nodeNumber = 0;
		
		int seqId = 0;
		PredictionTree curNode;

		
		//CCF Strategy
		//Identifying the frequent sequential itemsets
		//setting up the encoder for future encoding tasks
		FIF finder = new FIFRaw();
		if(parameters.paramBoolOrDefault("CCF", CCF)) {
			List<List<Item>> itemsets = finder.findFrequentItemsets(trainingSequences, parameters.paramInt("CCFmin"), parameters.paramInt("CCFmax"), parameters.paramInt("CCFsup"));
			
			//filling the encoder with the frequent itemsets
			for(List<Item> itemset : itemsets) {
				encoder.addEntry(itemset);
			}
		}
		

		//for each training sequence
		for(Sequence seq : trainingSequences) {
			
			//slicing the sequence if needed
			if(parameters.paramInt("splitMethod") > 0) {
				seq = helper.keepLastItems(seq, parameters.paramInt("splitLength"));
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
				
				//if this itemCompressed is not a child of the current node, we add him
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
		

		//Patch collapsing for added compression
		if(parameters.paramBoolOrDefault("CBS", CBS)) {
			pathCollapse();
		}
		
		return true;
	}
	
	
	@Override
	public Sequence Predict(Sequence target) {
		
		
		//remove items that were never seen before from the Target sequence before LLCT try to make a prediction
		//If set to false, those items will be still ignored later on (in updateCountTable())
		target = helper.removeUnseenItems(target);

		CountTable ct = null;
		ct = predictionByActiveNoiseReduction(target);
		

		Sequence predicted = ct.getBestSequence(1);
		return predicted;
	}
	
	
	protected CountTable predictionByActiveNoiseReduction(Sequence target) {
		
		//Queues setup
		HashSet<Sequence> seen = new HashSet<Sequence>(); //contains the sequence already seen to avoid work duplication
		Queue<Sequence> queue = new LinkedList<Sequence>(); //contains the sequence to process
		queue.add(target); //adding the target sequence as the initial sequence to process
		

		//Setting parameters
		int maxPredictionCount = 1 + (int) (target.size() * parameters.paramDouble("minPredictionRatio")); //minimum number of required prediction to ensure the best accuracy
		int predictionCount = 0; //current number of prediction done (one by default because of the CountTable being updated with the target initially) 
		double noiseRatio = parameters.paramDouble("noiseRatio"); //Ratio of items to remove in a sequence per level (level = target.size)
		int initialTargetSize = target.size();
		
		
		//Initializing the count table
		CountTable ct = new CountTable(helper);
		ct.update(target.getItems().toArray(new Item[0]), target.size());
		
		//Initial prediction
		Sequence predicted = ct.getBestSequence(1);
		if(predicted.size() > 0) {
			predictionCount++;
		}
		
		
		//while the min prediction count is not reached and the target sequence is big enough
		Sequence seq;
		while((seq = queue.poll()) != null && predictionCount < maxPredictionCount) {
		
			
			//if this sequence has not been seen yet
			if(seen.contains(seq) == false) {
			
				//set this sequence to seen
				seen.add(seq);
				
				//get the sub sequences for this level while respecting the maxRatioForReduction
				List<Item> noises = getNoise(seq, noiseRatio);
				
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
					if(candidate.size() > 1) {
						queue.add(candidate);
					}
					
					//update count table with this sequence
 					Item[] candidateItems = candidate.getItems().toArray(new Item[0]);

					int branches = ct.update(candidateItems, initialTargetSize);
					
 					//do a prediction if this CountTable update did something
					if(branches > 0) {
						predicted = ct.getBestSequence(1);
						if(predicted.size() > 0) {
							predictionCount++;
						}
					}
				}
			}
		}
		
		return ct;
	}
	
	
	/**
	 * Return the list of items with the lowest support
	 * @param target Sequence to consider to find the alphabet of items
	 * @param noiseRatio [0,1] Portion of the sequence to identify as noisy (it defines the number of item returned)
	 */
	protected List<Item> getNoise(Sequence target, double noiseRatio) {
		
		//Converting the noiseRatio (relative to the noiseCount (absolute)
		int noiseCount = (int) Math.floor(target.size() * noiseRatio);
		
		//When the noise is <= 0, noiseCount is set to one
		//Optimization for noiseCount == 1
		if(noiseCount <= 0) {
			//Find the lowest supporting item
			int minSup = Integer.MAX_VALUE;
			int itemVal = -1;
			for(Item item : target.getItems()) {
				if(II.get(item.val).cardinality() < minSup) {
					minSup = II.get(item.val).cardinality();
					itemVal = item.val;
				}
			}
			
			List<Item> noises = new ArrayList<Item>();
			noises.add(new Item(itemVal));
			return noises;
		}
		else {
			//sort the items of a sequence by frequency
			//then return the last [noiseCount] items
			List<Item> noises = target.getItems().stream().sorted(
					(i1, i2) -> Integer.compare(
							II.get(i2.val).cardinality(), II.get(i1.val).
							cardinality())).collect(Collectors.toList());
					
			return noises.subList(target.size() - noiseCount, target.size());
		}
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
							
							//saving the number of node saved
							nodeSaved += pathLength - 1;
						}
						singlePath = false;
					}
					//this node has only one child and so it is added to the itemset 
					else {
						List<Item> curItemset = encoder.getEntry(cur.Item.val);
						
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
	}

	@Override
	public long size() {
		return nodeNumber;
	}
	
	@Override
	public float memoryUsage() {
		
		float sizePredictionTree = nodeNumber * 3 * 4; // each node uses 3 integers, one for value, one for parent link, and one on average for child
		
		float sizeInvertedIndex = (float) (II.size() * ( Math.ceil(LT.size() / 8) + 4));
		
		float sizeLookupTable = LT.size() * 2 * 4; //the key and the value of this hashmap are integer and pointer respectively (4 bytes)
		
		return sizePredictionTree + sizeInvertedIndex + sizeLookupTable;
	}
}
