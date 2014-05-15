package ca.ipredict.predictor.TDAG;

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
 * Source: Laird, Philip, and Ronald Saul. "Discrete sequence prediction and its applications." Machine learning 15.1 (1994): 43-68.
 */
public class TDAGPredictor implements Predictor {

	/**
	 * FIFO used during training to remember the last Node inserted in the tree
	 */
	private List<Node> state;
	
	/**
	 * Contains the training sequences
	 */
	private List<Sequence> trainingSequences;
	
	/**
	 * Root of the tree
	 */
	private Node root;
	
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
	private HashMap<List<Integer>, Node> dictionnary;
	
	
	public TDAGPredictor() {
		root = new Node(0, new ArrayList<Integer>());
		size = 1;
		state = new ArrayList<Node>();
		dictionnary = new HashMap<List<Integer>, Node>();
	}
	
	@Override
	public void setTrainingSequences(List<Sequence> trainingSequences) {
		this.trainingSequences = trainingSequences;
	}

	@Override
	public Boolean Preload() {
		
		
		//for each training sequence
		for(Sequence seq : trainingSequences) {
			
			//resetting the states
			state.clear();
			state.add(root);
			
			//For each item in the current sequence
			for(Item item : seq.getItems()) {
				
				//Initiating the newState
				List<Node> newState = new ArrayList<Node>();
				newState.add(root);
				
				//Adding a child with this item to each of the nodes in State
				for(Node node : state) {
					
					//if the node has not the maximal allowed height
					if(node.pathFromRoot.size() <= maxTreeHeight) {
						
						//Create and insert the node
						Node child = node.addChild(item.val);
						size++;
						dictionnary.put(child.pathFromRoot, child);
						
						//Pushing the new child in the next state
						newState.add(child);
					}
				}
				
				//overwritting State with the newState
				state = newState;
			}
		}
		
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
		Node context = dictionnary.get(symbols);
		while(context == null && symbols.size() > 0) {
			
			//removing the less relevant symbol from the symbols
			symbols.remove(0);
			
			//Attempting to extract the right node
			context = dictionnary.get(symbols);
		}
		
		
		if(context != null) {
			Node candidate1 = null; //Best candidate
			Node candidate2 = null; //Second best candidate
			
			//For each child of this context, we calculate the score (probability of appearance given the context)
			for(Entry<Integer, Node> entry : context.children.entrySet()) {
				
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

	@Override
	public String getTAG() {
		return "TDAG";
	}

	@Override
	public long size() {
		return size;
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
		p.setTrainingSequences(trainingSet);
		p.Preload();
		
		
		
		Sequence X = new Sequence(3);
		X.addItem(new Item(4));
		
		Sequence predicted = p.Predict(X);
		System.out.println("Predicted "+ predicted);
	}
}
