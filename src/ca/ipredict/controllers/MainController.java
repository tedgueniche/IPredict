package ca.ipredict.controllers;

import ca.ipredict.database.DatabaseHelper.Format;
import ca.ipredict.predictor.Evaluator;
import ca.ipredict.predictor.Parameters;
import ca.ipredict.predictor.CPT.NewCPTPredictor;
import ca.ipredict.predictor.DG.DGPredictor;
import ca.ipredict.predictor.Markov.MarkovAllKPredictor;
import ca.ipredict.predictor.Markov.MarkovFirstOrderPredictor;

/**
 * Main controller to compare all the predictors
 */
public class MainController {


	public static void main(String[] args) {
			
		Evaluator evaluator = new Evaluator();
	
		//Loading data sets
		evaluator.addDataset(Format.BMS, 		5000);
		evaluator.addDataset(Format.FIFA, 		5000);
		
		//Loading predictors
		evaluator.addPredictor(new DGPredictor());
		evaluator.addPredictor(new NewCPTPredictor());
		evaluator.addPredictor(new MarkovFirstOrderPredictor());
		evaluator.addPredictor(new MarkovAllKPredictor());
		
		evaluator.Start(Evaluator.KFOLD, 12, true);
		
		System.out.println();
		System.out.print(Parameters.tostring());	
	}

}
