package xyz.olivermartin.multichat.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLDatabase extends GenericDatabase {

	private static final String URL_PREFIX = "jdbc:mysql:";
	private Connection conn;

	public MySQLDatabase(String url, String databaseName, String username, String password) throws SQLException {
		super(URL_PREFIX + "//" + url + "/" + databaseName, username, password);
		//System.out.println(URL_PREFIX + "//" + url + "/" + databaseName);
		//System.out.println(username);
		//System.out.println(password);
	}

	@Override
	protected boolean setupDatabase(String url) throws SQLException {

		Connection conn = DriverManager.getConnection(url, username, password);

		if (conn != null) {
			DatabaseMetaData meta = conn.getMetaData();
			//System.out.println("The driver name is " + meta.getDriverName());
			//System.out.println("A new database has been created.");

			this.conn = conn;

			// Database successfully created

			return true;
		} else {
			return false;
		}

	}

	@Override
	protected void disconnect() throws SQLException {

		if (conn != null) {
			try {
				if (!conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	protected boolean connect() throws SQLException {

		if (conn == null) {

			return setupDatabase(url);

		} else {
			try {
				if (conn.isClosed()) {
					return setupDatabase(url);
				}
			} catch (SQLException e) {
				return false;
			}
		}

		// Already connected
		return true;
	}

	@Override
	public ResultSet query(String sql) throws SQLException {

		ResultSet results = conn.createStatement().executeQuery(sql);
		return results;

	}

	@Override
	public void update(String sql) throws SQLException {

		conn.createStatement().executeUpdate(sql);

	}

	@Override
	public void execute(String sql) throws SQLException {

		conn.createStatement().execute(sql);

	}



}
