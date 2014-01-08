package ca.ipredict.predictor.CPT;

import java.util.Map.Entry;
import java.util.Map;
import java.util.TreeMap;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Parameters;

/**
 * Represents a CountTable for the CPT algorithm
 */
public class CountTable {

	//internal representation of the CountTable
	private TreeMap<Integer, Float> table;
	
	
	public CountTable() {
		table = new TreeMap<Integer, Float>();
	}

	/**
	 * Push a value to the CountTable, if a key already exists then
	 * the given value is added to the old one
	 */
	public void push(Integer key, Float value) {
		
		Float oldVal = table.get(key);
		if(oldVal == null) {
			table.put(key, value);
		}
		else {
			table.put(key, oldVal + value);
		}
	}
	
	/**
	 * Return a sequence containing the highest scored items from
	 * the counts table
	 * @param count Number of items to put in the sequence
	 * @param II The inverted index corresponding
	 * @return The sequence containing the |count| best items sorted from the CountTable
	 */
	public Sequence getBestSequence(int count, Map<Integer, Bitvector> II) {
		
		//Iterating through the CountTable to sort the items by score
		TreeMap<Double, Integer> bestOfCT = new TreeMap<Double, Integer>();
		for(Entry<Integer, Float> it : table.entrySet()) {
			
			//the following measure of confidence and lift are "simplified" but are exactly the same as in the literature.
			//CONFIDENCE : |X -> Y|
			//LIFT: CONFIDENCE(X -> Y) / (|Y|)
			//Calculate score based on lift or confidence
			double support = II.get(it.getKey()).cardinality();
			double lift = it.getValue() / support;
			double confidence = it.getValue();
			
			double score = (Parameters.firstVote == 1) ? confidence : lift; //Use confidence or lift, depending on Parameter.firstVote
			
			bestOfCT.put(score, it.getKey());
		}
		
		//Filling a sequence with the best |count| items
		Sequence seq = new Sequence(-1);
		for(Entry<Double, Integer> entry : bestOfCT.entrySet()) {
			seq.addItem(new Item(entry.getValue()));
		}
		
		return seq;
	}

}
