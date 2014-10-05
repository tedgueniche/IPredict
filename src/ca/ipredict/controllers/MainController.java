package ca.ipredict.controllers;

import ca.ipredict.database.DatabaseHelper.Format;
import ca.ipredict.predictor.Evaluator;
import ca.ipredict.predictor.CPT.CPTPredictor;
import ca.ipredict.predictor.CPT.compressed.CPTPredictorCompressed;
import ca.ipredict.predictor.CPT2013.CPT13FastPredictor;
import ca.ipredict.predictor.CPT2013.CPT13Predictor;
import ca.ipredict.predictor.DG.DGPredictor;
import ca.ipredict.predictor.LZ78.LZ78Predictor;
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
		evaluator.addDataset(Format.MSNBC, 		2000);
		evaluator.addDataset(Format.BIBLE_WORD, 1000);
		evaluator.addDataset(Format.BIBLE_CHAR, 1000);
		evaluator.addDataset(Format.KOSARAK, 	2000);
		evaluator.addDataset(Format.FIFA, 		2000);
		
		//Loading predictors
		evaluator.addPredictor(new DGPredictor());
		evaluator.addPredictor(new TDAGPredictor());
		evaluator.addPredictor(new CPTPredictorCompressed());
		evaluator.addPredictor(new CPTPredictor());
		evaluator.addPredictor(new CPT13FastPredictor());
		evaluator.addPredictor(new CPT13Predictor());
		evaluator.addPredictor(new MarkovFirstOrderPredictor());
		evaluator.addPredictor(new LZ78Predictor());
		
		//Start the experiment
		evaluator.Start(Evaluator.KFOLD, 8 , true);
		
		//Shows the parameters used
		System.out.println();
		System.out.print(Profile.tostring());	
	}

}
