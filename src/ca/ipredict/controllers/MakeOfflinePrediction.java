package ca.ipredict.controllers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Predictor;
import ca.ipredict.predictor.Markov.MarkovAllKPredictor;
import ca.ipredict.predictor.profile.DefaultProfile;

/**
 * This controller loads a trained model from disk (see SerializePredictor.java)
 * and can make prediction from STDIN. For each line in STDIN, this controller
 * will parse the line as a space separated integer list into a Sequence. It will then
 * output a single prediction for that sequence.
 */
public class MakeOfflinePrediction {

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
	
	
	public static void main(String...args) throws ClassNotFoundException, IOException {
		
		//load the model from disk
		MarkovAllKPredictor akom = (MarkovAllKPredictor) load("/home/ted/akom.ser");
		
		//setting the experiment parameters
		DefaultProfile profile = new DefaultProfile();
		profile.Apply();
		
		Scanner sc = new Scanner(System.in);
		
		String line = sc.nextLine();
		while(line.equals("exit") == false || line.equals("quit")) {
			
			Sequence sequence = Sequence.fromString(1, line);
			
			//make a prediction
			Sequence predicted = akom.Predict(sequence);
			 
			//output result on STDOUT
			System.out.println(predicted);
			
			//read and parse the line as a sequence
			line = sc.nextLine();
		}
		
	}
}
