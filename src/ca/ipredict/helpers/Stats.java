package ca.ipredict.helpers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Stats {

	private String name;
	private Map<String, Map<String, String>> data;
	
	
	public Stats(String name, List<String> rows) {
		this.name = name;
		//this.rows = rows;
		data = new HashMap<String, Map<String,String>>();
		for(String row : rows) {
			data.put(row, prepareRow());
		}
	}
	
	public HashMap<String, String> prepareRow() {
		HashMap<String, String> row = new HashMap<String, String>();
		/*
		for(String column : columns) {
			row.put(column, "-");
		}
		*/
		return row;
	}
	
	/**
	 * Set a value for the appropriate row and column.
	 */
	public void set(String row, String column, String value) {
		
		//if the column did not exist
		if(data.containsKey(row)) {
			data.put(row, new HashMap<String, String>());
		}
		//Adding the value to the right column and prow combination
		data.put(row, prepareRow());
		data.get(row).put(column, value);	
	}
	
	/**
	 * Get a value from the appropriate row and column.
	 */
	public String get(String row, String column) {
		//if the column did not exist
		if(data.containsKey(row) && data.get(row).containsKey(column)) {
			return data.get(row).get(column);
		}
		else {
			return "0";
		}
	}
	
	public String toString() {
		
		String output = "----"+ name +"----\n";
		
		boolean columnDisplayed = false;
		
		Iterator<?> rows = data.entrySet().iterator();
		while(rows.hasNext()) {
			Map.Entry<String, Map<String, String>> pairRow = (Map.Entry)rows.next();
			String row = pairRow.getKey();
			
			Iterator<?> columns = data.get(row).entrySet().iterator();
			
			if(columnDisplayed == false) {
				
				output += "\t";
				while(columns.hasNext()) {
					Map.Entry<String, String> pairColumn = (Map.Entry)columns.next();
					
					output += "\t" + pairColumn.getKey();
				}
				output += "\n";
				
				columnDisplayed = true;
				columns = data.get(row).entrySet().iterator();
			}
			
			output += row +":";
			
			while(columns.hasNext()) {
				Map.Entry<String, String> pairColumn = (Map.Entry)columns.next();
				
				output += "\t" + pairColumn.getValue();
			}
			
			output += "\n";
		}
		
		
		return output;
	}
	
	public void Inc(String row, String column) {
		
		if(data.containsKey(row)) {
			String val = data.get(row).get(column);
			if(val != null) {
				Integer newVal = 1 + Integer.valueOf(val);
				data.get(row).put(column, newVal.toString());
			}
			else {
				data.get(row).put(column, "1");
			}
		}
		else {
			data.put(row, prepareRow());
			data.get(row).put(column, "1");
		}
	}
	
	public void Divide(String row, String column, float divisor) {
		
		if(data.containsKey(row)) {
			String val = data.get(row).get(column);
			if(val != null) {
				Float newVal = Float.valueOf(val) / divisor;
				data.get(row).put(column, newVal.toString());
			}
			else {
				data.get(row).put(column, "0");
			}
		}
		else {
			data.put(row, prepareRow());
			data.get(row).put(column, "0");
		}
	}
	
	

}
