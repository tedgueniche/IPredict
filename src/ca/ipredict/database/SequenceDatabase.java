package ca.ipredict.database;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.ipredict.predictor.profile.Profile;


public class SequenceDatabase {

	private List<Sequence> sequences = new ArrayList<Sequence>();
	
	
	public SequenceDatabase() {
	}
	
	//Setter
	public void setSequences(List<Sequence> newSequences)	{
		this.sequences = new ArrayList<Sequence>(newSequences);
	}
	
	//Getter
	public List<Sequence> getSequences() {
		return sequences;
	}
	
	public int size() {
		return sequences.size();
	}
	
	public void clear() {
		sequences.clear();
	}
	
	
	public void loadFileBMSFormat(String filepath, int maxCount, int minSize, int maxSize) throws IOException {
		String thisLine;
		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(filepath));
			myInput = new BufferedReader(new InputStreamReader(fin));
			int lastId = 0;
			
			int count = 0;
			Sequence sequence = null; //current sequence
			while ((thisLine = myInput.readLine()) != null  && count < maxCount) { //until end of file
				
				String[] split = thisLine.split(" ");
				int id = Integer.parseInt(split[0]);
				int val = Integer.parseInt(split[1]);
				
				if(lastId != id){ //if new sequence
					if(lastId !=0 && sequence.size() >= minSize && sequence.size() <= maxSize ){  //adding last sequence to sequences list
						sequences.add(sequence);
						count++;
					}
					sequence = new Sequence(id); //creating new sequence with current id
					lastId = id;
				}
				Item item = new Item(val); //adding current val to current sequence
				sequence.addItem(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (myInput != null) {
				myInput.close();
			}
		}
		
	}
	
