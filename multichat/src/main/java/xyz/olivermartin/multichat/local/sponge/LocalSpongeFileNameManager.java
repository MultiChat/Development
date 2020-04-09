package xyz.olivermartin.multichat.local.sponge;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;

import xyz.olivermartin.multichat.local.LocalFileNameManager;
import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;

public class LocalSpongeFileNameManager extends LocalFileNameManager {

	public LocalSpongeFileNameManager() {
		super(MultiChatLocalPlatform.SPONGE);
	}

	/**
	 * Returns the FORMATTED NICKNAME of a player if they have one set, otherwise returns their username
	 * 
	 * @param uuid The Unique ID of the player to lookup
	 * @param withPrefix Should the nickname prefix also be returned if it is set?
	 * @return The NICKNAME of the player if it is set, otherwise their username
	 */
	@Override
	public String getCurrentName(UUID uuid, boolean withPrefix) {

		synchronized (mapUUIDNick) {
			if (mapUUIDNick.containsKey(uuid)) {
				if (MultiChatLocal.getInstance().getConfigManager().getLocalConfig().showNicknamePrefix && withPrefix) {
					return MultiChatLocal.getInstance().getConfigManager().getLocalConfig().nicknamePrefix + mapNickFormatted.get(mapUUIDNick.get(uuid));
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
	@Override
	public String getName(UUID uuid) {

		UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
		Optional<User> opUser = uss.get(uuid);

		if (opUser.isPresent()) {
			return opUser.get().getName();
		}

		return "";

	}

	/**
	 * Returns a player's UUID given their username
	 * 
	 * @param username The player's username
	 * @return An optional value which may contain their UUID if the username was found
	 */
	@Override
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
	 * @param uuid
	 * @param username
	 */
	@Override
	public void registerPlayer(UUID uuid, String username) {

		online.add(uuid);

	}

	/**
	 * Register a player into the system without them being online
	 * <p>Used mainly for legacy conversion of old nickname file</p>
	 * @param uuid Player's UUID
	 */
	@Override
	public void registerOfflinePlayerByUUID(UUID uuid, String username) {

		/* This is handled by Sponge */
		return;

	}

	/**
	 * Set the nickname of a player
	 * @param uuid
	 * @param nickname
	 */
	@Override
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
	@Override
	public boolean existsPlayer(String username) {

		UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
		Optional<User> opUser = uss.get(username);

		return opUser.isPresent();
	}

	/**
	 * Return the UUIDs of players who have names containing characters provided in the name argument
	 * @param name The characters of the name to check
	 * @return An optional which might contain a players UUID if a partial match was found
	 */
	@Override
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

}
