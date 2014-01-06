package ca.ipredict.predictor.TRuleGrowth_D;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * TRULEGROWTH algorithm.
 * Written by Philippe Fournier-Viger
 * 
 * @author Philippe Fournier-Viger, 2011
 */
public class AlgoTRULEGROWTH {
	
	// statistics
	long timeStart = 0;
	long timeEnd = 0;
	
	Map<Integer,  Map<Integer, Occurence>> mapItemCount;
	
	TRGSequenceDatabase database;
	
	// PARAMETERS
	double minconf; 
	int minsuppRelative;
	int windowSize =0;
	boolean findRulesWithOnlyOneItemInRightPart = false;

	// STATISTICS
	int ruleCount;
	double maxMemory = 0;
	
	List<Rule> result = null;

	public AlgoTRULEGROWTH() {
		
	}
	
	private void checkMemory() {
		double currentMemory = ( (double)((double)(Runtime.getRuntime().totalMemory()/1024)/1024))- ((double)((double)(Runtime.getRuntime().freeMemory()/1024)/1024));
		if(currentMemory > maxMemory){
			maxMemory = currentMemory;
		}
	}

	
	/**
	 * Run the algorithm.  Minsup is an absolute value (ex: 0.05 = 5 %)
	 * @throws IOException 
	 */
	public List<Rule> runAlgorithm(double minSupport, double minConfidence, TRGSequenceDatabase database, int windowSize, boolean findRulesWithOnlyOneItemInRightPart) throws IOException{
		this.findRulesWithOnlyOneItemInRightPart = findRulesWithOnlyOneItemInRightPart;
		this.minsuppRelative = (int) Math.ceil(minSupport * database.size());
		runAlgorithm(database, minsuppRelative, minConfidence, windowSize);
		return result;
	}
	
