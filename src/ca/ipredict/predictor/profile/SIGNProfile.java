package ca.ipredict.predictor.profile;

public class SIGNProfile extends Profile {

	@Override
	public void Apply() {
		
		//Global parameters
		//Pre-processing
		parameters.put("sequenceMinSize", "10");
		parameters.put("sequenceMaxSize", "999");
		parameters.put("removeDuplicatesMethod", "1");
		parameters.put("consequentSize", "3"); 
		parameters.put("windowSize", "7"); 

		///////////////
		//CPT parameters
		//Training
		parameters.put("splitMethod", "0"); //0 for no split", "1 for basicSplit", "2 for complexSplit
		parameters.put("splitLength", "20"); // max tree height
		parameters.put("minSup", "0.06"); //SEI compression, minSup to remove low supporting items
		
		//Prediction
		parameters.put("recursiveDividerMin", "3"); //should be >= 0 and < recursiveDividerMax 
		parameters.put("recursiveDividerMax", "99"); //should be > recusiveDividerMax and < windowSize
		parameters.put("minPredictionRatio", "1.0f"); //should be over 0
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

}
