package ca.ipredict.predictor.DG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.Predictor;
import ca.ipredict.predictor.profile.Profile;

public class DGPredictor extends Predictor {
	
	private HashMap<Integer, DGNode> mDictionary; //link unique items to their node in a DG
	private int count;
	
	
	public DGPredictor() {
		TAG = "DG";
	}
	
	public DGPredictor(String tag) {
		TAG = tag;
	}

	@Override
	public Boolean Train(List<Sequence> trainingSequences) {
		count = 0;
		//TODO:  Resolve ABB...AC...AD...ABB problem, described in Mogul&Padmanabhan (3. some Issues)
		
		int w = Profile.windowSize; //Window size parameter
		
		mDictionary = new HashMap<Integer, DGNode>();
		
		//For each sequence of the training set
		for(Sequence seq : trainingSequences) {
			
			//for each items in this sequence, but the last one
			List<Item> items = seq.getItems();
			for(int i = 0 ; i < (items.size() - 1); i++) {
				
				//Getting or creating the DGNode associated with this item
				DGNode node = mDictionary.get(items.get(i).val);
				if(node == null) {
					node = new DGNode(items.get(i).val);
					count++;
				}
				node.totalSupport++; //incrementing the absolute support of this node
				
				//Linking this node with the following w items in the sequence
				for(int k = (i+1) ; k < ((i+1)+w) && k < items.size() ; k++) {
					
					node.UpdOrAddArc(items.get(k).val);
				}
				
				//Saving DGNode in the dictionary
				mDictionary.put(items.get(i).val, node);
			}
			
		}
		
		
		return null;
	}

	@Override
	public Sequence Predict(Sequence target) {
		
		double threshold = 0.12; //Parameter, discard prediction with a confidence level below the threshold
		
		//Getting the DGNode associated with the last item of the target sequence
		//or return an empty sequence if there is no match
		DGNode node = null;
		for(int offset = 0 ; node == null && offset < target.size(); offset++) {

			//Getting the DGNode
			Item lastItem = target.get(target.size() - (1 + offset)); 
			node = mDictionary.get(lastItem.val);
		}
		
		//if no match, then return an empty sequence
		if(node == null) {
			return new Sequence(-1);
		}
		
		
		//Getting the best item (the one with the highest score)
		double max = 0;
		int best = 0;
		for(DGArc arc : node.arcs) {
			
			//Calculating the score for this arc
			double score = ((double)arc.support) / node.totalSupport;
			
			//Testing against threshold and the max score
			if(score >= threshold && score > max) {
				max = score;
				best = arc.dest;
			}
		}
		
		if(best == 0) {
			return new Sequence(-1); 
		}
		
		//Generating the sequence from the best item
		Sequence predicted = new Sequence(-1);
		predicted.addItem(new Item(best));
		
		return predicted;
	}

	public long size() {
		return count;
	}
	
	public static void main(String[] args) {
			
			//DG predictor
			DGPredictor predictor = new DGPredictor();
			
			//Training sequences
			List<Sequence> training = new ArrayList<Sequence>();
			
			//1 2 3 4
			Sequence seq1 = new Sequence(-1);
			seq1.addItem(new Item(1));
			seq1.addItem(new Item(2));
			seq1.addItem(new Item(3));
			seq1.addItem(new Item(4));
			training.add(seq1);
			
			//1 2 5 4
			Sequence seq2 = new Sequence(-1);
			seq2.addItem(new Item(1));
			seq2.addItem(new Item(2));
			seq2.addItem(new Item(5));
			seq2.addItem(new Item(4));
			training.add(seq2);
			
			//Training the predictor
			predictor.Train(training);
			
			//Testing sequence
			Sequence seqT = new Sequence(-1);
			seqT.addItem(new Item(2));
			seqT.addItem(new Item(3));
			
			//Actual prediction
			Sequence result = predictor.Predict(seqT);
			
			//Show results
			System.out.println(result.toString());
		}
}
