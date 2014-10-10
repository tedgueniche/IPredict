package ca.ipredict.controllers;

import org.junit.Test;

import ca.ipredict.database.DatabaseHelper.Format;
import ca.ipredict.predictor.Evaluator;
import ca.ipredict.predictor.CPT.CPTPredictor;
import ca.ipredict.predictor.CPT.compressed.CPTPredictorCompressed;
import ca.ipredict.predictor.CPT2013.CPT13FastPredictor;
import ca.ipredict.predictor.CPT2013.CPT13Predictor;
import ca.ipredict.predictor.DG.DGPredictor;
import ca.ipredict.predictor.LZ78.LZ78Predictor;
import ca.ipredict.predictor.Markov.MarkovAllKPredictor;
import ca.ipredict.predictor.Markov.MarkovFirstOrderPredictor;
import ca.ipredict.predictor.TDAG.TDAGPredictor;
import ca.ipredict.predictor.profile.Profile;

public class performances {

	@Test
	public void CPTCompression() {
		
		
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
		evaluator.addPredictor(new CPTPredictorCompressed());
		evaluator.addPredictor(new CPTPredictor());
		evaluator.addPredictor(new CPT13FastPredictor());
		evaluator.addPredictor(new CPT13Predictor());
		
		//Start the experiment
//		evaluator.Start(Evaluator.KFOLD, 12 , true);
		evaluator.Start(Evaluator.HOLDOUT, 0.75f , true);
		
		//Shows the parameters used
		System.out.println();
		System.out.print(Profile.tostring());
		
	}
	
	@Test
	public void AllPredictorsAllDatasetsLong() {
		
		
		//instantiate the evaluator
		Evaluator evaluator = new Evaluator();
		
		//Loading data sets
//		evaluator.addDataset(Format.BMS, 		5000);
//		evaluator.addDataset(Format.SIGN, 		1000);
//		evaluator.addDataset(Format.MSNBC, 		5000);
//		evaluator.addDataset(Format.BIBLE_WORD, 5000);
//		evaluator.addDataset(Format.BIBLE_CHAR, 5000);	
//		evaluator.addDataset(Format.KOSARAK, 	20000);
//		evaluator.addDataset(Format.FIFA, 		5000);
		
		//Loading predictors
//		evaluator.addPredictor(new DGPredictor());
//		evaluator.addPredictor(new TDAGPredictor());
		evaluator.addPredictor(new CPTPredictorCompressed());
////		evaluator.addPredictor(new CPTPredictor());
//		evaluator.addPredictor(new CPT13FastPredictor());
//		evaluator.addPredictor(new CPT13Predictor());
//		evaluator.addPredictor(new MarkovFirstOrderPredictor());
//		evaluator.addPredictor(new MarkovAllKPredictor());
//		evaluator.addPredictor(new LZ78Predictor());
		
		//Start the experiment
		evaluator.Start(Evaluator.KFOLD, 12 , true);
//		evaluator.Start(Evaluator.HOLDOUT, 0.75f , true);
		
		//Shows the parameters used
		System.out.println();
		System.out.print(Profile.tostring());
		
	}

}