	/**
	 * Load the sequences for a FIFA (World cup) log file
	 * see: http://ita.ee.lbl.gov/html/contrib/WorldCup.html
	 * @param maxSize 
	 */
	public void loadFileFIFAFormat(String filepath, int maxCount, int minSize, int maxSize) throws IOException {
		String thisLine;
		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(filepath));
			myInput = new BufferedReader(new InputStreamReader(fin));
			int i = 0;
			while ((thisLine = myInput.readLine()) != null) {
				// ajoute une séquence
				String[] split = thisLine.split(" ");

				if (maxCount == i) {
					break;
				}
				if(split.length >= minSize && split.length <= maxSize )	{ 
					Sequence sequence = new Sequence(-1);

					Set<Integer> alreadySeen = new HashSet<Integer>();
					int lastValue = -1;
					
					for (String value : split) {
						int intVal = Integer.valueOf(value);
						
						// PHIL08: J'ai ajouté le choix de la méthode
						// pour enlever les duplicats.
						//  2 = tous les duplicats
						//  1 = seulement les duplicats consécutifs 
						if(Profile.removeDuplicatesMethod == 2){
							
							if(alreadySeen.contains(intVal)){
								continue;
							}else{
								alreadySeen.add(intVal);
							}
						}else if(Profile.removeDuplicatesMethod == 1){
							//approach B
							if(lastValue == intVal) {
								continue;
							}
							lastValue = intVal;
						}
						
						Item item = new Item(intVal); //adding current val to current sequence
						sequence.addItem(item);
					}
					i++;
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
	
	
	public void loadFileMsnbsFormat(String filepath, int maxCount, int minSize, int maxSize) throws IOException {
		String thisLine;
		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(filepath));
			myInput = new BufferedReader(new InputStreamReader(fin));
			int i = 0;
			while ((thisLine = myInput.readLine()) != null) {
				Set<Integer> alreadySeen = new HashSet<Integer>();
				String[] split = thisLine.trim().split(" ");
				
				if (maxCount == i) {
					break;
				}
				Sequence sequence = new Sequence(-1);
				int lastValue = 0;
				for (String val : split) {
					int value = Integer.valueOf(val);
					
					// PHIL08: J'ai ajouté le choix de la méthode
					// pour enlever les duplicats.
					//  2 = tous les duplicats
					//  1 = seulement les duplicats consécutifs 
					if(Profile.removeDuplicatesMethod == 2){
						
						if(alreadySeen.contains(value)){
							continue;
						}else{
							alreadySeen.add(value);
						}
					}else if(Profile.removeDuplicatesMethod == 1){
						//approach B
						if(lastValue == value) {
							continue;
						}
						lastValue = value;
					}
					sequence.addItem(new Item(value));
				}
				if(sequence.size() >= minSize && sequence.size() <= maxSize ) {
					sequences.add(sequence);
					i++;
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
	
	
	public void loadFileKosarakFormat(String filepath, int maxCount, int minSize, int maxSize) throws IOException {
		String thisLine;
		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(filepath));
			myInput = new BufferedReader(new InputStreamReader(fin));
			int i = 0;
			while ((thisLine = myInput.readLine()) != null) {
				// ajoute une séquence
				String[] split = thisLine.split(" ");

				if (maxCount == i) {
					break;
				}
				if(split.length >= minSize && split.length <= maxSize )	{ 
					Sequence sequence = new Sequence(-1);
					for (String value : split) {
						Item item = new Item(Integer.valueOf(value)); //adding current val to current sequence
						sequence.addItem(item);
					}
					i++;
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
	
	public void loadFileLargeTextFormatAsCharacter(String filepath, int maxCount, int minSize, int maxSize) throws IOException {
		String thisLine;
		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(filepath));
			myInput = new BufferedReader(new InputStreamReader(fin));
			int i = 0;
			while ((thisLine = myInput.readLine()) != null) {
				if (maxCount == i) {
					break;
				}
				
				if(thisLine.length() >= minSize && thisLine.length() <= maxSize )	{ 
					
					Sequence sequence = new Sequence(-1);
					
					for(int k = 0 ; k < thisLine.length(); k++) {
						int value = thisLine.charAt(k);
						sequence.addItem(new Item(value));
					}
					
					i++;
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
	
	// PHIL08 -  J'ai ajouté un booléen pour indiquer si la fin d'une ligne
	// indique la fin d'une phrase ou non.  Pour certains datasets comme
	// le roman "leviathan" ou le coran, il est mieux de le mettre à "false" alors
	// que pour la bible, il est préférable d'utiliser "true".
	public void loadFileLargeTextFormatAsWords(String filepath, int maxCount, int minSize, int maxSize, boolean doNotAllowSentenceToContinueOnNextLine) throws IOException {
		String thisLine;
		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(filepath));
			myInput = new BufferedReader(new InputStreamReader(fin));
			// variable to count the number of sequences found until now
			int seqCount = 0;
			// variable to remember the last assigned item ID.
			int lastWordID = 1;
			
			// map to store the mapping between word (key) to item ID (value)
			Map<String, Integer> mapWordToID = new HashMap<String, Integer>();
			
			// the current sequence
			Sequence sequence = new Sequence(-1);
			// for each line
			while ((thisLine = myInput.readLine()) != null) {
				// if we have found enough sequences, stop
				if (maxCount == seqCount) {
					break;
				}
				
				// filter unwanted characters  (integers, [], #, 0,1,2,..)
				StringBuffer modifiedLine = new StringBuffer(thisLine.length());
				for(int i=0; i < thisLine.length(); i++){
					char currentChar = thisLine.charAt(i);
					if(Character.isLetter(currentChar) ||  currentChar == '.'
							||  currentChar == '?' ||  currentChar == ':'  ||  currentChar == ' '){
						modifiedLine.append(currentChar);
					}
				}
				
				// split the string into tokens
				String split [] = modifiedLine.toString().split(" ");
				for(int i=0; i < split.length; i++){
					String token = split[i];
					// if the current token contains a end of sentence character (.  ?  or :) 
					// of if it is the last word of the line,
					// we consider this as the end of the sequence.
					boolean containsPunctuation = token.contains(".") || token.contains("?") || token.contains(":");
					
					if(containsPunctuation){
						seqCount++;
						
						// if there is a punctuation character in this word, remove it
						
						// IMPORTANT:  ADD THIS CONDITION:   || i == split.length -1
						// IF YOU WANT TO ALLOW SENTENCES TO CONTINUE ON THE NEXT LINE
						if(containsPunctuation || i == split.length -1 && doNotAllowSentenceToContinueOnNextLine){ // 
							token = token.substring(0, token.length()-1);
						}
						// get the itemID
						Integer itemID = mapWordToID.get(token);
						if(itemID == null){
							itemID = lastWordID++;
							mapWordToID.put(token, itemID);
						}
						
						// add the item to the sequence
						sequence.addItem(new Item(itemID));
						// add the sequence to the set of sequences, if the size is ok.
						if(sequence.size() >= minSize && sequence.size() <= maxSize )	{ 
							sequences.add(sequence);
						}
						// create a new sequence
						sequence = new Sequence(-1);
					}
					else{
						// if it is not the last word of a sentence
						Integer itemID = mapWordToID.get(token);
						if(itemID == null){
							itemID = lastWordID++;
							mapWordToID.put(token, itemID);
						}
						sequence.addItem(new Item(itemID));
					}
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
	
	public void loadFileSignLanguage(String fileToPath, int maxCount, int minsize, int maxsize) {
		String thisLine;
		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(fileToPath));
			myInput = new BufferedReader(new InputStreamReader(fin));
			String oldUtterance = "-1";
			Sequence sequence = null;
			int count = 0;
			
			HashSet<Integer> alreadySeen = new HashSet<Integer>();
			int id =0;
			int lastValue = -1;
			while ((thisLine = myInput.readLine()) != null) {
				if(thisLine.length() >= 1 && thisLine.charAt(0) != '#'){
					String []tokens = thisLine.split(" ");
					String currentUtterance = tokens[0];
					if(!currentUtterance.equals(oldUtterance)){
						if(sequence != null){
							if(sequence.size() >= minsize &&
									sequence.size() <= maxsize){
										sequences.add(sequence);
										count++;
								}
						}
						sequence = new Sequence(id++);
						alreadySeen = new HashSet<Integer>();
						oldUtterance = currentUtterance;
					}
					for(int j=1; j< tokens.length; j++){
						int character = Integer.parseInt(tokens[j]);
						if(character == -11 || character == -12){
							continue;
						}

						// PHIL08: J'ai ajouté le choix de la méthode
						// pour enlever les duplicats.
						//  2 = tous les duplicats
						//  1 = seulement les duplicats consécutifs 
						if(Profile.removeDuplicatesMethod == 2){
							if(alreadySeen.contains(character)){
								continue;
							}
							alreadySeen.add(character);
						}else if(Profile.removeDuplicatesMethod == 1){
							if(lastValue == character){
								continue;
							}
							lastValue = character;
						}
						sequence.getItems().add(new Item(character));
					}
				}
				if (maxCount == count) {
					break;
				}
			}
			if(sequence.size() >= minsize &&
				sequence.size() <= maxsize){
					sequences.add(sequence);
			}
			
			System.out.println(sequence.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void loadFileSPMFFormat(String path, int maxCount,
			int minSize, int maxSize) {
		
		String thisLine;
		BufferedReader myInput = null;
		try {
			int count = 0;
			FileInputStream fin = new FileInputStream(new File(path));
			myInput = new BufferedReader(new InputStreamReader(fin));
			Set<Integer> alreadySeen = new HashSet<Integer>();
			while ((thisLine = myInput.readLine()) != null && count < maxCount) {
				Sequence sequence = new Sequence(sequences.size());
				for (String entier : thisLine.split(" ")) {
					if (entier.equals("-1")) { // séparateur d'itemsets
						
					} else if (entier.equals("-2")) { // indicateur de fin de séquence
						if(sequence.size()>= minSize &&
							sequence.size() <= maxSize){
							sequences.add(sequence);
							count++;
						}
					} else { 
						int val = Integer.parseInt(entier);
						if(alreadySeen.contains(val)){
							continue;
						}
						alreadySeen.add(val);
						sequence.getItems().add(new Item(val));
					}
				}
			}
			
			if (myInput != null) {
				myInput.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void loadSnakeDataset(String filepath, int nbLine, 
			int minSize, int maxSize) {
		String thisLine;
		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(filepath));
			myInput = new BufferedReader(new InputStreamReader(fin));
			while ((thisLine = myInput.readLine()) != null) {
				if(thisLine.length() >= 50){
					Sequence sequence = new Sequence(sequences.size());
					for(int i=0; i< thisLine.length(); i++){
						int character = thisLine.toCharArray()[i ] - 65;
//						System.out.println(thisLine.toCharArray()[i ] + " " + character);

						sequence.addItem(new Item(character));
					}
					if(sequence.size()>= minSize &&
							sequence.size() <= maxSize){
							sequences.add(sequence);
					}
					sequences.add(sequence);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
