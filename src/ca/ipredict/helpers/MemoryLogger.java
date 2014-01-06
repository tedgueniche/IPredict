package ca.ipredict.helpers;
import java.util.ArrayList;
import java.util.List;


public class MemoryLogger {

	
	public static List<Integer> mMemoryUsage = new ArrayList<Integer>(); //used for memory usage
	
	
	
	public MemoryLogger() {
		// should not be instantiated 
	}
	
	public static void reset() {
		mMemoryUsage.clear();
	}
	
	public static void addUpdate() {
		mMemoryUsage.add(getUsedMemory());
	}
	
	public static int getUsedMemory() {
	
		int mb = 1024*1024; // 1mb  = 1024 * 1024 bytes
        Runtime runtime = Runtime.getRuntime();
        
        int usage = (int)( runtime.totalMemory() - runtime.freeMemory() ) / mb;
        		
        return usage;
	}
	
	public static int getMaxMemory() {
		int mb = 1024*1024; // 1mb  = 1024 * 1024 bytes
        Runtime runtime = Runtime.getRuntime();
        
        int usage = (int)( runtime.totalMemory() ) / mb;
        		
        return usage;
	}

	public static void displayUsage() {
		
		int max = 0;
		String output = "Memory history: ";
		for(int i: mMemoryUsage) {
			output += i + " "; 
			if(i > max)
				max = i;
		}
		System.out.println(output);
		System.out.println("Max memory used: " + max + "mb");
	}
	
}
