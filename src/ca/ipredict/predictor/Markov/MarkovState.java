package ca.ipredict.predictor.Markov;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
		
		Iterator it = transitions.entrySet().iterator();
		while(it.hasNext()) {
			
			Map.Entry pairs = (Map.Entry)it.next();
			
			if((Integer)(pairs.getValue()) > highestCount) {
				highestCount = (Integer)(pairs.getValue());
				highestValue = (Integer)(pairs.getKey());
			}
			
		}
		
		return highestValue;
	}
	
	
	public String toString() {
		String output = "";
		Iterator it = transitions.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			output += pairs.getKey() + "("+ pairs.getValue() + ") ";
		}
		return output;
	}
	
}



