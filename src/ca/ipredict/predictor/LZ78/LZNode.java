package ca.ipredict.predictor.LZ78;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LZNode implements Serializable {

	private static final long serialVersionUID = 6683917387605870777L;

	/**
	 * Label of the node
	 */
	public int value;
	
	/**
	 * List of children nodes
	 */
	public HashSet<Integer> children;
	
	/**
	 * Support of the node
	 */
	private int support;
	
	/**
	 * Sum of its child's support
	 */
	private int childSumSupport;
	
	public LZNode(int value) {
		this.value = value;
		children = new HashSet<Integer>();
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
