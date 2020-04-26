package xyz.olivermartin.multichat.common.database;

import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

public class MySQLPooledDatabase extends GenericDatabase {

	private static final String URL_PREFIX = "jdbc:mysql:";

	private BasicDataSource ds;

	public MySQLPooledDatabase(String url, String databaseName, String username, String password) throws SQLException {
		super(URL_PREFIX + "//" + url + "/" + databaseName, username, password);
	}

	@Override
	protected boolean setupDatabase(String url) throws SQLException {
		connect();
		return true;
	}

	@Override
	protected void disconnect() throws SQLException {
		if (ds != null) {
			ds.close();
		}
	}

	@Override
	protected boolean connect() throws SQLException {

		ds = new BasicDataSource();
		ds.setUrl(url);
		ds.setUsername(username);
		ds.setPassword(password);
		ds.setMinIdle(5);
		ds.setMaxIdle(10);
		ds.setMaxOpenPreparedStatements(100);
		ds.getConnection();
		return true;

	}

	@Override
	public SimpleConnection getConnection() throws SQLException {
		return new SimpleConnection(ds.getConnection());
	}

}
