package ca.ipredict.predictor.profile;

import ca.ipredict.predictor.Parameters;

public class BMSProfile implements Profile {

	@Override
	public void Apply() {
		
		//Preprocessing
		Parameters.sequenceMinSize = 6;
		Parameters.sequenceMaxSize = 999;
		Parameters.removeDuplicatesMethod = 1;
		Parameters.consequentSize = 1; 
		Parameters.windowSize = 5; 
		
		//LLCT
		//Training
		Parameters.splitMethod = 1; //0 for no split, 1 for basicSplit, 2 for complexSplit
		Parameters.splitLength = 15; // max tree height
		
		//Prediction
		Parameters.recursiveDividerMin = 4; //should be >= 0 and < recursiveDividerMax 
		Parameters.recursiveDividerMax = 99; //should be > recusiveDividerMax and < windowSize
		
		//best prediction from the count table
		Parameters.firstVote = 1; //1 for confidence, 2 for lift
		Parameters.secondVote = 2; //0 for none, 1 for support, 2 for lift
		Parameters.voteTreshold = 0.0; //confidence threshold to validate firstVote, else it uses the secondVote 
		
		//Countable weight system
		Parameters.countTableWeightMultiplier = 2; // 0 for no weight (1), 1 for 1/targetSize, 2 for level/targetSize
		Parameters.countTableWeightDivided = 1; // 0 for no divider, 1 for x/(#ofBranches for this sequence)
		
		//Others
		Parameters.useHashSidVisited = true;
		Parameters.branchTraversalTopToBottom = true; //used for branches with duplicates, set to true to allow with duplicates
		Parameters.removeUnknownItemsForPrediction = true; //remove items that were never seen before from the Target sequence before LLCT try to make a prediction
		
	}

	

}
