package ca.ipredict.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Predictor;
import ca.ipredict.predictor.CPT.CPTPlus.CPTPlusPredictor;
import ca.ipredict.predictor.DG.DGPredictor;
import ca.ipredict.predictor.Markov.MarkovAllKPredictor;
import ca.ipredict.predictor.Markov.MarkovFirstOrderPredictor;
import ca.ipredict.predictor.profile.DefaultProfile;

/**
 * This controller demonstrates how to train multiple models and compare
 * their predictions
 */
public class ComparePrediction {
	
	public static void main(String...args) {
				
		//Initializing the predictors
		HashMap<String, Predictor> predictors = new HashMap<String, Predictor>();
		predictors.put("All-k-order Markov", new MarkovAllKPredictor("akom", "order:1"));
		predictors.put("First Order Markov", new MarkovFirstOrderPredictor("fos"));
		predictors.put("Dependency Graph", new DGPredictor("dg", "lookahead:4"));
		predictors.put("CPT+", new CPTPlusPredictor("cpt+"));
		
		//setting the experiment parameters
		DefaultProfile profile = new DefaultProfile();
		profile.Apply();
		
		//generating the training set
		List<Sequence> trainingSet = new ArrayList<Sequence>();
		trainingSet.add(Sequence.fromString(1, "1 4 2 5 3"));
		trainingSet.add(Sequence.fromString(2, "1 3 5 2 3 2 1 5 3"));
		trainingSet.add(Sequence.fromString(3, "1 5 3"));
		trainingSet.add(Sequence.fromString(4, "1 5 2 3"));
		
		//Sequence to predict
		Sequence toPredict = Sequence.fromString(5, "1 4 3 2");
		
		//training the models
		for(Predictor predictor : predictors.values()) {
			predictor.Train(trainingSet);
		}
		
		//making a prediction per model
		for(String predictorName : predictors.keySet()) {
			
			Sequence predicted = predictors.get(predictorName).Predict(toPredict);
			
			System.out.println(predictorName +": "+ predicted.toString());
		}
	}
}