	/**
	 * Run the algorithm.  Minsup is a relative value (ex: 5  = 5 sequences of the database)
	 * @throws IOException 
	 */
	public  List<Rule> runAlgorithm(TRGSequenceDatabase database, int relativeMinSupport, double minConfidence, int windowSize 
			) throws IOException{
		this.minconf = minConfidence;
		this.database = database;
		result = new ArrayList<Rule>();
		this.windowSize = windowSize + 1;  // IMPORTANT : THIS IS A FIX SO THAT THE DEFINITION IS THE SAME AS IN THE ARTICLE!!
		
		this.minsuppRelative = relativeMinSupport;
		if(this.minsuppRelative == 0){ // protection
			this.minsuppRelative = 1;
		}

		maxMemory = 0;

		timeStart = System.currentTimeMillis(); // for stats
		
		removeItemsThatAreNotFrequent(database);	
		
		// NOTE ITEMS THAT ARE FREQUENT IN A LIST
		List<Integer> listFrequents = new ArrayList<Integer>();
		for(Entry<Integer,Map<Integer, Occurence>> entry : mapItemCount.entrySet()){
			if(entry.getValue().size() >= minsuppRelative){
				listFrequents.add(entry.getKey());
			}
		}
		
		// FOR EACH FREQUENT ITEM WE COMPARE WITH EACH OTHER FREQUENT ITEM TO 
		// TRY TO GENERATE A RULE 1-1.
		for(int i=0; i< listFrequents.size(); i++){
			Integer intI = listFrequents.get(i);
			Map<Integer,Occurence> occurencesI = mapItemCount.get(intI);
			for(int j=i+1; j< listFrequents.size(); j++){
				Integer intJ = listFrequents.get(j);
				Map<Integer,Occurence> occurencesJ = mapItemCount.get(intJ);
				
				// (1) Calculate tidsI  and tidsI->J
				Set<Integer> tidsI = new HashSet<Integer>();
				Set<Integer> tidsJ = null;
				Set<Integer> tidsIJ = new HashSet<Integer>();
				Set<Integer> tidsJI= new HashSet<Integer>();

				// CALCULATE TIDI  and TIDIJ
	looptid:	for(Occurence occI : occurencesI.values()){
					Occurence occJ = occurencesJ.get(occI.transactionID);
					if(occJ == null){
						continue looptid;
					}
					
					boolean addedIJ= false;
					boolean addedJI= false;
			loopIJ:	for(Short posI : occI.occurences){
						for(Short posJ : occJ.occurences){
							if(!posI.equals(posJ) && Math.abs(posI - posJ) <= windowSize){
								if(posI <= posJ){
									tidsIJ.add(occI.transactionID);
									addedIJ = true;
								}else{
									tidsJI.add(occI.transactionID);
									addedJI = true;
								}
								if(addedIJ && addedJI){
									break loopIJ;
								}
							}
						}
					}
					tidsI.add(occI.transactionID);
				}
				// END
				
				// (2) check if the two itemsets have enough common tids
				// if not, we don't need to generate a rule for them.
				// create rule IJ
				if(tidsIJ.size() >= minsuppRelative){
					double confIJ = ((double)tidsIJ.size()) / occurencesI.size();
//					Rule ruleIJ = new Rule(new Itemset(intI), new Itemset(intJ), confIJ, tidsIJ.size());
					int[] itemset1 = new int[]{intI};
					int[] itemset2 = new int[]{intJ};
					if(confIJ >= minConfidence){
						saveRule(tidsIJ, confIJ, itemset1, itemset2);
					}
					// Calculate tidsJ.
					tidsJ = new HashSet<Integer>();
					for(Occurence occJ : occurencesJ.values()){
						tidsJ.add(occJ.transactionID);
					}
					
					expandLeft(itemset1, itemset2, tidsI, tidsIJ);
					if(findRulesWithOnlyOneItemInRightPart == false){
						expandRight(itemset1, itemset2, tidsI, tidsJ, tidsIJ);
					}
				}
					
				// create rule JI
				if(tidsJI.size() >= minsuppRelative){
						double confJI = ((double)tidsJI.size()) / occurencesJ.size();
//						Rule ruleJI = new Rule(new Itemset(intJ), new Itemset(intI),  confJI, tidsJI.size());
						int[] itemset1 = new int[]{intI};
						int[] itemset2 = new int[]{intJ};
						
						if(confJI >= minConfidence){
							saveRule(tidsJI, confJI, itemset2, itemset1);
//							rules.addRule(ruleJI);
						}
						
						// Calculate tidsJ.
						if(tidsJ == null){
							tidsJ = new HashSet<Integer>();
							for(Occurence occJ : occurencesJ.values()){
								tidsJ.add(occJ.transactionID);
							}
						}
						if(findRulesWithOnlyOneItemInRightPart == false){
							expandRight(itemset2, itemset1, tidsJ,  tidsI, tidsJI /*, occurencesJ, occurencesI*/);
						}
						expandLeft(itemset2, itemset1, tidsJ, tidsJI /*, occurencesI*/);
					}
				}
		}
		timeEnd = System.currentTimeMillis(); // for stats
		
		database = null;
		
		return result;
	}

