package edu.upenn.cis455.storage;
import java.io.File;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;


public class BerkeleyDB {	
	
	public static BerkeleyDB getDBObject(String directory){
		if(db == null) {
			db = new BerkeleyDB(directory);
		}
		return db;		
	}

	// this constructor initializes the environment and the directory.
	private BerkeleyDB(String directory) throws DatabaseException{
		// Create the directory in which this store will live.
		String currDir = System.getProperty("user.dir");
		File dir = new File(currDir, directory);
		boolean success = dir.mkdirs();
		if (success) {
			System.out.println("Created the berkeleydb directory.");
		}
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		environment = new Environment(dir,  envConfig);
	}
	
	public Environment getEnv(){
		return environment;
	}
	// Close the environment
	public void close() {
		if (environment != null) {
			try {
				environment.close();
			} catch(DatabaseException dbe) {
				System.err.println("Error closing environment" + dbe.toString());
			}
		}
	}
	
	private Environment environment;
	private static BerkeleyDB db = null;
}
