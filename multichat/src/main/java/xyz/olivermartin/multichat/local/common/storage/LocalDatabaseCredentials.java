package xyz.olivermartin.multichat.local.common.storage;

import java.util.List;

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
	private List<String> flags;

	public void updateValues(String url, String database, String user, String password, List<String> flags) {
		this.url = url;
		this.database = database;
		this.user = user;
		this.password = password;
		this.flags = flags;
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

	public List<String> getFlags() {
		return this.flags;
	}

}
