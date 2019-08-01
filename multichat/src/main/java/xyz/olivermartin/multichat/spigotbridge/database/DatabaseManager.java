package xyz.olivermartin.multichat.spigotbridge.database;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/*
 * DROP TABLE IF EXISTS nicknames;

CREATE TABLE nicknames(id VARCHAR(128) PRIMARY KEY, u_nick VARCHAR(255), f_nick VARCHAR(255));

INSERT INTO nicknames VALUES ('255-444-2221--11-11', 'revilo', '&4Revilo');

SELECT * FROM nicknames;
 */

public class DatabaseManager {

	private static DatabaseManager instance;

	public static DatabaseManager getInstance() {
		return instance;
	}

	static {
		instance = new DatabaseManager();
	}

	/* END STATIC */

	private File databasePath;
	private DatabaseMode databaseMode = DatabaseMode.SQLite;

	private Map<String, GenericDatabase> databases;

	private DatabaseManager() {
		databases = new HashMap<String, GenericDatabase>();
	}

	////////////
	
	public static void main(String args[]) {
		DatabaseManager.getInstance().setPath(new File("C:\\multichat\\db\\"));
		DatabaseManager.getInstance().createDatabase("testing.db");
	}
	
	////////////
	
	public void setPath(File path) {
		this.databasePath = path;
	}

	public GenericDatabase createDatabase(String name) {
		return createDatabase(name, name);
	}
	
	public boolean isReady() {
		return databasePath != null;
	}

	/**
	 * Generic class to create a sqlite database
	 */
	public GenericDatabase createDatabase(String databaseName, String fileName) {

		if (!isReady()) throw new RuntimeException("MultiChat Database Manager Not Ready!");
		
		if (!databasePath.exists()) {
			databasePath.mkdirs();
		}
		
		switch (databaseMode) {
		case SQLite:
		default:
			// TODO check doesnt already exist
			databases.put(databaseName.toLowerCase(), new SQLiteDatabase(databasePath, fileName));
			return databases.get(databaseName.toLowerCase());
		}

	}

	public Optional<GenericDatabase> getDatabase(String databaseName) {
		if (databases.containsKey(databaseName.toLowerCase())) {
			return Optional.of(databases.get(databaseName.toLowerCase()));
		} else {
			return Optional.empty();
		}
	}

	public void removeDatabase(String databaseName) {
		if (databases.containsKey(databaseName.toLowerCase())) {
			GenericDatabase gdb = databases.get(databaseName.toLowerCase());
			gdb.disconnectFromDatabase();
			databases.remove(databaseName.toLowerCase());
		}
	}

}
