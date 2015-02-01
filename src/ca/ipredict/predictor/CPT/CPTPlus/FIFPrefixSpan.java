package ca.ipredict.predictor.CPT.CPTPlus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;

/**
 * Algorithm based on the popular PrefixSpan but adapted to find
 * sequential itemsets of size between [minSize, maxSize].
 * The actual projection per item is simulated.
 * 
 * Original paper:
 * Jian Pei, Jiawei Han, Behzad Mortazavi-Asl, Jianyong Wang, Helen Pinto, Qiming Chen, Umeshwar Dayal, Meichun Hsu: Mining Sequential Patterns by Pattern-Growth: The PrefixSpan Approach. IEEE Trans. Knowl. Data Eng. 16(11): 1424-1440 (2004)
 *
 */
public class FIFPrefixSpan implements FIF {

	public List<List<Item>> results;
	
	
	@Override
	public List<List<Item>> findFrequentItemsets(List<Sequence> seqs,
			int minLength, int maxlength, int minSup) {
		
		//store the resulting frequent itemsets
		results = new ArrayList<List<Item>>();
		
		//itemset to process for expansion
		LinkedList<List<Item>> toProcess = new LinkedList<>();
		
		//Initializing the projection
		Projection projection = new Projection();
		projection.initialize(seqs);
		
		//Initializing the queue with the frequent itemset of size 1
		HashMap<Item, Integer> oneItemCandidates = projection.initialize(seqs);
		for(Entry<Item, Integer> entry : oneItemCandidates.entrySet()) {
			
			//only keeping the item with a high enough support
			if(entry.getValue() >= minSup) {
				//Transforming the item into a itemset of size 1
				List<Item> candidate = new ArrayList<>();
				candidate.add(entry.getKey());
				
				//adding toProcess and to results
				toProcess.add(candidate);
				results.add(candidate);
			}
		}
		
		//consume the toProcess queue
		List<Item> itemset = null;
		while( (itemset = toProcess.poll()) != null) {
			
			//expand
			HashMap<Item, Integer> itemCandidates = projection.projectAndSelect(itemset);
			
			
			//generate itemset candidate
			for(Entry<Item, Integer> item : itemCandidates.entrySet()) {
				
				//if this item is frequent enough
				if(item.getValue() >= minSup) {
				
					//expanding the prefix
					List<Item> candidate = new ArrayList<Item>(itemset);
					candidate.add(item.getKey());
					
					//adding toProcess and to results
					toProcess.add(candidate);
					results.add(candidate);
				}
			}		
		}
		
		return results;
	}
	
	/**
	 * Return the items in the frequency map that have a high enough support
	 */
	public List<Item> select(HashMap<Item, Integer> frequencies, int minSup) {
		
		List<Item> frequents = new ArrayList<>();
		for(Entry<Item, Integer> pair : frequencies.entrySet()) {
			if(pair.getValue() >= minSup) {
				frequents.add(pair.getKey());
			}
		}
		return frequents;
	}
	
	
	/**
	 * Projection for this variation of PrefixSpan
	 */
	public class Projection {
		
		private List<Sequence> seqs;
		private Map<Integer, Bitvector> II; //Inverted Index
		
		public Projection(){
			II = new HashMap<Integer, Bitvector>();
		}
		
		/**
		 * Generate the II and extract the frequencies of unique items
		 */
		public HashMap<Item, Integer> initialize(List<Sequence> sequences) {
			
			//saving the sequences
			seqs = sequences;
			
			HashMap<Item, Integer> frequencies = new HashMap<Item, Integer>();
			
			int id = 0;
			for(Sequence seq : sequences) {
				
				for(Item item : seq.getItems()) {
					
					//Update the II
					Bitvector vector = II.get(item.val);
					if(vector == null) {
						vector = new Bitvector();
					}
					vector.setBit(id);
					II.put(item.val, vector);
					
					//Update the support of the current item
					Integer support = frequencies.get(item);
					if(support == null) {
						support = 0;
					}
					frequencies.put(item, support + 1);
				}
				
				id++;
			}
			
			return frequencies;
		}
		
