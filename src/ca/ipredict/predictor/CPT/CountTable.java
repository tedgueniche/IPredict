package ca.ipredict.predictor.CPT;

import java.util.Map.Entry;
import java.util.HashSet;
import java.util.TreeMap;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.CPT.NewCPTHelper;
import ca.ipredict.predictor.profile.Profile;

/**
 * Represents a CountTable for the CPT algorithm
 */
public class CountTable {

	/**
	 * Internal representation of the CountTable
	 */
	private TreeMap<Integer, Float> table;
	private HashSet<Integer> branchVisited;
	private NewCPTPredictor predictor;
	
	/**
	 * Basic controller
	 */
	public CountTable(NewCPTPredictor predictor) {
		table = new TreeMap<Integer, Float>();
		branchVisited = new HashSet<Integer>();
		this.predictor = predictor;
	}

	/**
	 * Calculate the score for an item and push the score to the CountTable, 
	 * if a key already exists then the given value is added to the old one
	 * @param curSeqLength Size of the sequence that contains the item
	 * @param fullSeqLength Size of the sequence before calling recursive divider
	 * @param numberOfSeqSameLength Number of similar sequence with the same size
	 */
	public void push(Integer key, int curSeqLength, int fullSeqLength, int numberOfSeqSameLength) {

		//Setting up the weight multiplier for the countTable
		float weight = 1f;		
		if(Profile.countTableWeightMultiplier == 1)
			weight = 1f  / curSeqLength;
		else if(Profile.countTableWeightMultiplier == 2)
			weight = (float)curSeqLength / fullSeqLength;
		
		//Update the countable with the right weight and value
		float curValue = (Profile.countTableWeightDivided == 0) ? 1f : 1f /((float)numberOfSeqSameLength);
		
		
		Float oldVal = table.get(key);
		if(oldVal == null) {
			table.put(key, (curValue * weight));
		}
		else {
			table.put(key, oldVal + (curValue * weight));
		}		
	}

	
	/**
	 * Update this CountTable with a sequence S, it finds the similar sequence SS of S
	 * All the selected items from SS are used to update the CountTable
	 * @param predictor Predictor used to access its data structures
	 * @param sequence Sequence to use to update the CountTable
	 * @param initialSequenceSize The initial size of the sequence to predict (used for weighting)
	 */
	public void update(Item[] sequence, int initialSequenceSize) {
		
		Bitvector ids = NewCPTHelper.getSimilarSequencesIds(sequence);

		//For each sequence similar of the given sequence
		for(int id = ids.nextSetBit(0); id >= 0 ; id = ids.nextSetBit(id + 1)) {
			
			if(Profile.useHashSidVisited && branchVisited.contains(id)) {
				continue;
			}
			
			//extracting the sequence from the PredictionTree
			Item[] seq = NewCPTHelper.getSequenceFromId(id);
			
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
			for(Item item : seq) {
				if(toAvoid.size() == 0) {
					//calculating the score for this item
					push(item.val, sequence.length, initialSequenceSize, ids.cardinality());
					branchVisited.add(id);
				}
				else if(toAvoid.contains(item)) {
					toAvoid.remove(item);
				}
			}			
		}
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
		TreeMap<Double, Integer> bestOfCT = new TreeMap<Double, Integer>();
		for(Entry<Integer, Float> it : table.entrySet()) {
			
			//the following measure of confidence and lift are "simplified" but are exactly the same as in the literature.
			//CONFIDENCE : |X -> Y|
			//LIFT: CONFIDENCE(X -> Y) / (|Y|)
			//Calculate score based on lift or confidence
			double support = predictor.II.get(it.getKey()).cardinality();
			double lift = it.getValue() / support;
			double confidence = it.getValue();
			
			double score = (Profile.firstVote == 1) ? confidence : lift; //Use confidence or lift, depending on Parameter.firstVote
			
			if(! bestOfCT.containsKey(score)) {
				bestOfCT.put(score, it.getKey());
			}
		}
		
		//Filling a sequence with the best |count| items
		Sequence seq = new Sequence(-1);
		int i = 0;
		for(Entry<Double, Integer> entry : bestOfCT.descendingMap().entrySet()) {
			if(i < count) {
				seq.addItem(new Item(entry.getValue()));
				i++;
			} else {
				break;
			}
		}
		
		return seq;
	}

}
