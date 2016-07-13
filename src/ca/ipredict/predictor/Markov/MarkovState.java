package ca.ipredict.predictor.Markov;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class MarkovState implements Serializable {

	private static final long serialVersionUID = -8819363846752644204L;

	/**
	 * Number of transitions for this state
	 */
	private Integer count;
	
	/**
	 * Hashmap of each transition and their respective support
	 */
	private HashMap<Integer, Integer> transitions; //outgoing states and their count
	
	
	public MarkovState() {
		count = 0;
		transitions = new HashMap<Integer, Integer>();
	}
	
	/**
	 * Returns the number of transition for this state - not the support
	 */
	public int getTransitionCount() {
		return count;
	}
	
	/**
	 * Adds or update a transition from this state
	 * @param val Value of the new state
	 */
	public void addTransition(Integer val) {
		
		//Getting the current value or creating it
		Integer support = transitions.get(val);
		if(support == null) {
			support = 0;
			count += 1;
		}
		
		//updating value
		support++;
		
		//pushing value back to the transitions map
		transitions.put(val, support);
		
	}
	
	
	public Integer getBestNextState() {
		Integer highestCount = 0;
		Integer highestValue = null;
		
		Iterator<Entry<Integer, Integer>> it = transitions.entrySet().iterator();
		while(it.hasNext()) {
			
			Entry<Integer, Integer> pairs = it.next();
			
			if((pairs.getValue()) > highestCount) {
				highestCount = (pairs.getValue());
				highestValue = (pairs.getKey());
			}
			
		}
		
		return highestValue;
	}
	
	
	public String toString() {
		String output = "";
		Iterator<Entry<Integer, Integer>> it = transitions.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Integer, Integer> pairs = it.next();
			output += pairs.getKey() + "("+ pairs.getValue() + ") ";
		}
		return output;
	}
	
}



