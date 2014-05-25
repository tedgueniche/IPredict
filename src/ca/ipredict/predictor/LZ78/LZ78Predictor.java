package ca.ipredict.predictor.LZ78;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Predictor;

public class LZ78Predictor implements Predictor {

	/**
	 * List of training sequences
	 */
	private List<Sequence> mTrainingSequences;
	
	/**
	 * Set of unique labels found in the training set
	 */
	private HashSet<Integer> alphabet;
	
	/**
	 * Number of nodes in the predictor
	 */
	private int count;
	
	/**
	 * Max order of this predictor (defined with the training data in Preload())
	 */
	private int order;
	
	/**
	 * Dictionary that maps a LZPhrase to a support
	 */
	private HashMap<List<Integer>, Node> dictionary;
	
	
	@Override
	public void setTrainingSequences(List<Sequence> trainingSequences) {
		mTrainingSequences = trainingSequences;
	}

	@Override
	public Boolean Preload() {
		
		dictionary = new HashMap<List<Integer>, Node>();
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
				Node node = dictionary.get(lzPhrase);
				if(node != null) {
					
					//incrementing the support of this phrase
					node.inc();
					dictionary.put(lzPhrase, node);
					
					//Updating the max order if needed
					order = (lzPhrase.size() > order) ? lzPhrase.size() : order;
					
					//adding the current node as a child of the prefix
					if(prefix.size() > 0 && dictionary.get(prefix) != null) {
						dictionary.get(prefix).incChildSupport();
					}
					
					//adding the current item to the prefix
					prefix.add(cur);
				}
				else {
					
					//adding the current node as a child of the prefix
					if(prefix.size() > 0 && dictionary.get(prefix) != null) {
						dictionary.get(prefix).addChild(cur);
					}
					
					//adding this phrase in the dictionary
					dictionary.put(lzPhrase, new Node(cur));
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
		Collections.reverse(lastItems);
		
		//for each order, starting with the highest one
		for(Item item : lastItems) {
			
			//adding the current element in reverse order
			prefix.add(0, item.val);
			
			Node parent = dictionary.get(prefix);
			
			//Stop the prediction if the current node does not exists
			//because if X does not exists than any node more precise than X cannot exists
			if(parent == null) {
				break;
			}
			
			//calculating the probability of the escape
			int escapeK = parent.getSup() - parent.getChildSup(); 
			
			//for each child of this prefix
			for(Integer label : parent.children) {
				
				lzPhrase = new ArrayList<Integer>(prefix);
				lzPhrase.add(label);
 				Node child = dictionary.get(lzPhrase);
				
				if(child != null) {
					
					//prob for this item for order k+1
					Double probK1 = results.getOrDefault(label, 0d);
					Double probK = ((double) child.getSup() / parent.getSup()) + (escapeK * probK1);
					results.put(label, probK);	
				}
			}
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
		
		//returns the resulting sequence
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
