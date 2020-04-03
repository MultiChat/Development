package xyz.olivermartin.multichat.spongebridge;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.PatternSyntaxException;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;

/**
 * Player Name Manager
 * <p>Manages players names, uuids and nicknames</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class SpongeFileNameManager extends SpongeNameManager {

	private Map<UUID,String> mapUUIDNick;
	private Map<String,UUID> mapNickUUID;
	private Map<String,String> mapNickFormatted;

	public SpongeFileNameManager() {

		super();

		setDefaultData();

	}

	private void setDefaultData() {

		mapUUIDNick = new HashMap<UUID,String>();
		mapNickUUID = new HashMap<String,UUID>();

		mapNickFormatted = new HashMap<String,String>();

	}

	public Map<UUID,String> getMapUUIDNick() {
		return mapUUIDNick;
	}
	public Map<String,UUID> getMapNickUUID() {
		return mapNickUUID;
	}
	public Map<String, String> getMapNickFormatted() {
		return mapNickFormatted;
	}
	
	public void loadFromFile(Map<UUID,String> mapUUIDNick, Map<String, UUID> mapNickUUID, Map<String,String> mapNickFormatted) {
		this.mapUUIDNick = mapUUIDNick;
		this.mapNickUUID = mapNickUUID;
		this.mapNickFormatted = mapNickFormatted;
	}

	/**
	 * Returns the FORMATTED NICKNAME of a player if they have one set, otherwise returns their username
	 * 
	 * @param uuid The Unique ID of the player to lookup
	 * @param withPrefix Should the nickname prefix also be returned if it is set?
	 * @return The NICKNAME of the player if it is set, otherwise their username
	 */
	public String getCurrentName(UUID uuid, boolean withPrefix) {

		synchronized (mapUUIDNick) {
			if (mapUUIDNick.containsKey(uuid)) {
				if (MultiChatSponge.showNicknamePrefix && withPrefix) {
					return MultiChatSponge.nicknamePrefix + mapNickFormatted.get(mapUUIDNick.get(uuid));
				} else {
					return mapNickFormatted.get(mapUUIDNick.get(uuid));
				}
			} 
		}

		UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
		Optional<User> opUser = uss.get(uuid);

		if (opUser.isPresent()) {
			return opUser.get().getName();
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

		UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
		Optional<User> opUser = uss.get(uuid);

		if (opUser.isPresent()) {
			return opUser.get().getName();
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
	protected Optional<UUID> getUUIDFromUnformattedNickname(String nickname) {

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

		UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
		Optional<User> opUser = uss.get(username);

		if (opUser.isPresent()) {
			return Optional.of(opUser.get().getUniqueId());
		}

		return Optional.empty();

	}

	/**
	 * Register a player as online
	 * <p>Also performs any setup needed to equip nicknames etc.</p>
	 * @param player
	 */
	public void registerPlayer(Player player) {

		UUID uuid = player.getUniqueId();

		online.add(uuid);
		System.out.println("[+] " + player.getName() + " has joined this server.");

	}

	/**
	 * Register a player into the system without them being online
	 * <p>Used mainly for legacy conversion of old nickname file</p>
	 * @param uuid Player's UUID
	 */
	public void registerOfflinePlayerByUUID(UUID uuid, String username) {

		/* This is handled by Sponge */
		return;

	}

	/**
	 * Register a player as offline
	 * @param player
	 */
	public void unregisterPlayer(Player player) {
		online.remove(player.getUniqueId());
		System.out.println("[-] " + player.getName() + " has left this server.");
	}

	/**
	 * Set the nickname of a player
	 * @param uuid
	 * @param nickname
	 */
	public void setNickname(UUID uuid, String nickname) {

		UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
		Optional<User> opUser = uss.get(uuid);

		if (!opUser.isPresent()) {
			return;
		}

		if (mapUUIDNick.containsKey(uuid)) {
			removeNickname(uuid);
		}

		String unformattedNickname = stripAllFormattingCodes(nickname.toLowerCase());

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

		UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
		Optional<User> opUser = uss.get(username);

		return opUser.isPresent();
	}

	/**
	 * @param nickname
	 * @return If this nickname is currently in use
	 */
	public boolean existsNickname(String nickname) {
		return mapNickUUID.containsKey(stripAllFormattingCodes(nickname.toLowerCase()));
	}

	/**
	 * Return the UUIDs of players who have nicknames containing characters provided in the nickname argument
	 * @param nickname The characters of the nickname to check
	 * @return An optional which might contain a players UUID if a partial match was found
	 */
	public Optional<Set<UUID>> getPartialNicknameMatches(String nickname) {

		Set<String> nickSet = mapNickUUID.keySet();
		nickname = stripAllFormattingCodes(nickname.toLowerCase());
		Set<UUID> uuidSet = new HashSet<UUID>();

		for (String nick : nickSet) {

			if (nick.startsWith(nickname)) {
				uuidSet.add(mapNickUUID.get(nick));
			}

		}

		if (!uuidSet.isEmpty()) return Optional.of(uuidSet);

		for (String nick : nickSet) {

			if (nick.contains(nickname)) {
				uuidSet.add(mapNickUUID.get(nick));
			}

		}

		if (!uuidSet.isEmpty()) return Optional.of(uuidSet);

		try {
			for (String nick : nickSet) {

				if (nick.matches(nickname)) {
					uuidSet.add(mapNickUUID.get(nick));
				}

			}
		} catch (PatternSyntaxException e) {
			/*
			 * Its not a valid regex, so we will just say there are no matches!
			 */
		}

		if (!uuidSet.isEmpty()) return Optional.of(uuidSet);

		return Optional.empty();

	}

	/**
	 * Return the UUIDs of players who have names containing characters provided in the name argument
	 * @param name The characters of the name to check
	 * @return An optional which might contain a players UUID if a partial match was found
	 */
	public Optional<Set<UUID>> getPartialNameMatches(String name) {

		UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
		Collection<GameProfile> profiles = uss.getAll();

		//Set<String> nameSet = mapNameUUID.keySet();
		name = stripAllFormattingCodes(name.toLowerCase());
		Set<UUID> uuidSet = new HashSet<UUID>();

		for (GameProfile gp : profiles) {

			Optional<String> opName = gp.getName();
			if (!opName.isPresent()) {
				continue;
			}

			if (opName.get().startsWith(name)) {
				uuidSet.add(gp.getUniqueId());
			}

		}

		if (!uuidSet.isEmpty()) return Optional.of(uuidSet);

		for (GameProfile gp : profiles) {

			Optional<String> opName = gp.getName();
			if (!opName.isPresent()) {
				continue;
			}

			if (opName.get().contains(name)) {
				uuidSet.add(gp.getUniqueId());
			}

		}

		if (!uuidSet.isEmpty()) return Optional.of(uuidSet);

		return Optional.empty();

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

	//TODO SAVE AND LOAD DATA FROM FILE

}
