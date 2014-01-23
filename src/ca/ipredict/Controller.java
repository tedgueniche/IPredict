package ca.ipredict;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import vmm.algs.BinaryCTWPredictor;
import vmm.algs.DCTWPredictor;

import ca.ipredict.database.DatabaseHelper;
import ca.ipredict.database.DatabaseHelper.Format;
import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.database.SequenceDatabase;
import ca.ipredict.database.SequenceStatsGenerator;
import ca.ipredict.helpers.MemoryLogger;
import ca.ipredict.helpers.StatsLogger;
import ca.ipredict.predictor.Parameters;
import ca.ipredict.predictor.Predictor;
import ca.ipredict.predictor.CPT.CPTPredictor;
import ca.ipredict.predictor.CPT.LLCT_Old;
import ca.ipredict.predictor.CPT.LossLessCompactPredictor;
import ca.ipredict.predictor.CPT.NewCPTPredictor;
import ca.ipredict.predictor.DG.DGPredictor;
import ca.ipredict.predictor.Markov.MarkovAllKPredictor;
import ca.ipredict.predictor.Markov.MarkovFirstOrderPredictor;

/**
 * Controls the predictors
 * @author Ted Gueniche
 *
 */
public class Controller {

	private List<Predictor> predictors; //list of predictors
	
	//Sampling type
	public final static int HOLDOUT = 0;
	public final static int KFOLD = 1;
	public final static int RANDOMSAMPLING = 2; 
	
	//statistics
	private long startTime;
	private long endTime;
	private long testingSetSize;
	
	//Database
	private DatabaseHelper database;
	
	//public Stats stats;
	public StatsLogger stats;
	public List<StatsLogger> experiements;
	
	public List<Format> datasets;  
	public List<Integer> datasetsMaxCount;  
	
	//PUBLIC INTERFACE
	//
	
	public Controller() {
		predictors = new ArrayList<Predictor>();
		datasets = new ArrayList<Format>();
		datasetsMaxCount = new ArrayList<Integer>();
		database = new DatabaseHelper();
	}
	
	/**
	 * Adds a Predictor to the list of predictors
	 * @param predictor
	 */
	public void addPredictor(Predictor predictor) {
		predictors.add(predictor);
	}

	/**
	 * Start the controller using the prefered SamplingRate on the list of predictor
	 * @param samplingType one of: HOLDOUT, RANDOMSAMPLING, KFOLD
	 * @param param The parameter associated with the sampling type
	 * @param showDatasetStats show statistics about the dataset
	 */
	public void Start(int samplingType, float param, boolean showDatasetStats) {
	
		//TODO: compress/move the creation of the stats logger
		//Setting statsLogger
		List<String> statsColumns = new ArrayList<String>();
		statsColumns.add("Success");
		statsColumns.add("Failure");
		statsColumns.add("No Match");
		statsColumns.add("Too Small");
		statsColumns.add("Overall");
		statsColumns.add("Size");
		statsColumns.add("Train Time");
		statsColumns.add("Test Time");
		
		//Extracting the name of each predictor
		List<String> predictorNames = new ArrayList<String>();
		for(Predictor predictor : predictors) {
			predictorNames.add(predictor.getTAG());
		}
		
		for(int i = 0; i < datasets.size(); i++) {
		
			int maxCount = datasetsMaxCount.get(i);
			Format format = datasets.get(i);
			database.loadDataset(format, maxCount, true); // PHILIPPE: AJOUT DU TROISIÈME PARAMÈTRE
			
			//Creating the statsLogger
			stats = new StatsLogger(statsColumns, predictorNames, false);
			
			//Saving current time for across time analysis
			startTime = System.currentTimeMillis();
			
			//For each predictor, do the sampling and do the training/testing
			for(int id = 0 ; id < predictors.size(); id++) {
				
				switch(samplingType) {
					case HOLDOUT:
						Holdout(param, id);
						break;
				
					case KFOLD:
						KFold((int)param, id);
						break;
						
					case RANDOMSAMPLING:
						RandomSubSampling(param, id);
						break;
					
					default: 
						System.out.println("Unknown sampling type."); 
				}
			}
			//Saving end time
			endTime = System.currentTimeMillis();
			
			displayStats(true);
		}
	}

	
	public void addDataset(Format format, int maxCount) {
		datasets.add(format);
		datasetsMaxCount.add(maxCount);
	}
	

	
	/**
	 * Holdout method
	 * Data are randomly partitioned into two sets (a training set and a test set) using a ratio.
	 * The classifier is trained using the training set and evaluated using the test set.
	 * @param ratio to divide the training and test sets
	 */
	public void Holdout(double ratio, int classifierId) {
		
		List<Sequence> trainingSequences = getDatabaseCopy();
		List<Sequence> testSequences = splitList(trainingSequences, ratio);
		testingSetSize = testSequences.size();
		
		//DEBUG
		//System.out.println("Dataset size: "+ (trainingSequences.size() + testSequences.size()));
		//System.out.println("Training: " + trainingSequences.size() + " and Test set: "+ testSequences.size());
		
		PrepareClassifier(trainingSequences, classifierId); //training (preparing) classifier
		
		StartClassifier(testSequences, classifierId); //classification of the test sequence
	}
	