		/**
		 * For a given prefix, return the possible suffix of size 1 and their frequency
		 */
		public HashMap<Item, Integer> projectAndSelect(List<Item> prefix) {
			
			//find the set of sequences containing the prefix
			Bitvector intersection = null;
			for(Item item : prefix) {
				if(intersection != null) {
					intersection.and(II.get(item.val));
				}
				else {
					intersection = (Bitvector) II.get(item.val).clone();
				}
			}
			
			//Calculating the frequencies of possible suffixes
			HashMap<Item, Integer> frequencies = new HashMap<Item, Integer>();
			for(int id = intersection.nextSetBit(0); id >= 0 ; id = intersection.nextSetBit(id + 1)) {
		
				Sequence seq = seqs.get(id);
				Item item = getSuffix(seq, prefix);
				
				if(item != null) {
					Integer support = frequencies.get(item);
					if(support == null) {
						support = 0;
					}
					frequencies.put(item, support + 1);
				}
			}
			
			return frequencies;
		}
		
		/**
		 * Finds the prefix in the sequence and returns the next item right after the prefix in the sequence
		 */
		protected Item getSuffix(Sequence seq, List<Item> prefix) {
			
			//if the prefix is empty, then return the first item from the sequence
			if(prefix.size() == 0) {
				return seq.get(0);
			}
			
			int offsetPrefix = 0;
			
			//finding the prefix in this sequence
			for(int offsetSeq = 0; offsetSeq < seq.size(); offsetSeq++) {
				
				//comparing the current item from the sequence with the current item from the prefix
				if(seq.get(offsetSeq).equals(prefix.get(offsetPrefix)) == true) {
					offsetPrefix++;
					
					if(offsetPrefix >= prefix.size()) {
						
						if(offsetSeq >= (seq.size() - 1)) {
							return null;
						}
						else {
							return seq.get(offsetSeq + 1);
						}
					}
				}
			}
			
			return null;
		}
	}
	
	
	public static void main(String...args) {
		

		//Training sequences
		List<Sequence> training = new ArrayList<Sequence>();
//		//1 2 3 4
		Sequence seq1 = new Sequence(-1);
		seq1.addItem(new Item(1));
		seq1.addItem(new Item(2));
		seq1.addItem(new Item(3));
		seq1.addItem(new Item(4));
		training.add(seq1);
		
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
		Sequence seq4 = new Sequence(-1);
		seq4.addItem(new Item(0));
		seq4.addItem(new Item(1));
		seq4.addItem(new Item(2));
		seq4.addItem(new Item(4));
		training.add(seq4);
		
		FIFPrefixSpan finder = new FIFPrefixSpan();
		Projection proj = finder.new Projection();
		
//		HashMap<Item, Integer> frequencies = proj.initialize(training);
//		System.out.println("Itemset-1 freq: "+ frequencies);
//		Map<Object, Object> candidates = frequencies.entrySet().stream().filter(e -> e.getValue() > 2).collect(Collectors.toMap(p -> ((Entry<Item, Integer>) p).getKey(), p -> ((Entry<Item, Integer>) p).getValue()));
//		System.out.println("Itemset-1 candidate: "+ candidates);
//		
//		List<Item> prefix = new ArrayList<Item>();
//		prefix.add(new Item(2));
//		frequencies = proj.projectAndSelect(prefix);
//		System.out.println(frequencies);
		
		
		List<List<Item>> frequentItemsets = finder.findFrequentItemsets(training, 1, 5, 2);
		
		System.out.println(frequentItemsets);
		
	}

	@Override
	public HashMap<Item, Integer> getItemFrequencies(List<Sequence> seqs) {
		// TODO Auto-generated method stub
		return null;
	}
}
