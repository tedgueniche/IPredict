package ca.ipredict.predictor.profile;

import java.util.HashMap;
import java.util.Map.Entry;


/**
 * Interface to load a parameters profile.
 */
public class Profile {
	
	//Contains the parameter as strings
	public static HashMap<String, String> parameters;
	
	public Profile() {
		parameters = new HashMap<String, String>();
	}
	
	public static Double paramDouble(String name) {
		Object value = parameters.get(name);
		return (value == null) ? null : Double.valueOf(parameters.get(name));
	}
	
	public static Integer paramInt(String name) {
		Object value = parameters.get(name);
		return (value == null) ? null : Integer.valueOf(parameters.get(name));	
	}
	
	public static Float paramFloat(String name) {
		Object value = parameters.get(name);
		return (value == null) ? null : Float.valueOf(parameters.get(name));	
	}
	
	public static Boolean paramBool(String name) {
		Object value = parameters.get(name);
		return (value == null) ? null : Boolean.valueOf(parameters.get(name));	
	}
	
	//Applies the parameters 
	public void Apply(){
		
		//Global parameters
		//Pre-processing
		parameters.put("sequenceMinSize", "6");
		parameters.put("sequenceMaxSize", "999");
		parameters.put("removeDuplicatesMethod", "1");
		parameters.put("consequentSize", "1"); 
		parameters.put("windowSize", "5"); 

		///////////////
		//CPT parameters
		//Training
		parameters.put("splitMethod", "0"); //0 for no split", "1 for basicSplit", "2 for complexSplit
		parameters.put("splitLength", "999"); // max tree height

		//Prediction
		parameters.put("recursiveDividerMin", "4"); //should be >= 0 and < recursiveDividerMax 
		parameters.put("recursiveDividerMax", "99"); //should be > recusiveDividerMax and < windowSize
		parameters.put("minPredictionRatio", "2.0f"); //should be over 0
		parameters.put("noiseRatio", "1.0f"); //should be in the range ]0,1]

		//best prediction from the count table
		parameters.put("firstVote", "1"); //1 for confidence", "2 for lift
		parameters.put("secondVote", "2"); //0 for none", "1 for support", "2 for lift
		parameters.put("voteTreshold", "0.0"); //confidence threshold to validate firstVote", "else it uses the secondVote 

		//Countable weight system
		parameters.put("countTableWeightMultiplier", "2"); // 0 for no weight (1)", "1 for 1/targetSize", "2 for level/targetSize
		parameters.put("countTableWeightDivided", "1"); // 0 for no divider", "1 for x/(#ofBranches for this sequence)

		//Others
		parameters.put("useHashSidVisited", "true");
		parameters.put("branchTraversalTopToBottom", "true"); //used for branches with duplicates", "set to true to allow with duplicates
		parameters.put("removeUnknownItemsForPrediction", "true"); //remove items that were never seen before from the Target sequence before LLCT try to make a prediction
	}
	

	public static String tostring() {
		String nl = "\n";
		String output = "---Global Parameters---" + nl;
		
		
		for(Entry<String, String> param : parameters.entrySet()) {
			output += param.getKey() + "\t" + param.getValue() + nl;
		}
		
		return output;
	}
}
