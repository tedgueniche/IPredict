package ca.ipredict.predictor.CPT.CPTPlus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;

public class CPTHelper {

	public CPTPlusPredictor predictor;
	
	private Encoder encoder;
	
	public CPTHelper(CPTPlusPredictor predictor) {
		this.predictor = predictor;
	}
	
	
	public void setEncoded(Encoder encoder) {
		this.encoder = encoder;
	}

	
	/**
	 * Return a sequence in sequential order from the Prediction Tree given its unique id
	 * @param id Id of the sequence to extract
	 * @return The full sequence matching the id
	 */
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
	
	/**
	 * Return the last Length items
	 * @param sequence the sequence to slice
	 * @param length the size of the subsequences
	 */
	public Sequence keepLastItems(Sequence sequence, int length) { 

		if(sequence.size() <= length){ 
			return sequence;
		}
		
		//slicing the seqence
		Sequence result = new Sequence(sequence.getId(), sequence.getItems().subList(sequence.size() - length, sequence.size()));
		return result;
	}
	
	public Sequence removeUnseenItems(Sequence seq) {
		
		Sequence target = new Sequence(seq);
		
		//Min support for items in the target sequence
		int treshold = 0;
		
		List<Item> selectedItems = new ArrayList<Item>();
		for(Item item : target.getItems()) {
			
			//Keep only the item that we have seen during training and that have a support 
			//above the specified threshold
			if(predictor.II.get(item.val) != null && predictor.II.get(item.val).cardinality() >= treshold) {
				selectedItems.add(item);
			}	
		}
		target.getItems().clear();
		target.getItems().addAll(selectedItems);
		
		return target;
	}
	
	
	/**
	 * Return a bit vector representing the set of similar sequence of the specified sequence
	 * @param sequence The sequence to used to find similar sequences
	 * @param II The inverted index containing the bit vectors
	 * @return The similar sequences as a bit vector, where each bit indicate whether a sequence is similar or not
	 */
	public Bitvector getSimilarSequencesIds(Item[] sequence) {
		if(sequence.length == 0) {
			return new Bitvector();
		}

		//for each item in the sequence; do the intersection of their bitset
		Bitvector intersection = null;
		for(int i = 0 ; i < sequence.length ; i++) {
			if(intersection == null) {

				intersection = (Bitvector) predictor.II.get(sequence[i].val).clone();
			}
			else {
				Bitvector other = predictor.II.get(sequence[i].val);
				if(other != null) {
					intersection.and(predictor.II.get(sequence[i].val));
				}
			}
		}
		
		return intersection;
	}
}
