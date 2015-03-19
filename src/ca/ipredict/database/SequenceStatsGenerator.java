package ca.ipredict.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;


/**
 * This class read a sequence database and calculates statistics
 * about this sequence database. Then it prints the statistics.
 * 
 * In this version this class reads the database into memory before calculating the
 * statistics. It could be optimized to calculate statistics without
 * reading the database in memory because a single pass is required. It
 * was done like that because the code is simpler and easier to understand.
 * 
 * Copyright (c) 2008-2012 Philippe Fournier-Viger
 * 
 * This file is part of the SPMF DATA MINING SOFTWARE
 * (http://www.philippe-fournier-viger.com/spmf).
 * 
 * SPMF is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * SPMF. If not, see <http://www.gnu.org/licenses/>.
 */
public class SequenceStatsGenerator {


	/**
	 * This method generates statistics for a sequence database (a file)
	 * @param path the path to the file
	 * @param name of the database
	 * @throws IOException  exception if there is a problem while reading the file.
	 */
	public static void prinStats(SequenceDatabase database, String name) throws IOException {

		/////////////////////////////////////
		//  We finished reading the database into memory.
		//  We will calculate statistics on this sequence database.
		///////////////////////////////////

		System.out.println("---" + name + "---");
		System.out.println("Number of sequences : \t" + database.size());
		
		int maxItem =0;
		// we initialize some variables that we will use to generate the statistics
		java.util.Set<Integer> items = new java.util.HashSet<Integer>();  // the set of all items
		List<Integer> sizes = new ArrayList<Integer>(); // the lengths of each sequence
		List<Integer> differentitems = new ArrayList<Integer>();  // the number of different item for each sequence
		List<Integer> appearXtimesbySequence = new ArrayList<Integer>(); // the average number of times that items appearing in a sequence, appears in this sequence.
		// Loop on sequences from the database
		for (Sequence sequence : database.getSequences()) {
			// we add the size of this sequence to the list of sizes
			sizes.add(sequence.size());
			
			// this map is used to calculate the number of times that each item
			// appear in this sequence.
			// the key is an item
			// the value is the number of occurences of the item until now for this sequence
			HashMap<Integer, Integer> mapIntegers = new HashMap<Integer, Integer>();
			
			// Loop on itemsets from this sequence
			for (Item item : sequence.getItems()) {
			// we add the size of this itemset to the list of itemset sizes
				// If the item is not in the map already, we set count to 0
				Integer count = mapIntegers.get(item.val);
				if (count == null) {
					count = 0;
				}
				// otherwise we set the count to count +1
				count = count + 1;
				mapIntegers.put(item.val, count);
				// finally, we add the item to the set of items
				items.add(item.val);
				
				if(item.val > maxItem){
					maxItem = item.val;
				}
			}

			// we add all items found in this sequence to the global list
			// of different items for the database
			differentitems.add(mapIntegers.entrySet().size());

			// for each item appearing in this sequence,
			// we put  the number of times in a global list "appearXtimesbySequence"
			// previously described.
			for (Entry<Integer, Integer> entry : mapIntegers.entrySet()) {
				appearXtimesbySequence.add(entry.getValue());
			}
		}
		
		// we print the statistics
//		System.out.println();
		System.out.println("Number of distinct items: \t" + items.size());
		System.out.println("Largest item id: \t" + maxItem);
		System.out.println("Itemsets per sequence: \t"+ calculateMean(sizes));
		System.out.println("Distinct item per sequence: \t" + calculateMean(differentitems));
		System.out.println("Occurences for each item: \t" + calculateMean(appearXtimesbySequence));
		System.out.println("Size of the dataset in MB: \t" + ((database.size() * 4d) + (database.size() * calculateMean(sizes) * 4d) / (1000 * 1000)));
		System.out.println();
		
	}


	/**
	 * This method calculate the mean of a list of integers
	 * @param list the list of integers
	 * @return the mean 
	 */
	private static double calculateMean(List<Integer> list) {
		double sum = 0;
		for (Integer val : list) {
			sum += val;
		}
		return sum / list.size();
	}

	/**
	 * This method calculate the standard deviation of a list of integers
	 * @param list the list of integers
	 * @return the standard deviation
	 */
	private static double calculateStdDeviation(List<Integer> list) {
		double deviation = 0;
		double mean = calculateMean(list);
		for (Integer val : list) {
			deviation += Math.pow(mean - val, 2);
		}
		return Math.sqrt(deviation / list.size());
	}

	/**
	 * This method calculate the mean of a list of doubles
	 * @param list the list of doubles
	 * @return the mean
	 */
	private static double calculateMeanD(List<Double> list) {
		double sum = 0;
		for (Double val : list) {
			sum += val;
		}
		return sum / list.size();
	}

	/**
	 * This method calculate the standard deviation of a list of doubles
	 * @param list the list of doubles
	 * @return the standard deviation
	 */
	private static double calculateStdDeviationD(List<Double> list) {
		double deviation = 0;
		double mean = calculateMeanD(list);
		for (Double val : list) {
			deviation += Math.pow(mean - val, 2);
		}
		return Math.sqrt(deviation / list.size());
	}

	/**
	 * This method calculate the variance of a list of integers
	 * @param list the list of integers
	 * @return the variance 
	 */
	private static double calculateVariance(List<Integer> list) {
		double deviation = 0;
		double mean = calculateMean(list);
		for (Integer val : list) {
			deviation += Math.pow(mean - val, 2);
		}
		return Math.pow(Math.sqrt(deviation / list.size()), 2);
	}

	/**
	 * This method return the smallest integer from a list of integers
	 * @param list the list of integers
	 * @return the smallest integer 
	 */
	private static int calculateMinValue(List<Integer> list) {
		int min = Integer.MIN_VALUE;
		for (Integer val : list) {
			if (val <= min) {
				min = val;
			}
		}
		return min;
	}

	/**
	 * This method return the largest integer from a list of integers
	 * @param list the list of integers
	 * @return the largest integer 
	 */
	private static int calculateMaxValue(List<Integer> list) {
		int max = 0;
		for (Integer val : list) {
			if (val >= max) {
				max = val;
			}
		}
		return max;
	}
}
