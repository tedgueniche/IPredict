package ca.ipredict.predictor.TRuleGrowth_D;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;

import ca.ipredict.predictor.Parameters;
import ca.ipredict.predictor.Predictor;
import ca.ipredict.predictor.TRuleGrowth_D.AlgoTRULEGROWTH;
import ca.ipredict.predictor.TRuleGrowth_D.Rule;

import ca.ipredict.database.Sequence;
import ca.ipredict.database.Item;
import ca.ipredict.database.SequenceDatabase;

/**
 * Predictor based on sequential rules defined
 * by Philippe Fournier-Viger (TODO: add reference)
 * 
 * This algorithm is defined also in :
 * Fournier-Viger, P. Gueniche, T., Tseng, V.S. (2012). 
 * Using Partially-Ordered Sequential Rules to Generate More Accurate Sequence Prediction. 
 * Proc. 8th International Conference on Advanced Data Mining and Applications (ADMA 2012), 
 * Springer LNAI 7713, pp.431-442.
 * 
 * @author Ted Gueniche
 *
 */
public class TRuleGrowth_DPredictor implements Predictor {

	private SequenceDatabase mTrainingDatabase; //list of sequences to test
	private List<Rule> mRules; //list of rules generated from training set
	
	@Override
	public void setTrainingSequences(List<Sequence> trainingSequences) {
		mTrainingDatabase = new SequenceDatabase();
		mTrainingDatabase.setSequences(trainingSequences);
	}

	@Override
	public Boolean Preload() {
		
		
		AlgoTRULEGROWTH algo = new AlgoTRULEGROWTH();
		double minsup = 0.0005;
		double minconf = 0.5;
		int    windowSize = 5;
		boolean findRulesWithOnlyOneItemInRightPart = true;  // si on veut seulement un item dans la partie droite de la regle.
		
		//TO FIX
		//the following loop make an entire copy of the training database in O(n)
		//Problem:	its take too much space and too much time
		//Solution: Make RTGSequence extend Sequence
		TRGSequenceDatabase TRGtrainingDatabase = new TRGSequenceDatabase();
		int id = 0;
		for(Sequence seq : mTrainingDatabase.getSequences()) {
			TRGSequence trgSeq = new TRGSequence(id);
			id++;
			
			for(Item item : seq.getItems()) {
				trgSeq.addItemset(new TRGItemset(item.val));
			}
			
			TRGtrainingDatabase.addSequence(trgSeq);
		}
		///////////////////////////////////////////////
		
		
		try {
			mRules = algo.runAlgorithm(minsup, minconf, TRGtrainingDatabase, windowSize, findRulesWithOnlyOneItemInRightPart);
			//algo.printStats();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		return false;
	}

	@Override
	public Sequence Predict(Sequence target) {
		
		List<Item> toTest = target.getItems(); //target sequence to predict
		List<Rule> ruleSet = new ArrayList<Rule>(); //best X rules
		
		// high complexity (nbRules * nbSequence)
		// rule sequential search
		// TODO: create better way to find rules 
		for (Rule rule : mRules) {
			//if the rule can apply to the sequence
			// we keep only the best rule
			if(ruleMatch(rule, toTest) == true)
			{						
				qualify(rule, Parameters.bestRuleCount, ruleSet, mTrainingDatabase.size());	//keep only the best rules
			}		
		}
		
		
		//if a matching rule has not been found
		//no match for this sequence
		if(ruleSet.isEmpty()) {
			return new Sequence(-1);
		}
		else 
		{
			//Getting the best rated rule and getting its first itemset
			//returns the itemset in a sequence
			Rule bestRule = ruleSet.get(ruleSet.size()-1);
			
			
			Item predictedItem = new Item(bestRule.getItemset2()[0]);
			Sequence predicted = new Sequence(-1); //toremove
			predicted.addItem(predictedItem);
			
			return predicted;
		}
	}
	
	@Override
	public String getTAG() {
		return "TRG";
	}
	
	@Override
	public long size() {
		return mRules.size();
	}
	
	/**
	 * Adds the rule into the ruleset if it has a better support/confidence 
	 * than any of the rules in the ruleset
	 * @param rule to add
	 * @param max number of rule in the ruleset
	 * @param an ordered list of rule
	 * @param Number of sequence used in the training (used to calculate the support)
	 */
	public static void qualify(Rule rule, int bestRuleCount, List<Rule> ruleSet, int SequenceCount)	{
		
		//for each Rule in ruleSet, try to insert rule at the right place
		for(int i = 0 ; i < ruleSet.size(); i++) {
			
			//if rule is better than ruleSet at (i)
			if(ruleSet.get(i).compareTo(rule) <= 0) {
				//add the rule before ruleSet at (i)
				ruleSet.add(i, rule);
				
				//if too many item in ruleSet, removes the last one (should be enough)
				if(ruleSet.size() > bestRuleCount) {
					ruleSet.remove(bestRuleCount);
				}
				
				//then quit
				return;
			}
		}
		
		//by this point, the current rule is worst than any other Rule in the ruleSet
		
		//if the ruleSet is not empty
		//we can automatically insert this rule
		if(ruleSet.size() < bestRuleCount) {
			ruleSet.add(rule);
		}
	}

	
	/**
	 * Tries to match the rule with the sequence
	 * @param rule to test
	 * @param sequence to match
	 * @return whether the rule matches the sequence
	 */
	public static Boolean ruleMatch(Rule rule, List<Item> seqItems)	{
		
		int seqSize = seqItems.size();
		if(seqSize == 0) //empty sequence
			return false;
		
		//if size of the postrule is larger than the sequence site
		if(rule.getItemset1().length > (seqSize) || rule.getItemset1().length == 0)
			return false;
		
		
		int[] preruleArray = rule.getItemset1(); // first half of the rule
		
		//generating hashMap of seq items (are duplicate possible? if so remove them, they are not necessary)
		HashSet<Integer> seq = new HashSet<Integer>();
		for(Item curSeq : seqItems)	{
				seq.add(curSeq.val);
		}
		
		
		for(Integer ruleItem : preruleArray){
			
			if(seq.contains(ruleItem) == false)	{
				return false; //one of the rule item is not found in sequence
			}
			
		}
		
		
		return true;
		
	}
	
}
