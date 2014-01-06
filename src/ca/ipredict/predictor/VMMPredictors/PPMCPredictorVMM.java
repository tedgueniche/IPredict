package ca.ipredict.predictor.VMMPredictors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vmm.algs.BinaryCTWPredictor;
import vmm.algs.DCTWPredictor;
import vmm.algs.PPMCPredictor;
import vmm.algs.PSTPredictor;
import vmm.pred.VMMPredictor;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.helpers.MemoryLogger;
import ca.ipredict.predictor.Parameters;
import ca.ipredict.predictor.Predictor;

public class PPMCPredictorVMM implements Predictor{
	
	PPMCPredictor predictor;
	
	private List<Sequence> mTrainingSequences; //list of sequences to test
	
	Set<Integer> allItems = new HashSet<Integer>();
	
	int abSize = 0;;
	int vmmOrder =0;
	
	
	public PPMCPredictorVMM(int abSize, int vmmOrder){
		predictor = new PPMCPredictor();
		this.abSize = abSize;
		this.vmmOrder = vmmOrder;
	}

	@Override
	public void setTrainingSequences(List<Sequence> trainingSequences) {
		mTrainingSequences = trainingSequences;
		
	}

	@Override
	public Boolean Preload() {
		System.out.println("START PRELOAD");
		//Logging memory usage
		MemoryLogger.addUpdate();

		// initialize predictor
		predictor.init(abSize, vmmOrder);
//		predictor.init(2560, 0.001, 0.0, 0.0001, 1.05, 20);
		
		// for each training sequence, convert it to char
		int count =0;
		for(Sequence seq : mTrainingSequences){
//			System.out.println(++count);
			char[] charSeq =  new char[seq.size()];
			for(int i=0; i< seq.size(); i++){
				int val = seq.get(i).val.intValue();
				charSeq[i] = intToChar(val);
				// add current item to the set of items
				allItems.add(val);
				// OPTIMIZATION - PHILIPPE  2013-05-25
				
			}
			// train the predictor with the sequence
			predictor.learn(charSeq.toString());
		}
		
		//Logging memory usage
		MemoryLogger.addUpdate();

		System.out.println("END PRELOAD");
		
		return true;
	}
	
	/**
	 * Convert an integer to a char
	 * @param i the integer
	 * @return a char
	 */
	private char intToChar(int i){
		return (char) i;
	}

	@Override
	public Sequence Predict(Sequence test) {
		// convert target test sequence to string
		char[] testSeq =  new char[test.size()];
		for(int i=0; i< test.size(); i++){
			int val = test.get(i).val.intValue();
			testSeq[i] = intToChar(val);
		}
		
		//  loop over all items to find the best prediction
		double maxPrediction = 0;
		int maxItem = -1;
//		System.out.println(allItems.size());
		for(Integer item : allItems){
			double prediction = predictor.predict(intToChar(item), testSeq.toString());
			if(prediction > maxPrediction){
				maxItem = item;
				maxPrediction = prediction;
//				if(prediction > 0.51){
//					break;
//				}
			}
		}
		
		Sequence predicted = new Sequence(-1);
		
//		System.out.println("prediction : " + maxItem);
		//if there is a max item (there needs to be at least on item in the CountTable)
		if(maxItem != -1) {
			Item predictedItem = new Item(maxItem);
			predicted.addItem(predictedItem);
		}
		return predicted;
	}

	@Override
	public String getTAG() {
		return "PPMC";
	}

	public long size() {
		return -999;
	}


}
