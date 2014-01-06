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
 * Created on Jul 8, 2003
 */

package vmm.algs.lzms;

/**
 * @author Moti
 *
 * LZNode is a generic node for an LZ-Tree, of size alphabet-size
 */
class LZNode {

	private final LZNodeStorage children;
	private final int alphabetSize;

	private LZNode parent; // can transfer trees
	private int depth;

	//	Counters are used to CALCULATE how many leaves are in the node's subtree (but ISN"T necessarily the number of leaves)
	private int counter;
	private int endCounter;
	private int numLeaves;

	private double logLikelihoodFromParent;
	private double logLikelihoodToVirtualChild;

	LZNode(int alphabetSize) {
		this.alphabetSize = alphabetSize;
		this.children = LZNodeStorageFactory.create(alphabetSize);

		initializeFromParent(null);
	}

	private void initializeFromParent(LZNode parent) {
		this.parent = parent;
		this.depth = (parent == null ? 0 : parent.depth + 1);

		this.counter = this.endCounter = 1; // only one leaf in the root's subtree (itself)
	}

	/**
	Constructs a single node
	@param parent The parent that created this node
	*/
	public LZNode(LZNode parent, int symbol) {
		parent.children.put(symbol, this); // add this into parent's children.

		this.alphabetSize = parent.alphabetSize;
		this.children = LZNodeStorageFactory.create(alphabetSize);


		initializeFromParent(parent);

		increment(1);
	}

	private LZNode(int symbol, LZNode parent) {
		parent.children.put(symbol, this); // add this into parent's children.

		this.alphabetSize = parent.alphabetSize;
		this.children = LZNodeStorageFactory.create(alphabetSize);


		initializeFromParent(parent);
	}

	static public LZNode createBranch(LZNode parent, int[] symbols, int off, int len) {
		LZNode[] descendents = new LZNode[len + 1];
		descendents[0] = parent;
		for (int i = 1, j = off; i < descendents.length; i++, j++)
			descendents[i] = new LZNode(symbols[j], descendents[i - 1]);
		for (int i = 1, addOn = len - 1; addOn >= 0; i++, addOn--)
			descendents[i].counter += addOn;

		parent.increment(len);

		return descendents[len]; // return the leaf at the end of the branch
	}

	private void increment(int value) {
		for (LZNode iter = parent; iter != null; iter = iter.parent)
			iter.counter += value;
	}

	public LZNode getChild(int symbol) {
		return children.get(symbol);
	}

	public void markEnd() {
		for (LZNode iter = this; iter != null; iter = iter.parent)
			iter.endCounter++;
	}

	// when one creates a node it has alphabetSize leaves. For every leaf expanded it gains alphabetSize - 1 leaves.
	// if the counter had started at 0 we would have a total of: alphabetSize + counter*(alphabetSize - 1) leaves =
	// = alphabetSize - 1 + 1 + counter*(alphabetSize - 1) leaves = (counter + 1)*(alphabetSize - 1) + 1 leaves.
	// since we started the counter from 1 and not from 0, we get the following equation: counter * normalizedABSize + 1
	// aditionally there are endCounter leaves for paths that ended mid-Tree.
	// however, endCounter started from 1, and so contains the +1 from before.
	public void setLikelihoods() {
		if (parent == null) {
			setNumLeaves();
			logLikelihoodFromParent = 0;
		}

		LZNode[] childNodes = children.toArray();

		if (childNodes.length > 0) {
			double inverseNumLeaves = 1.0/numLeaves;
			for (int i = 0; i < childNodes.length; i++) {
				childNodes[i].setNumLeaves();
				childNodes[i].logLikelihoodFromParent =
					Math.log(childNodes[i].numLeaves * inverseNumLeaves);
			}
		}

		logLikelihoodToVirtualChild = -Math.log(numLeaves);

		for (int i = 0; i < childNodes.length; i++)
			childNodes[i].setLikelihoods();
	}

	private void setNumLeaves() {
		numLeaves = counter * (alphabetSize - 1) + endCounter;
	}

	public int getAlphabetSize() {
		return alphabetSize;
	}

	public int getDepth() {
		return depth;
	}

	public double getLogLikelihoodFromParent() {
		return logLikelihoodFromParent;
	}

	public double getLogLikelihoodToVirtualChild() {
		return logLikelihoodToVirtualChild;
	}

	public LZNode getParent() {
		return parent;
	}

}
