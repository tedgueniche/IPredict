package ca.ipredict.predictor.CPT.CPT;

import java.util.ArrayList;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;

public class CPTHelper {
	
	
	/**
	 * Divides the target sequence into all possible sub sequence with a minimum size of minSize
	 * @param result The resulting list of sequence
	 * @param target The initial sequence to divide
	 * @param minSize The minimum size of a sub sequence
	 */
	public static void RecursiveDivider(List<Sequence> result, Sequence target, int minSize) {
		int size = target.size();
		
		result.add(target); //adding the resulting sequence to the result list
		
		//if the target is small enough or already too small
		if(size <= minSize) {
			return;
		}

		//Hiding one item at the time from the target
		for(int toHide = 0; toHide < size; toHide++) {
			
			//Constructing a new sequence from the target without the "toHide" item
			Sequence newSequence = new Sequence(-1);
			for(int toUse = 0 ; toUse < size; toUse++) {
				
				if(toUse != toHide) {
					newSequence.addItem(target.get(toUse));
				}
			}
			
			RecursiveDivider(result, newSequence, minSize);
		}
	}
	
	/**
	 * Return the last Length items
	 * @param sequence the sequence to slice
	 * @param length the size of the subsequences
	 */
	public static List<Sequence> sliceBasic(Sequence sequence, int length) { 
		List<Sequence> cutted = new ArrayList<Sequence>(); //result
		
		if(sequence.size() <= length){ 
			cutted.add(sequence);
			return cutted; //nothing to do for this sequence
		}
		
		cutted.add(new Sequence(0,sequence.getItems().subList(sequence.size() - length, sequence.size())));
		
		return cutted;
	}
	
	/**
	 * Slice a sequence into subsequence of size of Length
	 * Caution: this can generate a lot of subsequences
	 * @param sequence the sequence to slice
	 * @param length the size of the subsequences
	 */
	public static List<Sequence> slice(Sequence sequence, int length) {
		List<Sequence> cutted = new ArrayList<Sequence>(); //result
		
		if(sequence.size() <= length){ 
			cutted.add(sequence);
			return cutted; //nothing to do for this sequence
		}
		
		List<Item> items = sequence.getItems();
		
		int maxSlices = (int) Math.floor(items.size() / length); //max number of slice side to side
		int offset = (int) Math.floor(length / 2); //offset for second pass
		
		//First pass
		for(int i = 1; i < maxSlices; i++) {
			cutted.add(new Sequence(0,items.subList((i * length), (i+1) * length ))); //First pass cut
		}
		
		
		//Offset cut
		maxSlices = (int)Math.floor((items.size() - offset) / length);
		for(int i = 0; i < maxSlices; i++) {
			cutted.add(new Sequence(0,items.subList((i * length) + offset, ((i+1) * length) + offset )));
		}
		
		
		
		//Checking if the last item has been taken already, if not then we generate a sequence for him
		if( (items.size() % length) > 0 && ((items.size() - offset) % length) > 0 ) {
			cutted.add(new Sequence(0,items.subList(items.size() - length, items.size() )));
		}
		
		return cutted;
	}
	
	
}