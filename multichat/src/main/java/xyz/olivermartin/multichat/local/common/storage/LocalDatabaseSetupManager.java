package xyz.olivermartin.multichat.local.common.storage;

import java.sql.SQLException;

import xyz.olivermartin.multichat.common.database.DatabaseManager;
import xyz.olivermartin.multichat.common.database.DatabaseMode;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;

public class LocalDatabaseSetupManager {

	private String databaseName;
	private boolean useMySQL;
	private boolean connected;

	public LocalDatabaseSetupManager(MultiChatLocalPlatform platform, boolean useMySQL) {

		if (platform == MultiChatLocalPlatform.SPIGOT) {
			databaseName = "multichatspigot.db";
		} else {
			databaseName = "multichatsponge.db";
		}

		this.useMySQL = useMySQL;

		connected = setupDatabase();

	}

	public boolean isConnected() {
		return connected;
	}

	private boolean setupDatabase() {

		try {

			if (useMySQL) {

				// MYSQL SETTINGS

				DatabaseManager.getInstance().setMode(DatabaseMode.MySQL);

				DatabaseManager.getInstance().setURLMySQL(LocalDatabaseCredentials.getInstance().getURL());
				DatabaseManager.getInstance().setUsernameMySQL(LocalDatabaseCredentials.getInstance().getUser());
				DatabaseManager.getInstance().setPasswordMySQL(LocalDatabaseCredentials.getInstance().getPassword());

				DatabaseManager.getInstance().createDatabase(databaseName, LocalDatabaseCredentials.getInstance().getDatabase());

			} else {

				// SQLITE SETTINGS

				DatabaseManager.getInstance().setMode(DatabaseMode.MySQL);
				DatabaseManager.getInstance().setPathSQLite(MultiChatLocal.getInstance().getConfigDirectory());

				DatabaseManager.getInstance().createDatabase(databaseName);

			}

			DatabaseManager.getInstance().getDatabase(databaseName).get().connectToDatabase();
			DatabaseManager.getInstance().getDatabase(databaseName).get().safeUpdate("CREATE TABLE IF NOT EXISTS name_data(id VARCHAR(128), f_name VARCHAR(255), u_name VARCHAR(255), PRIMARY KEY (id));");
			DatabaseManager.getInstance().getDatabase(databaseName).get().safeUpdate("CREATE TABLE IF NOT EXISTS nick_data(id VARCHAR(128), u_nick VARCHAR(255), f_nick VARCHAR(255), PRIMARY KEY (id));");

			return true;

		} catch (SQLException e) {

			return false;

		}

	}

}
