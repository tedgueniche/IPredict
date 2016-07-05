package ca.ipredict.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Predictor;
import ca.ipredict.predictor.DG.DGPredictor;
import ca.ipredict.predictor.Markov.MarkovAllKPredictor;
import ca.ipredict.predictor.Markov.MarkovFirstOrderPredictor;

/**
 * This controller demonstrates how to train multiple models and compare
 * their predictions
 */
public class ComparePrediction {
	
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
		
		//Initializing the predictors
		HashMap<String, Predictor> predictors = new HashMap<String, Predictor>();
		predictors.put("All-k-order Markov", new MarkovAllKPredictor("akom", "order:1"));
		predictors.put("First Order Markov", new MarkovFirstOrderPredictor("fos"));
		predictors.put("Dependency Graph", new DGPredictor("dg", "lookahead:4"));
		
		//generating the training set
		List<Sequence> trainingSet = new ArrayList<Sequence>();
		trainingSet.add(stringToSequence(1, "1 4 2 5 3"));
		trainingSet.add(stringToSequence(2, "1 3 5 2 3 2 1 5 3"));
		trainingSet.add(stringToSequence(3, "1 5 3"));
		trainingSet.add(stringToSequence(4, "1 5 2 3"));
		
		//Sequence to predict
		Sequence toPredict = stringToSequence(5, "1 4 3 2");
		
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
