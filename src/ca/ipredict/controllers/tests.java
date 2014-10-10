package ca.ipredict.controllers;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.ipredict.database.DatabaseHelper.Format;
import ca.ipredict.predictor.Evaluator;
import ca.ipredict.predictor.Predictor;
import ca.ipredict.predictor.CPT.CPTPredictor;
import ca.ipredict.predictor.CPT.compressed.CPTPredictorCompressed;
import ca.ipredict.predictor.CPT2013.CPT13FastPredictor;
import ca.ipredict.predictor.CPT2013.CPT13Predictor;
import ca.ipredict.predictor.profile.Profile;

public class tests {

	@Test
	public void test() {
		
		CPT13FastPredictor p = new CPT13FastPredictor("CPT13Fa", "k:12.3 order:2");
		System.out.println(p.parameters.paramDouble("k"));
		System.out.println(p.parameters.paramInt("order"));
		
		
		//instantiate the evaluator
		Evaluator evaluator = new Evaluator();
		
		//Loading data sets
		evaluator.addDataset(Format.BMS, 		5000);
		
		//Loading predictors
		evaluator.addPredictor(new CPT13FastPredictor("CPT13Fa", "recursiveDividerMin:4"));
//		evaluator.addPredictor(new CPTPredictorCompressed());
		
		//Start the experiment
//		evaluator.Start(Evaluator.KFOLD, 12 , true);
		evaluator.Start(Evaluator.HOLDOUT, 0.75f , true);

		//Shows the parameters used
		System.out.println();
		System.out.print(Profile.tostring());
	}

}
