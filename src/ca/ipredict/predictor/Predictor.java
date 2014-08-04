package ca.ipredict.predictor;
import java.util.List;

import ca.ipredict.database.Sequence;

/**
 * Interface for all the predictors
 */
public abstract class Predictor {

	/**
	 * Represent the unique name of this predictor
	 * each subclass should overwrite this TAG
	 */
	protected String TAG;
	
	public Predictor(){}
	
	public Predictor(String tag) {
		this.TAG = tag;
	}
	
	/**
	 * Trains this predictor with the provided training data
	 * @return true on success
	 */
	public abstract Boolean Train(List<Sequence> trainingSequences);
	
	/**
	 * Predict the next element in the given sequence
	 * @param sequence to predict
	 */
	public abstract Sequence Predict(Sequence target);
	
	/**
	 * Get the predictor's TAG (unique string identifier)
	 */
	public String getTAG() {
		return TAG;
	}
	
	/**
	 * Get the size of the predictor
	 */
	public abstract long size();
	
}
