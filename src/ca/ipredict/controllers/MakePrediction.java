package ca.ipredict.controllers;

import java.util.ArrayList;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.CPT.CPTPlus.CPTPlusPredictor;
import ca.ipredict.predictor.profile.DefaultProfile;

/**
 * This controller demonstrates how to train a single model
 * and make a prediction
 */
public class MakePrediction {

	
	/**
	 * Converts a string into a Sequence
	 * @param sequenceId id of the sequence to create
	 * @param input A string containing space separated integers. Eg: "1 4 7 1" 
	 * @return A sequence
	 */
	public static Sequence stringToSequence(int sequenceId, String input) {
		
		Sequence sequence = new Sequence(sequenceId);
		
		//splitting the string by space characters
		String[] items = input.split("\\s+");
		
		//parsing each item of the string
		//adding them in the sequence
		for(String item : items) {	
			sequence.addItem(new Item(Integer.parseInt(item)));
		}
		
		return sequence;
	}
	
	
	public static void main(String...args) {
		
		//initializing the CPT Plus predictor 
		CPTPlusPredictor akom = new CPTPlusPredictor();
		
		//setting the experiment parameters
		DefaultProfile profile = new DefaultProfile();
		profile.Apply();
		
		//generating the training set
		List<Sequence> trainingSet = new ArrayList<Sequence>();
		trainingSet.add(stringToSequence(1, "1 4 2 5 3"));
		trainingSet.add(stringToSequence(2, "1 3 5 2 3 2 1 5 3"));
		trainingSet.add(stringToSequence(3, "1 5 3"));
		trainingSet.add(stringToSequence(4, "1 5 2 3"));
		
		//training the model
		akom.Train(trainingSet);
		
		//predicting a sequence
		Sequence predicted = akom.Predict(stringToSequence(5, "1 4 3 2"));
		
		//output prediction
		System.out.println("Predicted symbol: "+ predicted);
	}
}