package xyz.olivermartin.multichat.local.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * MultiChatLocal's Name Manager
 * 
 * <p>Manages players' names, nicknames, uuids etc.</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public abstract class LocalNameManager {

	protected LocalNameManagerMode mode;
	protected List<UUID> online;

	protected LocalNameManager(LocalNameManagerMode mode) {

		this.mode = mode;
		this.online = new ArrayList<UUID>();

	}

	public LocalNameManagerMode getMode() {
		return this.mode;
	}

	/**
	 * Returns the FORMATTED NICKNAME (WITH PREFIX IF SET) of a player if they have one set, otherwise returns their username
	 * 
	 * @param uuid The Unique ID of the player to lookup
	 * @return The NICKNAME of the player if it is set, otherwise their username
	 */
	public String getCurrentName(UUID uuid) {
		return getCurrentName(uuid, true);
	}

	/**
	 * Returns the FORMATTED NICKNAME of a player if they have one set, otherwise returns their username
	 * 
	 * @param uuid The Unique ID of the player to lookup
	 * @param withPrefix Should the nickname prefix also be returned if it is set?
	 * @return The NICKNAME of the player if it is set, otherwise their username
	 */
	public abstract String getCurrentName(UUID uuid, boolean withPrefix);

	/**
	 * Returns the username of a player
	 * 
	 * @param uuid The Unique ID of the player to lookup
	 * @return The username of the player
	 */
	public abstract String getName(UUID uuid);

	/**
	 * Gets the UUID of a player from their UNFORMATTED nickname
	 * THIS MEANS THE NICKNAME PROVIDED MUST BE IN LOWERCASE WITH ALL FORMATTING CODES REMOVED
	 * 
	 * @param nickname The UNFORMATTED nickname of the player
	 * @return An optional which may contain their UUID if the nickname was found in the system
	 */
	protected abstract Optional<UUID> getUUIDFromUnformattedNickname(String nickname);

	/**
	 * Returns a player's UUID given their username
	 * 
	 * @param username The player's username
	 * @return An optional value which may contain their UUID if the username was found
	 */
	public abstract Optional<UUID> getUUIDFromName(String username);

	/**
	 * Gets a player's UUID from their nickname
	 * 
	 * @param nickname The player's nickname (which may contain formatting codes etc.)
	 * @return An optional value which may contain their UUID if the nickname was found
	 */
	public Optional<UUID> getUUIDFromNickname(String nickname) {

		nickname = nickname.toLowerCase();
		nickname = stripAllFormattingCodes(nickname);

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
	 * Gets a player's formatted name from their username
	 * 
	 * @param name The player's username
	 * @return An optional value which may contain their name, as long as their username can be found
	 */
	public Optional<String> getFormattedNameFromName(String username) {

		username = username.toLowerCase();

		Optional<UUID> oUUID = getUUIDFromName(username);

		if (!oUUID.isPresent()) {
			return Optional.empty();
		}

		UUID uuid = oUUID.get();

		return Optional.of(getName(uuid));

	}

	/**
	 * Register a player as online
	 * <p>Also performs any setup needed to equip nicknames etc.</p>
	 * @param uuid The Unique Id of the player
	 * @param username The formatted username of the player
	 */
	public abstract void registerPlayer(UUID uuid, String username);

	/**
	 * Register a player into the system without them being online
	 * <p>Used mainly for legacy conversion of old nickname file</p>
	 * @param uuid Player's UUID
	 */
	public abstract void registerOfflinePlayerByUUID(UUID uuid, String username);

	/**
	 * Register a player as offline
	 * @param uuid The unique id of the player
	 */
	public abstract void unregisterPlayer(UUID uuid);

	/**
	 * Set the nickname of a player
	 * @param uuid
	 * @param nickname
	 */
	public abstract void setNickname(UUID uuid, String nickname);

	/**
	 * @param username
	 * @return If this player has logged into the server before
	 */
	public abstract boolean existsPlayer(String username);

	/**
	 * @param nickname
	 * @return If this nickname is currently in use
	 */
	public abstract boolean existsNickname(String nickname);

	/**
	 * Return the UUIDs of players who have nicknames containing characters provided in the nickname argument
	 * @param nickname The characters of the nickname to check
	 * @return An optional which might contain a players UUID if a partial match was found
	 */
	public abstract Optional<Set<UUID>> getPartialNicknameMatches(String nickname);

	/**
	 * Return the UUIDs of players who have names containing characters provided in the name argument
	 * @param name The characters of the name to check
	 * @return An optional which might contain a players UUID if a partial match was found
	 */
	public abstract Optional<Set<UUID>> getPartialNameMatches(String name);

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
	public abstract void removeNickname(UUID uuid);

	/*
	 * Remove all colour / format codes from a string (using the '&' char)
	 */
	public String stripAllFormattingCodes(String input) {

		char COLOR_CHAR = '&';
		Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-OR]");

		if (input == null) {
			return null;
		}

		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");

	}

	/**
	 * @param input
	 * @return True if the input contains colour codes (e.g. '&a')
	 */
	public boolean containsColorCodes(String input) {

		char COLOR_CHAR = '&';
		Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-F]");

		if (input == null) {
			return false;
		}

		return !STRIP_COLOR_PATTERN.matcher(input).replaceAll("").equals(input);

	}

	/**
	 * @param input
	 * @return True if the input contains format codes (e.g. '&l')
	 */
	public boolean containsFormatCodes(String input) {

		char COLOR_CHAR = '&';
		Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[K-OR]");

		if (input == null) {
			return false;
		}

		return !STRIP_COLOR_PATTERN.matcher(input).replaceAll("").equals(input);

	}

}
