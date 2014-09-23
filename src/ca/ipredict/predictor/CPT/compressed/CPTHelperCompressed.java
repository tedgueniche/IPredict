package ca.ipredict.predictor.CPT.compressed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.CPT.CPTHelper;
import ca.ipredict.predictor.CPT.CPTPredictor;
import ca.ipredict.predictor.CPT.PredictionTree;

public class CPTHelperCompressed extends CPTHelper {

	private Encoder encoder;
	
	public CPTHelperCompressed(CPTPredictor predictor) {
		super(predictor);
	}
	
	
	public void setEncoded(Encoder encoder) {
		this.encoder = encoder;
	}

	
	/**
	 * Return a sequence in sequential order from the Prediction Tree given its unique id
	 * @param id Id of the sequence to extract
	 * @return The full sequence matching the id
	 */
	@Override
	public Item[] getSequenceFromId(Integer id) {
		
		if(encoder == null) {
			System.err.println("Encoded needs to be set in CPTHelperEncoded");
		}
		
		List<Item> items = new ArrayList<Item>();
		PredictionTree curNode = predictor.LT.get(id);
		
		//Reading the whole branch from bottom to top
		items.add(curNode.Item);
		while(curNode.Parent != null && curNode.Parent != predictor.Root) {
			curNode = curNode.Parent;
			items.add(curNode.Item);
		}
		
		//Reversing the sequence so that the leaf item is last and 
		//the item closer to the root be first
		Collections.reverse(items);

		//Decoding the sequence
		Sequence sequence = encoder.decode(new Sequence(id, items));

		//Returning the sequence as an array
		return sequence.getItems().toArray(new Item[0]);
	}
	
	
	/**
	 * Extract the common prefix, if any, between two itemset. <br/>
	 * Eg:  <br/>
	 * 		{1,3,6,7,8} and {1,3,2} -> {1,3} <br/>
	 *  	{1,3,6,7,8} and {1,3} -> {1,3}<br/>
	 *  	{1,3} and {1,3} -> {1,3}<br/>
	 */
	public List<Item> getCommonPrefix(List<Item> A, List<Item> B) {
		
		if(A.size() < 1 || B.size() < 1) {
			return null;
		}
		
		List<Item> prefix = new ArrayList<>();
		for(int i = 0; i < A.size() && i < B.size(); i++) {
			
			if(A.get(i).equals(B.get(i))) {
				prefix.add(A.get(i));
			}
			else {
				return prefix;
			}
		}
		
		return prefix;
	}
	
	
}
