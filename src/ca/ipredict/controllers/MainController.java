package ca.ipredict.controllers;

import ca.ipredict.database.DatabaseHelper.Format;
import ca.ipredict.predictor.Evaluator;
import ca.ipredict.predictor.CPT.CPTPredictor;
import ca.ipredict.predictor.CPT.compressed.CPTPredictorCompressed;
import ca.ipredict.predictor.DG.DGPredictor;
import ca.ipredict.predictor.profile.Profile;

/**
 * Main controller to compare all the predictors
 */
public class MainController {

	public static void main(String[] args) {
		
		//instantiate the evaluator
		Evaluator evaluator = new Evaluator();
		
		//Loading data sets
//		evaluator.addDataset(Format.BMS, 		5000);
//		evaluator.addDataset(Format.SIGN, 		1000);
//		evaluator.addDataset(Format.MSNBC, 		1000);
		evaluator.addDataset(Format.BIBLE_WORD, 2000);
//		evaluator.addDataset(Format.BIBLE_CHAR, 1000);
//		evaluator.addDataset(Format.KOSARAK, 	1000);
		
		//Loading predictors
		evaluator.addPredictor(new DGPredictor());
//		evaluator.addPredictor(new TDAGPredictor());
		evaluator.addPredictor(new CPTPredictor());
		evaluator.addPredictor(new CPTPredictorCompressed());
//		evaluator.addPredictor(new MarkovFirstOrderPredictor());
		
		//Start the experiment
		evaluator.Start(Evaluator.KFOLD, 8 , true);
		
		//Shows the parameters used
		System.out.println();
		System.out.print(Profile.tostring());	
	}

}
