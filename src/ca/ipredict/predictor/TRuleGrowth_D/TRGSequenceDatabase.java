package ca.ipredict.predictor.TRuleGrowth_D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



/**
 * Implementation of a sequence database. Each sequence should have a unique id.
 * See examples in /test/ directory for the format of input files.
 * 
 * @author Philippe Fournier-Viger
 **/
public class TRGSequenceDatabase {
	
	int seqid = 0;

	// Contexte
	private List<TRGSequence> sequences = new ArrayList<TRGSequence>();
	
	
	public void setSequences(List<TRGSequence> newSequences)	{
		
		this.sequences = new ArrayList<TRGSequence>(newSequences);
	}
	
	
	/** This method split a List<Sequence> in two using an absolute ratio
	 */
	/*
	private List<Sequence> splitList(double absoluteRatio)	{
		int relativeRatio = (int) (sequences.size() * absoluteRatio);
		List<Sequence> tmp = sequences.subList(relativeRatio , sequences.size());
		List<Sequence> toReturn = new ArrayList<Sequence>(tmp);
		tmp.clear();
		
		return toReturn;
	}
	*/
	
	public void loadFile(String path) throws IOException {
		String thisLine;
		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(path));
			myInput = new BufferedReader(new InputStreamReader(fin));
			while ((thisLine = myInput.readLine()) != null) {
				// si la ligne n'est pas un commentaire
				if (thisLine.charAt(0) != '#') {
					// ajoute une séquence
					addSequence(thisLine.split(" "));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (myInput != null) {
				myInput.close();
			}
		}
	}
	
	public void loadFileSignLanguage(String fileToPath, int i) {
		String thisLine;
		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(fileToPath));
			myInput = new BufferedReader(new InputStreamReader(fin));
			String oldUtterance = "-1";
			TRGSequence sequence = null;
			while ((thisLine = myInput.readLine()) != null) {
				if(thisLine.length() >= 1 && thisLine.charAt(0) != '#'){
					String []tokens = thisLine.split(" ");
					String currentUtterance = tokens[0];
					if(!currentUtterance.equals(oldUtterance)){
						if(sequence != null){
							sequences.add(sequence);
						}
						sequence = new TRGSequence(seqid++);
						oldUtterance = currentUtterance;
					}
					for(int j=1; j< tokens.length; j++){
						int character = Integer.parseInt(tokens[j]);
						if(character == -11 || character == -12){
							continue;
						}
//						if(character >= maxItem){
//							maxItem = character;
//						}
//						if(character < minItem){
//							minItem = character;
//						}
						sequence.addItemset(new TRGItemset(character));
					}
				}
			}
			sequences.add(sequence);
			System.out.println(sequence.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public void addSequence(String[] entiers) { //
		TRGSequence sequence = new TRGSequence(sequences.size());
		TRGItemset itemset = new TRGItemset();
		for (String entier : entiers) {
			if (entier.codePointAt(0) == '<') { // Timestamp
//				String valeur = entier.substring(1, entier.length() - 1);
			} else if (entier.equals("-1")) { // séparateur d'itemsets
				sequence.addItemset(itemset);
				itemset = new TRGItemset();
			} else if (entier.equals("-2")) { // indicateur de fin de séquence
				sequences.add(sequence);
			} else { // un item au format : id(valeurentiere) ou format : id
				// si l'item à une valeur entière, extraire la valeur
				// extraire la valeur associée à un item
				itemset.addItem(Integer.parseInt(entier));
			}
		}
	}

	public void addSequence(TRGSequence sequence) {
		sequences.add(sequence);
	}

	public void printContext() {
		System.out.println("============  CONTEXTE ==========");
		for (TRGSequence sequence : sequences) { // pour chaque objet
			System.out.print(sequence.getId() + ":  ");
			sequence.print();
			System.out.println("");
		}
	}
	
	public void printDatabaseStats() {
		System.out.println("============  STATS ==========");
		System.out.println("Number of sequences : " + sequences.size());
		// average size of sequence
		long size = 0;
		for(TRGSequence sequence : sequences){
			size += sequence.size();
		}
		double meansize = ((float)size) / ((float)sequences.size());
		System.out.println("mean size" + meansize);
	}

	public String toString() {
		StringBuffer r = new StringBuffer();
		for (TRGSequence sequence : sequences) { // pour chaque objet
			r.append(sequence.getId());
			r.append(":  ");
			r.append(sequence.toString());
			r.append('\n');
		}
		return r.toString();
	}

	public int size() {
		return sequences.size();
	}

	public List<TRGSequence> getSequences() {
		return sequences;
	}

	public Set<Integer> getSequenceIDs() {
		Set<Integer> ensemble = new HashSet<Integer>();
		for (TRGSequence sequence : getSequences()) {
			ensemble.add(sequence.getId());
		}
		return ensemble;
	}


	public void loadFileKosarakFormat(String filepath, int nblinetoread)
	throws IOException {
String thisLine;
BufferedReader myInput = null;
try {
	FileInputStream fin = new FileInputStream(new File(filepath));
	myInput = new BufferedReader(new InputStreamReader(fin));
	int i = 0;
	while ((thisLine = myInput.readLine()) != null) {
		// ajoute une séquence
		String[] split = thisLine.split(" ");
		i++;
		if (nblinetoread == i) {
			break;
		}
		if(split.length >= 5)	{ //min size restriction
			TRGSequence sequence = new TRGSequence(seqid++);
			for (String value : split) {
				TRGItemset itemset = new TRGItemset();
				itemset.addItem(Integer.parseInt(value));
				sequence.addItemset(itemset);
			}
			sequences.add(sequence);
		}
		
	}
} catch (Exception e) {
	e.printStackTrace();
} finally {
	if (myInput != null) {
		myInput.close();
	}
}
}


	public void loadFileBMSFormat(String filepath) {
		String thisLine;
		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(filepath));
			myInput = new BufferedReader(new InputStreamReader(fin));
			int realID = 0;
			int lastId = 0;
			TRGSequence sequence = null; //current sequence
			while ((thisLine = myInput.readLine()) != null) { //until end of file
				// ajoute une séquence
				String[] split = thisLine.split(" ");
				int id = Integer.parseInt(split[0]);
				int val = Integer.parseInt(split[1]);
				
				if(lastId != id){ //if new sequence
					if(lastId!=0 && sequence.size() >= 5){  //adding last sequence to sequences list
						sequences.add(sequence);
						realID++;
					}
					sequence = new TRGSequence(realID); //creating new sequence with current id
					lastId = id;
				}
				TRGItemset itemset = new TRGItemset(); //adding current val to current sequence
				itemset.addItem(val);
				sequence.addItemset(itemset);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void loadSnakeDataset(String filepath, int nbLine) {
		String thisLine;
		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(filepath));
			myInput = new BufferedReader(new InputStreamReader(fin));
			int realID = 0;
			while ((thisLine = myInput.readLine()) != null) { 
				if(thisLine.length() >= 50){
					TRGSequence sequence = new TRGSequence(realID++);
					for(int i=0; i< thisLine.length(); i++){
						TRGItemset itemset = new TRGItemset();
						int character = thisLine.toCharArray()[i] - 65;
//						System.out.println(thisLine.toCharArray()[i] + " " + character);
						itemset.addItem(character);
						sequence.addItemset(itemset);
					}
					sequences.add(sequence);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	

}
