package xyz.olivermartin.multichat.local.common.storage;

public class LocalDatabaseCredentials {
	
	private static LocalDatabaseCredentials instance;
	
	static {
		instance = new LocalDatabaseCredentials();
	}
	
	public static LocalDatabaseCredentials getInstance() {
		return instance;
	}
	
	// END STATIC
	
	private LocalDatabaseCredentials() {
		/* EMPTY */
	}

	private String url;
	private String database;
	private String user;
	private String password;

	public void updateValues(String url, String database, String user, String password) {
		this.url = url;
		this.database = database;
		this.user = user;
		this.password = password;
	}

	public String getURL() {
		return this.url;
	}

	public String getDatabase() {
		return this.database;
	}

	public String getUser() {
		return this.user;
	}

	public String getPassword() {
		return this.password;
	}

}
