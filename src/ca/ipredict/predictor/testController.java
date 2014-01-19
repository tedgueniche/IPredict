package ca.ipredict.predictor;

import java.util.ArrayList;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.CPT.CPTPredictor;
import ca.ipredict.predictor.CPT.LossLessCompactPredictor;
import ca.ipredict.predictor.CPT.NewCPTPredictor;

public class testController {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Sequence training1 = new Sequence(1);
		training1.addItem(new Item(1));
		training1.addItem(new Item(2));
		training1.addItem(new Item(1));
		training1.addItem(new Item(2));
		training1.addItem(new Item(3));
		
		Sequence training2 = new Sequence(2);
		training2.addItem(new Item(1));
		training2.addItem(new Item(2));
		training2.addItem(new Item(3));
		training2.addItem(new Item(4));
		training2.addItem(new Item(2));
		training2.addItem(new Item(1));
		training2.addItem(new Item(6));
		
		Sequence training3 = new Sequence(3);
		training3.addItem(new Item(1));
		training3.addItem(new Item(2));
		training3.addItem(new Item(4));
		training3.addItem(new Item(6));
		training3.addItem(new Item(5));
		
		List<Sequence> training = new ArrayList<Sequence>();
		training.add(training1);
//		training.add(training2);
//		training.add(training3);
		
		Sequence testing1 = new Sequence(1);
		testing1.addItem(new Item(1));
		testing1.addItem(new Item(2));
		testing1.addItem(new Item(3));
		testing1.addItem(new Item(4));
		
		List<Sequence> testing = new ArrayList<Sequence>();
		testing.add(testing1);
		
		NewCPTPredictor newcpt = new NewCPTPredictor();
		newcpt.setTrainingSequences(training);
		newcpt.Preload();
		System.out.println(newcpt.Predict(testing1));
		
//		CPTPredictor oldcpt = new CPTPredictor();
//		oldcpt.setTrainingSequences(training);
//		oldcpt.Preload();
//		System.out.println(oldcpt.Predict(testing1));
		
		LossLessCompactPredictor regcpt = new LossLessCompactPredictor();
		regcpt.setTrainingSequences(training);
		regcpt.Preload();
		System.out.println(regcpt.Predict(testing1));
		
		
	}

}
