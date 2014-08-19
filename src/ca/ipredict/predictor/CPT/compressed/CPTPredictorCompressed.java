package ca.ipredict.predictor.CPT.compressed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import ca.ipredict.database.Item;
import ca.ipredict.database.Sequence;
import ca.ipredict.predictor.CPT.Bitvector;
import ca.ipredict.predictor.CPT.CPTPredictor;
import ca.ipredict.predictor.CPT.PredictionTree;
import ca.ipredict.predictor.profile.Profile;

public class CPTPredictorCompressed extends CPTPredictor {

	public Encoder encoder;
	
	protected boolean seqEncoding;
	
	public CPTPredictorCompressed() {
		super("CPTC");
		
		this.seqEncoding = true;
		
		//using the custom compressed helper
		helper = new CPTHelperCompressed(this);
	}

	@Override
	public Boolean Train(List<Sequence> trainingSequences) {
		
		Root = new PredictionTree();
		LT = new HashMap<Integer, PredictionTree>();
		II = new HashMap<Integer, Bitvector>();
		encoder = new Encoder();
		((CPTHelperCompressed) helper).setEncoded(encoder);
		nodeNumber = 0;
		
		int seqId = 0;
		PredictionTree curNode;

		//Identifying the frequent sequential itemsets
		//setting up the encoder for futur encoding tasks
		if(this.seqEncoding == true) {
			List<List<Item>> itemsets = ItemsetFinder.findFrequentItemsets(trainingSequences, 2, 4, 2);
			
//			System.out.println("Found "+ itemsets.size() + " frequent itemsets");
			for(List<Item> itemset : itemsets) {
				encoder.addEntry(itemset);
			}
		}

		//for each training sequence
		for(Sequence seq : trainingSequences) {
			
			//slicing the sequence if needed
			if(Profile.splitMethod > 0) {
				seq = helper.keepLastItems(seq, Profile.splitLength);
			}

			//Generating the compressed version of this sequence
			Sequence seqCompressed = new Sequence(seq);
			seqCompressed = encoder.encode(seqCompressed);
			
			//resetting node pointer to root node
			curNode = Root;

			//for each item in the compressed sequence
			for(Item itemCompressed : seqCompressed.getItems()) {
				
				
				//decoding the current item the encoded sequence
				List<Item> itemset = encoder.getEntry(itemCompressed.val);
				
				//II update
				for(Item item : itemset) {
				
					//adding the item in the Inverted Index if needed
					if(II.containsKey(item.val) == false) {
						Bitvector tmpBitset = new Bitvector();
						II.put(item.val, tmpBitset);
					}
	
					//updating Inverted Index with seqId for this Item
					II.get(item.val).setBit(seqId);
				}
				
				if(curNode.hasChild(itemCompressed) == false) {
					curNode.addChild(itemCompressed);
					nodeNumber++;
					curNode = curNode.getChild(itemCompressed);
				}
				//if this itemCompressed is already a child of the current node
				else {
					curNode = curNode.getChild(itemCompressed);
				}
			}

			
			//adding the sequence id in the Lookup Table
			LT.put(seqId, curNode); //adding <sequence id, last node in sequence>
			seqId++; //increment sequence id number
		}
		
		return true;
	}
	
	/**
	 * Extract the common prefix, if any, between two itemset. <br/>
	 * Eg:  <br/>
	 * 		{1,3,6,7,8} and {1,3,2} -> {1,3} <br/>
	 *  	{1,3,6,7,8} and {1,3} -> {1,3}<br/>
	 *  	{1,3} and {1,3} -> {1,3}<br/>
	 */
	public List<Item> getCommonPrefix(List<Item> A, List<Item> B) {
		
		if(A.size() < 1 || B.size() < 1) {
			return null;
		}
		
		List<Item> prefix = new ArrayList<>();
		for(int i = 0; i < A.size() && i < B.size(); i++) {
			
			if(A.get(i).equals(B.get(i))) {
				prefix.add(A.get(i));
			}
			else {
				return prefix;
			}
		}
		
		return prefix;
	}

}
