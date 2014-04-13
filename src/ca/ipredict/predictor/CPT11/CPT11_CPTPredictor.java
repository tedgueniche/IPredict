package ca.ipredict.predictor.CPT11;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.helpers.Bitvector;
import ca.ipredict.predictor.Predictor;
import ca.ipredict.predictor.profile.Profile;

/**
 * Predictor based on a 3 main structures
 * a prediction tree, an inverted index and a lookup table
 */
public class CPT11_CPTPredictor implements Predictor {

	//The three data structure
	public CPT11_PredictionTree Root; 				//Prediction Tree
	public Map<Integer, CPT11_PredictionTree> LT; 	//Lookup Table
	public Map<Integer, Bitvector> II; 		//Inverted Index
	
	private long nodeNumber; 					//number of node in the prediction tree (used for size())
	private List<Sequence> trainingSequences; 	//list of sequences to test
	
	
	public CPT11_CPTPredictor() {
		Root = new CPT11_PredictionTree();
		LT = new HashMap<Integer, CPT11_PredictionTree>();
		II = new HashMap<Integer, Bitvector>();
		nodeNumber = 0;
	}
	
	@Override
	public void setTrainingSequences(List<Sequence> trainingSet) {
		trainingSequences = trainingSet;
	}

	/**
	 * Iterating through each sequence in the trainingSequences. 
	 * For each sequence, we add the items in the PredictionTree, 
	 * the Lookup Table and the Inverted Index
	 * @return True on success
	 */
	public Boolean Preload() {
		Root = new CPT11_PredictionTree();
		LT = new HashMap<Integer, CPT11_PredictionTree>();
		II = new HashMap<Integer, Bitvector>();
		nodeNumber = 0;
		
		int seqId = 0;
		CPT11_PredictionTree curNode;

		//for each training sequence
		for(Sequence seq : trainingSequences) {
			
			//slicing the sequence if needed
			if(Profile.splitMethod > 0) {
				seq = CPT11_CPTHelper.keepLastItems(seq, Profile.splitLength);
			}
			
			//resetting node pointer to root node
			curNode = Root;

			//for each item in the sequence
			for(Item item : seq.getItems()) {
			
				//adding the item in the Inverted Index if needed
				if(II.containsKey(item.val) == false) {
					Bitvector tmpBitset = new Bitvector();
					II.put(item.val, tmpBitset);
				}

				//TODO: possible improvement: use setBitAndIncrementCardinality() instead of setBit()
				//updating Inverted Index with seqId for this Item
				II.get(item.val).setBit(seqId);
				
				//adding the item in the Prediction Tree if needed
				if(curNode.hasChild(item) == false) {
					curNode.addChild(item);
					nodeNumber++;
				}
				curNode = curNode.getChild(item);
				
			}
			//adding the sequence id in the Lookup Table
			LT.put(seqId, curNode); //adding <sequence id, last node in sequence>
			seqId++; //increment sequence id number

		}
		return true;
	}

	@Override
	public Sequence Predict(Sequence target) {
		
		CPT11_CPTHelper.predictor = this;
		
		//remove items that were never seen before from the Target sequence before LLCT try to make a prediction
		//If set to false, those items will be still ignored later on (in updateCountTable())
		if(Profile.removeUnknownItemsForPrediction){
			Iterator<Item> iter = target.getItems().iterator();
			while (iter.hasNext()) {
				Item item = (Item) iter.next();
				// if there is no bitset for that item (we have never seen it)
				if(II.get(item.val) == null){
					// then remove it from target.
					iter.remove();  
				}
			}
		}
		
		//Empty predicted sequence
		Sequence predicted = new Sequence(-1);
		
		//For every step of the recursive divider
		//from recursiveDividerMin to recursiveDividerMax
		int recursion = Profile.recursiveDividerMin;
		while(predicted.size() == 0 && recursion < Profile.recursiveDividerMax ) {
			
			//Call recursive divider to update the countable
			CPT11_CountTable ct = new CPT11_CountTable(this);
			
			int minSize = target.size() - recursion;
			Item[] targetArray = target.getItems().toArray(new Item[0]);
			CPT11_CPTHelper.recursiveDivider(targetArray, minSize, ct,target.size());
			
			//Extract prediction from the CountTable
			predicted = ct.getBestSequence(1);
			recursion++;
		}
		
		return predicted;
	}

	@Override
	public String getTAG() {
		return "OLD_CPT";
	}

	@Override
	public long size() {
		return nodeNumber;
	}
}