	/**
	 * This method search for items for expanding left side of a rule I --> J 
	 * with any item c. This results in rules of the form I U {c} --> J. The method makes sure that:
	 *   - c  is not already included in I or J
	 *   - c appear at least minsup time in tidsIJ before last occurence of J
	 *   - c is lexically bigger than all items in I
	 * @param mapWindowsIJ 
	 * @throws IOException 
	 */
    private void expandLeft(int[] itemsetI, int[] itemsetJ,
    						Collection<Integer> tidsI, 
    						Collection<Integer> tidsIJ // ,
//    						Map<Integer, Occurence> mapOccurencesJ
    						) throws IOException {    	
    	
    	// map-key: item   map-value: set of tids containing the item
    	Map<Integer, Set<Integer>> frequentItemsC  = new HashMap<Integer, Set<Integer>>();  
    	
    	////////////////////////////////////////////////////////////////////////
    	// for each sequence containing I-->J
    	for(Integer tid : tidsIJ){
    		TRGSequence sequence = database.getSequences().get(tid);
    		
    		LinkedHashMap<Integer, Integer> mapMostLeftFromI = new LinkedHashMap<Integer, Integer>();
    		LinkedHashMap<Integer, Integer> mapMostLeftFromJ = new LinkedHashMap<Integer, Integer>();
    		LinkedHashMap<Integer, LinkedList<Integer>> mapMostRightFromJ = new LinkedHashMap<Integer, LinkedList<Integer>>();

        	int lastItemsetScannedForC = Integer.MAX_VALUE;
        	
    		// For each itemset starting from the last...
        	int k= sequence.size()-1;
        	do{
    			final int fistElementOfWindow = k;    //  - windowSize +1
    			final int lastElementOfWindow = k + windowSize -1; 
    			
    			// remove items from J that fall outside the time window
    			int previousJSize = mapMostLeftFromJ.size();
    			removeElementOutsideWindow(mapMostLeftFromJ, lastElementOfWindow);
    			// important: if J was all there, but become smaller we need to clear the
    			// hashmap for items of I.
    			int currentJSize = mapMostLeftFromJ.size();
    			if(previousJSize == itemsetJ.length && previousJSize != currentJSize){
    				mapMostLeftFromI.clear();
    			}
				
    			// remove items from I that fall outside the time window
    			removeElementOutsideWindow(mapMostLeftFromI, lastElementOfWindow);
    			
    			// For each item of the current itemset
    			for(Integer item : sequence.get(k).getItems()){
    				// record the first position until now of each item in I or J
    				if(mapMostLeftFromJ.size() == itemsetJ.length  && contains(itemsetI, item)){ 
    					addToLinked(mapMostLeftFromI, item, k);
    				}else if(contains(itemsetJ, item)){ 
    					addToLinked(mapMostLeftFromJ, item, k);
    					LinkedList<Integer> list = mapMostRightFromJ.get(item);
    					if(list == null){
    						list = new LinkedList<Integer>();
    						addToLinked(mapMostRightFromJ, item, list);
    					}
    					list.add(k);
    				}
    			}
 
    			// if all the items of IJ are in the current window
    			if(mapMostLeftFromI.size() == itemsetI.length && mapMostLeftFromJ.size() == itemsetJ.length){
    				
    				//remove items from mostRight that fall outside the time window.
        			// at the same time, calculate the minimum index for items of J.
        			int minimum = Integer.MAX_VALUE;
        			for(LinkedList<Integer> list: mapMostRightFromJ.values()){
        				while(true){
        					Integer last = list.getLast();
        					if(last > lastElementOfWindow){
        						list.removeLast();
        					}else{ 
        						if(last < minimum){
                					minimum = last -1;
                				}
        						break;
    						}
    					}
    				}
    				
	    			// we need to scan for items C to extend the rule...	
	    		    // Such item c has to appear in the window before the last occurence of J (before "minimum")
	    		    // and if it was scanned before, it should not be scanned again.
    				int itemsetC = minimum;
    				if(itemsetC >= lastItemsetScannedForC){
    					itemsetC = lastItemsetScannedForC -1;
    				}
    				
    				for(; itemsetC >= fistElementOfWindow; itemsetC--){
    					for(Integer itemC : sequence.get(itemsetC).getItems()){
//    						if lexical order is not respected or c is included in the rule already.			
							if(containsLEXPlus(itemsetI, itemC)  
						   || containsLEX(itemsetJ, itemC)){
								continue;
							}	
							Set<Integer> tidsItemC = frequentItemsC.get(itemC);
							if(tidsItemC == null){
								tidsItemC = new HashSet<Integer>();
								frequentItemsC.put(itemC, tidsItemC);
							}
							tidsItemC.add(tid);	
    					}
    				}
    				lastItemsetScannedForC = fistElementOfWindow;
    			}
    			k--;
        	}while(k >= 0  && lastItemsetScannedForC >0);
 		}
    	////////////////////////////////////////////////////////////////////////

     	// for each item c found, we create a rule	 	
    	for(Entry<Integer, Set<Integer>> entry : frequentItemsC.entrySet()){
    		Set<Integer> tidsIC_J = entry.getValue();
    		
    		// if the support is enough      Sup(R)  =  sup(IC -->J)
    		if(tidsIC_J.size() >= minsuppRelative){ 
        		Integer itemC = entry.getKey();
    			int [] itemsetIC = new int[itemsetI.length+1];
				System.arraycopy(itemsetI, 0, itemsetIC, 0, itemsetI.length);
				itemsetIC[itemsetI.length] = itemC;

    			// ---- CALCULATE ALL THE TIDS CONTAINING IC WITHIN A TIME WINDOW ---
    			Set<Integer> tidsIC = new HashSet<Integer>();
   loop1:	    for(Integer tid: tidsI){
    	    		TRGSequence sequence = database.getSequences().get(tid);
    	    		// MAP: item : itemset index
    	    		LinkedHashMap<Integer, Integer> mapAlreadySeenFromIC = new LinkedHashMap<Integer, Integer>();
    	    		
    	    		// For each itemset
    	    		for(int k=0; k< sequence.size(); k++){
    					// For each item
    	    			for(Integer item : sequence.get(k).getItems()){
    	    				if(contains(itemsetIC, item)){ // record the last position of each item in IC
    	    					addToLinked(mapAlreadySeenFromIC, item, k);
    	    				}
    	    			}
    	    			// remove items that fall outside the time window
    	    			Iterator<Entry<Integer, Integer>> iter = mapAlreadySeenFromIC.entrySet().iterator();
    	    			while(iter.hasNext()){
    	    				Entry<Integer, Integer> entryMap = iter.next();
    	    				if(entryMap.getValue() < k - windowSize +1){
    	    					iter.remove();
    	    				}else{
    	    					break;
    	    				}
    	    			}
    	    			// if all the items of I are inside the current window, then record the tid
    	    			if(mapAlreadySeenFromIC.keySet().size() == itemsetIC.length){
    	    				tidsIC.add(tid);
    	    				continue loop1;
    	    			}
    	    		}
    	    	}
    			// ----  ----
    			
    			// Create rule and calculate its confidence:  Conf(r) = sup(IUC -->J) /  sup(IUC)			
				double confIC_J = ((double)tidsIC_J.size()) / tidsIC.size();

				if(confIC_J >= minconf){
					saveRule(tidsIC_J, confIC_J, itemsetIC, itemsetJ);
				}
				
				// recursive call to expand left side of the rule
				expandLeft(itemsetIC, itemsetJ, tidsIC, tidsIC_J );
    		}
    	}

    	checkMemory();
    	////////////////////////////////////////////////////////////////////////
	}

