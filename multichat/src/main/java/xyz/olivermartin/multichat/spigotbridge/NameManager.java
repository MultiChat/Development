package xyz.olivermartin.multichat.spigotbridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Player Name Manager
 * <p>Manages players names, uuids and nicknames</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class NameManager {

	private static NameManager instance;

	static {
		instance = new NameManager();
	}

	public static NameManager getInstance() {
		return instance;
	}

	// END OF STATIC DEFINITIONS

	private Map<UUID,String> mapUUIDNick;
	private Map<UUID,String> mapUUIDName;
	private Map<String,UUID> mapNickUUID;
	private Map<String,UUID> mapNameUUID;

	private Map<String,String> mapNickFormatted;
	private Map<String,String> mapNameFormatted;

	private List<UUID> online;

	private NameManager() {

		mapUUIDNick = new HashMap<UUID,String>();
		mapUUIDName = new HashMap<UUID,String>();
		mapNickUUID = new HashMap<String,UUID>();
		mapNameUUID = new HashMap<String,UUID>();

		mapNickFormatted = new HashMap<String,String>();
		mapNameFormatted = new HashMap<String,String>();

		online = new ArrayList<UUID>();

	}

	/**
	 * Returns the FORMATTED NICKNAME of a player if they have one set, otherwise returns their username
	 * 
	 * @param uuid The Unique ID of the player to lookup
	 * @return The NICKNAME of the player if it is set, otherwise their username
	 */
	public String getCurrentName(UUID uuid) {

		synchronized (mapUUIDNick) {
			if (mapUUIDNick.containsKey(uuid)) {
				return mapNickFormatted.get(mapUUIDNick.get(uuid));
			} 
		}

		synchronized (mapUUIDName) {
			if (mapUUIDName.containsKey(uuid)) {
				return mapNameFormatted.get(mapUUIDName.get(uuid));
			}
		}

		return "";
	}

	/**
	 * Returns the username of a player
	 * 
	 * @param uuid The Unique ID of the player to lookup
	 * @return The username of the player
	 */
	public String getName(UUID uuid) {

		synchronized (mapUUIDName) {
			if (mapUUIDName.containsKey(uuid)) {
				return mapNameFormatted.get(mapUUIDName.get(uuid));
			}
		}

		return "";

	}

	/**
	 * Gets the UUID of a player from their UNFORMATTED nickname
	 * THIS MEANS THE NICKNAME PROVIDED MUST BE IN LOWERCASE WITH ALL FORMATTING CODES REMOVED
	 * 
	 * @param nickname The UNFORMATTED nickname of the player
	 * @return An optional which may contain their UUID if the nickname was found in the system
	 */
	private Optional<UUID> getUUIDFromUnformattedNickname(String nickname) {

		nickname = nickname.toLowerCase();

		synchronized (mapNickUUID) {
			if (mapNickUUID.containsKey(nickname)) {
				return Optional.of(mapNickUUID.get(nickname));
			}
		}

		return Optional.empty();

	}

	/**
	 * Returns a player's UUID given their username
	 * 
	 * @param username The player's username
	 * @return An optional value which may contain their UUID if the username was found
	 */
	public Optional<UUID> getUUIDFromName(String username) {

		username = username.toLowerCase();

		synchronized (mapNameUUID) {
			if (mapNameUUID.containsKey(username)) {
				return Optional.of(mapNameUUID.get(username));
			}
		}

		return Optional.empty();

	}

	/**
	 * Gets a player's UUID from their nickname
	 * 
	 * @param nickname The player's nickname (which may contain formatting codes etc.)
	 * @return An optional value which may contain their UUID if the nickname was found
	 */
	public Optional<UUID> getUUIDFromNickname(String nickname) {

		nickname = nickname.toLowerCase();
		nickname = ChatColor.stripColor(nickname);

		return getUUIDFromUnformattedNickname(nickname);

	}

	/**
	 * Gets a player's username from their nickname
	 * 
	 * @param nickname The player's nickname (which may contain formatting codes etc.)
	 * @return An optional value which may contain their username if the nickname was found
	 */
	public Optional<String> getNameFromNickname(String nickname) {

		Optional<UUID> oUUID = getUUIDFromNickname(nickname);

		if (!oUUID.isPresent()) {
			return Optional.empty();
		}

		UUID uuid = oUUID.get();

		return Optional.of(getName(uuid));

	}

	/**
	 * Gets a player's nickname from their username
	 * 
	 * @param nickname The player's username
	 * @return An optional value which may contain their nickname, or their username if a nickname was not set, as long as their username can be found
	 */
	public Optional<String> getCurrentNameFromName(String username) {

		username = username.toLowerCase();

		Optional<UUID> oUUID = getUUIDFromName(username);

		if (!oUUID.isPresent()) {
			return Optional.empty();
		}

		UUID uuid = oUUID.get();

		return Optional.of(getCurrentName(uuid));

	}

	/**
	 * Register a player as online
	 * <p>Also performs any setup needed to equip nicknames etc.</p>
	 * @param player
	 */
	public void registerPlayer(Player player) {

		UUID uuid = player.getUniqueId();
		String username = player.getName();
		String oldUsername;

		synchronized (mapUUIDName) {

			if (mapUUIDName.containsKey(uuid)) {

				oldUsername = mapUUIDName.get(uuid);

				if (!oldUsername.equalsIgnoreCase(username)) {

					synchronized (mapNameUUID) {

						mapUUIDName.remove(uuid);
						mapUUIDName.put(uuid, username.toLowerCase());
						mapNameUUID.remove(oldUsername);
						mapNameUUID.put(username.toLowerCase(), uuid);

					}

				}

				mapNameFormatted.remove(oldUsername);
				mapNameFormatted.put(username.toLowerCase(), username);

			} else {

				synchronized (mapNameUUID) {

					mapUUIDName.put(uuid, username.toLowerCase());
					mapNameUUID.put(username.toLowerCase(), uuid);
					mapNameFormatted.put(username.toLowerCase(), username);

				}

			}

		}

		online.add(uuid);
		System.out.println("[MultiChat] [SPIGOT] [+] " + username + " has joined this server.");

	}

	/**
	 * Register a player as offline
	 * @param player
	 */
	public void unregisterPlayer(Player player) {

		online.remove(player.getUniqueId());
		System.out.println("[MultiChat] [SPIGOT] [-] " + player.getName() + " has left this server.");

	}

	/**
	 * Set the nickname of a player
	 * @param uuid
	 * @param nickname
	 */
	public void setNickname(UUID uuid, String nickname) {

		if (!mapUUIDName.containsKey(uuid)) {
			return;
		}

		String unformattedNickname = ChatColor.stripColor(nickname.toLowerCase());

		synchronized (mapNickUUID) {

			// Check for duplicates
			if (mapNickUUID.containsKey(unformattedNickname)) {
				if (mapNickUUID.get(unformattedNickname) != uuid) {
					return;
				}
			}

			mapUUIDNick.put(uuid, unformattedNickname);
			mapNickUUID.put(unformattedNickname, uuid);
			mapNickFormatted.put(unformattedNickname, nickname);

		}

	}

	/**
	 * @param username
	 * @return If this player has logged into the server before
	 */
	public boolean existsPlayer(String username) {
		return mapNameUUID.containsKey(username.toLowerCase());
	}

	/**
	 * @param nickname
	 * @return If this nickname is currently in use
	 */
	public boolean existsNickname(String nickname) {
		return mapNickUUID.containsKey(ChatColor.stripColor(nickname.toLowerCase()));
	}

	/**
	 * @param uuid
	 * @return If this player is currently online on the server
	 */
	public boolean isOnline(UUID uuid) {
		return online.contains(uuid);
	}

	/**
	 * Removes the nickname for a specified player
	 * @param uuid
	 */
	public void removeNickname(UUID uuid) {

		synchronized (mapUUIDNick) {

			if (!mapUUIDNick.containsKey(uuid)) {
				return;
			}

			String nickname = mapUUIDNick.get(uuid);
			
			mapUUIDNick.remove(uuid);
			mapNickUUID.remove(nickname);
			mapNickFormatted.remove(nickname);

		}

	}

	// TODO EVENTS

	// TODO SAVE

	// TODO LOAD

}
