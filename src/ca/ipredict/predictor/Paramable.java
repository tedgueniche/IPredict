package ca.ipredict.predictor;

import java.io.Serializable;
import java.util.HashMap;

import ca.ipredict.predictor.profile.Profile;

public class Paramable implements Serializable {

	HashMap<String, String> parameters;
	
	public Paramable() {
		parameters = new HashMap<String, String>();
	}
	
	public void setParameter(String params) {
		if(params != null && params.length() > 0 && params.contains(":")) {
			
			String[] paramsStr = params.split("\\s");
			for(String param : paramsStr) {
				
				String[] keyValue = param.split(":");
				parameters.put(keyValue[0], keyValue[1]);
			}	
		}
	}
	
	public Double paramDouble(String name) {
		Object value = parameters.get(name);
		
		if(value != null) {
			return Double.valueOf(parameters.get(name));
		}
		else {
			return Profile.paramDouble(name);
		}	
	}
	
	public double paramDoubleOrDefault(String paramName, double defaultValue) {
		Double param = paramDouble(paramName);
		return (param != null) ? param : defaultValue;
	}
	
	public Integer paramInt(String name) {
		Object value = parameters.get(name);
		
		if(value != null) {
			return Integer.valueOf(parameters.get(name));
		}
		else {
			return Profile.paramInt(name);
		}
	}
	
	public int paramIntOrDefault(String paramName, int defaultValue) {
		Integer param = paramInt(paramName);
		return (param != null) ? param : defaultValue;
	}
	
	public Float paramFloat(String name) {
		Object value = parameters.get(name);
		
		if(value != null) {
			return Float.valueOf(parameters.get(name));
		}
		else {
			return Profile.paramFloat(name);
		}
	}
	
	public float paramFloatOrDefault(String paramName, float defaultValue) {
		Float param = paramFloat(paramName);
		return (param != null) ? param : defaultValue;
	}
	
	public Boolean paramBool(String name) {
		Object value = parameters.get(name);
		
		if(value != null) {
			return Boolean.valueOf(parameters.get(name));
		}
		else {
			return Profile.paramBool(name);
		}
	}
	
	public boolean paramBoolOrDefault(String paramName, boolean defaultValue) {
		Boolean param = paramBool(paramName);
		return (param != null) ? param : defaultValue;
	}
}
