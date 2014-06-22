package ca.ipredict.predictor.CPT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Predictor;
import ca.ipredict.predictor.profile.Profile;

/**
 * Predictor based on a 3 main structures
 * a prediction tree, an inverted index and a lookup table
 */
public class CPTPredictor implements Predictor {

	//The three data structure
	public PredictionTree Root; 				//Prediction Tree
	public Map<Integer, PredictionTree> LT; 	//Lookup Table
	public Map<Integer, Bitvector> II; 		//Inverted Index
	
	private long nodeNumber; 					//number of node in the prediction tree (used for size())
	private List<Sequence> trainingSequences; 	//list of sequences to test
	
	
	public CPTPredictor() {
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
			if(Profile.splitMethod > 0) {
				seq = CPTHelper.keepLastItems(seq, Profile.splitLength);
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

		CPTHelper.predictor = this;

		//remove items that were never seen before from the Target sequence before LLCT try to make a prediction
		//If set to false, those items will be still ignored later on (in updateCountTable())
		if(Profile.removeUnknownItemsForPrediction){
			target = CPTHelper.removeUnseenItems(target);
		}
		
		
		//Initializing the count table
		CountTable ct = new CountTable(this);
		ct.update(target.getItems().toArray(new Item[0]), target.size());
		

		//Removed noisy items from the target sequence to enhance the coverage of this predictor
		Sequence predicted = new Sequence(-1);
		while(predicted.size() == 0 && target.size() > 1) {
			
			List<Item> cutSeq = new ArrayList<Item>();
			
			//Find the lowest supporting item
			int minSup = Integer.MAX_VALUE;
			int itemVal = -1;
			for(Item item : target.getItems()) {
				if(II.get(item.val).cardinality() < minSup) {
					minSup = II.get(item.val).cardinality();
					itemVal = item.val;
				}
			}
			
			//Remove the lowest supporting item
			for(Item item : target.getItems()) {
				if(item.val.equals(itemVal) == false) {
					cutSeq.add(item);
				}
			}

			//Updating target sequence without the lowest supporting item
			target.getItems().clear();
			target.getItems().addAll(cutSeq);
			
			//Update the count table with the newly generated sequence
			ct.update(target.getItems().toArray(new Item[0]), target.size());
			
			//Do the prediction from the count table
			predicted = ct.getBestSequence(1);
		}
		
		return predicted;
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
