package ca.ipredict.predictor.CPT;

import java.util.ArrayList;
import java.util.List;

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
