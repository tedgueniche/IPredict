package ca.ipredict.predictor.CPT;
import java.util.ArrayList;
import java.util.List;

import ca.ipredict.database.Item;

/**
 * Recursive definition of a prediction tree for the CPT Predictor
 * PredictionTree is a node in a tree that has zero or more children
 * A node has a support (occurrence count) and a item which represents its value.
 */
public class PredictionTree {

	/**
	 * Number of occurrences
	 */
	public int Support;
	
	/**
	 * Value of the node
	 */
	public Item Item;
	
	/**
	 * A link to its parent node
	 */
	public PredictionTree Parent; //parent's node
	
	/**
	 * List of its children
	 */
	private List<PredictionTree> Children; //children list
	
	
	public PredictionTree(Item itemValue) {
		Support = 0; //default support
		Item = itemValue;
		Children = new ArrayList<PredictionTree>();
		Parent = null;
	}
	
	/**
	 * Construct an empty node 
	 */
	public PredictionTree() {
		Support = 0; //default support
		Item = new Item();
		Children = new ArrayList<PredictionTree>();
		Parent = null;
	}
	
	/**
	 * Adds a child to the current node
	 */
	public void addChild(Item child) {
		PredictionTree newChild = new PredictionTree(child);
		newChild.Parent = this;
		Children.add(newChild);
	}
	
	/**
	 * Return true if the given item is a child of this node
	 */
	public Boolean hasChild(Item target) {
		
		PredictionTree found = getChild(target);
		return (found == null) ?  false : true;
	}
	
	/**
	 * Returns the prediction tree associated with the given child of this node
	 */
	public PredictionTree getChild(Item target) {

		for(PredictionTree child : Children) {
			if(child.Item.val.equals(target.val))
				return child;
		}
		
		return null;
	}

	/**
	 * Return the number of child of this node
	 */
	public int getChildrenCount() {
		return Children.size();
	}

}