	/**
	 * Random subsampling
	 * Holdout method repeated 10 times
	 * @param ratio to use for the holdout method
	 */
	public void RandomSubSampling(double ratio, int classifierId) {
		
		int k = 10;
		for(int i = 0 ; i < k; i++) {
			Holdout(ratio, classifierId);
			
			//Logging memory usage
			MemoryLogger.addUpdate();
		}
		
	}
	
	/**
	 * k-fold cross-validation
	 * Data are partitioned in k exclusive subsets (folds) of same size.
	 * Training and testing is done k times. For each time; a fold is used for testing 
	 * and the k-1 other folds for training
	 */
	public void KFold(int k, int classifierId) {
		
		//k has to be at least 2
		if(k < 2) {
			throw new RuntimeException("K needs to be 2 or more");
		}

		List<Sequence> dataSet = getDatabaseCopy();
		
		//calculating absolute ratio
		double relativeRatio = 1/(double)k;
		int absoluteRatio = (int) (dataSet.size() * relativeRatio);
		testingSetSize = dataSet.size();
		
		//DEBUG
		//System.out.println("Dataset size: "+ database.size());
		//System.out.println("K = "+ k + ", Fold size: "+ absoluteRatio);
		
		//For each fold, it does training and testing
		for(int i = 0 ; i < k ; i++) {

			//Partitioning database 
			//
			int posStart = i * absoluteRatio; //start position of testing set
			int posEnd = posStart + absoluteRatio; //end position of testing set
			if(i == (k-1)) { //if last fold we adjust the size to include all the left-over sequences
				posEnd = dataSet.size(); //special case
			}
			
			//declaring the sets
			List<Sequence> trainingSequences = new LinkedList<Sequence>();
			List<Sequence> testSequences = new LinkedList<Sequence>();
			
			//actual partitioning
			for(int j = 0 ; j < dataSet.size(); j++) {
				
				Sequence toAdd = dataSet.get(j);
				
				//is in testing set
				if(j >= posStart && j < posEnd) {
					testSequences.add(toAdd);
				}
				else {
					trainingSequences.add(toAdd);
				}
			}
			//
			//End of Partitioning
			
			//DEBUG
			//System.out.println("---------------------");
			//System.out.println("FOLD "+ i);
			PrepareClassifier(trainingSequences, classifierId); //training (preparing) classifier	
			StartClassifier(testSequences, classifierId); //classification of the test sequence
			
			//Logging memory usage
			MemoryLogger.addUpdate();
		}
		
	}
	
	
	
	public void displayStats(boolean showExecutionStats) {
		double size = testingSetSize; //data size according to the stats
		
		//For each predictor, updates the stats
		for(Predictor predictor : predictors) {
			
			int success = (int)(stats.get("Success", predictor.getTAG()));
			int failure = (int)(stats.get("Failure", predictor.getTAG()));
			int noMatch = (int)(stats.get("No Match", predictor.getTAG()));
			int tooSmall =(int)(stats.get("Too Small", predictor.getTAG()));
			
			
			long matchingSize = success + failure; //For relative success (success / (success + failure))
			long testingSize = matchingSize + noMatch + tooSmall; //For global success (success / All_the_testing)
			
			stats.divide("Success", predictor.getTAG(), matchingSize);
			stats.divide("Failure", predictor.getTAG(), matchingSize);
			stats.divide("No Match", predictor.getTAG(), testingSize);
			stats.divide("Too Small", predictor.getTAG(), testingSize);
			
			stats.divide("Train Time", predictor.getTAG(), 100);
			stats.divide("Test Time", predictor.getTAG(), 100);
			
			//Adding overall success
			stats.set("Overall", predictor.getTAG(), success);
			stats.divide("Overall", predictor.getTAG(), testingSize);
			
			//Size of the predictor
			stats.set("Size", predictor.getTAG(), predictor.size());
			stats.divide("Size", predictor.getTAG(), 100);
			
			
		}
		//Display the stats in the console
		System.out.println(stats.toString());

		if(showExecutionStats) {
	        //memory usage
	  		MemoryLogger.addUpdate();
	        MemoryLogger.displayUsage();
	        
	        //Displaying the execution time
	        System.out.println("Execution time: "+ (endTime - startTime) / 1000 + " seconds");
		}
	}
	

