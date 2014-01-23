package ca.ipredict.predictor.Markov;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class MarkovState {

	public Integer count;
	public HashMap<Integer, Integer> transitions; //outgoing states and their count
	
	
	public MarkovState() {
		count = 0;
		transitions = new HashMap<Integer, Integer>();
	}
	
	
	public void addTransition(Integer val) {
		
		//Getting the current value or creating it
		Integer transitionCount = transitions.get(val);
		if(transitionCount == null) {
			transitionCount = 0;
		}
		
		//updating value
		transitionCount++;
		
		//pushing value back to the transitions map
		transitions.put(val, transitionCount);
		
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



