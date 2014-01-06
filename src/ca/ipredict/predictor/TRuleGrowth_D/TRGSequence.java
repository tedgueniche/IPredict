package ca.ipredict.predictor.TRuleGrowth_D;

import java.util.ArrayList;
import java.util.List;



/**
 * Implementation of a sequence.
 * A sequence is a list of itemsets.
 * @author Philippe Fournier-Viger 
 **/
public class TRGSequence{
	
	private final List<TRGItemset> itemsets = new ArrayList<TRGItemset>();
	private int id; // id de la sequence
	
	
	public TRGSequence(int id){
		this.id = id;
	}

	public void addItemset(TRGItemset itemset) {
		itemsets.add(itemset);
	}
	
	public void print() {
		System.out.print(toString());
	}
	
	public String toString() {
		StringBuffer r = new StringBuffer("");
		for(TRGItemset itemset : itemsets){
			r.append('(');
			for(Integer item : itemset.getItems()){
				String string = item.toString();
				r.append(string);
				r.append(' ');
			}
			r.append(')');
		}

		return r.append("    ").toString();
	}
	
	
	
	public int getId() {
		return id;
	}

	public List<TRGItemset> getItemsets() {
		return itemsets;
	}
	
	public TRGItemset get(int index) {
		return itemsets.get(index);
	}
	
	public int size(){
		return itemsets.size();
	}
	

	public void setID(int id2) {
		id = id2;
	}
	

}
