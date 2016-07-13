package ca.ipredict.predictor.DG;

import java.io.Serializable;

/**
 * Represents an arc in a DG
 */
public class DGArc implements Serializable {

	private static final long serialVersionUID = -6372144456750105502L;

	public int dest; //Destination of this arc
	public int support; //Support for this arc

	/**
	 * Initialize a new arc to the destination with a support of 1
	 */
	public DGArc(int destination) {
		dest = destination;
		support = 1;
	}
	
}
