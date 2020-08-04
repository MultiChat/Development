package xyz.olivermartin.multichat.local.common.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.common.database.DatabaseManager;
import xyz.olivermartin.multichat.common.database.GenericPooledDatabase;
import xyz.olivermartin.multichat.common.database.SimpleConnection;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;

/**
 * MultiChatLocal's Name Manager using a SQL backend
 * 
 * <p>Manages players' names, nicknames, uuids etc.</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class LocalSQLNameManager extends LocalNameManager {

	private boolean connected;
	private GenericPooledDatabase localDatabase;

	public LocalSQLNameManager(String databaseName) {

		super(LocalNameManagerMode.SQL);
		connected = getDatabase(databaseName);
		MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Connection Staus: " + connected);

	}

	private boolean getDatabase(String databaseName) {

		Optional<GenericPooledDatabase> ogdb = DatabaseManager.getInstance().getDatabase(databaseName);
		if (ogdb.isPresent()) {
			localDatabase = ogdb.get();
			return true;
		} else {
			return false;
		}

	}

	@Override
	public String getCurrentName(UUID uuid, boolean withPrefix) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		SimpleConnection conn = null;
		String name;

		try {

			conn = localDatabase.getConnection();

			ResultSet results = conn.safeQuery(
					"SELECT f_name, f_nick FROM name_data LEFT JOIN nick_data ON name_data.id = nick_data.id WHERE name_data.id = ?;"
					, uuid.toString());
			results.next();

			if (results.getString("f_nick") == null) {
				name = results.getString("f_name");
			} else {
				name = results.getString("f_nick");
				if (MultiChatLocal.getInstance().getConfigManager().getLocalConfig().isShowNicknamePrefix() && withPrefix) {
					name = MultiChatLocal.getInstance().getConfigManager().getLocalConfig().getNicknamePrefix() + name;
				}
			}

			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] CurrentName = " + name);

		} catch (SQLException e) {
			e.printStackTrace();
			name = "";
		} finally {
			SimpleConnection.safelyCloseAll(conn);
		}

		return name;

	}

	@Override
	public String getName(UUID uuid) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		SimpleConnection conn = null;
		String name;

		try {

			conn = localDatabase.getConnection();

			ResultSet results = conn.safeQuery("SELECT f_name FROM name_data WHERE id = ?;", uuid.toString());
			results.next();
			name = results.getString("f_name");

			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Name = " + name);

		} catch (SQLException e) {
			e.printStackTrace();
			name = "";
		} finally {
			SimpleConnection.safelyCloseAll(conn);
		}

		return name;

	}

	@Override
	protected Optional<UUID> getUUIDFromUnformattedNickname(String nickname) {

		nickname = nickname.toLowerCase();

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		Optional<UUID> opId;
		SimpleConnection conn = null;

		try {

			conn = localDatabase.getConnection();

			ResultSet results = conn.safeQuery("SELECT id FROM nick_data WHERE u_nick = ?;", nickname);

			if (results.next()) {
				UUID id = UUID.fromString(results.getString("id"));
				opId = Optional.of(id);
			} else {
				opId = Optional.empty();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			opId = Optional.empty();
		} finally {
			SimpleConnection.safelyCloseAll(conn);
		}

		return opId;

	}

	@Override
	public Optional<UUID> getUUIDFromName(String username) {

		username = username.toLowerCase();

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		SimpleConnection conn = null;
		Optional<UUID> opId;

		try {

			conn = localDatabase.getConnection();

			ResultSet results = conn.safeQuery("SELECT id FROM name_data WHERE u_name = ?;", username);

			if (results.next() ) {
				UUID id = UUID.fromString(results.getString("id"));
				opId = Optional.of(id);
			} else {
				opId = Optional.empty();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			opId = Optional.empty();
		} finally {
			SimpleConnection.safelyCloseAll(conn);
		}

		return opId;

	}

	@Override
	public void registerPlayer(UUID uuid, String username) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Registering Player:" + uuid.toString() + ", " + username);

		String oldUsername;

		if (existsUUID(uuid)) {

			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] UUID already exists in database...");

			oldUsername = getName(uuid);

			if (!oldUsername.equalsIgnoreCase(username)) {

				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Player has a new username (" + username + "), was previously (" + oldUsername + ")");

				SimpleConnection conn = null;

				try {

					conn = localDatabase.getConnection();

					MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Updating database...");
					conn.safeUpdate("UPDATE name_data SET u_name = ?, f_name = ? WHERE id = ?;"
							, username.toLowerCase(), username, uuid.toString());
					MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Database updated!");

				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					SimpleConnection.safelyCloseAll(conn);
				}

			}

		} else {

			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] UUID does not already exist in database...");

			SimpleConnection conn = null;

			try {

				conn = localDatabase.getConnection();

				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Updating database...");
				conn.safeUpdate("INSERT INTO name_data VALUES (?, ?, ?);", uuid.toString(), username, username.toLowerCase());
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Database updated!");

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				SimpleConnection.safelyCloseAll(conn);
			}

		}

		online.add(uuid);

	}

	public void testRegisterFakePlayer(UUID uuid, String username) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		String oldUsername;

		if (existsUUID(uuid)) {

			oldUsername = getName(uuid);

			if (!oldUsername.equalsIgnoreCase(username)) {

				SimpleConnection conn = null;

				try {

					conn = localDatabase.getConnection();
					conn.safeUpdate("UPDATE name_data SET u_name = ?, f_name = ? WHERE id = ?;"
							, username.toLowerCase(), username, uuid.toString());

				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					SimpleConnection.safelyCloseAll(conn);
				}

			}

		} else {

			SimpleConnection conn = null;

			try {

				conn = localDatabase.getConnection();
				conn.safeUpdate("INSERT INTO name_data VALUES (?, ?, ?);", uuid.toString(), username, username.toLowerCase());

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				SimpleConnection.safelyCloseAll(conn);
			}

		}

		online.add(uuid);

	}

	public boolean existsUUID(UUID uuid) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		SimpleConnection conn = null;
		boolean exists;

		try {

			conn = localDatabase.getConnection();

			ResultSet results = conn.safeQuery("SELECT id FROM name_data WHERE id = ?;", uuid.toString());
			if (results.next()) {
				exists = true;
			} else {
				exists = false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			exists = false;
		} finally {
			SimpleConnection.safelyCloseAll(conn);
		}

		return exists;

	}

	public boolean hasNickname(UUID uuid) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Checking if: " + uuid + " has a nickname...");

		SimpleConnection conn = null;
		boolean exists;

		try {

			conn = localDatabase.getConnection();
			ResultSet results = conn.safeQuery("SELECT id FROM nick_data WHERE id = ?;", uuid.toString());

			if (results.next()) {
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] They do have a nickname!");
				exists = true;
			} else {
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] They do not have a nickname!");
				exists = false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			exists = false;
		} finally {
			SimpleConnection.safelyCloseAll(conn);
		}

		return exists;

	}

	public void registerMigratedPlayer(UUID uuid, String name, String formattedName, String nick, String formattedNick) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		boolean setNick = (nick != null);

		if (existsUUID(uuid)) {

			SimpleConnection conn = null;

			try {

				conn = localDatabase.getConnection();
				conn.safeUpdate("UPDATE name_data SET f_name = ?, u_name = ? WHERE id = ?;"
						, formattedName, name, uuid.toString());

				if (setNick) {

					if (hasNickname(uuid)) {
						conn.safeUpdate("UPDATE nick_data SET u_nick = ?, f_nick = ? WHERE id = ?;", nick, formattedNick, uuid.toString());
					} else {
						conn.safeUpdate("INSERT INTO nick_data VALUES (?, ?, ?);", uuid.toString(), nick, formattedNick);
					}

				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				SimpleConnection.safelyCloseAll(conn);
			}

		} else {

			SimpleConnection conn = null;

			try {

				conn = localDatabase.getConnection();
				conn.safeUpdate("INSERT INTO name_data VALUES (?, ?, ?);", uuid.toString(), formattedName, name);

				if (setNick) {
					conn.safeUpdate("INSERT INTO nick_data VALUES (?, ?, ?);", uuid.toString(), nick, formattedNick);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				SimpleConnection.safelyCloseAll(conn);
			}

		}

	}

	@Override
	public void registerOfflinePlayerByUUID(UUID uuid, String username) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		if (existsUUID(uuid)) {

			return; // Player does not need registering

		} else {

			SimpleConnection conn = null;

			try {
				conn = localDatabase.getConnection();
				conn.safeUpdate("INSERT INTO name_data VALUES (?, ?, ?);", uuid.toString(), username, username.toLowerCase());
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				SimpleConnection.safelyCloseAll(conn);
			}

		}

	}

	@Override
	public void unregisterPlayer(UUID uuid) {

		MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Unregistering player with UUID: " + uuid);

		online.remove(uuid);

	}

	@Override
	public void setNickname(UUID uuid, String nickname) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Setting new nickname (" + nickname + ") for " + uuid);

		if (!existsUUID(uuid)) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] This UUID does not exist! Abandoning...");
			return;
		}

		String unformattedNickname = MultiChatUtil.stripColorCodes(nickname.toLowerCase(), false);

		MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Unformatted nickname = " + unformattedNickname);

		if (otherPlayerHasNickname(unformattedNickname, uuid)) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Someone else already has this nickname... Abandoning...");
			return;
		}

		SimpleConnection conn = null;

		try {

			conn = localDatabase.getConnection();

			if (hasNickname(uuid)) {
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Player previously had a nickname already... updating database...");
				conn.safeUpdate("UPDATE nick_data SET u_nick = ?, f_nick = ? WHERE id = ?;", unformattedNickname, nickname, uuid.toString());
			} else {
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Player did not have a nickname before... inserting into database...");
				conn.safeUpdate("INSERT INTO nick_data VALUES (?, ?, ?);", uuid.toString(), unformattedNickname, nickname);
			}

			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Process completed. Nickname is set.");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SimpleConnection.safelyCloseAll(conn);
		}

	}

	@Override
	public boolean existsPlayer(String username) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		SimpleConnection conn = null;
		boolean exists;

		try {

			conn = localDatabase.getConnection();
			ResultSet results = conn.safeQuery("SELECT u_name FROM name_data WHERE u_name = ?;", username.toLowerCase());

			if (results.next()) {
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Player " + username + " exists");
				exists = true;
			} else {
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Player " + username + " does not exist");
				exists = false;
			}


		} catch (SQLException e) {
			e.printStackTrace();
			exists = false;
		} finally {
			SimpleConnection.safelyCloseAll(conn);
		}

		return exists;

	}

	@Override
	public boolean existsNickname(String nickname) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		SimpleConnection conn = null;
		boolean exists;

		try {

			conn = localDatabase.getConnection();
			ResultSet results = conn.safeQuery("SELECT u_nick FROM nick_data WHERE u_nick = ?;", MultiChatUtil.stripColorCodes(nickname.toLowerCase(), false));

			if (results.next()) {
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Nickname " + nickname + " exists");
				exists = true;
			} else {
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Nickname " + nickname + " does not exist");
				exists = false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			exists = false;
		} finally {
			SimpleConnection.safelyCloseAll(conn);
		}

		return exists;

	}

	public boolean otherPlayerHasNickname(String nickname, UUID uuid) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		SimpleConnection conn = null;
		boolean exists;

		try {

			conn = localDatabase.getConnection();
			ResultSet results = conn.safeQuery("SELECT id, u_nick FROM nick_data WHERE u_nick = ?;"
					, MultiChatUtil.stripColorCodes(nickname.toLowerCase(), false));

			if (results.next()) {
				if (results.getString("id").equals(uuid.toString())) {
					exists = false;
				} else {
					exists = true;
				}
			} else {
				exists = false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			exists = false;
		} finally {
			SimpleConnection.safelyCloseAll(conn);
		}

		return exists;

	}

	@Override
	public Optional<Set<UUID>> getPartialNicknameMatches(String nickname) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		SimpleConnection conn = null;
		Optional<Set<UUID>> opUUIDs;

		try {

			conn = localDatabase.getConnection();
			ResultSet results = conn.safeQuery("SELECT id FROM nick_data WHERE (u_nick LIKE ?);"
					, "%" + MultiChatUtil.stripColorCodes(nickname.toLowerCase(), false) + "%");

			if (results.next()) {
				Set<UUID> uuids = new HashSet<UUID>();
				uuids.add(UUID.fromString(results.getString("id")));

				while (results.next()) {
					uuids.add(UUID.fromString(results.getString("id")));
				}

				opUUIDs = Optional.of(uuids);
			} else {
				opUUIDs = Optional.empty();
			}


		} catch (SQLException e) {
			e.printStackTrace();
			opUUIDs = Optional.empty();
		} finally {
			SimpleConnection.safelyCloseAll(conn);
		}

		return opUUIDs;

	}

	@Override
	public Optional<Set<UUID>> getPartialNameMatches(String name) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		SimpleConnection conn = null;
		Optional<Set<UUID>> opUUIDs;

		try {

			conn = localDatabase.getConnection();
			ResultSet results = conn.safeQuery("SELECT id, f_name FROM name_data WHERE (u_name LIKE ?);", "%" + name.toLowerCase() + "%");

			if (results.next()) {
				Set<UUID> uuids = new HashSet<UUID>();
				uuids.add(UUID.fromString(results.getString("id")));

				while (results.next()) {
					uuids.add(UUID.fromString(results.getString("id")));
				}

				opUUIDs = Optional.of(uuids);
			} else {
				opUUIDs = Optional.empty();
			}


		} catch (SQLException e) {
			e.printStackTrace();
			opUUIDs = Optional.empty();
		} finally {
			SimpleConnection.safelyCloseAll(conn);
		}

		return opUUIDs;

	}

	@Override
	public void removeNickname(UUID uuid) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Removing nickname for " + uuid);

		if (!existsUUID(uuid)) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] This UUID doesn't exist... Abandoning...");
			return;
		}

		SimpleConnection conn = null;

		try {

			if (!hasNickname(uuid)) {
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] This player does not have a nickname to remove... Abandoning...");
				return;
			}

			conn = localDatabase.getConnection();
			conn.safeUpdate("DELETE FROM nick_data WHERE id  = ?;", uuid.toString());

			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Process complete, nickname removed.");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			SimpleConnection.safelyCloseAll(conn);
		}

	}

}
