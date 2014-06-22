package ca.ipredict.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ScoreDistribution<K> {

	/**
	 * Contains a map of with a score as a key and a list of
	 * items as the value.
	 */
	private TreeMap<Double, List<K>> dict;
	
	
	public ScoreDistribution() {
		dict = new TreeMap<Double, List<K>>();
	}
	
	/**
	 * Put a pair of key value in the distribution
	 */
	public void put(K key, Double value) {
		
		List<K> keys = dict.get(value);
		if(keys == null) {
			keys = new ArrayList<K>();
		}
		
		keys.add(key);
		
		dict.put(value, keys);
	}
	
	
	/**
	 * Removes all of the mappings from this distribution
	 */
	public void clear() {
		dict.clear();
	}
	
	/**
	 * Get the list of key with the best value 
	 * @param minThreshold Min ratio between the best and second best value [1.0,0.0]
	 * @return The list of keys with the best value
	 */
	public List<K> getBest(double minThreshold ) {
		
		if(dict.size() == 0) {
			return null;
		}
		else if(dict.size() == 1) {
			return dict.lastEntry().getValue();
		}
		
		Double bestVal1 = dict.lastKey(); //best value in the dictionary
		Double bestVal2 = dict.lowerKey(bestVal1); //second best value in the dictionary
		
		if( (bestVal1 / bestVal2) < minThreshold) {
			return null;
		}
		else {
			return dict.get(bestVal1);
		}
	}
	
	public List<K> getNextBest(double best) {
		
		Double nextBest = dict.lowerKey(best);
		if(nextBest == null) {
			return null;
		}
	
		return dict.get(nextBest);
	}
}
