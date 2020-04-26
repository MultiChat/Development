package xyz.olivermartin.multichat.common.database;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class SQLitePooledDatabase extends GenericPooledDatabase {

	private static final String URL_PREFIX = "jdbc:sqlite:";

	private HikariDataSource ds;
	private HikariConfig config;

	public SQLitePooledDatabase(File path, String filename, int poolSize) throws SQLException {
		super(URL_PREFIX + path + File.separator + filename, poolSize);
	}

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
		config.setMaximumPoolSize(getPoolSize());
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
