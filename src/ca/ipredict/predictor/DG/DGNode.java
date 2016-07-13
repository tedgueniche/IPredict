package ca.ipredict.predictor.DG;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in a DG
 */
public class DGNode implements Serializable {

	private static final long serialVersionUID = 7718487181871055891L;
	
	public int value; //value of this node
	public List<DGArc> arcs; //list of outgoing arcs from this node
	public int totalSupport;

	public int numberOfArcs;
	
	public DGNode(int value) {
		this.value = value;
		arcs = new ArrayList<DGArc>();
		totalSupport = 0;
	}
	
	/**
	 * Returns the number of transition for this state - not the support
	 */
	public int getArcCount() {
		return arcs.size();
	}
	
	/**
	 * Update or create an arc from this node to another one (target)
	 * @param target node to link
	 */
	public void UpdOrAddArc(int target) {
		
		//Searching for an existing arc in the arc list
		boolean isFound = false;
		for(DGArc arc : arcs) {
			if(arc.dest == target) {
				arc.support++;
				isFound = true;
			}	
		}
		
		//if no matching arc, creates one
		if(isFound == false) {
			arcs.add(new DGArc(target));
		}
	}
}
