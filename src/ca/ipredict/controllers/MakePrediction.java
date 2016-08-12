package ca.ipredict.controllers;

import java.util.ArrayList;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.CPT.CPTPlus.CPTPlusPredictor;
import ca.ipredict.predictor.Markov.MarkovAllKPredictor;
import ca.ipredict.predictor.profile.DefaultProfile;

/**
 * This controller demonstrates how to train a single model
 * and make a prediction
 */
public class MakePrediction {
	
	public static void main(String...args) {
		
		//initializing the CPT Plus predictor 
		MarkovAllKPredictor akom = new MarkovAllKPredictor();
		
		//setting the experiment parameters
		DefaultProfile profile = new DefaultProfile();
		profile.Apply();
		
		//generating the training set
		List<Sequence> trainingSet = new ArrayList<Sequence>();
		trainingSet.add(Sequence.fromString(1, "1 2 3 4"));
		trainingSet.add(Sequence.fromString(2, "1 2 3 5"));
		trainingSet.add(Sequence.fromString(3, "5 5 3 6"));
		trainingSet.add(Sequence.fromString(4, "1 2 3 7"));
		
		//training the model
		akom.Train(trainingSet);
		
		//predicting a sequence
		Sequence predicted = akom.Predict(Sequence.fromString(5, "5 5 3"));
		
		//output prediction
		System.out.println("Predicted symbol: "+ predicted);
	}
}