package ca.ipredict.controllers;

import java.io.IOException;
import java.util.Arrays;

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
 * This controller demonstrates how to compare all the predictors.
 * The results are expressed with various performance measures:
 * 
 * Success: is the ratio of successful predictions against the number of
 * wrong predictions, it is defined as (Number of success) / (Number of success +
 * number of failure)
 * 
 * Failure: is the inverse of the local accuracy: 1 - (Success)
 * 
 * No Match: is the ratio of unsuccessful prediction against the total num-
 * ber of tested sequences: (Number of sequence without prediction) / (number of
 * sequence)
 * 
 * Too Small: is the ratio of sequences too small to be used in the experimentation, it counts 
 * any sequence with a length smaller than the parameter consequentSize.
 * 
 * Overall: is our main measure to evaluates the accuracy of a given
 * predictor. It is the number of successful prediction against the total number of
 * tested sequences. (Number of success) / ( number of sequence)
 */
public class MainController {

	public static void main(String[] args) throws IOException {
			if (args.length < 1) {
				System.out.println("Missing required argument with data directory.");
				System.exit(1);
			}

			//instantiate the evaluator
			Evaluator evaluator = new Evaluator(args[0]);
			
			//Loading datasets
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
			evaluator.addPredictor(new CPTPredictor());
			evaluator.addPredictor(new MarkovFirstOrderPredictor());
			evaluator.addPredictor(new MarkovAllKPredictor());
			evaluator.addPredictor(new LZ78Predictor());
			
			//Start the experiment
			StatsLogger results = evaluator.Start(Evaluator.KFOLD, 14 , true, true, true);
	}

}
