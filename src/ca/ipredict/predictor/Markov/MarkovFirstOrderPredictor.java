package ca.ipredict.predictor.Markov;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Paramable;
import ca.ipredict.predictor.Predictor;

/**
 * First-order markov model is a Variable order Markov Model of first order.
 * 
 * 
 * Source: J. G. Cleary and I. Witten, "Data compression using adaptive coding and partial string matching"  Communications, IEEE Transactions on, vol. 32, pp. 396-402, 1984. 
 * Source: J. Pitkow and P. Pirolli, "Mining longest repeating subsequences to predict world wide web surfing" in Proc. USENIX Symp. on Internet Technologies and Systems, 1999, pp. 1.
 */
public class MarkovFirstOrderPredictor extends Predictor implements Serializable {

	private static final long serialVersionUID = -4801796583385392872L;

	/**
	 * List of unique items and their state in the Markov model
	 */
	private HashMap<Integer, MarkovState> mDictionary;
	

	public Paramable parameters;
	
	public MarkovFirstOrderPredictor() {
		TAG = "Mark1";
		parameters = new Paramable();
	}
	
	public MarkovFirstOrderPredictor(String tag) {
		TAG = tag;
		parameters = new Paramable();
	}
	
	public MarkovFirstOrderPredictor(String tag, String params) {
		this(tag);
		parameters.setParameter(params);
	}

	@Override
	public Boolean Train(List<Sequence> trainingSequences) {
		mDictionary = new HashMap<Integer, MarkovState>();
		
		//for each sequence in the training set
		for(Sequence seq : trainingSequences) {
			
			//for each items in this sequence, but the last one
			List<Item> items = seq.getItems();
			for(int i = 0 ; i < (items.size() - 1); i++) {
				
				//Getting or creating the state associated with this item
				MarkovState state = mDictionary.get(items.get(i).val);
				if(state == null) {
					state = new MarkovState();
				}
				
				//Adding the transition to the next item
				state.addTransition(items.get(i + 1).val);
				
				//Saving the changes into the dictionary
				mDictionary.put(items.get(i).val, state);
			}
			
		}

		return true;
	}

	@Override
	public Sequence Predict(Sequence target) {
		
		//Getting the last item in the target sequence
		Item lastItem = target.get(target.size() - 1);
		
		
		MarkovState state = mDictionary.get(lastItem.val);
		if(state == null) {
			return new Sequence(-1);
		}

		Integer nextState = state.getBestNextState();
		Sequence predicted = new Sequence(-1);
		predicted.addItem(new Item(nextState));

		return predicted;
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
}
