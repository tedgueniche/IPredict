package ca.ipredict.predictor.TRuleGrowth_D;



/**
 * This class is for representing a sequential rule.
 * @author Philippe Fournier-Viger, 2009
 */
public class Rule implements Comparable<Rule>{
	
	private int[] itemset1; // antecedent
	private int[] itemset2; // consequent
	public int support; // absolute support

	private double confidence;
	
	public Rule(int[] itemset1, int[] itemset2, double confidence, int support){
		this.itemset1 = itemset1;
		this.itemset2 = itemset2;
		this.confidence = confidence;
		this.support = support;
	}

	public int[] getItemset1() {
		return itemset1;
	}

	public int[] getItemset2() {
		return itemset2;
	}
	
	public int getAbsoluteSupport(){
		return support;
	}
	
	public double getRelativeSupport(int sequencecount) {
		return ((double)support) / ((double) sequencecount);
	}

	public void print(){
		System.out.println(toString());
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i< itemset1.length; i++){
			buffer.append(itemset1[i]);
			if(i != itemset1.length-1){
				buffer.append(",");
			}
		}
		buffer.append(" ==> ");
		for(int i=0; i< itemset2.length; i++){
			buffer.append(itemset2[i]);
			if(i != itemset2.length-1){
				buffer.append(",");
			}
		}
		return buffer.toString();
	}

	public double getConfidence() {
		return confidence;
	}
	
	/** return 0 if both rules are equal
	 *  return > 0 if this rule has higher support + confidence than rule o
	 * 
	 */
	public double compareTo(Rule o, int sequenceCount) {
		if(o == this){
			return 0;
		}
		
		//support is too small to matter
		double support = this.getRelativeSupport(sequenceCount) - o.getRelativeSupport(sequenceCount);
		double confidence = this.getConfidence() - o.getConfidence();
		
		
		
		double score = ((confidence * 0.7) + (support * 0.3));
		return score;
	}

	@Override
	public int compareTo(Rule arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}
