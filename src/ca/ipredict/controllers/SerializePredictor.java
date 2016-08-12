package ca.ipredict.controllers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Predictor;
import ca.ipredict.predictor.DG.DGPredictor;
import ca.ipredict.predictor.Markov.MarkovAllKPredictor;
import ca.ipredict.predictor.profile.DefaultProfile;

public class SerializePredictor {

	/**
	 * Save the model to disk
	 * @param model
	 * @throws IOException
	 */
	public static void save(String filepath, Predictor model) throws IOException {
		
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(filepath));
		stream.writeObject(model);
		stream.close();
	}
	
	/**
	 * Loads the model from disk
	 * @return the model
	 */
	public static Predictor load(String filepath) throws IOException, ClassNotFoundException {
		
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(filepath));
		Predictor model = (Predictor) stream.readObject();
		stream.close();
		
		return model;
	}
	
	public static void main(String...args) throws IOException, ClassNotFoundException {
		

		//initializing the CPT Plus predictor 
		MarkovAllKPredictor akom = new MarkovAllKPredictor();
		
		//setting the experiment parameters
		DefaultProfile profile = new DefaultProfile();
		profile.Apply();
		
		//generating the training set
		List<Sequence> trainingSet = new ArrayList<Sequence>();
		trainingSet.add(Sequence.fromString(1, "1 4 2 5 3"));
		trainingSet.add(Sequence.fromString(2, "1 3 5 2 3 2 1 5 3"));
		trainingSet.add(Sequence.fromString(3, "1 5 3"));
		trainingSet.add(Sequence.fromString(4, "1 5 2 3"));
		
		//training the model
		akom.Train(trainingSet);
		
		//save the model to disk
		save("/home/ted/akom.ser", akom);
		
		//for testing purposes
		//at this point the model is saved on disk and can be reloaded from
		//disk in the future
		akom = null;
		
		//load the model from disk
		akom = (MarkovAllKPredictor) load("/home/ted/akom.ser");
		
		//predicting a sequence
		Sequence predicted = akom.Predict(Sequence.fromString(5, "1 4 3 2"));
		
		//output prediction
		System.out.println("Predicted symbol: "+ predicted);
	}
}