    // this method is to make sure that the insertion order is preserved.
    // It was necessary to do that because when an element is re-inserted in a linked list,
    // the access order remain the one of the first insertion. 
	private void addToLinked(LinkedHashMap mapMostLeftFromI,
			Object key, Object value) {
		if(mapMostLeftFromI.containsKey(key)){
			mapMostLeftFromI.remove(key);
		}
		mapMostLeftFromI.put(key, value);
	}

	private void removeElementOutsideWindow(
			LinkedHashMap<Integer, Integer> mapMostLeftFromI,
			final int lastElementOfWindow) {
		Iterator<Entry<Integer, Integer>> iter = mapMostLeftFromI.entrySet().iterator();
		while(iter.hasNext()){
			if(iter.next().getValue() > lastElementOfWindow){
				iter.remove();
			}else{
				break;
			}
		}
	}
	
	private void removeElementOutsideWindowER(
			LinkedHashMap<Integer, Integer> mapMostRightfromI,
			final int firstElementOfWindow) {
		Iterator<Entry<Integer, Integer>> iter = mapMostRightfromI.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Integer, Integer> entry = iter.next();
			if(entry.getValue() < firstElementOfWindow){
				iter.remove();
			}else{
				break;
			}
		}
	}
    
	/**
	 * This method search for items for expanding left side of a rule I --> J 
	 * with any item c. This results in rules of the form I --> J U {c}. The method makes sure that:
	 *   - c  is not already included in I or J
	 *   - c appear at least minsup time in tidsIJ after the first occurence of I
	 *   - c is lexically bigger than all items in J
	 * @param mapWindowsJI 
	 * @throws IOException 
	 */
    private void expandRight(int[] itemsetI, int[] itemsetJ, 
							Set<Integer> tidsI, 
    						Collection<Integer> tidsJ, 
    						Collection<Integer> tidsIJ //,
//    						Map<Integer, Occurence> occurencesI,
//    						Map<Integer, Occurence> occurencesJ
    						) throws IOException {

//    	// map-key: item   map-value: set of tids containing the item
    	Map<Integer, Set<Integer>> frequentItemsC  = new HashMap<Integer, Set<Integer>>();  
    	
    	// for each sequence containing I-->J
    	 for(Integer tid : tidsIJ){
    		TRGSequence sequence = database.getSequences().get(tid);
    		
    		LinkedHashMap<Integer, Integer> mapMostRightFromI = new LinkedHashMap<Integer, Integer>();
    		LinkedHashMap<Integer, Integer> mapMostRightFromJ = new LinkedHashMap<Integer, Integer>();
    		LinkedHashMap<Integer, LinkedList<Integer>> mapMostLeftFromI = new LinkedHashMap<Integer, LinkedList<Integer>>();

        	int lastItemsetScannedForC = Integer.MIN_VALUE;
        	
    		// For each itemset starting from the first...
        	int k= 0;
        	do{
    			final int firstElementOfWindow = k - windowSize +1;   
    			int lastElementOfWindow = k; 
				
    			// remove items from I that fall outside the time window
    			int previousISize = mapMostRightFromI.size();
    			removeElementOutsideWindowER(mapMostRightFromI, firstElementOfWindow);
    			// important: if I was all there, but become smaller we need to clear the
    			// hashmap for items of J.
    			int currentISize = mapMostRightFromI.size();
    			if(previousISize == itemsetJ.length && previousISize != currentISize){
    				mapMostRightFromJ.clear();
    			}

    			// remove items from J that fall outside the time window
    			removeElementOutsideWindowER(mapMostRightFromJ, firstElementOfWindow);
    			
    			// For each item of the current itemset
    			for(Integer item : sequence.get(k).getItems()){
    				// record the first position until now of each item in I or J
    				if(mapMostRightFromI.size() == itemsetI.length && 	contains(itemsetJ, item)){ 
    					addToLinked(mapMostRightFromJ, item, k);
    				}else if(contains(itemsetI, item)){ 
    					addToLinked(mapMostRightFromI, item, k);
    					LinkedList<Integer> list = mapMostLeftFromI.get(item);
    					if(list == null){
    						list = new LinkedList<Integer>();
    						addToLinked(mapMostLeftFromI, item, list);
    					}
    					list.add(k);
    				}
    			}
 
    			// if all the items of IJ are in the current window
    			if(mapMostRightFromI.size() == itemsetI.length && mapMostRightFromJ.size() == itemsetJ.length){
    				
    				//remove items from mostLeft that fall outside the time window.
        			// at the same time, calculate the minimum index for items of I.
        			int minimum = 1;
        			for(LinkedList<Integer> list: mapMostLeftFromI.values()){
        				while(true){
        					Integer last = list.getLast();
        					if(last < firstElementOfWindow){
        						list.removeLast();
        					}else{ 
        						if(last > minimum){
                					minimum = last + 1;  
                				}
        						break;
    						}
    					}
    				}
    				
	    			// we need to scan for items C to extend the rule...	
	    		    // Such item c has to appear in the window before the last occurence of J (before "minimum")
	    		    // and if it was scanned before, it should not be scanned again.
    				int itemsetC = minimum;
    				if(itemsetC < lastItemsetScannedForC){
    					itemsetC = lastItemsetScannedForC +1;
    				}
    				
    				for(; itemsetC <= lastElementOfWindow; itemsetC++){
    					for(Integer itemC : sequence.get(itemsetC).getItems()){
//    	    						if lexical order is not respected or c is included in the rule already.			
							if(containsLEX(itemsetI, itemC) 
						   ||  containsLEXPlus(itemsetJ, itemC)){
								continue;
							}	
							Set<Integer> tidsItemC = frequentItemsC.get(itemC);
							if(tidsItemC == null){
								tidsItemC = new HashSet<Integer>();
								frequentItemsC.put(itemC, tidsItemC);
							}
							tidsItemC.add(tid);	
    					}
    				}
    				lastItemsetScannedForC = lastElementOfWindow;
    			}
    			k++;
        	}while(k < sequence.size() && lastItemsetScannedForC < sequence.size()-1);
 		}  	
    	 
      	////////////////////////////////////////////////////////////////////////
    	// for each item c found, we create a rule	 	
     	for(Entry<Integer, Set<Integer>> entry : frequentItemsC.entrySet()){
     		Set<Integer> tidsI_JC = entry.getValue();
     		
     		// if the support is enough      Sup(R)  =  sup(IC -->J)
     		if(tidsI_JC.size() >= minsuppRelative){ 
         		Integer itemC = entry.getKey();
         		int[] itemsetJC = new int[itemsetJ.length+1];
				System.arraycopy(itemsetJ, 0, itemsetJC, 0, itemsetJ.length);
				itemsetJC[itemsetJ.length]= itemC;
//
//     			Itemset itemsetJC = new Itemset(ruleIJ.getItemset2()); 
// 				itemsetJC.addItem(itemC);
 				
     			// ---- CALCULATE ALL THE TIDS CONTAINING JC WITHIN A TIME WINDOW ---
     			Set<Integer> tidsJC = new HashSet<Integer>();
    loop1:	    for(Integer tid: tidsJ){
     	    		TRGSequence sequence = database.getSequences().get(tid);
     	    		// MAP: item : itemset index
     	    		LinkedHashMap<Integer, Integer> mapAlreadySeenFromJC = new LinkedHashMap<Integer, Integer>();
     	    		
     	    		// For each itemset
     	    		for(int k=0; k< sequence.size(); k++){
     					// For each item
     	    			for(Integer item : sequence.get(k).getItems()){
     	    				if(contains(itemsetJC, item)){ // record the last position of each item in JC
     	    					addToLinked(mapAlreadySeenFromJC, item, k);
     	    				}
     	    			}
     	    			// remove items that fall outside the time window
     	    			Iterator<Entry<Integer, Integer>> iter = mapAlreadySeenFromJC.entrySet().iterator();
     	    			while(iter.hasNext()){
     	    				Entry<Integer, Integer> entryMap = iter.next();
     	    				if(entryMap.getValue() < k - windowSize +1){
     	    					iter.remove();
     	    				}else{
     	    					break;
     	    				}
     	    			}
     	    			// if all the items of I are inside the current window, then record the tid
     	    			if(mapAlreadySeenFromJC.keySet().size() == itemsetJC.length){
     	    				tidsJC.add(tid);
     	    				continue loop1;
     	    			}
     	    		}
     	    	}
     			// ----  ----
     			
    			// Create rule and calculate its confidence:  Conf(r) = sup(I-->JC) /  sup(I)	
				double confI_JC = ((double)tidsI_JC.size()) / tidsI.size();
//				Rule ruleI_JC = new Rule(ruleIJ.getItemset1(), itemsetJC, confI_JC, tidsI_JC.size());
				
				// if the confidence is enough
				if(confI_JC >= minconf){
					saveRule(tidsI_JC, confI_JC, itemsetI, itemsetJC);
				}

				if(findRulesWithOnlyOneItemInRightPart == false){
					expandRight(itemsetI, itemsetJC, tidsI, tidsJC, tidsI_JC);  // 
				}

				// recursive call to expand left and right side of the rule
				expandLeft(itemsetI, itemsetJC, tidsI, tidsI_JC);  // occurencesJ
     		}
     	}
    	checkMemory();
	}


	/**
	 * This method calculate the frequency of each item in one database pass.
	 * Then it remove all items that are not frequent.
	 * @param database : a sequence database 
	 * @return A map such that key = item
	 *                         value = a map  where a key = tid  and a value = Occurence
	 * This map allows knowing the frequency of each item and their first and last occurence in each sequence.
	 */
	private Map<Integer, Map<Integer, Occurence>> removeItemsThatAreNotFrequent(TRGSequenceDatabase database) {
		// (1) Count the support of each item in the database in one database pass
		mapItemCount = new HashMap<Integer, Map<Integer, Occurence>>(); // <item, Map<tid, occurence>>
		
		// for each sequence
		for(TRGSequence sequence : database.getSequences()){
			// for each itemset
			for(short j=0; j< sequence.getItemsets().size(); j++){
				TRGItemset itemset = sequence.get(j);
				// for each item
				for(int i=0; i< itemset.size(); i++){
					Integer itemI = itemset.get(i);
					Map<Integer, Occurence> occurences = mapItemCount.get(itemI);
					if(occurences == null){
						occurences = new HashMap<Integer, Occurence>();
						mapItemCount.put(itemI, occurences);
					}
					Occurence occurence = occurences.get(sequence.getId());
					if(occurence == null){
						occurence = new Occurence(sequence.getId());
						occurences.put(sequence.getId(), occurence);
					}
					occurence.add(j);
				}
			}
		}
//		System.out.println("NUMBER OF DIFFERENT ITEMS : " + mapItemCount.size());
		// (2) remove all items that are not frequent from the database
		for(TRGSequence sequence : database.getSequences()){
			int i=0;
			while(i < sequence.getItemsets().size()){
				TRGItemset itemset = sequence.getItemsets().get(i);
				int j=0;
				while(j < itemset.size()){
					
					int count = mapItemCount.get(itemset.get(j)).size();
					
					if(count < minsuppRelative){
						itemset.getItems().remove(j);
					}else{
						j++;
					}
				}
				i++;
			}
		}
		return mapItemCount;
	}
	
	private void saveRule(Set<Integer> tidsIJ, double confIJ, int[] itemsetI, int[] itemsetJ) throws IOException {
		ruleCount++;
		result.add(new Rule(itemsetI, itemsetJ, confIJ, tidsIJ.size()));
		
	}
	
	public boolean contains(int[] itemset, int item) {
		for(int i=0; i<itemset.length; i++){
			if(itemset[i] == item){
				return true;
			}else if(itemset[i] > item){
				return false;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * This method checks if the item "item" is in the itemset.
	 * It asumes that items in the itemset are sorted in lexical order
	 * This version also checks that if the item "item" was added it would be the largest one
	 * according to the lexical order
	 */
	public boolean containsLEXPlus(int[] itemset, int item) {
		for(int i=0; i< itemset.length; i++){
			if(itemset[i] == item){
				return true;
			}else if(itemset[i] > item){
				return true; // <-- xxxx
			}
		}
		return false;
	}
	
	/**
	 * This method checks if the item "item" is in the itemset.
	 * It asumes that items in the itemset are sorted in lexical order
	 * @param item
	 * @return
	 */
	public boolean containsLEX(int[] itemset, int item) {
		for(int i=0; i< itemset.length; i++){
			if(itemset[i] == item){
				return true;
			}else if(itemset[i] > item){
				return false;  // <-- xxxx
			}
		}
		return false;
	}
	

	public void printStats() {
		System.out
				.println("=============  TRULEGROWTH - STATS =============");
//		System.out.println("minsup: " + minsuppRelative);
		System.out.println("Sequential rules count: " + ruleCount);
		System.out.println("Total time : " + (timeEnd - timeStart) + " ms");
		System.out.println("Max memory (mb)" + maxMemory);
		System.out.println("=====================================");
	}

	public double getTotalTime(){
		return timeEnd - timeStart;
	}
}
