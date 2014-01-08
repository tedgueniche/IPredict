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
	
	/**
	 * Return a list of sequence containing less item than the original sequence.
	 * The items removed a considered noise, at level c , it removes c items.
	 * The returned sequences are all the possible combinations when removing c items;
	 * @param level The level of noise to remove
	 */
	public static List<Item[]> noiseRemover(Item[] sequence, int level) {
		
		if(level < 1 || level > 2) {
			System.err.println("Level of "+ level +" not supported in noiseRemover()");
			return null;
		}
		/*
		List<Item[]> results = new ArrayList<Item[]>();
		int offset = 0;
		for(int i = 0 ; i < sequence.length ; i++) {
			
			Item[] newSeq = new Item[sequence.length - level];
			
			for(int j = 0 ; j < level; j++) {
				
			}
			
			offset++;
		}
		
		
		
		return results;
		*/
		return null;
	}
	
	public static List<Item[]> noiseRemover(Item[] sequence) {
		
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