	/**
	 * Tell whether the predicted sequence match the consequent sequence
	 */
	public static Boolean isGoodPrediction(Sequence consequent, Sequence predicted) {
		
		Boolean hasError = false;
		
		for(Item it : predicted.getItems()) {
			
			Boolean isFound = false;
			for(Item re : consequent.getItems()) {
				if( re.val.equals(it.val) )
					isFound = true;
			}
			if(isFound == false)
				hasError = true;
			
		}
		
		
		return (hasError == false);
	}
	
	//Private methods
	//
	
	private void PrepareClassifier(List<Sequence> trainingSequences, int classifierId) {
		long start = System.currentTimeMillis(); //Training starting time
		
		predictors.get(classifierId).setTrainingSequences(trainingSequences);
		predictors.get(classifierId).Preload(); //actual training
		
		long end = System.currentTimeMillis(); //Training ending time
		double duration = (double)(end - start) / 1000;
		stats.set("Train Time", predictors.get(classifierId).getTAG(), duration);
	}
	
	private void StartClassifier(List<Sequence> testSequences, int classifierId) {	
		
		long start = System.currentTimeMillis(); //Testing starting time
		
		//for each sequence; it classifies it and evaluates it
		for(Sequence target : testSequences) {
			
			//if sequence is long enough
			if(target.size() > (Parameters.consequentSize)) {
				
				Sequence consequent = target.getLastItems(Parameters.consequentSize,0); //the lasts actual items in target
				Sequence finalTarget = target.getLastItems(Parameters.windowSize,Parameters.consequentSize);
				
				Sequence predicted = predictors.get(classifierId).Predict(finalTarget);
				
				//if no sequence is returned, it means that they is no match for this sequence
				if(predicted.size() == 0) {
					stats.inc("No Match", predictors.get(classifierId).getTAG());
				}
				//evaluates the prediction
				else if(isGoodPrediction(consequent, predicted)) {
					stats.inc("Success", predictors.get(classifierId).getTAG());
				}
				else {
					stats.inc("Failure", predictors.get(classifierId).getTAG());
				}
				
			}
			//sequence is too small
			else {
				stats.inc("Too Small", predictors.get(classifierId).getTAG());
			}
		}
		
		long end = System.currentTimeMillis(); //Training ending time
		double duration = (double)(end - start) / 1000;
		stats.set("Test Time", predictors.get(classifierId).getTAG(), duration);
	}

	private List<Sequence> splitList(List<Sequence> toSplit, double absoluteRatio){
		
		int relativeRatio = (int) (toSplit.size() * absoluteRatio); //absolute ratio: [0.0-1.0]
		
		List<Sequence> sub=toSplit.subList(relativeRatio , toSplit.size());
		List<Sequence> two= new ArrayList<Sequence>(sub);
		sub.clear();
		
		return two;
	}
	
	private List<Sequence> getDatabaseCopy() {
		return new ArrayList<Sequence>(database.getDatabase().getSequences().subList(0, database.getDatabase().size()));
	}
	
	public static void main(String[] args){
		
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Started");
		
		for(int i = 1 ; i < 2; i+= 1) {
			
			Controller controller = new Controller();
		
			//Loading data sets
			controller.addDataset(Format.BMS, 		1000);
//			controller.addDataset(Format.SIGN, 		8000);  // AJOUT PHILIPPE
//			controller.addDataset(Format.CANADARM1, 10000);  // AJOUT PHILIPPE
//			controller.addDataset(Format.CANADARM2, 10000);  // AJOUT PHILIPPE
//			controller.addDataset(Format.KOSARAK,	15000);
//			controller.addDataset(Format.FIFA, 		10000);
//			controller.addDataset(Format.MSNBC, 	15);
//			controller.addDataset(Format.BIBLE_CHAR, 	5000);
//			controller.addDataset(Format.BIBLE_WORD, 	5000);
//			controller.addDataset(Format.KORAN_WORD, 	5000);
//			controller.addDataset(Format.LEVIATHAN_WORD, 	5000);
			
			//Loading predictors
			controller.addPredictor(new DGPredictor());
			controller.addPredictor(new LossLessCompactPredictor());
			controller.addPredictor(new NewCPTPredictor());
			controller.addPredictor(new CPTPredictor());
			controller.addPredictor(new LLCT_Old());
//			controller.addPredictor(new MarkovFirstOrderPredictor());
//			controller.addPredictor(new MarkovAllKPredictor());
			

			controller.Start(KFOLD, 14, true);    // PHILIPPE AJOUT DU TROISIÈME PARAMÈTRE
//			controller.Start(HOLDOUT, 0.75f, true);    // PHILIPPE AJOUT DU TROISIÈME PARAMÈTRE
			
			System.out.println();
			System.out.print(Parameters.tostring());
		}
	}

	
	

}
