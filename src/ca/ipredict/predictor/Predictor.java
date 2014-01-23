package ca.ipredict.predictor;
import java.util.List;

import ca.ipredict.database.Sequence;

/**
 * Interface for all the predictors
 */
public interface Predictor {
	
	
	/**
	 * Set the training set
	 */
	public void setTrainingSequences(List<Sequence> trainingSequences);
	
	/**
	 * Trains this predictor with training data
	 * @return true on success
	 */
	public Boolean Preload();
	
	/**
	 * Predict the next element in the given sequence
	 * @param sequence to predict
	 */
	public Sequence Predict(Sequence target);
	
	/**
	 * Get the predictor's TAG (unique string identifier)
	 */
	public String getTAG();
	
	/**
	 * Get the size of the predictor
	 */
	public long size();
	
}
