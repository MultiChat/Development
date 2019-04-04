package xyz.olivermartin.multichat.spigotbridge.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabase extends GenericDatabase {

	private static final String URL_PREFIX = "jdbc:sqlite:";
	private Connection conn;

	public SQLiteDatabase(File path, String filename) {
		super(URL_PREFIX + path + File.separator + filename);
	}

	protected boolean setupDatabase(String url) {

		try (Connection conn = DriverManager.getConnection(url)) {

			if (conn != null) {
				DatabaseMetaData meta = conn.getMetaData();
				System.out.println("The driver name is " + meta.getDriverName());
				System.out.println("A new database has been created.");

				this.conn = conn;

				// Database successfully created
				return true;
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		// Database not successfully created
		return false;

	}

	@Override
	protected void disconnect() {

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
	protected boolean connect() {

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

}
