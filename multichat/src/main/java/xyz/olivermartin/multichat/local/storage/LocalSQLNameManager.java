package xyz.olivermartin.multichat.local.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.common.database.DatabaseManager;
import xyz.olivermartin.multichat.common.database.GenericDatabase;
import xyz.olivermartin.multichat.local.MultiChatLocal;

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
	private GenericDatabase localDatabase;

	public LocalSQLNameManager(String databaseName) {

		super(LocalNameManagerMode.SQL);
		connected = getDatabase(databaseName);
		MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Connection Staus: " + connected);

	}

	private boolean getDatabase(String databaseName) {

		Optional<GenericDatabase> ogdb = DatabaseManager.getInstance().getDatabase(databaseName);
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

		try {

			String name;

			synchronized (localDatabase) {
				localDatabase.connectToDatabase();
				ResultSet results = localDatabase.safeQuery("SELECT f_name, f_nick FROM name_data LEFT JOIN nick_data ON name_data.id = nick_data.id WHERE name_data.id = ?;", uuid.toString());
				results.next();
				if (results.getString("f_nick") == null) {
					name = results.getString("f_name");
				} else {
					name = results.getString("f_nick");
					if (MultiChatLocal.getInstance().getConfigManager().getLocalConfig().isShowNicknamePrefix() && withPrefix) {
						name = MultiChatLocal.getInstance().getConfigManager().getLocalConfig().getNicknamePrefix() + name;
					}
				}
			}

			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] CurrentName = " + name);

			return name;

		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}


	}

	@Override
	public String getName(UUID uuid) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		try {
			String name;
			synchronized (localDatabase) {
				localDatabase.connectToDatabase();
				ResultSet results = localDatabase.safeQuery("SELECT f_name FROM name_data WHERE id = ?;", uuid.toString());
				results.next();
				name = results.getString("f_name");
			}

			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Name = " + name);

			return name;

		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}

	}

	@Override
	protected Optional<UUID> getUUIDFromUnformattedNickname(String nickname) {

		nickname = nickname.toLowerCase();

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		try {

			synchronized (localDatabase) {
				localDatabase.connectToDatabase();
				ResultSet results = localDatabase.safeQuery("SELECT id FROM nick_data WHERE u_nick = ?;", nickname);
				if (results.next()) {
					UUID id = UUID.fromString(results.getString("id"));
					return Optional.of(id);
				} else {
					return Optional.empty();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return Optional.empty();
		}

	}

	@Override
	public Optional<UUID> getUUIDFromName(String username) {

		username = username.toLowerCase();

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		try {

			synchronized (localDatabase) {
				localDatabase.connectToDatabase();
				ResultSet results = localDatabase.safeQuery("SELECT id FROM name_data WHERE u_name = ?;", username);
				if (results.next() ) {
					UUID id = UUID.fromString(results.getString("id"));
					return Optional.of(id);
				} else {
					return Optional.empty();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return Optional.empty();
		}

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

				try {
					synchronized (localDatabase) {
						localDatabase.connectToDatabase();
						MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Updating database...");
						localDatabase.safeUpdate("UPDATE name_data SET u_name = ?, f_name = ? WHERE id = ?;", username.toLowerCase(), username, uuid.toString());
						MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Database updated!");
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

		} else {

			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] UUID does not already exist in database...");

			try {
				synchronized (localDatabase) {
					localDatabase.connectToDatabase();
					MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Updating database...");
					localDatabase.safeUpdate("INSERT INTO name_data VALUES (?, ?, ?);", uuid.toString(), username, username.toLowerCase());
					MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Database updated!");
				}

			} catch (SQLException e) {
				e.printStackTrace();
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

				try {
					synchronized (localDatabase) {
						localDatabase.connectToDatabase();

						localDatabase.safeUpdate("UPDATE name_data SET u_name = ?, f_name = ? WHERE id = ?;", username.toLowerCase(), username, uuid.toString());
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

		} else {

			try {
				synchronized (localDatabase) {
					localDatabase.connectToDatabase();

					localDatabase.safeUpdate("INSERT INTO name_data VALUES (?, ?, ?);", uuid.toString(), username, username.toLowerCase());
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		online.add(uuid);

	}

	public boolean existsUUID(UUID uuid) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		try {
			synchronized (localDatabase) {
				localDatabase.connectToDatabase();

				ResultSet results = localDatabase.safeQuery("SELECT id FROM name_data WHERE id = ?;", uuid.toString());
				if (results.next()) {
					return true;
				} else {
					return false;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean hasNickname(UUID uuid) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Checking if: " + uuid + " has a nickname...");

		try {
			synchronized (localDatabase) {
				localDatabase.connectToDatabase();

				ResultSet results = localDatabase.safeQuery("SELECT id FROM nick_data WHERE id = ?;", uuid.toString());
				if (results.next()) {
					MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] They do have a nickname!");
					return true;
				} else {
					MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] They do not have a nickname!");
					return false;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public void registerMigratedPlayer(UUID uuid, String name, String formattedName, String nick, String formattedNick) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		boolean setNick = (nick != null);

		if (existsUUID(uuid)) {

			synchronized (localDatabase) {

				try {
					localDatabase.connectToDatabase();
					localDatabase.safeUpdate("UPDATE name_data SET f_name = ?, u_name = ? WHERE id = ?;", formattedName, name, uuid.toString());

					if (setNick) {

						if (hasNickname(uuid)) {
							localDatabase.safeUpdate("UPDATE nick_data SET u_nick = ?, f_nick = ? WHERE id = ?;", nick, formattedNick, uuid.toString());
						} else {
							localDatabase.safeUpdate("INSERT INTO nick_data VALUES (?, ?, ?);", uuid.toString(), nick, formattedNick);
						}

					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

		} else {

			try {
				synchronized (localDatabase) {

					localDatabase.connectToDatabase();
					localDatabase.safeUpdate("INSERT INTO name_data VALUES (?, ?, ?);", uuid.toString(), formattedName, name);

					if (setNick) {
						localDatabase.safeUpdate("INSERT INTO nick_data VALUES (?, ?, ?);", uuid.toString(), nick, formattedNick);
					}

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void registerOfflinePlayerByUUID(UUID uuid, String username) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		if (existsUUID(uuid)) {

			return; // Player does not need registering

		} else {

			try {
				synchronized (localDatabase) {
					localDatabase.connectToDatabase();

					localDatabase.safeUpdate("INSERT INTO name_data VALUES (?, ?, ?);", uuid.toString(), username, username.toLowerCase());
				}

			} catch (SQLException e) {
				e.printStackTrace();
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

		String unformattedNickname = stripAllFormattingCodes(nickname.toLowerCase());

		MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Unformatted nickname = " + unformattedNickname);

		if (otherPlayerHasNickname(unformattedNickname, uuid)) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Someone else already has this nickname... Abandoning...");
			return;
		}

		try {
			synchronized (localDatabase) {

				if (hasNickname(uuid)) {
					MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Player previously had a nickname already... updating database...");
					localDatabase.connectToDatabase();
					localDatabase.safeUpdate("UPDATE nick_data SET u_nick = ?, f_nick = ? WHERE id = ?;", unformattedNickname, nickname, uuid.toString());
				} else {
					MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Player did not have a nickname before... inserting into database...");
					localDatabase.connectToDatabase();
					localDatabase.safeUpdate("INSERT INTO nick_data VALUES (?, ?, ?);", uuid.toString(), unformattedNickname, nickname);
				}

			}

			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Process completed. Nickname is set.");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean existsPlayer(String username) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		try {
			synchronized (localDatabase) {
				localDatabase.connectToDatabase();

				ResultSet results = localDatabase.safeQuery("SELECT u_name FROM name_data WHERE u_name = ?;", username.toLowerCase());
				if (results.next()) {
					MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Player " + username + " exists");
					return true;
				} else {
					MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Player " + username + " does not exist");
					return false;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean existsNickname(String nickname) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		try {
			synchronized (localDatabase) {
				localDatabase.connectToDatabase();

				ResultSet results = localDatabase.safeQuery("SELECT u_nick FROM nick_data WHERE u_nick = ?;", stripAllFormattingCodes(nickname.toLowerCase()));
				if (results.next()) {
					MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Nickname " + nickname + " exists");
					return true;
				} else {
					MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Nickname " + nickname + " does not exist");
					return false;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean otherPlayerHasNickname(String nickname, UUID uuid) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		try {
			synchronized (localDatabase) {
				localDatabase.connectToDatabase();

				ResultSet results = localDatabase.safeQuery("SELECT id, u_nick FROM nick_data WHERE u_nick = ?;", stripAllFormattingCodes(nickname.toLowerCase()));
				if (results.next()) {
					if (results.getString("id").equals(uuid.toString())) {
						return false;
					} else {
						return true;
					}
				} else {
					return false;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public Optional<Set<UUID>> getPartialNicknameMatches(String nickname) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		try {
			synchronized (localDatabase) {
				localDatabase.connectToDatabase();

				ResultSet results = localDatabase.safeQuery("SELECT id FROM nick_data WHERE (u_nick LIKE ?);", "%" + stripAllFormattingCodes(nickname.toLowerCase()) + "%");
				if (results.next()) {
					Set<UUID> uuids = new HashSet<UUID>();
					uuids.add(UUID.fromString(results.getString("id")));

					while (results.next()) {
						uuids.add(UUID.fromString(results.getString("id")));
					}

					return Optional.of(uuids);
				} else {
					return Optional.empty();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return Optional.empty();
		}

	}

	@Override
	public Optional<Set<UUID>> getPartialNameMatches(String name) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		try {
			synchronized (localDatabase) {
				localDatabase.connectToDatabase();

				ResultSet results = localDatabase.safeQuery("SELECT id, f_name FROM name_data WHERE (u_name LIKE ?);", "%" + name.toLowerCase() + "%");
				if (results.next()) {
					Set<UUID> uuids = new HashSet<UUID>();
					uuids.add(UUID.fromString(results.getString("id")));

					while (results.next()) {
						uuids.add(UUID.fromString(results.getString("id")));
					}

					return Optional.of(uuids);
				} else {
					return Optional.empty();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return Optional.empty();
		}

	}

	@Override
	public void removeNickname(UUID uuid) {

		if (!connected) throw new IllegalStateException("MultiChatLocal's Name Manager could not connect to specified database!");

		MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Removing nickname for " + uuid);

		if (!existsUUID(uuid)) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] This UUID doesn't exist... Abandoning...");
			return;
		}

		try {

			synchronized (localDatabase) {

				if (!hasNickname(uuid)) {
					MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] This player does not have a nickname to remove... Abandoning...");
					return;
				}

				localDatabase.connectToDatabase();

				localDatabase.safeUpdate("DELETE FROM nick_data WHERE id  = ?;", uuid.toString());

				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalSQLNameManager] Process complete, nickname removed.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
