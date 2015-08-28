package ca.ipredict.controllers;

import ca.ipredict.helpers.StatsLogger;
import ca.ipredict.predictor.Evaluator;
import ca.ipredict.predictor.CPT.CPT.CPTPredictor;
import ca.ipredict.predictor.CPT.CPTPlus.CPTPlusPredictor;
import ca.ipredict.predictor.DG.DGPredictor;
import ca.ipredict.predictor.LZ78.LZ78Predictor;
import ca.ipredict.predictor.Markov.MarkovAllKPredictor;
import ca.ipredict.predictor.Markov.MarkovFirstOrderPredictor;
import ca.ipredict.predictor.TDAG.TDAGPredictor;

/**
 * Main controller to compare all the predictors
 */
public class MainController {

	public static void main(String[] args) {
		
			//instantiate the evaluator
			Evaluator evaluator = new Evaluator("/home/ted/java/IPredict/datasets");
			
			//Loading data set
			evaluator.addDataset("BMS", 		5000);
			evaluator.addDataset("SIGN", 		1000);
			evaluator.addDataset("MSNBC", 		5000);
			evaluator.addDataset("BIBLE_WORD", 	5000);
			evaluator.addDataset("BIBLE_CHAR", 	5000);
			evaluator.addDataset("KOSARAK", 	45000);
			evaluator.addDataset("FIFA", 		5000);
			
			//Loading predictors
			evaluator.addPredictor(new DGPredictor("DG", "lookahead:4"));
			evaluator.addPredictor(new TDAGPredictor());
			evaluator.addPredictor(new CPTPlusPredictor("CPT+",		"CCF:true CBS:true"));
//			evaluator.addPredictor(new CPTPlusPredictor("CPT++",		"CCF:false CBS:true"));
			evaluator.addPredictor(new CPTPredictor());
			evaluator.addPredictor(new MarkovFirstOrderPredictor());
			evaluator.addPredictor(new MarkovAllKPredictor());
			evaluator.addPredictor(new LZ78Predictor());
			
			//Start the experiment
			StatsLogger results = evaluator.Start(Evaluator.KFOLD, 14 , true, true, true);
			
	}

}
