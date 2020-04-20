package xyz.olivermartin.multichat.proxy.common;

import java.util.Optional;
import java.util.UUID;

public abstract class ProxyPlayerMetaStore {

	private ProxyPlayerMetaStoreMode mode;

	public ProxyPlayerMetaStore(ProxyPlayerMetaStoreMode mode) {
		this.mode = mode;
	}

	public ProxyPlayerMetaStoreMode getMode() {
		return this.mode;
	}

	/**
	 * Register a player as being online with the meta store
	 * @param uuid
	 */
	public abstract void registerPlayer(UUID uuid);

	/**
	 * Register a player as being offline with the meta store
	 * @param uuid
	 * @param name
	 */
	public abstract void unregisterPlayer(UUID uuid);

	/**
	 * Check if a player is currently registered as being online
	 * @param uuid
	 */
	public abstract boolean isOnline(UUID uuid);

	/**
	 * Get the prefix of an online player
	 * @param uuid
	 * @return An optional that may contain the prefix of the player
	 */
	public abstract Optional<String> getPrefix(UUID uuid);

	/**
	 * Offer a value for the prefix for this player
	 * Note, if the offer will be used or discarded is dependent on the MODE of the PlayerMetaStore
	 * @param uuid
	 * @param prefix
	 */
	public abstract void offerPrefix(UUID uuid, String prefix);

	/**
	 * Get the suffix of an online player
	 * @param uuid
	 * @return An optional that may contain the suffix of the player
	 */
	public abstract Optional<String> getSuffix(UUID uuid);

	/**
	 * Offer a value for the suffix for this player
	 * Note, if the offer will be used or discarded is dependent on the MODE of the PlayerMetaStore
	 * @param uuid
	 * @param suffix
	 */
	public abstract void offerSuffix(UUID uuid, String suffix);

	/**
	 * Get the world of an online player
	 * @param uuid
	 * @return An optional that may contain the world of the player
	 */
	public abstract Optional<String> getWorld(UUID uuid);

	/**
	 * Offer a value for the world for this player
	 * Note, if the offer will be used or discarded is dependent on the MODE of the PlayerMetaStore
	 * @param uuid
	 * @param world
	 */
	public abstract void offerWorld(UUID uuid, String world);

	/**
	 * Gets the nickname if one is set
	 * @param uuid
	 * @return An optional that may contain the nickname of the player
	 */
	public abstract Optional<String> getNickname(UUID uuid);

	/**
	 * Offer a value for the nickname for this player
	 * Note, if the offer will be used or discarded is dependent on the MODE of the PlayerMetaStore
	 * @param uuid
	 * @param nickname
	 */
	public abstract void offerNickname(UUID uuid, String nickname);

	/**
	 * Get the display name of an online player
	 * @param uuid
	 * @return An optional which may contain the display name of the player
	 */
	public abstract Optional<String> getDisplayName(UUID uuid);

	/**
	 * Offer a value for the display name for this player
	 * Note, if the offer will be used or discarded is dependent on the MODE of the PlayerMetaStore
	 * @param uuid
	 * @param displayname
	 */
	public abstract void offerDisplayName(UUID uuid, String displayName);

}
