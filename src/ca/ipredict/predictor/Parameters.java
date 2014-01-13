package ca.ipredict.predictor;

/**
 * Static set of parameters used globally
 * by the predictor and the controller
 * @author Ted Gueniche
 *
 */
public class Parameters {
	
	//Preprocessing
	public static int sequenceMinSize = 10; //min sequence size in the dataset
	public static int sequenceMaxSize = 999;  //max sequence size in the dataset
	public static int removeDuplicatesMethod = 1;  // 0 for none, 1 for consecutive duplicates, 2 for all duplicates
	public static int consequentSize = 3; //suffix-size for prediction
	public static int windowSize = 7; //prefix size for prediction

	//LLCT
	//Training
	public static int splitMethod = 0; //0 for no split, 1 for basicSplit, 2 for complexSplit
	public static int splitLength = 99; // max tree height
	
	//Prediction
	public static int recursiveDividerMin = 1;//default to 1 //should be >= 0 and < recursiveDividerMax 
	public static int recursiveDividerMax = 2; //should be > recusiveDividerMax and < windowSize
	
	//best prediction from the count table
	public static int firstVote = 1; //1 for confidence, 2 for lift
	public static int secondVote = 2; //0 for none, 1 for support, 2 for lift
	public static double voteTreshold = 0.0; //confidence threshold to validate firstVote, else it uses the secondVote 
	
	//Countable weight system
	public static int countTableWeightMultiplier = 2; // 0 for no weight (1), 1 for 1/targetSize, 2 for level/targetSize
	public static int countTableWeightDivided = 1; // 0 for no divider, 1 for x/(#ofBranches for this sequence)
	
	//Others
	public static boolean useHashSidVisited = true;
	public static boolean branchTraversalTopToBottom = true; //used for branches with duplicates, set to true to allow with duplicates
	public static boolean removeUnknownItemsForPrediction = true; //remove items that were never seen before from the Target sequence before LLCT try to make a prediction
	//public static boolean dontRemoveLastItemFromTargetByRecursiveDivider = false; //to test
	
	// PHIL08 : if the last item from Target should appear last in sequences for prediction
//	public static boolean lastTargetItemShouldAppearLast = false;
	// PHIL08 : the following parameter do not seems to improve the accuracy if activated
//	public static int targetItemsShouldAppearClosely = 0;  // 0 = desactivate this
	    // >= 1 indicate that the items of target should appear whithin |target| + x items.

	
	
	//Rule evaluation non utilise!!
	public static int bestRuleCount = 1; 
	
	//Rule generation
	public static double minsup = 0.0005;
	public static double minconf = 0.5;

	//Instantiation is forbidden
	private Parameters(){}
	
	public static String tostring() {
		String nl = "\n";
		String output = "---Global Parameters---" + nl;
		output += "sequenceMinSize: \t"+ sequenceMinSize + nl;
		output += "sequenceMaxSize: \t"+ sequenceMaxSize + nl;
		output += "removeDuplicatesMethod: \t"+ removeDuplicatesMethod + nl;
		output += "consequentSize: \t"+ consequentSize + nl;
		output += "windowSize: \t"+ windowSize + nl;
		output += nl;
		output += "---LLCT Parameters---" + nl;
		output += "splitMethod: \t"+ splitMethod + nl;
		output += "splitLength: \t"+ splitLength + nl;
		output += "recursiveDividerMin: \t"+ recursiveDividerMin + nl;
		output += "recursiveDividerMax: \t"+ recursiveDividerMax + nl;
		output += "firstVote: \t"+ firstVote + nl;
		output += "secondVote: \t"+ secondVote + nl;
		output += "voteTreshold: \t"+ voteTreshold + nl;
		output += "countTableWeightMultiplier: \t"+ countTableWeightMultiplier + nl;
		output += "countTableWeightDivided: \t"+ countTableWeightDivided + nl;
		output += "useHashSidVisited: \t"+ useHashSidVisited + nl;
		output += "branchTraversalTopToBottom: \t"+ branchTraversalTopToBottom + nl;
		output += "removeUnknownItemsForPrediction: \t"+ removeUnknownItemsForPrediction + nl;
		return output;
	}
}
