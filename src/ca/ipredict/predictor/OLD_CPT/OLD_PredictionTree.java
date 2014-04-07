package ca.ipredict.predictor.OLD_CPT;
import java.util.ArrayList;
import java.util.List;

import ca.ipredict.database.Item;

/**
 * Represent the prediction tree for CPT
 */
public class OLD_PredictionTree {

	public int Support; //support count
	public Item Item; //actual item
	public OLD_PredictionTree Parent; //parent's node
	
	private List<OLD_PredictionTree> Children; //children list
	
	public OLD_PredictionTree(Item itemValue) {
		Support = 0; //default support
		Item = itemValue;
		Children = new ArrayList<OLD_PredictionTree>();
		Parent = null;
	}
	
	public OLD_PredictionTree() {
		Support = 0; //default support
		Item = new Item();
		Children = new ArrayList<OLD_PredictionTree>();
		Parent = null;
	}
	
	public void addChild(Item child) {
		OLD_PredictionTree newChild = new OLD_PredictionTree(child);
		newChild.Parent = this;
		Children.add(newChild);
	}
	
	public Boolean hasChild(Item target) {
		
		for(OLD_PredictionTree child : Children) {
			if(child.Item.val.equals(target.val)) {
				return true;
			}
		}
		
		return false;
	}
	
	public OLD_PredictionTree getChild(Item target) {

		for(OLD_PredictionTree child : Children) {
			if(child.Item.val.equals(target.val))
				return child;
		}
		
		return null;
	}


	public int getChildrenCount() {
		return Children.size();
	}

}
