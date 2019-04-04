package xyz.olivermartin.multichat.spigotbridge.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DatabaseManager {

	private static DatabaseManager instance;

	public static DatabaseManager getInstance() {
		return instance;
	}

	static {
		instance = new DatabaseManager();
	}

	/* END STATIC */

	private String databasePath = "C:/multichat/db/";
	private DatabaseMode databaseMode = DatabaseMode.SQLite;

	private Map<String, GenericDatabase> databases;

	private DatabaseManager() {
		databases = new HashMap<String, GenericDatabase>();
	}

	public static void main(String args[]) {
		DatabaseManager.getInstance().createDatabase("testing.db");
	}

	public GenericDatabase createDatabase(String name) {
		return createDatabase(name, name);
	}

	/**
	 * Generic class to create a sqlite database
	 */
	public GenericDatabase createDatabase(String databaseName, String fileName) {

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
