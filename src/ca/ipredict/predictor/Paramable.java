package ca.ipredict.predictor;

import java.util.HashMap;

import ca.ipredict.predictor.profile.Profile;

public class Paramable {

	HashMap<String, String> parameters;
	
	public Paramable() {
		parameters = new HashMap<String, String>();
	}
	
	public void setParameter(String params) {
		String[] paramsStr = params.split("\\s");
		for(String param : paramsStr) {
			
			String[] keyValue = param.split(":");
			parameters.put(keyValue[0], keyValue[1]);
		}	
	}
	
	
	public double paramDouble(String name) {
		Object value = parameters.get(name);
		
		if(value != null) {
			return Double.valueOf(parameters.get(name));
		}
		else {
			return Profile.paramDouble(name);
		}	
	}
	
	public int paramInt(String name) {
		Object value = parameters.get(name);
		
		if(value != null) {
			return Integer.valueOf(parameters.get(name));
		}
		else {
			return Profile.paramInt(name);
		}
	}
	
	public float paramFloat(String name) {
		Object value = parameters.get(name);
		
		if(value != null) {
			return Float.valueOf(parameters.get(name));
		}
		else {
			return Profile.paramFloat(name);
		}
	}
	
	public boolean paramBool(String name) {
		Object value = parameters.get(name);
		
		if(value != null) {
			return Boolean.valueOf(parameters.get(name));
		}
		else {
			return Profile.paramBool(name);
		}
	}
	
}
