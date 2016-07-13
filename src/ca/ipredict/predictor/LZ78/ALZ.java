package ca.ipredict.predictor.LZ78;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Predictor;

/**
 * Predictor based on the Active LeZi algorithm
 * 
 * K. Gopalratnam and D. J. Cook. Active Lezi: An incremental parsing algorithm for sequential prediction. 
 * In Proceedings of the Florida Artiﬁcial Intelligence Research Symposium, 2003.
 * 
 * Not implemented yet
 */
public class ALZ extends Predictor implements Serializable {

	private static final long serialVersionUID = -5976132591876875727L;

	/**
	 * Number of nodes in the predictor
	 */
	private int count;
	
//	/**
//	 * Max order of this predictor (defined with the training data in Preload())
//	 */
//	private int order;
	
	/**
	 * Dictionary that maps a LZPhrase to a support
	 */
	private HashMap<List<Integer>, LZNode> mDictionary;
	
	
	public ALZ() {
		TAG = "ALZ";
	}
	
	public ALZ(String tag) {
		TAG = tag;
	}
	
	@Override
	public Boolean Train(List<Sequence> trainingSequences) {
		
		
		mDictionary = new HashMap<List<Integer>, LZNode>();

		LinkedList<Integer> window = new LinkedList<Integer>();
		int maxWindowLength = 0;
		
		
		for(Sequence seq : trainingSequences) {
			
			List<Item> items = seq.getItems();
			List<Integer> prefix = new ArrayList<Integer>();
			List<Integer> lzPhrase = new ArrayList<Integer>();
			
			//for each given item in this sequence
			for(int offset = 0; offset < items.size(); offset++) {
				
				
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
					
					//Updating the max order if needed
					maxWindowLength = (lzPhrase.size() > maxWindowLength) ? lzPhrase.size() : maxWindowLength;
					
					//adding this phrase in the dictionary
					mDictionary.put(lzPhrase, new LZNode(cur));
					prefix.clear();
					count++;
					
				}
				
				
				
				//adding the current item to the window,
				//if the window is too large, it removes the first oldest item
				window.add(cur);
				if(window.size() > maxWindowLength) {
					window.remove(0);
				}
				updateFromWindow(window);
				
				
				System.out.println(window);
				
			}
		}
		
		
		
		return null;
	}
	
	
	private void updateFromWindow(List<Integer> window) {
	
		
		int size = window.size();
		while(size > 0) {
			
			addToDictionnary(window);
			
			window = window.subList(1, window.size());
		}
		
		
	}
	
	
	private void addToDictionnary(List<Integer> lzPhrase) {
		
		
		Integer lastItem = lzPhrase.get(lzPhrase.size() - 1);
		List<Integer> prefix = new LinkedList<Integer>();
		if(lzPhrase.size() > 1) {
			prefix = lzPhrase.subList(0, lzPhrase.size() - 1);
		}
		
		addToDictionnary(lzPhrase, prefix, lastItem);
	}
	
	
	private void addToDictionnary(List<Integer> lzPhrase, List<Integer> prefix, Integer lastItem) {
		
		
		//update the prefix childs
		if(prefix.size() > 0) {
			
			if(mDictionary.get(prefix) == null) {
				mDictionary.put(prefix, new LZNode(prefix.subList(prefix.size() - 1, prefix.size()).get(0)));
				count++;
			}
			
			LZNode prefixNode = mDictionary.get(prefix);
			prefixNode.addChild(lastItem);
		}
		
		//adding this phrase in the dictionary
		mDictionary.put(lzPhrase, new LZNode(lastItem));

		count++;
	}
	

	@Override
	public Sequence Predict(Sequence target) {


		//TOBE FIXED: the support in the dictionary does not seems updated right in the updateFromWindow method
		
		
		
		return null;
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public static void main(String...args) {
		
		//abababcdcbdab
		Sequence s1 = new Sequence(1);
		s1.addItem(new Item(1));
		s1.addItem(new Item(2));
		s1.addItem(new Item(1));
		s1.addItem(new Item(2));
		s1.addItem(new Item(1));
		s1.addItem(new Item(2));
		s1.addItem(new Item(3));
		s1.addItem(new Item(4));
		s1.addItem(new Item(3));
		s1.addItem(new Item(2));
		s1.addItem(new Item(4));
		s1.addItem(new Item(1));
		s1.addItem(new Item(2));
		
		LinkedList<Sequence> training = new LinkedList<Sequence>();
		training.add(s1);
		
		ALZ alz = new ALZ();
		alz.Train(training);
	}

	@Override
	public float memoryUsage() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
