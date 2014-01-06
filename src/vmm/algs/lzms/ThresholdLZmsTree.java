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
 * Created on Jul 9, 2003
 */

package vmm.algs.lzms;

/**
 * @author Moti
 *
 * ThresholdLZmsTree extends a standard LZmsTree by adding on a threshold
 */
public class ThresholdLZmsTree extends LZmsTree {

	double threshold;

	public ThresholdLZmsTree(double threshold, int minContext, int numShifts, int alphabetSize) {
		super(minContext, numShifts, alphabetSize);
		this.threshold = threshold;
	}

	public boolean verifySequence(int[] sequence) {
		return rateSequence(sequence) <= threshold;
	}

	public boolean[] verifySequence(int[] sequence, double[] thresholds) {
		double sequenceLikelihood = rateSequence(sequence);
		boolean[] result = new boolean[thresholds.length];

		for (int i = 0; i < thresholds.length; i++)
			result[i] = sequenceLikelihood <= thresholds[i];

		return result;
	}


	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double d) {
		threshold = d;
	}

}
