/* HEADER 
If you use this code don’t forget to reference us :) BibTeX: http://www.cs.technion.ac.il/~rani/el-yaniv_bib.html#BegleiterEY04 

This code is free software; you can redistribute it and/or 
modify it under the terms of the GNU General Public License 
as published by the Free Software Foundation; either version 2 
of the License, or (at your option) any later version. 

This code is distributed in the hope that it will be useful, 
but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
GNU General Public License (<a href="http://www.gnu.org/copyleft/gpl.html">GPL</a>) for more details.*/ 
 
/*
 * Created on Apr 29, 2003
 */

package vmm.algs.lzms;

/**
 * @author Moti
 *
 * SingleClassClassificationModelBuilder is used to solve the single-class problem
 */
public class SingleClassClassificationModelBuilder {

	int alphabetSize;
	byte[] minContexts;
	byte[] numShifts;
	double[] stdMults;
	int[][] sequences;

	int[][] redividedSequences;

	ModelRank selectedModel;
	double selectedAve;
	double selectedStd;

	public SingleClassClassificationModelBuilder(
		int alphabetSize,
		byte[] minContexts,
		byte[] numShifts,
		double[] stdMults,
		int[][] sequences, int numSplits) {

		this.alphabetSize = alphabetSize;

		this.minContexts = minContexts;
		this.numShifts = numShifts;
		this.stdMults = stdMults;

		this.sequences = sequences;

		this.redividedSequences = splitSequences(sequences, numSplits);
	}

	static private int[][] splitSequences(int[][] sequences, int numSplits) {
		int[][] result = new int[numSplits][];

		int length = 0;
		for (int i = 0; i < sequences.length; i++)
			length += sequences[i].length;

		int[] concatenatedSequence = new int[length];
		for (int cI = 0, sI = 0; sI < sequences.length; sI++) {
			System.arraycopy(sequences[sI], 0, concatenatedSequence, cI, sequences[sI].length);
			cI += sequences[sI].length;
		}

		return splitSequence(concatenatedSequence, length/numSplits);
	}

	static private int[][] splitSequence(int[] sequence, int limitLength) {
		int[][] result = new int[(sequence.length - 1) / limitLength + 1][];
		for (int i = 0; i < result.length; i++) {
			if (i < result.length - 1)
				result[i] = new int[limitLength];
			else
				result[i] = new int[sequence.length - i * limitLength];
			System.arraycopy(sequence, i * limitLength, result[i], 0, result[i].length);
		}
		return result;
	}


	public ThresholdLZmsTree buildSingleClassClassifier() {
		ThresholdLZmsTreeBuilder bestBuilder = null;
		double bestStdMult = 0;
		ModelRank bestModelRank = null;
		double bestMax = Double.MAX_VALUE;

		for (int m = 0; m < minContexts.length; m++)
			for (int s = 0; s < numShifts.length; s++) {
				double max = 0;

				int numValues = 0;

				ThresholdLZmsTreeBuilder builder = new ThresholdLZmsTreeBuilder(minContexts[m], numShifts[s], alphabetSize, redividedSequences);
				double[] selfValues = builder._selfChecks;

				for (int i = 0; i < selfValues.length; i++)
					if (selfValues[i] > max)
						max = selfValues[i];

				if (max < bestMax) {
					double stdMult = (max - builder._ave) / builder._std;
					int roundedStdMult = (int)Math.ceil(stdMult * ModelRank.stdMultNormalization);

					bestBuilder = builder;
					bestModelRank = new ModelRank(minContexts[m], numShifts[s], roundedStdMult);
					bestStdMult = bestModelRank.getOriginalStdMult();
					bestMax = max;
				}
			}

		ThresholdLZmsTree result = bestBuilder.getTree(bestStdMult);
		for (int i = 0; i < redividedSequences.length; i++)
			result.learnSequence(redividedSequences[i]);
		selectedModel = bestModelRank;
		selectedAve = bestBuilder._ave;
		selectedStd = bestBuilder._std;
		return result;
	}

	public ModelRank getSelectedModel() {
		return selectedModel;
	}

	public double getSelectedAve() {
		return selectedAve;
	}

	public double getSelectedStd() {
		return selectedStd;
	}

}
