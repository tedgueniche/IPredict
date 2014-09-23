package ca.ipredict.database;

public class Item implements Comparable<Item> {

	public Integer val;
	
	public Item(Integer value) {
		val = value;
	}
	
	@Override
	public Item clone() {
		return new Item(val);
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
	

	public boolean equals(Item b) {
		return val.equals(b.val);
	}
	
	@Override
	public boolean equals(Object obj) {
		Item b = (Item) obj;
		return val.equals(b.val);
	};

	@Override
	public int compareTo(Item o) {
		return this.val.compareTo(o.val);
	}

}
