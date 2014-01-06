package ca.ipredict.database;

public class Item {

	public Integer val;
	
	public Item(Integer value) {
		val = value;
	}
	
	public Item() {
		val = -1;
	}
		
	public String toString() {
		return val.toString();
	}
	
	public int hashCode() {
		return val.hashCode();
	}
	
	public boolean equals(Object b) {
		Item tmp = (Item) b;
		return val.equals(tmp.val);
	}

}
