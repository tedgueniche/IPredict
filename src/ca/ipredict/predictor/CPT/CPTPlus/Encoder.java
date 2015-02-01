package ca.ipredict.predictor.CPT.CPTPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;

/**
 * The encoder can compress sequences by replacing itemset by a single item
 * This class can do the conversion from a regular sequence to/from an encoded sequence
 */
public class Encoder {

	
	protected List<List<Item>> Dict;
	protected HashMap<List<Item>, Integer> InvDict;
	
	public Encoder() {
		Dict = new ArrayList<List<Item>>();
		InvDict = new HashMap<List<Item>, Integer>();
	}
	
	/**
	 * Add an itemset in the dictionary
	 * @return the Id of the itemset
	 */
	public int addEntry(List<Item> entry) {
		
		Integer id = getId(entry);
		if(id == null) {
			
			Dict.add(entry);
			id = Dict.size() - 1;
			InvDict.put(entry, id);
		}
		
		return id;
	}
	
	/**
	 * Return the itemset with the given id
	 */
	public List<Item> getEntry(int id) {
		return Dict.get(id);
	}
	
	/**
	 * Return the id of the given itemset
	 * @return the id or null if the itemset is not found
	 */
	public Integer getId(List<Item> entry) {
		Integer id = InvDict.get(entry);
		return id;
	}
	
	/**
	 * Return the id of an itemset and adds it in the dictionary if needed
	 */
	public Integer getIdorAdd(List<Item> entry) {
		return addEntry(entry);
	}
	
	/**
	 * Encode a sequence by replacing sequential items with known itemsets.
	 * It always try to use the longuest itemsets possible.
	 * @return A encoded hard copy of the original sequence
	 */
	public Sequence encode(Sequence seq) {

		if(seq == null || seq.getItems().size() == 0) {
			return seq ;
		}
		
		Sequence encoded = new Sequence(seq.getId());
		int seqSize = seq.getItems().size();
		
		//For each items in the sequence
		for(int i = 0; i < seqSize; i++) {
			
			//Finds the longuest itemset (taking everything from the current item to the end
			//end removing one item at the time (from the end) until it finds a known itemset
			LinkedList<Item> candidate = new LinkedList<Item>(seq.getItems().subList(i, seqSize));
			Integer idFound = null;
			while(idFound == null && candidate.size() > 0) {
				
				//if it found a known itemset
				idFound = getId(candidate);
				if(idFound != null) {		
					encoded.addItem(new Item(idFound));
					
					i += candidate.size() - 1; 
				}
				//special case when the candidate list has only a single item left
				else if(candidate.size() == 1) {
					idFound = addEntry(candidate);
					encoded.addItem(new Item(idFound));
				}
				//removing the last item
				else {
					candidate.removeLast();
				}
			}
		}
		
		return encoded;
	}
	
	/**
	 * Replace each itemset in the sequence with the original sequential items
	 * @return a hard decoded copy of the encoded sequence
	 */
	public Sequence decode(Sequence seq) {
		
		if(seq == null || seq.getItems().size() == 0) {
			return seq ;
		}
		
		Sequence decoded = new Sequence(seq.getId());
		
		//for each encoded item, it decodes 
		//it and adds it in the decoded sequence
		for(Item encodedItem : seq.getItems()) {
			
			//if it founds the itemset
			List<Item> itemset = getEntry(encodedItem.val);
			if(itemset != null) {
				
				for(Item decodedItem : itemset) {
					decoded.addItem(decodedItem);
				}
				
			}
			else {
				System.err.println("Could not find item: "+ encodedItem.val);
			}
		}
		
		
		return decoded;
	}

	
	public static void main(String...args) {
		
		
		Encoder en = new Encoder();
		
		//Pattern
		List<Item> p1 = new LinkedList<Item>();
		p1.add(new Item(42));
		p1.add(new Item(43));
		List<Item> p2 = new LinkedList<Item>();
		p2.add(new Item(42));
		List<Item> p3 = new LinkedList<Item>();
		p3.add(new Item(42));
		p3.add(new Item(43));
		p3.add(new Item(44));
		
		//1 2 3 4
		Sequence seq1 = new Sequence(-1);
		seq1.addItem(new Item(42));
		seq1.addItem(new Item(43));
		seq1.addItem(new Item(44));
		seq1.addItem(new Item(45));

		
		en.addEntry(p1);
		en.addEntry(p2);
		en.addEntry(p3);
		
		
		Sequence encoded = en.encode(seq1);
		System.out.println(seq1);
		System.out.println(encoded);
		System.out.println(en.decode(encoded));
		
	}
	
}
