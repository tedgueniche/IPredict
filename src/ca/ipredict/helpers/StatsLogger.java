package ca.ipredict.helpers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StatsLogger {


	private List<String> statsNames;
	private List<Algo> algorithms;
	
	private boolean useSteps;
	
	public StatsLogger(List<String> statsNames, List<String> algoNames, boolean useSteps) {
		useSteps = false;
		this.statsNames = statsNames;
		algorithms = new ArrayList<Algo>();
		this.useSteps = useSteps;
		for(String algoName : algoNames) {
			algorithms.add(new Algo(algoName, useSteps));
		}
	}
	
	public void addStep() {
		for(Algo algo : algorithms) {
			algo.addStep();
		}
	}
	
	public void set(String stat, String algoName, double value) {
		getAlgoByName(algoName).set(stat, value);
	}
	
	public void inc(String stat, String algoName) {
		double value = getAlgoByName(algoName).get(stat);
		value++;
		getAlgoByName(algoName).set(stat, value);
	}
	
	public void divide(String stat, String algoName, long divisor) {
		double value = getAlgoByName(algoName).get(stat);
		value = value / divisor;
		getAlgoByName(algoName).set(stat, value);
	}
	
	public double get(String stat, String algoName) {
		return getAlgoByName(algoName).get(stat);
	}
	
	public double get(String stat, String algoName, int step) {
		return getAlgoByName(algoName).get(step, stat);
	}
	
	private Algo getAlgoByName(String algoName) {
		for(Algo algo : algorithms) {
			if(algo.name.compareTo(algoName) == 0) {
				return algo;
			}
		}
		return null;
	}
	
	public String toString() {
		String output = "";
		if(useSteps) {
			
		}
		else {
			//Display the header row (name of algorithms)
			output += "\t\t";
			for(Algo algo : algorithms) {
				output += "" + algo.name + "\t";
			}
			output += "\n";
			
			//for each stats, display the stat name and each values			
			for(String stat : statsNames) {
				
				DecimalFormat tenForm = new DecimalFormat("##.###"); 
				String empty = "          ";
				
				output += (stat.length() < 9) ? (stat + empty.substring(stat.length())) : stat.substring(0, 9);
				for(Algo algo : algorithms) {
					double value = algo.get(stat) * 100;
					output += "\t" + ((value == 0.0)? "00.000" : tenForm.format(value));
				}
				output += "\n";
			}
			
			
		}
		return output;
	}
	
	public String toJsonString() {
		
		String output = "";
		if(useSteps) {
			
		}
		else {
			//The list of algorithms. algorithms: ['CPT', 'AKOM',...]
			output += "\"algorithms\": [";
			for(Algo algo : algorithms) {
				output += "\"" + algo.name + "\",";
			}
			output = output.substring(0, output.length() - 1);
			output += "], ";
			
			output += "\"resuls\": [";
			//for each stats, display the stat name and each values			
			for(String stat : statsNames) {
				
				DecimalFormat tenForm = new DecimalFormat("##.###"); 
				
				output += "{\"name\": \""+ stat + "\",";
				output += "\"data\": [";
				for(Algo algo : algorithms) {
					double value = algo.get(stat) * 100;
					output += "" + ((value == 0.0)? "00.000" : tenForm.format(value)) + ",";
				}
				output = output.substring(0, output.length() - 1);
				output += "]},";
			}
			output = output.substring(0, output.length() - 1);
			output += "]";
			
		}
		
		return "{" + output + "}";
	}

}
