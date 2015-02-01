package ca.ipredict.predictor.CPT.CPTPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;

/**
 * Frequence Itemset Finder (FIF)
 * Using a brute-force approach with a complexity of O()
 * This approach generate all the possible candidates including low supporting ones.
 */
public class FIFRaw implements FIF {
	
	
	public HashMap<Item, Integer> itemFrequencies;
	
	
	@Override
	public HashMap<Item, Integer> getItemFrequencies(List<Sequence> seqs) {
		if(itemFrequencies == null) {

			itemFrequencies = new HashMap<Item, Integer>();
//			for(Sequence seq : seqs) {
//
//				for(Item item : seq.getItems()) {
//					
//					Integer support = itemFrequencies.get(item);
//					if(support == null) {
//						support = 0;
//					}
//					support++;
//					itemFrequencies.put(item, support);
//				}
//			}
		}
		
		return itemFrequencies;
	}
	
	
	/**
	 * Return all the consecutive items (length between [minLength,maxlength] ) found in the 
	 * given sequences with a high enough support (support >= minSup)
	 * @param minLength Minimum length for the itemsets
	 * @param maxlength Maximum length for the itemsets
	 */
	public List<List<Item>> findFrequentItemsets(List<Sequence> seqs, int minLength, int maxlength, int minSup) {
		
		itemFrequencies = new HashMap<Item, Integer>();

		List<List<Item>> frequents = new ArrayList<List<Item>>();
		HashMap<List<Item>, Integer> frequencies = new HashMap<List<Item>, Integer>();
		
		if(maxlength <= 1 || minLength > maxlength) {
			return frequents;
		}
		
		//Calculating frequencies by iterating through each sequence
		for(Sequence seq : seqs) {
			
			if(seq.size() >= minLength) {
				
				for(int i = 0; i < seq.size() - 1; i++) {
					
					//Calculate the frequencies of itemsets of size in range [minLength, maxlength]
					List<Item> itemset = new ArrayList<Item>();
					for(int offset = i; (offset - i) < maxlength && offset < seq.size(); offset++) {
						
						//adding one item at the time to the itemset
						itemset = new ArrayList<Item>(itemset);
						itemset.add(seq.get(offset));
						
						//saving the frequency of itemset if it is long enough
						if(itemset.size() >= minLength) {
							
							//Updating the frequency of this itemset
							Integer support = frequencies.get(itemset);
							if(support == null) {
								support = 0;
							}
							frequencies.put(itemset, support + 1);
							
						}
					}
					
					Integer support = itemFrequencies.get(seq.get(i));
					if(support == null) {
						support = 0;
					}
					support++;
					itemFrequencies.put(seq.get(i), support);
				}
			}
		}
		
		//Identifying the itemsets with a support high enough
		frequencies.entrySet().stream().
			filter(entry -> entry.getValue() >= minSup).
			forEach(entry -> frequents.add(entry.getKey()));
		
		return frequents;
	}
	
	
	public static void main(String...args) {
		
		
		//Training sequences
		List<Sequence> training = new ArrayList<Sequence>();
//		//1 2 3 4
//		Sequence seq1 = new Sequence(-1);
//		seq1.addItem(new Item(1));
//		seq1.addItem(new Item(2));
//		seq1.addItem(new Item(3));
//		seq1.addItem(new Item(4));
//		training.add(seq1);
		
		//1 2 3 4
		Sequence seq2 = new Sequence(-1);
		seq2.addItem(new Item(1));
		seq2.addItem(new Item(2));
		seq2.addItem(new Item(3));
		seq2.addItem(new Item(4));
		training.add(seq2);
		
		//1 2 3 4
		Sequence seq3 = new Sequence(-1);
		seq3.addItem(new Item(1));
		seq3.addItem(new Item(2));
		seq3.addItem(new Item(3));
		seq3.addItem(new Item(4));
		training.add(seq3);
		
//		//0 1 2 4
//		Sequence seq4 = new Sequence(-1);
//		seq4.addItem(new Item(0));
//		seq4.addItem(new Item(1));
//		seq4.addItem(new Item(2));
//		seq4.addItem(new Item(4));
//		training.add(seq4);
		
		
		FIF finder = new FIFRaw();
		System.out.println(finder.findFrequentItemsets(training,2,4,2));
	}
	
}
