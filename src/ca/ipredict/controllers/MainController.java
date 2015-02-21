package ca.ipredict.controllers;

import ca.ipredict.database.DatabaseHelper.Format;
import ca.ipredict.predictor.Evaluator;
import ca.ipredict.predictor.CPT.CPT.CPTPredictor;
import ca.ipredict.predictor.CPT.CPTPlus.CPTPlusPredictor;
import ca.ipredict.predictor.DG.DGPredictor;
import ca.ipredict.predictor.LZ78.LZ78Predictor;
import ca.ipredict.predictor.Markov.MarkovAllKPredictor;
import ca.ipredict.predictor.Markov.MarkovFirstOrderPredictor;
import ca.ipredict.predictor.TDAG.TDAGPredictor;
import ca.ipredict.predictor.profile.Profile;

/**
 * Main controller to compare all the predictors
 */
public class MainController {

	public static void main(String[] args) {
		
		//instantiate the evaluator
		Evaluator evaluator = new Evaluator();
		
		//Loading data sets
		evaluator.addDataset(Format.BMS, 		5000);
		evaluator.addDataset(Format.SIGN, 		1000);
		evaluator.addDataset(Format.MSNBC, 		5000);
		evaluator.addDataset(Format.BIBLE_WORD, 5000);
		evaluator.addDataset(Format.BIBLE_CHAR, 5000);
		evaluator.addDataset(Format.KOSARAK, 	20000);
		evaluator.addDataset(Format.FIFA, 		5000);
		
		//Loading predictors
		evaluator.addPredictor(new DGPredictor());
		evaluator.addPredictor(new TDAGPredictor());
		evaluator.addPredictor(new CPTPlusPredictor("CPT+",		"CCF:true CBS:true"));
		evaluator.addPredictor(new CPTPredictor());
		evaluator.addPredictor(new MarkovFirstOrderPredictor());
		evaluator.addPredictor(new MarkovAllKPredictor());
		evaluator.addPredictor(new LZ78Predictor());
		
		//Start the experiment
		evaluator.Start(Evaluator.KFOLD, 14 , true);
//		evaluator.Start(Evaluator.HOLDOUT, 0.75f , true);
		
		//Shows the parameters used
		System.out.println();
		System.out.print(Profile.tostring());	
	}

}
