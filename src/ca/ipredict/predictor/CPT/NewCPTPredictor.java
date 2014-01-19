package ca.ipredict.predictor.CPT;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Parameters;
import ca.ipredict.predictor.Predictor;

/**
 * Predictor based on a 3 main structures
 * a prediction tree, an inverted index and a lookup table
 * @author Ted Gueniche
 *
 */
public class NewCPTPredictor implements Predictor {

	//The three data structure
	public PredictionTree Root; 				//Prediction Tree
	public Map<Integer, PredictionTree> LT; 	//Lookup Table
	public Map<Integer, Bitvector> II; 		//Inverted Index
	
	private long nodeNumber; 					//number of node in the prediction tree (used for size())
	private List<Sequence> trainingSequences; 	//list of sequences to test
	
	
	public NewCPTPredictor() {
		Root = new PredictionTree();
		LT = new HashMap<Integer, PredictionTree>();
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
		Root = new PredictionTree();
		LT = new HashMap<Integer, PredictionTree>();
		II = new HashMap<Integer, Bitvector>();
		nodeNumber = 0;
		
		int seqId = 0;
		PredictionTree curNode;

		//for each training sequence
		for(Sequence seq : trainingSequences) {
			
			//slicing the sequence if needed
			if(Parameters.splitMethod > 0) {
				seq = NewCPTHelper.keepLastItems(seq, Parameters.splitLength);
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
		
		//remove items that were never seen before from the Target sequence before LLCT try to make a prediction
		//If set to false, those items will be still ignored later on (in updateCountTable())
		if(Parameters.removeUnknownItemsForPrediction){
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
		int recursion = Parameters.recursiveDividerMin;
		while(predicted.size() == 0 && recursion < Parameters.recursiveDividerMax ) {
			
			//Call recursive divider to update the countable
			CountTable ct = new CountTable();
			
			int minSize = target.size() - recursion;
			Item[] targetArray = target.getItems().toArray(new Item[0]);
			NewCPTHelper.recursiveDivider(this, targetArray, minSize, ct,target.size());
			
			//Extract prediction from the CountTable
			predicted = ct.getBestSequence(1, II);
			recursion++;
		}
		
		return predicted;
	}

	@Override
	public String getTAG() {
		return "newCPT";
	}

	@Override
	public long size() {
		return nodeNumber;
	}

	

}
