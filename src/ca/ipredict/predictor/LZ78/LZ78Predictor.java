package ca.ipredict.predictor.LZ78;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Predictor;

public class LZ78Predictor implements Predictor {

	private List<Sequence> mTrainingSequences; //list of sequences to test
	private HashSet<Integer> alphabet;
	private int count;
	private int order;
	
	private HashMap<List<Integer>, Integer> dictionary;
	
	@Override
	public void setTrainingSequences(List<Sequence> trainingSequences) {
		mTrainingSequences = trainingSequences;
	}

	@Override
	public Boolean Preload() {
		
		dictionary = new HashMap<List<Integer>, Integer>();
		alphabet = new HashSet<Integer>();
		order = 0;
		
		//for each training sequence
		for(Sequence seq : mTrainingSequences) {
			
			List<Item> items = seq.getItems();
			List<Integer> lzPhrase = new ArrayList<Integer>();
			List<Integer> prefix = new ArrayList<Integer>();
			
			//for each given item in this sequence
			int offset = 0;
			while(offset < items.size()) {
				
				//generating the lzPhrase from the prefix and the current item
				Integer cur = items.get(offset).val;
				lzPhrase = new ArrayList<Integer>(prefix);
				lzPhrase.add(cur);
				
				
				//if the dictionary contains this phrase already
				Integer support = dictionary.get(lzPhrase);
				if(support != null) {
					
					//incrementing the support of this phrase
					dictionary.put(lzPhrase, support + 1);
					
					order = (lzPhrase.size() > order) ? lzPhrase.size() : order;
					
					//adding the current item to the prefix
					prefix.add(cur);
				}
				else {
					
					//adding this phrase in the dictionary
					dictionary.put(lzPhrase, 1);
					prefix.clear();
					count++;
				}
				
				
				//adding the current item if it not in the alphabet
				alphabet.add(cur);
				
				//incrementing the offset
				offset++;
			}
		
		}
		
		
		return true;
	}

	@Override
	public Sequence Predict(Sequence target) {
		
		//Map each item from the alphabet to a probability
		HashMap<Integer, Double> results = new HashMap<Integer, Double>();
		
		//keeping the last X items from the target sequence
		//X being the order of this predictor.
		List<Integer> lzPhrase = new ArrayList<Integer>();
		List<Integer> prefix = new ArrayList<Integer>();
		List<Item> lastItems = target.getLastItems(order, 0).getItems();
		for(Item item : lastItems) {
			prefix.add(item.val);
		}
		
		//for each order, starting with the highest one
		while(prefix.size() > 0) {
			
			
			HashMap<Integer, Double> intermResults = new HashMap<Integer, Double>();
			Double supportSum = 0d;
			
			//for each item in the alphabet, calculate its probability of order K
			for(Integer cur : alphabet) {
				
				lzPhrase = new ArrayList<Integer>(prefix);
				lzPhrase.add(cur);
				
				//if the dictionary contains this phrase
				Integer support = dictionary.get(lzPhrase);
				if(support != null) {
					
					//calculating the prob this item for order k
					Integer parentSupport = dictionary.get(prefix);
					Double probK = ((double) support / parentSupport);
					intermResults.put(cur, probK);
					
					//incrementing the total support for this order
					supportSum += support;
				}
				else {
					intermResults.put(cur, 0d);
				}
			}
			
			//calculating the probability of the escape
			Integer supParent = (dictionary.get(prefix) != null) ? dictionary.get(prefix) : 1;
			Double escapeK = 1 - ((double) supportSum / supParent);

			//for each item in the alphabet, update its probability for order K
			for(Integer cur : alphabet) {
				
				//prob for this item for order k+1
				Double probK1 = results.get(cur);
				if(probK1 == null) {
					probK1 = 0d;
				}
				
				Double probK = intermResults.get(cur) + (escapeK * probK1);
				results.put(cur, probK);
				
			}
			
			
			//removing the first element from the prefix
			prefix.subList(0, 1).clear();

		}
		
		
		//generating a prediction from the most probable item in the dictionary
		Double highestScore = 0.0d;
		Integer mostProbableItem = null;
		for(Entry<Integer, Double> entry : results.entrySet()) {
			
			if(entry.getValue() > highestScore) {
				highestScore = entry.getValue();
				mostProbableItem = entry.getKey();
			}
		}
		
		
		Sequence predicted = new Sequence(-1);
		predicted.addItem(new Item(mostProbableItem));
		return predicted;
	}

	@Override
	public String getTAG() {
		return "LZ78";
	}

	@Override
	public long size() {
		return count;
	}

}
