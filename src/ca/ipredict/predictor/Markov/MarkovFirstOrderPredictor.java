package ca.ipredict.predictor.Markov;

import java.util.HashMap;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Predictor;

/**
 * First-order markov model
 */
public class MarkovFirstOrderPredictor extends Predictor {

	/**
	 * List of unique items and their state in the Markov model
	 */
	private HashMap<Integer, MarkovState> mDictionary;
	

	public MarkovFirstOrderPredictor() {
		TAG = "1Mark";
	}
	
	public MarkovFirstOrderPredictor(String tag) {
		TAG = tag;
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
		return mDictionary.keySet().size();
	}
}
