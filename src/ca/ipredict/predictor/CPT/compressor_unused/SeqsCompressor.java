package ca.ipredict.predictor.CPT.compressor_unused;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;

public class SeqsCompressor {

	
	
	
	public static List<Sequence> compress(List<Sequence> seqs) {

		HashMap<Item, Integer> frequencies = getItemsFrequency(seqs);
		
		//Finding rare items to exclude
		HashSet<Item> rareItems = findLowSupportItems(frequencies, 2);
		
		//Finding frequent itemset
		HashMap<Item,List<Item>> itemsets = findFreqItemset(seqs, frequencies, 2, 10);
		
		//Reconstructing the dataset without the rare items and by
		//introducing the itemsets
		Item lastItem = null;
		List<Sequence> compressedSequences = new ArrayList<Sequence>();
		for(Sequence seq : seqs) {
		
			//reconstructing the current sequence
			Sequence newSeq = new Sequence(seq.getId());
			for(Item item : seq.getItems()) {
				
				if(rareItems.contains(item) == true) {}
				
				else if(itemsets.containsKey(item) == true) {
					
					if(lastItem != null) {
						
						Integer id = lastItem.val * item.val * -1;
						newSeq.addItem(new Item(id));
					}
					lastItem = item;
				}
				else {
					if(lastItem != null) {
						newSeq.addItem(lastItem);
					}
					lastItem = null;
					
					newSeq.addItem(item);
				
				}
			}
			compressedSequences.add(newSeq);
		}
		
		return compressedSequences;
	}
	
	private static HashMap<Item, Integer> getItemsFrequency(List<Sequence> seqs) {
		
		HashMap<Item, Integer> items = new HashMap<Item, Integer>();
		
		//Calculating the frequencies of each items in the dataset
		for(Sequence seq : seqs) {
			for(Item item : seq.getItems()) {
				
				//Adding/Updating the support of itemset of size 1
				Integer support = items.get(item);
				if(support == null) {
					support = 0;
				}
				items.put(item, support + 1);				
			}
		}
		
		return items;
	}
	
	
	
	/**
	 * Rare Items Removal (RIR)
	 * Finds really low supporting items in the dataset
	 * @param seqs The dataset
	 * @param minSup The maxmimum support for a rare item
	 */
	private static HashSet<Item> findLowSupportItems(HashMap<Item, Integer> frequencies, int minSup) {
		
		HashSet<Item> rares = new HashSet<Item>();
		for(Entry<Item, Integer> entry : frequencies.entrySet()) {
			if(entry.getValue() <= minSup) {
				rares.add(entry.getKey());
			}
		}
		
		return rares;
	}
	
	/**
	 * Item Collapsing
	 * Transforms frequent sub-sequences and generate frequent itemsets
	 * @param seqs The dataset
	 * @param maxLength The maximum size of the generated itemsets
	 * @param minSup The minimum support of an itemset
	 */
	private static HashMap<Item, List<Item>> findFreqItemset(List<Sequence> seqs, HashMap<Item, Integer> frequencies, int maxLength, int minSup) {
		
		HashMap<Item, List<Item>> itemsets = new HashMap<Item, List<Item>>();
		
		HashMap<List<Item>, Integer> itemsetsFrequencies = new HashMap<List<Item>, Integer>();
		
		Item lastItem = null;
		
		//Find frequency of itemset of size 2
		for(Sequence seq : seqs) {
			for(Item item : seq.getItems()) {
				
				if(frequencies.containsKey(item) && frequencies.get(item) > minSup) {
					
					if(lastItem != null) {
						List<Item> itemset = new ArrayList<Item>();
						itemset.add(lastItem);
						itemset.add(item);
						
						Integer frequency = itemsetsFrequencies.get(itemset);
						if(frequency == null) {
							frequency = 0;
						}
						itemsetsFrequencies.put(itemset, frequency + 1);
					}
					lastItem = item;
					
				}
				else {
					lastItem = null;
				}
			}
		}
		
		System.out.println("Found "+ itemsetsFrequencies.size() + " pairs of items");
		
		int sumOfSupport = 0;
		
		//find frequent itemsets of size 2
		for(Entry<List<Item>, Integer> entry : itemsetsFrequencies.entrySet()) {
			
			if(entry.getValue() >= minSup) {
				itemsets.put(entry.getKey().get(0), entry.getKey());
				itemsets.put(entry.getKey().get(1), entry.getKey());
				
				sumOfSupport += entry.getValue();
			}
		}
		
		System.out.println("Found "+ itemsets.size() + " frequent itemsets");
		System.out.println("Saving "+ (sumOfSupport / 2) + " items in the trainingset");
		
		return itemsets;
	}

}
