package xyz.olivermartin.multichat.common.database;

import java.sql.SQLException;

public abstract class GenericPooledDatabase {

	protected String url;
	private boolean ready;

	protected String username;
	protected String password;

	private int poolSize;

	public GenericPooledDatabase(String url, int poolSize) throws SQLException {
		this.url = url;
		this.poolSize = poolSize;
		ready = setupDatabase(url);
	}

	public GenericPooledDatabase(String url, String user, String pass, int poolSize) throws SQLException {
		this.url = url;
		this.poolSize = poolSize;
		this.username = user;
		this.password = pass;
		ready = setupDatabase(url);
	}

	public String getURL() {
		return this.url;
	}

	public int getPoolSize() {
		return this.poolSize;
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
