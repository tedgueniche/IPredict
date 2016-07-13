package ca.ipredict.predictor.LZ78;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Predictor;

/**
 * Predictor based on the LZ78 Algorithm
 * 
 * K. Gopalratnam and D. J. Cook. Active Lezi: An incremental parsing algorithm for sequential prediction. 
 * In Proceedings of the Florida Artiﬁcial Intelligence Research Symposium, 2003.
 */
public class LZ78Predictor extends Predictor implements Serializable {

	private static final long serialVersionUID = 4653613493481192897L;

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
	private HashMap<List<Integer>, LZNode> mDictionary;
	
	
	public LZ78Predictor() {
		TAG = "LZ78";
	}
	
	public LZ78Predictor(String tag) {
		TAG = tag;
	}

	@Override
	public Boolean Train(List<Sequence> trainingSequences) {
		
		//TODO: [Reduce spatial size] Implement the mDictionary with a String hash instead of a List<Integer>, see AllKOrderMarkov.
		mDictionary = new HashMap<List<Integer>, LZNode>();
		order = 0;
		
		//for each training sequence
		for(Sequence seq : trainingSequences) {
			
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
				LZNode node = mDictionary.get(lzPhrase);
				if(node != null) {
					
					//incrementing the support of this phrase
					node.inc();
					mDictionary.put(lzPhrase, node);
					
					//Updating the max order if needed
					order = (lzPhrase.size() > order) ? lzPhrase.size() : order;
					
					//adding the current node as a child of the prefix
					if(prefix.size() > 0 && mDictionary.get(prefix) != null) {
						mDictionary.get(prefix).incChildSupport();
					}
					
					//adding the current item to the prefix
					prefix.add(cur);
				}
				else {
					
					//adding the current node as a child of the prefix
					if(prefix.size() > 0 && mDictionary.get(prefix) != null) {
						mDictionary.get(prefix).addChild(cur);
					}
					
					//adding this phrase in the dictionary
					mDictionary.put(lzPhrase, new LZNode(cur));
					prefix.clear();
					count++;
					
				}
				
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
			
			LZNode parent = mDictionary.get(prefix);
			
			//Stop the prediction if the current node does not exists
			//because if X does not exists than any node more precise than X cannot exists
			if(parent == null) {
				break;
			}
			
			//calculating the probability of the escape
			int escapeK = parent.getSup() - parent.getChildSup(); 
			
			//for each child of this prefix
			for(Integer value : parent.children) {
				
				lzPhrase = new ArrayList<Integer>(prefix);
				lzPhrase.add(value);
 				LZNode child = mDictionary.get(lzPhrase);
				
				if(child != null) {
					
					//prob for this item for order k+1
					Double probK1 = results.getOrDefault(value, 0d);
					Double probK = ((double) child.getSup() / parent.getSup()) + (escapeK * probK1);
					results.put(value, probK);	
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

	
	public long size() {
		return count;
	}

	/**
	 * There is 3 integer per node (12 bytes) + a list of pointers to the other nodes
	 */
	public float memoryUsage() {
		
		float size = 0f;
		
		for(LZNode node : mDictionary.values()) {
			size += 12 + (4 * node.children.size());
		}
		
		return size; 
	}
	
	public static void main(String...args) {
		
		//aaababbbbbaabccddcbaaaa
		Sequence s1 = new Sequence(1);
		s1.addItem(new Item(1));//a
		s1.addItem(new Item(1));//a
		s1.addItem(new Item(1));//a
		s1.addItem(new Item(2));//b
		s1.addItem(new Item(1));//a
		s1.addItem(new Item(2));//b
		s1.addItem(new Item(2));//b
		s1.addItem(new Item(2));//b
		s1.addItem(new Item(2));//b
		s1.addItem(new Item(2));//b
		s1.addItem(new Item(1));//a
		s1.addItem(new Item(1));//a
		s1.addItem(new Item(2));//b
		s1.addItem(new Item(3));//c
		s1.addItem(new Item(3));//c
		s1.addItem(new Item(4));//d
		s1.addItem(new Item(4));//d
		s1.addItem(new Item(3));//c
		s1.addItem(new Item(2));//b
		s1.addItem(new Item(1));//a
		s1.addItem(new Item(1));//a
		s1.addItem(new Item(1));//a
		s1.addItem(new Item(1));//a
		
		LinkedList<Sequence> training = new LinkedList<Sequence>();
		training.add(s1);
		
		LZ78Predictor lz = new LZ78Predictor();
		lz.Train(training);
		
		System.out.println(lz.size());
		System.out.println(lz.memoryUsage() + " bytes");
	}

}
