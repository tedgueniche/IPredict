package ca.ipredict.predictor.Markov;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Paramable;
import ca.ipredict.predictor.Predictor;

/**
 * All-Kth-Order Markov Model is a Variable order Markov Model
 * 
 * Source: J. Pitkow and P. Pirolli, "Mining longest repeating subsequences to predict world wide web surfing" in Proc. USENIX Symp. on Internet Technologies and Systems, 1999, pp. 1.
 */
public class MarkovAllKPredictor extends Predictor implements Serializable {
	
	/**
	 * order of the model (default value)
	 */
	private int K = 5;
	
	/**
	 * contains a list of unique items (one or multiple) and their state in the Markov model
	 */
	private HashMap<String, MarkovState> mDictionary;
	
	public Paramable parameters;
	
	public MarkovAllKPredictor() {
		TAG = "AKOM";
		parameters = new Paramable();
	}
	
	public MarkovAllKPredictor(String tag) {
		TAG = tag;
		parameters = new Paramable();
	}
	
	public MarkovAllKPredictor(String tag, String params) {
		this(tag);
		parameters.setParameter(params);
	}

	@Override
	public Boolean Train(List<Sequence> trainingSequences) {
		
		mDictionary = new HashMap<String, MarkovState>();
		
		//for each sequence in the training set
		for(Sequence seq : trainingSequences) {
			
			//for each items in this sequence, but the last one
			List<Item> items = seq.getItems();
			for(int i = 0 ; i < (items.size() - 1); i++) {

				int k = parameters.paramIntOrDefault("order", K);
				k = ( (items.size() - i) > k) ? k : (items.size() - i - 1);
				
				//For each order (from 1 to K)
				for(int c = 1 ; c <= k ; c++) {
					
					String key = "";
					//For each items for this order
					for(int j = 0 ; j < c; j++) {
						key += items.get(i + j).val.toString() + "_";
					}
					key = key.substring(0, key.length()-1);
					
					
					//Getting or creating the state associated with this item
					MarkovState state = mDictionary.get(key);
					if(state == null) {
						state = new MarkovState();
					}
					
					//Adding the transition to the next item
					state.addTransition(items.get(i + c).val);
					
					//Saving the changes into the dictionary
					mDictionary.put(key, state);
				}
			}
			
		}
		
		return true;
	}

	@Override
	public Sequence Predict(Sequence target) {
		
		int k = parameters.paramIntOrDefault("order", K);
		k = (target.size() >= k) ? k : (target.size());
		
		
		//for each order (from K to 1) or until we have a match
		for(int i = k; i > 0 ; i--) {
			
			//Building the key from the last i items of the target
			String key = "";
			for(int j = (target.size() - i) ; j < target.size(); j++) {
				key += target.get(j) + "_";
			}
			key = key.substring(0, key.length()-1);
			
			//Getting the associated state
			MarkovState state = mDictionary.get(key);
			
			//if the state is in the dictionary
			if(state != null) {
				Integer nextState = state.getBestNextState();
				Sequence predicted = new Sequence(-1);				
				predicted.addItem(new Item(nextState));
				
				if(i < K) {
					//return new Sequence(-1);
				}
				
				return predicted;
			}
			
		}
		
		//In case of failure (no match)
		return new Sequence(-1);
	}
	
	public long size() {
		
		long nodeCount = 0;
		
		for(MarkovState state : mDictionary.values()) {
			nodeCount += 1 + state.getTransitionCount();
		}
		
		return nodeCount;
	}

	/**
	 * Each node on the first level is an int (4 bytes)
	 * For each of these nodes, each child is two ints (8 bytes), one for the value/id and the other for its support 
	 */
	public float memoryUsage() {
		float size = 0f;
		
		for(MarkovState state : mDictionary.values()) {
			size += 4 + (8 * state.getTransitionCount());
		}
		
		return size;
	}
	
	

	
	public static void main(String[] args) {
		
		
		MarkovAllKPredictor predictor = new MarkovAllKPredictor();
		
		//Training sequences
		List<Sequence> training = new ArrayList<Sequence>();
		//1 2 3 4
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
		
		//0 1 2 4
		Sequence seq4 = new Sequence(-1);
		seq4.addItem(new Item(0));
		seq4.addItem(new Item(1));
		seq4.addItem(new Item(2));
		seq4.addItem(new Item(4));
		training.add(seq4);
		
		predictor.Train(training);
		
		//Testing
		Sequence seqT = new Sequence(-1);
		seqT.addItem(new Item(0));
		seqT.addItem(new Item(1));
		seqT.addItem(new Item(2));
		
		Sequence result = predictor.Predict(seqT);
		
		System.out.println(result.toString());
	}
	

}
