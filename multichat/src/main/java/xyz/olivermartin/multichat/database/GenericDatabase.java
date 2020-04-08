package xyz.olivermartin.multichat.database;

import java.sql.ResultSet;
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

	protected abstract boolean setupDatabase(String url) throws SQLException;

	protected abstract void disconnect() throws SQLException;

	protected abstract boolean connect() throws SQLException;

	public abstract ResultSet safeQuery(String sqlTemplate, String... stringParameters) throws SQLException;

	public abstract void safeUpdate(String sqlTemplate, String... stringParameters) throws SQLException;

	public abstract void safeExecute(String sqlTemplate, String... stringParameters) throws SQLException;

	//public abstract ResultSet query(String sql) throws SQLException;

	//public abstract void update(String sql) throws SQLException;

	//public abstract void execute(String sql) throws SQLException;

	public void connectToDatabase() throws SQLException {
		this.ready = connect();
	}

	public void disconnectFromDatabase() throws SQLException {
		disconnect();
		this.ready = false;
	}

}
