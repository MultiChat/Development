package xyz.olivermartin.multichat.spigotbridge;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.olivermartin.multichat.database.DatabaseManager;
import xyz.olivermartin.multichat.database.GenericDatabase;

public class SQLNameManager extends NameManager {

	private boolean connected;
	private GenericDatabase spigotdatabase;

	public SQLNameManager() {

		super();

		connected = getDatabase();

	}

	private boolean getDatabase() {

		Optional<GenericDatabase> ogdb = DatabaseManager.getInstance().getDatabase("multichatspigot.db");
		if (ogdb.isPresent()) {
			spigotdatabase = ogdb.get();
			return true;
		} else {
			return false;
		}

	}

	@Override
	public String getCurrentName(UUID uuid, boolean withPrefix) {

		if (connected) {

			try {

				String name;

				synchronized (spigotdatabase) {
					spigotdatabase.connectToDatabase();
					ResultSet results = spigotdatabase.safeQuery("SELECT f_name, f_nick FROM name_data LEFT JOIN nick_data ON name_data.id = nick_data.id WHERE name_data.id = ?;", uuid.toString());
					results.next();
					if (results.getString("f_nick") == null) {
						name = results.getString("f_name");
					} else {
						name = results.getString("f_nick");
					}
					//spigotdatabase.disconnectFromDatabase();
				}

				if (MultiChatSpigot.showNicknamePrefix && withPrefix) {
					return MultiChatSpigot.nicknamePrefix + name;
				} else {
					return name;
				}

			} catch (SQLException e) {
				e.printStackTrace();
				return "";
			}

		} else {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return "";
		}

	}

	@Override
	public String getName(UUID uuid) {

		if (connected) {

			try {
				String name;
				synchronized (spigotdatabase) {
					spigotdatabase.connectToDatabase();
					ResultSet results = spigotdatabase.safeQuery("SELECT f_name FROM name_data WHERE id = ?;", uuid.toString());
					results.next();
					name = results.getString("f_name");
					//spigotdatabase.disconnectFromDatabase();
				}

				return name;

			} catch (SQLException e) {
				e.printStackTrace();
				return "";
			}

		} else {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return "";
		}

	}

