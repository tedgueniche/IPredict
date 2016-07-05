package ca.ipredict.predictor.profile;

import java.util.HashMap;
import java.util.Map.Entry;


/**
 * Interface to load a parameters profile.
 */
public class Profile {
	
	//Contains the parameter as strings
	public static HashMap<String, String> parameters;
	
	public Profile() {
		parameters = new HashMap<String, String>();
	}
	
	public static Double paramDouble(String name) {
		Object value = parameters.get(name);
		return (value == null) ? null : Double.valueOf(parameters.get(name));
	}
	
	public static Integer paramInt(String name) {
		Object value = parameters.get(name);
		return (value == null) ? null : Integer.valueOf(parameters.get(name));	
	}
	
	public static Float paramFloat(String name) {
		Object value = parameters.get(name);
		return (value == null) ? null : Float.valueOf(parameters.get(name));	
	}
	
	public static Boolean paramBool(String name) {
		Object value = parameters.get(name);
		return (value == null) ? null : Boolean.valueOf(parameters.get(name));	
	}
	
	//Applies the parameters 
	public void Apply(){}
	

	public static String tostring() {
		String nl = "\n";
		String output = "---Global Parameters---" + nl;
		
		
		for(Entry<String, String> param : parameters.entrySet()) {
			output += param.getKey() + "\t" + param.getValue() + nl;
		}
		
		return output;
	}
}
