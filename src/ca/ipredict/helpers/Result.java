package ca.ipredict.helpers;

import java.util.HashMap;

//Represent a list of statistics and their values
public class Result {
	private HashMap<String, Double> data;
	
	public Result() {
		data = new HashMap<String, Double>();
	}
	
	public Double get(String stat) {
		
		if(data.get(stat) == null)
			data.put(stat, 0.0);
		
		return data.get(stat);
	}
	
	public void set(String stat, Double value) {
		data.put(stat, value);
	}
	
}
