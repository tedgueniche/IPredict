package ca.ipredict.helpers;

import java.util.ArrayList;
import java.util.List;

public class Algo {
	
	//a algo as a list of step or only 1 step
	//each step has a bunch of stats and their value
	

	public String name;
	
	private boolean useSteps;
	
	//List of steps (results)
	public List<Result> steps;
	public int currentStep;
	
	//Main result
	public Result result;
	
	
	public Algo(String name, boolean useSteps){
		this.useSteps = useSteps;
		this.name = name;
		if(useSteps) {
			steps = new ArrayList<Result>();
			currentStep = -1;
		}
		else {
			result = new Result();
		}
	}
	
	public boolean useSteps() {
		return useSteps;
	}
	
	
	public void addStep() {
		if(useSteps()) {
			currentStep++;
			if((steps.size() - 1) < currentStep) {
				steps.add(new Result());
			}
		}
	}
	
	public void set(String stat, Double value) {
		
		if(useSteps()) {
			steps.get(currentStep).set(stat, value);
		}
		else {
			result.set(stat, value);
		}
	}
	
	public double get(String stat) {
		if(useSteps()) {
			return steps.get(currentStep).get(stat);
		}
		else {
			return result.get(stat);
		}
	}
	
	public double get(int step, String stat) {
		if(useSteps()) {
			return steps.get(step).get(stat);
		}
		else {
			return result.get(stat);
		}
	}
	
	
	
	


}
