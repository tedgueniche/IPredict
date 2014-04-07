package ca.ipredict.controllers;

import ca.ipredict.database.DatabaseHelper.Format;
import ca.ipredict.predictor.Evaluator;
import ca.ipredict.predictor.DG.DGPredictor;
import ca.ipredict.predictor.Markov.MarkovAllKPredictor;
import ca.ipredict.predictor.Markov.MarkovFirstOrderPredictor;
import ca.ipredict.predictor.OLD_CPT.OLD_CPTPredictor;
import ca.ipredict.predictor.profile.Profile;

/**
 * Main controller to compare all the predictors
 */
public class MainController {

	public static void main(String[] args) {
		
		//instantiate the evaluator
		Evaluator evaluator = new Evaluator();
	
		//Loading data sets
		evaluator.addDataset(Format.BMS, 		2000);
		evaluator.addDataset(Format.SIGN, 		1000);
		
		//Loading predictors
		evaluator.addPredictor(new DGPredictor());
		evaluator.addPredictor(new OLD_CPTPredictor());
		evaluator.addPredictor(new MarkovFirstOrderPredictor());
		evaluator.addPredictor(new MarkovAllKPredictor());
		
		//Start the experiment
		evaluator.Start(Evaluator.KFOLD, 12, true);
		
		//Shows the parameters used
		System.out.println();
		System.out.print(Profile.tostring());	
	}

}
