package ca.ipredict.predictor.CPT.CPTPlus;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ca.ipredict.database.Item;

/**
 * Recursive definition of a prediction tree for the CPT Predictor
 * PredictionTree is a node in a tree that has zero or more children
 * A node has a support (occurrence count) and a item which represents its value.
 * 
 * A node has a theoretical size of (16 + (4 * n)) bytes, where n is the number of children of the node
 * 
 */
public class PredictionTree {

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
		Item = itemValue;
		Children = new ArrayList<PredictionTree>();
		Parent = null;
	}
	
	/**
	 * Construct an empty node 
	 */
	public PredictionTree() {
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
	 * Adds a child to the current node
	 */
	public void addChild(PredictionTree child) {
		child.Parent = this;
		Children.add(child);
	}
	
	public void removeChild(Item child) {
		Children = Children.stream().filter(c -> c.Item.equals(child) == false).collect(Collectors.toList());
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
	 * Returns all of its children as a list
	 */
	public List<PredictionTree> getChildren() {
		return Children;
	}

}
