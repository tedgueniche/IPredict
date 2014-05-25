package ca.ipredict.predictor.LZ78;

import java.util.ArrayList;
import java.util.List;

public class Node {

	/**
	 * Label of the node
	 */
	public int label;
	
	/**
	 * List of children nodes
	 */
	public List<Integer> children;
	
	/**
	 * Support of the node
	 */
	private int support;
	
	/**
	 * Sum of its child's support
	 */
	private int childSumSupport;
	
	public Node(int label) {
		this.label = label;
		children = new ArrayList<Integer>();
		support = 1;
		childSumSupport = 0;
	}
	
	/**
	 * Add child to the node
	 */
	public void addChild(Integer child) {
		children.add(child);
		incChildSupport();
	}
	
	public void incChildSupport() {
		childSumSupport++;
	}
	
	/**
	 * Increment the support of this node
	 */
	public void inc() {
		support++;
	}
	
	/**
	 * Returns the support of this node
	 */
	public int getSup() {
		return support;
	}
	
	/**
	 * Returns the sum of its child's support
	 */
	public int getChildSup() {
		return childSumSupport;
	}
	
	
}
