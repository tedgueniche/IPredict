package ca.ipredict.predictor.CPT;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;

/**
 * Best friend of CPT
 * @author Ted
 *
 */
public class NewCPTHelper {

	/**
	 * Return the last Length items
	 * @param sequence the sequence to slice
	 * @param length the size of the subsequences
	 */
	public static Sequence keepLastItems(Sequence sequence, int length) { 

		if(sequence.size() <= length){ 
			return sequence;
		}
		
		//slicing the seqence
		Sequence result = new Sequence(sequence.getId(), sequence.getItems().subList(sequence.size() - length, sequence.size()));
		return result;
	}

	/**
	 * Return a bit vector representing the set of similar sequence of the specified sequence
	 * @param sequence The sequence to used to find similar sequences
	 * @param II The inverted index containing the bit vectors
	 * @return The similar sequences as a bit vector, where each bit indicate whether a sequence is similar or not
	 */
	public static Bitvector getSimilarSequencesIds(NewCPTPredictor predictor, Item[] sequence) {
		if(sequence.length == 0) {
			return new Bitvector();
		}

		//for each item in the sequence; do the intersection of their bitset
		Bitvector intersection = null;
		for(int i = 0 ; i < sequence.length ; i++) {
			if(intersection == null) {
				intersection = predictor.II.get(sequence[i].val);
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
	
	/**
	 * Return a sequence in sequential order from the Prediction Tree given its unique id
	 * @param id Id of the sequence to extract
	 * @return The full sequence matching the id
	 */
	public static Item[] getSequenceFromId(NewCPTPredictor predictor, Integer id) {
		
		List<Item> sequence = new ArrayList<Item>();
		PredictionTree curNode = predictor.LT.get(id);
		
		//Reading the whole branch from bottom to top
		sequence.add(curNode.Item);
		while(curNode.Parent != null && curNode.Parent != predictor.Root) {
			curNode = curNode.Parent;
			sequence.add(curNode.Item);
		}
		
		//Reversing the sequence so that the leaf item is last and 
		//the item closer to the root be first
		Collections.reverse(sequence);
		
		//Returning the sequence as an array
		return sequence.toArray(new Item[0]);
	}
	
	/**
	 * Recursively remove one item at the time from the sequence S and use it to update the countTable
	 * @param sequence Sequence to remove item from
	 * @param minSize Minimum size of the sequence (stop removing items when it reaches that size)
	 * @param ct The CountTable to update
	 * @param initialSequenceSize Initial size of the sequence to predict (use for the weighting function in CountTable)
	 */
	public static void recursiveDivider(NewCPTPredictor predictor, Item[] sequence, int minSize, CountTable ct, int initialSequenceSize) {
		
		//Exit recursion condition
		int size = sequence.length;
		if(size < minSize) {
			return;
		}
		
		//Updating the count table with the current sequence
		ct.update(predictor, sequence, initialSequenceSize);
		
		//Return if no possible child
		if(size == minSize) {
			return;
		}
		
		//Recursive call on all subsequence of size (sequence.size() - 1)
		List<Item[]> sequences = noiseRemover(sequence);
		for(Item[] seq : sequences) {
			recursiveDivider(predictor, seq, minSize, ct, initialSequenceSize);
		}
	}
	
	/**
	 * Return a list of all possible subsequence of size (sequence.size() - 1)
	 */
	private static List<Item[]> noiseRemover(Item[] sequence) {
		
		List<Item[]> results = new ArrayList<Item[]>();
		for(Item toHide : sequence) {
			Item[] newSeq = new Item[sequence.length - 1];
			
			int index = 0;
			for(Item it : sequence) {
				if(it != toHide) {
					newSeq[index] = it;
					index++;
				}
			}
			results.add(newSeq);
		}
		return results;
	}
	
	
	public static void main(String[] args){
		
		Sequence a = new Sequence(0);
		a.addItem(new Item(1));
		a.addItem(new Item(2));
		a.addItem(new Item(3));
		a.addItem(new Item(4));
		
//		Sequence b = keepLastItems(a, 15);
//		System.out.println(b.toString());
		
		Item[] aa = new Item[4];
		aa[0] = new Item(0);
		aa[1] = new Item(1);
		aa[2] = new Item(2);
		aa[3] = new Item(3);
		List<Item[]> results = noiseRemover(aa);
		
		System.out.println(results);
		
	}
}