	@Override
	protected Optional<UUID> getUUIDFromUnformattedNickname(String nickname) {

		nickname = nickname.toLowerCase();

		if (connected) {

			try {

				synchronized (spigotdatabase) {
					spigotdatabase.connectToDatabase();
					ResultSet results = spigotdatabase.safeQuery("SELECT id FROM nick_data WHERE u_nick = ?;", nickname);
					if (results.next()) {
						UUID id = UUID.fromString(results.getString("id"));
						//spigotdatabase.disconnectFromDatabase();
						return Optional.of(id);
					} else {
						//spigotdatabase.disconnectFromDatabase();
						return Optional.empty();
					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
				return Optional.empty();
			}

		} else {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return Optional.empty();
		}

	}

	@Override
	public Optional<UUID> getUUIDFromName(String username) {

		username = username.toLowerCase();

		if (connected) {

			try {

				synchronized (spigotdatabase) {
					spigotdatabase.connectToDatabase();
					ResultSet results = spigotdatabase.safeQuery("SELECT id FROM name_data WHERE u_name = ?;", username);
					if (results.next() ) {
						UUID id = UUID.fromString(results.getString("id"));
						//spigotdatabase.disconnectFromDatabase();
						return Optional.of(id);
					} else {
						//spigotdatabase.disconnectFromDatabase();
						return Optional.empty();
					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
				return Optional.empty();
			}

		} else {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return Optional.empty();
		}

	}

	@Override
	public void registerPlayer(Player player) {

		if (!connected) {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return;
		}

		UUID uuid = player.getUniqueId();
		String username = player.getName();
		String oldUsername;

		if (existsUUID(uuid)) {

			oldUsername = getName(uuid);

			if (!oldUsername.equalsIgnoreCase(username)) {

				try {
					synchronized (spigotdatabase) {
						spigotdatabase.connectToDatabase();

						spigotdatabase.safeUpdate("UPDATE name_data SET u_name = ?, f_name = ? WHERE id = ?;", username.toLowerCase(), username, uuid.toString());

						//spigotdatabase.disconnectFromDatabase();
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

		} else {

			try {
				synchronized (spigotdatabase) {
					spigotdatabase.connectToDatabase();

					spigotdatabase.safeUpdate("INSERT INTO name_data VALUES (?, ?, ?);", uuid.toString(), username, username.toLowerCase());

					//spigotdatabase.disconnectFromDatabase();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		online.add(uuid);
		Bukkit.getLogger().info("[+] " + username + " has joined this server.");

	}

	public void testRegisterFakePlayer(UUID uuid, String username) {

		if (!connected) {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return;
		}

		String oldUsername;

		if (existsUUID(uuid)) {

			oldUsername = getName(uuid);

			if (!oldUsername.equalsIgnoreCase(username)) {

				try {
					synchronized (spigotdatabase) {
						spigotdatabase.connectToDatabase();

						spigotdatabase.safeUpdate("UPDATE name_data SET u_name = ?, f_name = ? WHERE id = ?;", username.toLowerCase(), username, uuid.toString());

						//spigotdatabase.disconnectFromDatabase();
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

		} else {

			try {
				synchronized (spigotdatabase) {
					spigotdatabase.connectToDatabase();

					spigotdatabase.safeUpdate("INSERT INTO name_data VALUES (?, ?, ?);", uuid.toString(), username, username.toLowerCase());

					//spigotdatabase.disconnectFromDatabase();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		online.add(uuid);

	}

	public boolean existsUUID(UUID uuid) {

		if (!connected) {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return false;
		}

		try {
			synchronized (spigotdatabase) {
				spigotdatabase.connectToDatabase();

				ResultSet results = spigotdatabase.safeQuery("SELECT id FROM name_data WHERE id = ?;", uuid.toString());
				if (results.next()) { //TODO fixed this line...
					//spigotdatabase.disconnectFromDatabase();
					return true;
				} else {
					//spigotdatabase.disconnectFromDatabase();
					return false;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean hasNickname(UUID uuid) {

		if (!connected) {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return false;
		}

		try {
			synchronized (spigotdatabase) {
				spigotdatabase.connectToDatabase();

				ResultSet results = spigotdatabase.safeQuery("SELECT id FROM nick_data WHERE id = ?;", uuid.toString());
				if (results.next()) { //TODO fixed this line...
					//spigotdatabase.disconnectFromDatabase();
					return true;
				} else {
					//spigotdatabase.disconnectFromDatabase();
					return false;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public void registerMigratedPlayer(UUID uuid, String name, String formattedName, String nick, String formattedNick) {

		if (!connected) {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return;
		}

		// TODO remove debug
		//System.out.println(name + "," + formattedName + "," + (nick == null ? "NULL!!!" : nick) + "," + (formattedNick == null ? "NULL!!!" : formattedNick) );

		boolean setNick = (nick != null);

		// TODO remove debug
		//System.out.println("SETNICK: " + setNick);

		if (existsUUID(uuid)) {

			synchronized (spigotdatabase) {

				try {
					spigotdatabase.connectToDatabase();
					spigotdatabase.safeUpdate("UPDATE name_data SET f_name = ?, u_name = ? WHERE id = ?;", formattedName, name, uuid.toString());

					if (setNick) {

						if (hasNickname(uuid)) {
							spigotdatabase.safeUpdate("UPDATE nick_data SET u_nick = ?, f_nick = ? WHERE id = ?;", nick, formattedNick, uuid.toString());
						} else {
							spigotdatabase.safeUpdate("INSERT INTO nick_data VALUES (?, ?, ?);", uuid.toString(), nick, formattedNick);
						}

					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		} else {

			try {
				synchronized (spigotdatabase) {

					spigotdatabase.connectToDatabase();
					spigotdatabase.safeUpdate("INSERT INTO name_data VALUES (?, ?, ?);", uuid.toString(), formattedName, name);

					if (setNick) {
						spigotdatabase.safeUpdate("INSERT INTO nick_data VALUES (?, ?, ?);", uuid.toString(), nick, formattedNick);
					}

					//spigotdatabase.disconnectFromDatabase();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void registerOfflinePlayerByUUID(UUID uuid, String username) {

		if (!connected) {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return;
		}

		if (existsUUID(uuid)) {

			/*
			 * EMPTY : Player does not need registering
			 */

		} else {

			try {
				synchronized (spigotdatabase) {
					spigotdatabase.connectToDatabase();

					spigotdatabase.safeUpdate("INSERT INTO name_data VALUES (?, ?, ?);", uuid.toString(), username, username.toLowerCase());

					//spigotdatabase.disconnectFromDatabase();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void unregisterPlayer(Player player) {

		online.remove(player.getUniqueId());
		Bukkit.getLogger().info("[-] " + player.getName() + " has left this server.");

	}

	@Override
	public void setNickname(UUID uuid, String nickname) {

		if (!connected) {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return;
		}

		if (!existsUUID(uuid)) {
			return;
		}

		String unformattedNickname = stripAllFormattingCodes(nickname.toLowerCase());

		if (otherPlayerHasNickname(unformattedNickname, uuid)) {
			return;
		}

		try {
			synchronized (spigotdatabase) {

				if (hasNickname(uuid)) {
					spigotdatabase.connectToDatabase();
					spigotdatabase.safeUpdate("UPDATE nick_data SET u_nick = ?, f_nick = ? WHERE id = ?;", unformattedNickname, nickname, uuid.toString());
				} else {
					spigotdatabase.connectToDatabase();
					spigotdatabase.safeUpdate("INSERT INTO nick_data VALUES (?, ?, ?);", uuid.toString(), unformattedNickname, nickname);
				}

				//spigotdatabase.disconnectFromDatabase();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean existsPlayer(String username) {

		if (!connected) {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return false;
		}

		try {
			synchronized (spigotdatabase) {
				spigotdatabase.connectToDatabase();

				ResultSet results = spigotdatabase.safeQuery("SELECT u_name FROM name_data WHERE u_name = ?;", username.toLowerCase());
				if (results.next()) {
					//spigotdatabase.disconnectFromDatabase();
					return true;
				} else {
					//spigotdatabase.disconnectFromDatabase();
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

		if (!connected) {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return false;
		}

		try {
			synchronized (spigotdatabase) {
				spigotdatabase.connectToDatabase();

				ResultSet results = spigotdatabase.safeQuery("SELECT u_nick FROM nick_data WHERE u_nick = ?;", stripAllFormattingCodes(nickname.toLowerCase()));
				if (results.next()) {
					//spigotdatabase.disconnectFromDatabase();
					return true;
				} else {
					//spigotdatabase.disconnectFromDatabase();
					return false;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean otherPlayerHasNickname(String nickname, UUID uuid) {

		if (!connected) {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return false;
		}

		try {
			synchronized (spigotdatabase) {
				spigotdatabase.connectToDatabase();

				ResultSet results = spigotdatabase.safeQuery("SELECT id, u_nick FROM nick_data WHERE u_nick = ?;", stripAllFormattingCodes(nickname.toLowerCase()));
				if (results.next()) {
					if (results.getString("id").equals(uuid.toString())) {
						//spigotdatabase.disconnectFromDatabase();
						return false;
					} else {
						//spigotdatabase.disconnectFromDatabase();
						return true;
					}
				} else {
					//spigotdatabase.disconnectFromDatabase();
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

		if (!connected) {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return Optional.empty();
		}

		try {
			synchronized (spigotdatabase) {
				spigotdatabase.connectToDatabase();

				ResultSet results = spigotdatabase.safeQuery("SELECT id FROM nick_data WHERE (u_nick LIKE ?);", "%" + stripAllFormattingCodes(nickname.toLowerCase()) + "%");
				if (results.next()) {
					Set<UUID> uuids = new HashSet<UUID>();
					uuids.add(UUID.fromString(results.getString("id")));

					while (results.next()) {
						uuids.add(UUID.fromString(results.getString("id")));
					}

					//spigotdatabase.disconnectFromDatabase();
					return Optional.of(uuids);
				} else {
					//spigotdatabase.disconnectFromDatabase();
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

		if (!connected) {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return Optional.empty();
		}

		try {
			synchronized (spigotdatabase) {
				spigotdatabase.connectToDatabase();

				ResultSet results = spigotdatabase.safeQuery("SELECT id, f_name FROM name_data WHERE (u_name LIKE ?);", "%" + name.toLowerCase() + "%");
				if (results.next()) {
					Set<UUID> uuids = new HashSet<UUID>();
					uuids.add(UUID.fromString(results.getString("id")));

					while (results.next()) {
						uuids.add(UUID.fromString(results.getString("id")));
					}

					//spigotdatabase.disconnectFromDatabase();
					return Optional.of(uuids);
				} else {
					//spigotdatabase.disconnectFromDatabase();
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

		if (!connected) {
			System.err.println("NOT CONNECTED TO DB?!?!?!?"); //TODO?
			return;
		}

		if (!existsUUID(uuid)) {
			return;
		}

		try {

			synchronized (spigotdatabase) {

				if (!hasNickname(uuid)) {
					return;
				}

				spigotdatabase.connectToDatabase();

				spigotdatabase.safeUpdate("DELETE FROM nick_data WHERE id  = ?;", uuid.toString());

				//spigotdatabase.disconnectFromDatabase();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
