package ca.ipredict.predictor.CPT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Parameters;
import ca.ipredict.predictor.Predictor;

/**
 * 
 * @author Ted Gueniche
 *
 */
public class NewCPTPredictor implements Predictor {

	//The three data structure
	private PredictionTree Root; 				//Prediction Tree
	private Map<Integer, PredictionTree> LT; 	//Lookup Table
	private Map<Integer, Bitvector> II; 		//Inverted Index
	
	private long nodeNumber; 					//number of node in the prediction tree (used for size())
	private List<Sequence> trainingSequences; 	//list of sequences to test
	
	
	public NewCPTPredictor() {
		Root = new PredictionTree();
		LT = new HashMap<Integer, PredictionTree>();
		II = new HashMap<Integer, Bitvector>();
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
				II.get(item.val).setBitAndIncrementCardinality(seqId);
				
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTAG() {
		return "CPT";
	}

	@Override
	public long size() {
		return nodeNumber;
	}

	

}
