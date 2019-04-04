package xyz.olivermartin.multichat.spigotbridge.database;

public class DatabaseManager {

	private static DatabaseManager instance;
	
	public static DatabaseManager getInstance() {
		return instance;
	}
	
	static {
		instance = new DatabaseManager();
	}
	
	/* END STATIC */
	
	private DatabaseManager() {
		/* EMPTY */
	}
	
}
