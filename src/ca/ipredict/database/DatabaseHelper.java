package ca.ipredict.database;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;

import ca.ipredict.predictor.profile.Profile;

public class DatabaseHelper {

	
	//Data sets
	public static enum Format{BMS, KOSARAK, FIFA, MSNBC, SIGN, CANADARM1, CANADARM2, SNAKE, BIBLE_CHAR, BIBLE_WORD, KORAN_WORD, LEVIATHAN_WORD}; 
	
	//Database
	private SequenceDatabase _database;

	/**
	 * Main constructor, instantiate an empty database
	 */
	public DatabaseHelper() {
		_database = new SequenceDatabase();
	}
	
	/**
	 * Return an instance of the database
	 * @return
	 */
	public SequenceDatabase getDatabase() {
		return _database;
	}
	
	/**
	 * Load the dataset used for the training and the testing
	 * This method must be called before starting the controller
	 * @param format 1: BMS , 2: Kosarak, 3: FIFA
	 * @param showDatasetStats show statistics about the dataset
	 * @param count Max number of sequence to get
	 */
	public void loadDataset(Format format, int maxCount, boolean showDatasetStats) {
		
		//Creating or resetting the database
		if(_database == null) {
			_database = new SequenceDatabase();
		}
		else 
			_database.clear();
		
		//Loading the specified dataset (according to the format)
		try {
			switch(format) {
			case BMS:
				_database.loadFileBMSFormat(fileToPath("BMS.dat"), maxCount, Profile.sequenceMinSize, Profile.sequenceMaxSize);
				break;
			case KOSARAK:
				_database.loadFileKosarakFormat(fileToPath("kosarak.dat"), maxCount, Profile.sequenceMinSize, Profile.sequenceMaxSize);
				break;
			case FIFA:
				_database.loadFileFIFAFormat(fileToPath("FIFA.dat"), maxCount, Profile.sequenceMinSize, Profile.sequenceMaxSize);
				break;
			case MSNBC:
				_database.loadFileMsnbsFormat(fileToPath("msnbc.seq"), maxCount, Profile.sequenceMinSize, Profile.sequenceMaxSize);
				break;
			case SIGN:
				_database.loadFileSignLanguage(fileToPath("sign_language.txt"), maxCount, Profile.sequenceMinSize, Profile.sequenceMaxSize);
				break;
			case CANADARM1:
				_database.loadFileSPMFFormat(fileToPath("Canadarm1_actions.txt"), maxCount, Profile.sequenceMinSize, Profile.sequenceMaxSize);
				break;
			case CANADARM2:
				_database.loadFileSPMFFormat(fileToPath("Canadarm2_states.txt"), maxCount, Profile.sequenceMinSize, Profile.sequenceMaxSize);
				break;
			case SNAKE:
				_database.loadSnakeDataset(fileToPath("snake.dat"), maxCount, Profile.sequenceMinSize, Profile.sequenceMaxSize);
				break;
			case BIBLE_CHAR:
				_database.loadFileLargeTextFormatAsCharacter(fileToPath("Bible.txt"), maxCount, Profile.sequenceMinSize, Profile.sequenceMaxSize);
				break;
			case BIBLE_WORD:
				_database.loadFileLargeTextFormatAsWords(fileToPath("Bible.txt"), maxCount, Profile.sequenceMinSize, Profile.sequenceMaxSize, true);
				break;
			case KORAN_WORD:
				_database.loadFileLargeTextFormatAsWords(fileToPath("koran.txt"), maxCount, Profile.sequenceMinSize, Profile.sequenceMaxSize, false);
				break;
			case LEVIATHAN_WORD:
				_database.loadFileLargeTextFormatAsWords(fileToPath("leviathan.txt"), maxCount, Profile.sequenceMinSize, Profile.sequenceMaxSize, false);
				break;
			default:
				System.out.println("Could not load dataset, unknown format.");
			}

			if(showDatasetStats){
				System.out.println();
				SequenceStatsGenerator.prinStats(_database, format.name());
			}
			
			Collections.shuffle(_database.getSequences()); //shuffle
		
		} catch (IOException e) {
			System.out.println("Could not load dataset, IOExeption");
			e.printStackTrace();
		}
	}
	
	
	/** 
	 * Return the path for the specified data set file
	 * @param filename Name of the data set file
	 * @throws UnsupportedEncodingException 
	 */
	public static String fileToPath(String filename)
			throws UnsupportedEncodingException {
		String parentPath = new File(SequenceDatabase.class.getResource("SequenceDatabase.class").getPath()).getParent();
		String newPath = parentPath + File.separator + "datasets" + File.separator + filename;
		return java.net.URLDecoder.decode(newPath, "UTF-8");
	}
	
}
