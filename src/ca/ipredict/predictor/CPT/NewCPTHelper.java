package ca.ipredict.predictor.CPT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;

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
	 * Return a bitvector representing the set of similar sequence of the 
	 * specified sequence
	 * @param sequence The sequence to used to find similar sequences
	 * @param II The inverted index containing the bitvectors
	 * @return The similar sequences as a bitvector, where each bit indicate whether a sequence is similar or not
	 */
	public static Bitvector getSimilarSequences(Item[] sequence, Map<Integer, Bitvector> II) {
		if(sequence.length == 0) {
			return new Bitvector();
		}
		//firt item as the inital bitset
		Bitvector intersection = II.get(sequence[0].val);
		
		//for each item in the sequence; do the intersection of their bitset
		for(int i = 0 ; i < sequence.length ; i++) {
			intersection.and(II.get(sequence[i].val));
		}
		
		return intersection;
	}
	
	
	
	
	public static void main(String[] args){
		
		Sequence a = new Sequence(0);
		a.addItem(new Item(1));
		a.addItem(new Item(2));
		a.addItem(new Item(3));
		a.addItem(new Item(4));
		
		Sequence b = keepLastItems(a, 15);
		System.out.println(b.toString());
	}
}
