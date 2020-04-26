package xyz.olivermartin.multichat.common.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SimpleConnection {

	public static void safelyCloseAll(SimpleConnection conn) {
		if (conn != null) conn.closeAll();
	}

	private Connection connection;
	private PreparedStatement ps;
	private ResultSet rs;

	public SimpleConnection(Connection connection) {
		this.connection = connection;
	}

	public void closeAll() {
		handleClose(connection, ps, rs);
	}

	protected void handleClose(Connection conn, PreparedStatement ps, ResultSet rs) {
		try {
			if (connection != null) conn.close();
			if (ps != null) ps.close();
			if (rs != null) rs.close();
		} catch (SQLException ignored) { /* EMPTY */ }
	}

	public void closeConnectionOnly() {
		handleClose(connection, null, null);
	}

	public void closeResultSetAndPreparedStatement() {
		handleClose(null, ps, rs);
	}

	public ResultSet safeQuery(String sqlTemplate, String... stringParameters) throws SQLException {

		closeResultSetAndPreparedStatement();

		ps = connection.prepareStatement(sqlTemplate);

		for (int i = 1; i <= stringParameters.length; i++) {
			ps.setString(i, stringParameters[i-1]);
		}

		rs = ps.executeQuery();

		return rs;

	}

	public void safeUpdate(String sqlTemplate, String... stringParameters) throws SQLException {

		closeResultSetAndPreparedStatement();

		ps = connection.prepareStatement(sqlTemplate);

		for (int i = 1; i <= stringParameters.length; i++) {
			ps.setString(i, stringParameters[i-1]);
		}

		ps.executeUpdate();

	}

	public void safeExecute(String sqlTemplate, String... stringParameters) throws SQLException {

		ps = connection.prepareStatement(sqlTemplate);

		for (int i = 1; i <= stringParameters.length; i++) {
			ps.setString(i, stringParameters[i-1]);
		}

		ps.execute();

	}

}
