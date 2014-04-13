package ca.ipredict.predictor.CPT11;
import java.util.ArrayList;
import java.util.List;

import ca.ipredict.database.Item;

/**
 * Represent the prediction tree for CPT
 */
public class CPT11_PredictionTree {

	public int Support; //support count
	public Item Item; //actual item
	public CPT11_PredictionTree Parent; //parent's node
	
	private List<CPT11_PredictionTree> Children; //children list
	
	public CPT11_PredictionTree(Item itemValue) {
		Support = 0; //default support
		Item = itemValue;
		Children = new ArrayList<CPT11_PredictionTree>();
		Parent = null;
	}
	
	public CPT11_PredictionTree() {
		Support = 0; //default support
		Item = new Item();
		Children = new ArrayList<CPT11_PredictionTree>();
		Parent = null;
	}
	
	public void addChild(Item child) {
		CPT11_PredictionTree newChild = new CPT11_PredictionTree(child);
		newChild.Parent = this;
		Children.add(newChild);
	}
	
	public Boolean hasChild(Item target) {
		
		for(CPT11_PredictionTree child : Children) {
			if(child.Item.val.equals(target.val)) {
				return true;
			}
		}
		
		return false;
	}
	
	public CPT11_PredictionTree getChild(Item target) {

		for(CPT11_PredictionTree child : Children) {
			if(child.Item.val.equals(target.val))
				return child;
		}
		
		return null;
	}


	public int getChildrenCount() {
		return Children.size();
	}

}
