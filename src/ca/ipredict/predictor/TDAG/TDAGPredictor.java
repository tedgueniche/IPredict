package ca.ipredict.predictor.TDAG;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Predictor;

/**
 * TDAG predictor is based on Markov Trees
 * Its fast but it has drawbacks in terms 
 * It takes a lot of space!
 * This implementation has a few of the optimization presented in the original paper below.
 * 
 * Optimization#1: max tree height as described in original papel -> reduce spacial size without noticeable effect on accuracy and coverage (to be double checked)
 * 
 * Source:  P. Laird and R. Saul, "Discrete sequence prediction and its applications"  Mach. Learning, vol. 15, pp. 43-68, 1994. 
 */
public class TDAGPredictor extends Predictor implements Serializable{

	private static final long serialVersionUID = -2326834780277423168L;

	/**
	 * FIFO used during training to remember the last Node inserted in the tree
	 */
	private List<TDAGNode> state;
	
	/**
	 * Root of the tree
	 */
	private TDAGNode root;
	
	/**
	 * Number of nodes in the tree
	 */
	private Integer size;
	
	/**
	 * Max tree height, forbid creating branch with a length higher
	 * than this parameter
	 */
	private final Integer maxTreeHeight = 6;
	
	/**
	 * Map a list of symbol to a specific node in the tree
	 * It is used to lookup specific nodes in the prediction method.
	 */
	private HashMap<List<Integer>, TDAGNode> mDictionary;
	
	
	public TDAGPredictor() {
		TAG = "TDAG";
	}

	public TDAGPredictor(String tag) {
		TAG = tag;
	}

	@Override
	public Boolean Train(List<Sequence> trainingSequences) {
		
		//reset
		root = new TDAGNode(0, new ArrayList<Integer>());
		size = 1;
		state = new ArrayList<TDAGNode>();
		mDictionary = new HashMap<List<Integer>, TDAGNode>();
		
		
		//for each training sequence
		for(Sequence seq : trainingSequences) {
			
			//resetting the states
			state.clear();
			state.add(root);
			
			//For each item in the current sequence
			for(Item item : seq.getItems()) {
				
				//Initiating the newState
				List<TDAGNode> newState = new ArrayList<TDAGNode>();
				newState.add(root);
				
				//Adding a child with this item to each of the nodes in State
				for(TDAGNode node : state) {
					
					//if the node has not the maximal allowed height
					if(node.pathFromRoot.size() <= maxTreeHeight) {
						
						//Create and insert the node
						TDAGNode child = node.addChild(item.val);
						size++;
						mDictionary.put(child.pathFromRoot, child);
						
						//Pushing the new child in the next state
						newState.add(child);
					}
				}
				
				//Overwriting State with the newState
				state = newState;
			}
		}
		
		//Free memory since this is only used in the training process
		state.clear();
		
		return true;
	}

	@Override
	public Sequence Predict(Sequence target) {
		Sequence predicted = new Sequence(-1);
		
		//Converting the target sequence into a list of symbol
		List<Integer> symbols = new ArrayList<Integer>();
		symbols.add(0);
		for(Item item : target.getItems()) {
			symbols.add(item.val);
		}
		
		//Looking for a Node in the tree that contains the same symbols as a 
		//path from the root.
		TDAGNode context = mDictionary.get(symbols);
		while(context == null && symbols.size() > 0) {
			
			//removing the less relevant symbol from the symbols
			symbols.remove(0);
			
			//Attempting to extract the right node
			context = mDictionary.get(symbols);
		}
		
		
		if(context != null) {
			TDAGNode candidate1 = null; //Best candidate
			TDAGNode candidate2 = null; //Second best candidate
			
			//For each child of this context, we calculate the score (probability of appearance given the context)
			for(Entry<Integer, TDAGNode> entry : context.children.entrySet()) {
				
				double score = ((double) entry.getValue().inCount / context.outCount);
				entry.getValue().score = score;
				
				if(candidate1 == null || candidate1.score < score) {
					candidate2 = candidate1;
					candidate1 = entry.getValue();
				}
				else if(candidate2 == null || candidate2.score < score) {
					candidate2 = entry.getValue();
				}
			}
			
			
			//Generating a prediction with candidate1 only if
			//candidate1 has a higher score than candidate2 
			Double treshold = 0.0;
			if(candidate1 != null && 
					(candidate2 == null || candidate1.score - candidate2.score > treshold)) {
				predicted.addItem(new Item(candidate1.symbol));
			}
		}
		
		return predicted;
	}


	public long size() {
		return size;
	}

	/**
	 * Each node has a list of children, the sum of the lists for all children is equals to the number of nodes (2 * size).
	 * Each nodes has also three integers (12 bytes)
	 */
	public float memoryUsage() {
		return 2 * size * 12;
	}
	
	public static void main(String...args) {
		
		Sequence A = new Sequence(1);
		A.addItem(new Item(1));
		A.addItem(new Item(2));
		A.addItem(new Item(3));
		
		Sequence B = new Sequence(2);
		B.addItem(new Item(1));
		B.addItem(new Item(3));
		B.addItem(new Item(2));
		
		List<Sequence> trainingSet = new ArrayList<Sequence>();
		trainingSet.add(A);
		trainingSet.add(B);
		
		TDAGPredictor p = new TDAGPredictor();
		p.Train(trainingSet);
		
		Sequence X = new Sequence(3);
		X.addItem(new Item(4));
		
		Sequence predicted = p.Predict(X);
		System.out.println("Predicted "+ predicted);
	}

}
