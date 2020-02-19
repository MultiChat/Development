package xyz.olivermartin.multichat.spigotbridge;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import xyz.olivermartin.multichat.spigotbridge.database.DatabaseManager;
import xyz.olivermartin.multichat.spigotbridge.database.GenericDatabase;

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
					ResultSet results = spigotdatabase.query("SELECT f_nick FROM name_data WHERE id = '" + uuid.toString() + "';");
					results.next();
					name = results.getString("f_nick");
					spigotdatabase.disconnectFromDatabase();
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
					ResultSet results = spigotdatabase.query("SELECT f_name FROM name_data WHERE id = '" + uuid.toString() + "';");
					results.next();
					name = results.getString("f_name");
					spigotdatabase.disconnectFromDatabase();
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
					ResultSet results = spigotdatabase.query("SELECT id FROM name_data WHERE u_nick = '" + nickname + "';");
					if (results.next()) {
						UUID id = UUID.fromString(results.getString("id"));
						spigotdatabase.disconnectFromDatabase();
						return Optional.of(id);
					} else {
						spigotdatabase.disconnectFromDatabase();
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
					ResultSet results = spigotdatabase.query("SELECT id FROM name_data WHERE u_name = '" + username + "';");
					if (results.next() ) {
						UUID id = UUID.fromString(results.getString("id"));
						spigotdatabase.disconnectFromDatabase();
						return Optional.of(id);
					} else {
						spigotdatabase.disconnectFromDatabase();
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

						spigotdatabase.update("UPDATE name_data SET u_name = '" + username.toLowerCase() + "', f_name = '" + username + "' WHERE id = '" + uuid.toString() + "';");

						spigotdatabase.disconnectFromDatabase();
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

		} else {

			try {
				synchronized (spigotdatabase) {
					spigotdatabase.connectToDatabase();

					spigotdatabase.update("INSERT INTO name_data VALUES ('" + uuid.toString() + "', '" + username + "', '" + username.toLowerCase() + "', '" + username.toLowerCase() + "', '" + username + "');");

					spigotdatabase.disconnectFromDatabase();
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

						spigotdatabase.update("UPDATE name_data SET u_name = '" + username.toLowerCase() + "', f_name = '" + username + "' WHERE id = '" + uuid.toString() + "';");

						spigotdatabase.disconnectFromDatabase();
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

		} else {

			try {
				synchronized (spigotdatabase) {
					spigotdatabase.connectToDatabase();

					spigotdatabase.update("INSERT INTO name_data VALUES ('" + uuid.toString() + "', '" + username + "', '" + username.toLowerCase() + "', '" + username.toLowerCase() + "', '" + username + "');");

					spigotdatabase.disconnectFromDatabase();
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

				ResultSet results = spigotdatabase.query("SELECT id FROM name_data WHERE id = '" + uuid.toString() + "';");
				if (results.next()) { //TODO fixed this line...
					spigotdatabase.disconnectFromDatabase();
					return true;
				} else {
					spigotdatabase.disconnectFromDatabase();
					return false;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
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

					spigotdatabase.update("INSERT INTO name_data VALUES ('" + uuid.toString() + "', '" + username + "', '" + username.toLowerCase() + "', '" + username.toLowerCase() + "', '" + username + "');");

					spigotdatabase.disconnectFromDatabase();
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
				spigotdatabase.connectToDatabase();

				spigotdatabase.update("UPDATE name_data SET u_nick = '" + unformattedNickname + "', f_nick = '" + nickname + "' WHERE id = '" + uuid.toString() + "';");

				spigotdatabase.disconnectFromDatabase();
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

				ResultSet results = spigotdatabase.query("SELECT u_name FROM name_data WHERE u_name = '" + username.toLowerCase() + "';");
				if (results.next()) {
					spigotdatabase.disconnectFromDatabase();
					return true;
				} else {
					spigotdatabase.disconnectFromDatabase();
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

				ResultSet results = spigotdatabase.query("SELECT u_nick FROM name_data WHERE u_nick = '" + stripAllFormattingCodes(nickname.toLowerCase()) + "';");
				if (results.next()) {
					spigotdatabase.disconnectFromDatabase();
					return true;
				} else {
					spigotdatabase.disconnectFromDatabase();
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

				ResultSet results = spigotdatabase.query("SELECT id, u_nick FROM name_data WHERE u_nick = '" + stripAllFormattingCodes(nickname.toLowerCase()) + "';");
				if (results.next()) {
					if (results.getString("id") == uuid.toString()) {
						spigotdatabase.disconnectFromDatabase();
						return false;
					} else {
						spigotdatabase.disconnectFromDatabase();
						return true;
					}
				} else {
					spigotdatabase.disconnectFromDatabase();
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

				ResultSet results = spigotdatabase.query("SELECT id, f_name, f_nick FROM name_data WHERE (u_nick LIKE '%" + stripAllFormattingCodes(nickname.toLowerCase()) + "%');");
				if (results.next()) {
					Set<UUID> uuids = new HashSet<UUID>();
					uuids.add(UUID.fromString(results.getString("id")));

					while (results.next()) {
						uuids.add(UUID.fromString(results.getString("id")));
					}

					spigotdatabase.disconnectFromDatabase();
					return Optional.of(uuids);
				} else {
					spigotdatabase.disconnectFromDatabase();
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

				ResultSet results = spigotdatabase.query("SELECT id, f_name FROM name_data WHERE (u_name LIKE '%" + name.toLowerCase() + "%');");
				if (results.next()) {
					Set<UUID> uuids = new HashSet<UUID>();
					uuids.add(UUID.fromString(results.getString("id")));

					while (results.next()) {
						uuids.add(UUID.fromString(results.getString("id")));
					}

					spigotdatabase.disconnectFromDatabase();
					return Optional.of(uuids);
				} else {
					spigotdatabase.disconnectFromDatabase();
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
			String f_name;
			synchronized (spigotdatabase) {
				spigotdatabase.connectToDatabase();

				ResultSet results = spigotdatabase.query("SELECT f_name FROM name_data WHERE id = '" + uuid.toString() + "';");
				results.next();

				f_name = results.getString("f_name");

				spigotdatabase.disconnectFromDatabase();
			}

			setNickname(uuid, f_name);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
