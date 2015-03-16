package ca.ipredict.predictor.CPT.CPTPlus;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.helpers.ScoreDistribution;


/**
 * Represents a CountTable for the CPT Predictor
 */
public class CountTable {

	/**
	 * Internal representation of the CountTable
	 */
	private TreeMap<Integer, Float> table;
	private HashSet<Integer> branchVisited;
	private CPTHelper helper;
	
	/**
	 * Basic controller
	 */
	public CountTable(CPTHelper helper) {
		table = new TreeMap<Integer, Float>();
		branchVisited = new HashSet<Integer>();
		this.helper = helper;
	}

	/**
	 * Calculate the score for an item and push the score to the CountTable, 
	 * if a key already exists then the given value is added to the old one
	 * @param curSeqLength Size of the sequence that contains the item
	 * @param fullSeqLength Size of the sequence before calling recursive divider
	 * @param numberOfSeqSameLength Number of similar sequence
	 */
	public void push(Integer key, int curSeqLength, int fullSeqLength, int numberOfSeqSameLength, int dist) {
				
		//Declare the various weights
		float weightLevel = 1f /numberOfSeqSameLength; //from [1.0,0[  -> higher is better
		float weightDistance = 1f / dist; //from [1.0,0[ -> higher is better
//		float weightLength = (float)curSeqLength / fullSeqLength; //from [1.0,0[ -> higher is better
//		float weightLength = (float)fullSeqLength / curSeqLength; //from [1.0,0[ -> higher is better
	
		//calculate the value for the current key 
//		float curValue = (weightLevel * 0.5f) + (weightLength * 5.0f) + (weightDistance * 1.8f);
//		float curValue = (weightLevel * 1f) + (weightLength * 1f) + (weightDistance * 0.0001f);
		float curValue = (weightLevel * 1f) + (1f) + (weightDistance * 0.0001f);
		
		//Update the count table
		Float oldVal = table.get(key);
		if(oldVal == null) {
			table.put(key, curValue);
		}
		else {
			table.put(key, oldVal * curValue);
		}		
	}

	
	/**
	 * Update this CountTable with a sequence S, it finds the similar sequence SS of S
	 * All the selected items from SS are used to update the CountTable
	 * @param predictor Predictor used to access its data structures
	 * @param sequence Sequence to use to update the CountTable
	 * @param initialSequenceSize The initial size of the sequence to predict (used for weighting)
	 */
	public int update(Item[] sequence, int initialSequenceSize) {

		int branchesUsed = 0;
		Bitvector ids = helper.getSimilarSequencesIds(sequence);

		//For each sequence similar of the given sequence
		for(int id = ids.nextSetBit(0); id >= 0 ; id = ids.nextSetBit(id + 1)) {
			
			if(branchVisited.contains(id)) {
				continue;
			}
			branchVisited.add(id);
			
			//extracting the sequence from the PredictionTree
			Item[] seq = helper.getSequenceFromId(id);
			
			//Generating a set of all the items from sequence
			HashSet<Item> toAvoid = new HashSet<Item>();
			for(Item item : sequence) {
				toAvoid.add(item);
			}
			

			//Updating this CountTable with the items {S}
			//Where {S} contains only the items that are in seq after
			//all the items from sequence have appeared at least once
			//Ex:	
			//	sequence: 	A B C
			//  seq: 		X A Y B C E A F
			//	{S}: 		E F
			int max = 99; //used to limit the number of items to push in the count table
			int count = 1; //current number of items already pushed
			for(Item item : seq) {
				//only enters this if toAvoid is empty
				//it means that all the items of toAvoid have been seen
				if(toAvoid.size() == 0 && count < max) {
					
					//calculating the score for this item
					push(item.val, sequence.length, initialSequenceSize, ids.cardinality(), count);
					count++;
				}
				else if(toAvoid.contains(item)) {
					toAvoid.remove(item);
				}
			}
			//meaning that the count table has been really updated
			if(count > 1 ) {
				branchesUsed++;
			}
		}
		
		return branchesUsed;
	}
	
	/**
	 * Return a sequence containing the highest scored items from
	 * the counts table
	 * @param count Number of items to put in the sequence
	 * @param II The inverted index corresponding
	 * @return The sequence containing the |count| best items sorted from the CountTable
	 */
	public Sequence getBestSequence(int count) {
		
		//Iterating through the CountTable to sort the items by score
		ScoreDistribution<Integer> sd = new ScoreDistribution<Integer>();
		for(Entry<Integer, Float> it : table.entrySet()) {
			
			//the following measure of confidence and lift are "simplified" but are exactly the same as in the literature.
			//CONFIDENCE : |X -> Y|
			//LIFT: CONFIDENCE(X -> Y) / (|Y|)
			double confidence = it.getValue();
//			double support = helper.predictor.II.get(it.getKey()).cardinality();
//			double lift = it.getValue() * support;
			
			//Calculate score based on lift or confidence
			double score =  confidence; //Use confidence or lift, depending on Parameter.firstVote
						
			sd.put(it.getKey(), score);
		}
		
		//Filling a sequence with the best |count| items
		Sequence seq = new Sequence(-1);
		List<Integer> bestItems = sd.getBest(1.002);
//		List<Integer> bestItems = sd.getBest(1.00001);
		if(bestItems != null && bestItems.size() > 0) {
			for(int i = 0; i < count && i < bestItems.size(); i++) {
				seq.addItem(new Item(bestItems.get(i)));
			}
		}

		return seq;
	}

}
