package xyz.olivermartin.multichat.common.database;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySQLPooledDatabase extends GenericDatabase {

	private static final String URL_PREFIX = "jdbc:mysql:";

	private HikariDataSource ds;
	private HikariConfig config;
	private int poolSize;

	public MySQLPooledDatabase(String url, String databaseName, String username, String password, int poolSize) throws SQLException {
		super(URL_PREFIX + "//" + url + "/" + databaseName, username, password);
		this.poolSize = poolSize;
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

		config = new HikariConfig();
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		config.setMaximumPoolSize(poolSize);
		ds = new HikariDataSource(config);
		Connection conn = ds.getConnection();
		conn.close();
		return true;

	}

	@Override
	public SimpleConnection getConnection() throws SQLException {
		return new SimpleConnection(ds.getConnection());
	}

}
