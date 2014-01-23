package ca.ipredict.database;
import java.util.ArrayList;
import java.util.List;


public class Sequence {

	private List<Item> items;
	private int id; // id de la sequence
	
	
	public Sequence(int id){
		this.id = id;
		items = new ArrayList<Item>();
	}
	
	/**
	 * Make a hard copy of the given sequence
	 * @param aSequence  sequence to copy
	 */
	public Sequence(Sequence aSequence) {
		this.id = aSequence.id;
		this.items = new ArrayList<Item>();
		for(Item item : aSequence.getItems()) {
			this.items.add(new Item(item.val));
		}
	}
	
	public Sequence(int id, List<Item> items) {
		this.id = id;
		this.items = (items != null) ? items : new ArrayList<Item>();
	}
	
	public int getId() {
		return id;
	}

	public List<Item> getItems() {
		return items;
	}
	
	private void setItems(List<Item> newItems) {
		items = newItems;
	}
	
	public Item get(int index) {
		return items.get(index);
	}
	
	public int size(){
		return items.size();
	}

	public void addItem(Item item) {
		items.add(item);
	}
	
	/**
	 * return the last [length] items from this sequence as a sequence
	 * @return [length] or less (if not enough items) items as a sequence or NULL on error
	 */
	public Sequence getLastItems(int length, int offset) {
		
		Sequence truncatedSequence = new Sequence(0);
		int size = size() - offset;
		
		//If there is not enough items then returns all available items
		if(items.isEmpty()) {
			return null; //ERROR
		}
		else if(length > size) {
			//creating new sequence with truncated list
			// PHIL08: HERE I MODIFIED TO MAKE A COPY OF THE LIST RETURNED BY SUBLIST 
			// BECAUSE BY DEFAULT SUBLIST MAKE POINTERS TO THE ORIGINAL LIST AND IF 
			// WE MODIFY THE LIST WE MAY GET A CONCURRENT ACCESS EXCEPTION (I was getting one!)
//		    //  new ArrayList(...)
			List<Item> truncatedList = new ArrayList<Item>(items.subList( 0, size ));
			truncatedSequence.setItems(truncatedList);
		}
		else {
			//splitting list
			// PHIL08: HERE I MODIFIED TO MAKE A COPY OF THE LIST RETURNED BY SUBLIST 
			// BECAUSE BY DEFAULT SUBLIST MAKE POINTERS TO THE ORIGINAL LIST AND IF 
			// WE MODIFY THE LIST WE MAY GET A CONCURRENT ACCESS EXCEPTION (I was getting one!)
			//  new ArrayList(...)
			List<Item> truncatedList = new ArrayList<Item>(items.subList( (size - length), (size) ));
			truncatedSequence.setItems(truncatedList);
		}
		
		return truncatedSequence;
		/*
		//TODO: this is way too too strict, let it loose
		int size = size() - offset;
		if(length <= size) {
			//splitting list
			List<Item> truncatedList = items.subList( (size - length), (size) );
			
			//creating new sequence with truncated list
			Sequence truncatedSequence = new Sequence(0);
			truncatedSequence.setItems(truncatedList);
			
			return truncatedSequence;
		}
		else {
			return null; //should never happen! it would cause the algo to not work properly
			//TODO: throw exception or maybe return empty sequence
		}
		*/
	}
	
	//adjust sequence to keep only the "length" items at the end of the sequence
	/*
	public void keepOnlyLastItems(int length) {
		if(length < size()) {
			//Sequence 
			//items = items.subList(size() - length, size()); //sketchy
		}
	}
	*/
	
	public void print() {
		System.out.print(toString());
	}
	
	public String toString() {
		StringBuffer r = new StringBuffer("");
		for(Item it : items){
			r.append('(');
			String string = it.toString();
			r.append(string);
			r.append(") ");
		}

		return r.append("    ").toString();
	}
	
	public void setID(int newid) {
		id = newid;
	}
}
