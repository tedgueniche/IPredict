package ca.ipredict.predictor.CPT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.helpers.MemoryLogger;
import ca.ipredict.predictor.Predictor;

/**
 * Predictor based on a 3 main structures
 * a compact tree, an inverted index and a lookup table
 * @author Ted Gueniche
 *
 */
public class LLCT_Old implements Predictor {

	private Map<Integer, List<Integer>> II; //Inverted Index
	private PredictionTree Root; //Prediction tree
	private Map<Integer, PredictionTree> LT; //Lookup Table
	private int size;
	
	private List<Sequence> mTrainingSequences; //list of sequences to test
	
	/**
	 * Set the training set
	 */
	public void setTrainingSequences(List<Sequence> trainingSequences) {
		mTrainingSequences = trainingSequences;
	}
	
	/**
	 * Predict the next element in the given sequence
	 * @param sequence to predict
	 * @return
	 */
	public Sequence Predict(Sequence target) {
		
		List<Integer> lastIndexes = null; //will contains the intersection
		
		//find all sequences that have all the target's items
		//for each item in the target sequence
		for(Item it : target.getItems()) {
			
			//getting the inverted index for this item
			List<Integer> curIndexes = II.get(it.val);
			
			if(curIndexes == null) { //this item has not been seen in preloading, ignore it
				
			}
			else {
				//instead of doing the intersection of all the inverted indexes at the same time
				//we do 2 at the time. Takes less time and memory
				if(lastIndexes != null) {
					
					//list of index to keep in lastIndexes
					List<Integer> curIntersection = new ArrayList<Integer>();
					
					//for each index in lastIndexes we check if it is in curIndexes
					for(Integer cI : lastIndexes) {
						
						//search cI in curIndexes, if found then OK, else we need to remove it from lastIndexes!
						Boolean isFound = false;
						for(int i = 0 ; i < curIndexes.size() && isFound == false ; i++) {
							if(curIndexes.get(i).equals(cI)) {
								isFound = true;
								curIntersection.add(curIndexes.get(i));
							}
						}
					}
					
					lastIndexes = curIntersection;
					
				}
				//first iteration only
				else {
					//saving the current indexes for the next iteration
					lastIndexes = curIndexes;
				}
			}
		}
		
		//now, the intersection of all inverted indexes is in lastIndexes
		
		//if intersection is empty (no sequences contained all target's item
		///EXIT POINT
		if(lastIndexes == null || lastIndexes.size() < 1)
			return new Sequence(-1);
		
		//creating an HashMap of the target's item (for O(1) search time)
		HashSet<Integer> hashTarget = new HashSet<Integer>();
		for(Item it : target.getItems()) {
			hashTarget.add(it.val);
		}
		
		//for each index in lastIndexes, get the "consequent" in tree
		//then we update the following CountTable
		Map<Integer, Integer> CountTable = new HashMap<Integer, Integer>();
		for(Integer i : lastIndexes) {
			
			PredictionTree curNode = LT.get(i); //getting the latest node in tree for this index
			
			//going up the tree from curNode to the first node in branch that is in target
			while( curNode != null && (hashTarget.contains(curNode.Item.val) == false) ) {
				
				//updating CountTable
				if(CountTable.containsKey(curNode.Item.val) == false) {
					CountTable.put(curNode.Item.val, 1);
				}
				else {
					int count = CountTable.get(curNode.Item.val);
					CountTable.put(curNode.Item.val, count + 1);
				}
				
				curNode = curNode.Parent; //going up!
			}
			
		}
		
		
		//Now, the CountTable contains all the necessary data for the prediction
		//TODO: use confidence instead of support 
		// confidence: SUP(X U Y) / SUP(X) , SUP(X): || intersection ||
		//TODO: use lift instead of support/confidence 
		// lift: Confidence / SUP(Y) , SUP(Y) : count ||1|| in InvertedIndex for Y
		//Looking for the item with the highest count in the CountTable
		int maxValue = -1;
		Integer maxItem = -1;
		for(Map.Entry<Integer, Integer> it : CountTable.entrySet()) {
			
			if(it.getValue() > maxValue) {
				maxItem = it.getKey();
				maxValue = it.getValue();
			}
		}
		
		//the result is in maxItem:
		Item predictedItem = new Item(maxItem);
		Sequence predicted = new Sequence(-1); //toremove
		
		if(maxItem != -1)
			predicted.addItem(predictedItem);
		
		//EXIT POINT
		return predicted;
	}
	
	/**
	 * Trains this predictor with training data, use "setTrainingSequences()" first
	 * @return true on success
	 */
	public Boolean Preload() {
		
		size = 0;
		int seqId = 0; //current sequence from database
		II = new HashMap<Integer, List<Integer>>();
		Root = new PredictionTree();
		LT = new HashMap<Integer, PredictionTree>();
		
		//Logging memory usage
		MemoryLogger.addUpdate();
			
		//For each line (sequence) in file
		for(Sequence curSeq : mTrainingSequences) {
		
			//resetting the current node to the root (top of the tree)
			PredictionTree curNode = Root;
			
			//for each item in this sequence
			for(Item it : curSeq.getItems()) {
				
				//if item is not in Inverted Index then we add it
				if(II.containsKey(it.val) == false) {
					List<Integer> tmpList = new ArrayList<Integer>();
					II.put(it.val, tmpList);
				}
				//updating Inverted Index with seqId for this Item
				II.get(it.val).add(seqId);
				
				
				//if item is not in prediction tree then we add it
				if(curNode.hasChild(it) == false) {
					curNode.addChild(it);
					size++;
				}
				curNode = curNode.getChild(it);
				curNode.Support++; //updating support of this node
			}
			
			LT.put(seqId, curNode); //adding <sequence id, last node in sequence>
			seqId++; //increment sequence id number
		}

		//Logging memory usage
		MemoryLogger.addUpdate();
		
		return true;
	}

	@Override
	public String getTAG() {
		return "LOLD";
	}

	@Override
	public long size() {
		return size;
	}
	
}
