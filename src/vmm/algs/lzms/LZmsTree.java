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
 
package vmm.algs.lzms;

import vmm.util.SampleIterator;

/**
@author The AIS DevTeam
@version 0.3
*/

// A single node in the LZTree

/**
   The tree structure defined by the LZ(m,s) algorithm
   @see VarSizedLZ
*/
public class LZmsTree {
	final LZNode root;
	final int minContext;
	final int numShifts;

	final double negativeInverseOfLogAlphabetSize;

        /**@ron */
        private static final double negativeInverseOfLg = -(1/Math.log(2.0));

	boolean likelihoodsSet;



	/**
	   Constructs a LZmsTree
	*/
	public LZmsTree(int minContext, int numShifts, int alphabetSize) {
		root = new LZNode(alphabetSize);
		this.minContext = minContext;
		this.numShifts = numShifts;
		negativeInverseOfLogAlphabetSize = -1/Math.log(alphabetSize);



		likelihoodsSet = false;
	}

	public void learnSequence(int[] sequence) {
		for (int i = 0; i <= numShifts; i++)
			learnOffsetSequence(sequence, i);
	}

	private void learnOffsetSequence(int[] sequence, int offset) {
		likelihoodsSet = false; // will have to update likelihoods before rating sequences
		LZNode currentNode = this.root;
		LZNode child, temp;

		// good code apparently. Added on 10/30/02
		for (int i = offset; offset <= i && i < sequence.length; i++) {
			child = currentNode.getChild(sequence[i]);
			if (child != null) {
				currentNode = child;
				continue;
			}

			// child == NULL
			if (minContext <= currentNode.getDepth())
				// just have to add on a single character
				child = new LZNode(currentNode, sequence[i]);
			else {
				// have to add on the rest of the window + a single character
				int length = Math.min(minContext - currentNode.getDepth() + 1, sequence.length - i);
				child = LZNode.createBranch(currentNode, sequence, i, length);
				i += length;
			}

			currentNode = root;
			i -= minContext;
		}

		if (currentNode != root)
			currentNode.markEnd();
	}

	/**
	   Calculates the probability of a string
	   @return A double between 0 and 1
	*/
	private double getProbablityOfSequence(int[] sequence) {
		if (!likelihoodsSet) {
			root.setLikelihoods();
			likelihoodsSet = true;
		}

		double result = 0; // Will hold the probability. Initialized to log(1).
		LZNode currentNode = this.root;
		LZNode child, temp = null;

		for (int i = 0; i < sequence.length; i++) {
			if (currentNode == null) {
				// currentNode can't be found in the previous position. Backtrack (upto) min spaces...
				int mark = i;
				i = (i < minContext) ? 0 : i - minContext;

				currentNode = root; // reset to the root
				// and trace downwards
				for (int j = i; j < mark && currentNode != null; j++)
					currentNode = currentNode.getChild(sequence[j]);

				if (currentNode == null) // the trace was broken. no similar path exists!
					currentNode = root; // currentNode will have to be root.

				i = mark; // continue from where we started.
			}

			// get the child.
			child = currentNode.getChild(sequence[i]);

			if (child != null)
				result += child.getLogLikelihoodFromParent();
			else result += currentNode.getLogLikelihoodToVirtualChild();

			currentNode = child;
		}

		return result;
	}

	public double rateSequence(int[] sequence) {
		if (sequence == null || sequence.length == 0)
			return 1;
/**@ron		return (getProbablityOfSequence(sequence) * negativeInverseOfLogAlphabetSize) / sequence.length;*/
		return (getProbablityOfSequence(sequence) * negativeInverseOfLg) / sequence.length;
	}

        /**
         * Calculates the loglikelihood of sequence
         * @param sequence of symbols
         * @return -lg (P (sequence | training) )
         * @author Ron B.
         */
        public double calcLogLikelihood(int []sequence) {
          return getProbablityOfSequence(sequence)*negativeInverseOfLg;
        }
}
