package xyz.olivermartin.multichat.common.database;

import java.sql.SQLException;

public abstract class GenericDatabase {

	protected String url;
	private boolean ready;

	protected String username;
	protected String password;

	public GenericDatabase(String url) throws SQLException {
		this.url = url;
		ready = setupDatabase(url);
	}

	public GenericDatabase(String url, String user, String pass) throws SQLException {
		this.url = url;
		this.username = user;
		this.password = pass;
		ready = setupDatabase(url);
	}

	public String getURL() {
		return this.url;
	}

	public boolean isReady() {
		return this.ready;
	}

	public abstract SimpleConnection getConnection() throws SQLException;

	protected abstract boolean setupDatabase(String url) throws SQLException;

	protected abstract void disconnect() throws SQLException;

	protected abstract boolean connect() throws SQLException;

	public void reconnectToDatabase() throws SQLException {
		this.ready = connect();
	}

	public void disconnectFromDatabase() throws SQLException {
		disconnect();
		this.ready = false;
	}

}
