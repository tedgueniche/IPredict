package ca.ipredict.predictor.TRuleGrowth_D;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an itemset (a set of items)
 * @author Philippe Fournier-Viger 
 */
public class TRGItemset{
	private final List<Integer> items = new ArrayList<Integer>(); // ordered
//	Set<Integer> transactionsIds = new HashSet<Integer>();
	
	public TRGItemset(){
	}
	
	public TRGItemset(Integer item){
		items.add(item);
	}

	public TRGItemset(TRGItemset itemset){
		items.addAll(itemset.getItems());
	}
	
	public boolean includedIn(TRGItemset itemset2) {
		return itemset2.items.containsAll(items);
	}
	
	public void addItem(Integer value){
			items.add(value);
	}
	
	public void addItemOrderedWithNoDuplicate(Integer value){
		for(int i=0; i< items.size(); i++){
			if(value == items.get(i) ){
				return; // already there!
			}
			if(value < items.get(i)){
				if(i ==0){
					items.add(0,value);
				}else{
					items.add(i,value);
				}
				return;
			}
		}
		items.add(value);
	}
	
	public List<Integer> getItems(){
		return items;
	}
	
	public Integer get(int index){
		return items.get(index);
	}
	
	public void print(){
		System.out.print(toString());
	}
	
	public String toString(){
		StringBuffer r = new StringBuffer ();
		for(Integer attribute : items){
			r.append(attribute.toString());
			r.append(' ');
		}
		return r.toString();
	}
	
	// 1, 2, 5
	/**
	 * 
	 * This method checks if the item "item" is in the itemset.
	 * It asumes that items in the itemset are sorted in lexical order
	 * This version also checks that if the item "item" was added it would be the largest one
	 * according to the lexical order
	 */
	public boolean containsLEXPlus(Integer item) {
		for(Integer itemI : items){
			if(itemI.equals(item)){
				return true;
			}else if(itemI > item){
				return true; // <-- xxxx
			}
		}
		return false;
	}
	
	/**
	 * This method checks if the item "item" is in the itemset.
	 * It asumes that items in the itemset are sorted in lexical order
	 * @param item
	 * @return
	 */
	public boolean containsLEX(Integer item) {
		for(Integer itemI : items){
			if(itemI.equals(item)){
				return true;
			}else if(itemI > item){
				return false;  // <-- xxxx
			}
		}
		return false;
	}
	
	public boolean contains(int item) {
		for(int i=0; i<items.size(); i++){
			if(items.get(i) == item){
				return true;
			}else if(items.get(i) > item){
				return false;
			}
		}
		return false;
	}

	
	public boolean isLexicallySmallerthan(TRGItemset itemset2){
		for(int i=0; i< items.size(); i++){
			if(items.get(i) > itemset2.items.get(i)){
				return false;
			}
			else if(items.get(i) < itemset2.items.get(i)){
				return true;
			}
		}
		return true;
	}
	
	
	public boolean isEqualTo(TRGItemset itemset2){
		if(items.size() != itemset2.items.size()){
			return false;
		}
		return items.containsAll(itemset2.items);
	}


//	public void setTransactioncount(Set<Integer> listTransactionIds) {
//		this.transactionsIds = listTransactionIds;
//	}

	// pour Apriori
	public TRGItemset cloneItemSetMinusOneItem(Integer itemsetToRemove){
		TRGItemset itemset = new TRGItemset();
		for(Integer item : items){
			if(!item.equals(itemsetToRemove)){
				itemset.addItem(item);
			}
		}
		return itemset;
	}
	
	public TRGItemset cloneItemSetMinusAnItemset(TRGItemset itemsetToNotKeep){
		TRGItemset itemset = new TRGItemset();
		for(Integer item : items){
			if(!itemsetToNotKeep.contains(item)){
				itemset.addItem(item);
			}
		}
		return itemset;
	}
	
	public int size(){
		return items.size();
	}

	/** 
	* check if the item from this itemset are all the same as those of itemset2 
	* except the last item 
	* and that itemset2 is lexically smaller than this itemset. If all these conditions are satisfied,
	* this method return the last item of itemset2. Otherwise it returns null.
	* @return the last item of itemset2, or null.
	* */
	public Integer allTheSameExceptLastItem(TRGItemset itemset2) {
		if(itemset2.size() != items.size()){
			return null;
		}
		for(int i=0; i< items.size(); i++){
			// if they are the last items
			if(i == items.size()-1){ 
				// the one from items should be smaller (lexical order) and different than the one of itemset2
				if(items.get(i) >= itemset2.get(i)){  
					return null;
				}
			}
			// if they are not the last items, they  should be the same
			else if(items.get(i) != itemset2.get(i)){ 
				return null; 
			}
		}
		return itemset2.get(itemset2.size()-1);
	}
	
	public boolean allTheSameExceptLastItemV2(TRGItemset itemset2) {
		if(itemset2.size() != items.size()){
			return false;
		}
		for(int i=0; i< items.size()-1; i++){
			// if they are not the last items, they  should be the same
			 if(items.get(i) != itemset2.get(i)){ 
				return false; 
			}
		}
		return true;
	}
	
	public Integer getLastItem(){
		return items.get(size()-1);
	}
	
	// another version of the previous method but don't assume the lexicographical order!
	public Integer allTheSameExcept(TRGItemset itemset2) {
		if(itemset2.size() != items.size()){
			return null;
		}
		Integer missingItem = null;
		for(Integer item : itemset2.getItems()){
			if(!items.contains(item)){
				if(missingItem !=null){ // more than one is different
					return null;
				}
				missingItem = item;
			}
		}
		return missingItem;
	}
	
	/**
	 * This method compare this itemset with another itemset to see if they are equal.
	 * The method assume that the two itemsets are lexically ordered.
	 * @return  true or false
	 */
	public boolean allTheSame(TRGItemset itemset2) {
		if(itemset2.size() != items.size()){
			return false;
		}
		for(int i=0; i< itemset2.size(); i++){
//			if(!getItems().contains(itemset2.getItems().get(i))){
//				return false;
//			}
			
			if(itemset2.getItems().get(i) != getItems().get(i)){
				return false;
			}
		}
		return true;
	}


//	public Set<Integer> getTransactionsIds() {
//		return transactionsIds;
//	}
}
