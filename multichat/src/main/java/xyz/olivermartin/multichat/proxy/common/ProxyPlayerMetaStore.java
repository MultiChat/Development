package xyz.olivermartin.multichat.proxy.common;

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
	 * @param name
	 */
	public abstract void registerPlayer(UUID uuid, String name);

	/**
	 * Register a player as being offline with the meta store
	 * @param uuid
	 * @param name
	 */
	public abstract void unregisterPlayer(UUID uuid);

	/**
	 * Get the prefix of an online player
	 * @param uuid
	 * @return The prefix if they are online, or blank if they are not
	 */
	public abstract String getPrefix(UUID uuid);

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
	 * @return The suffix if they are online, or blank if they are not
	 */
	public abstract String getSuffix(UUID uuid);

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
	 * @return The world if they are online, or blank if they are not
	 */
	public abstract String getWorld(UUID uuid);

	/**
	 * Offer a value for the world for this player
	 * Note, if the offer will be used or discarded is dependent on the MODE of the PlayerMetaStore
	 * @param uuid
	 * @param world
	 */
	public abstract void offerWorld(UUID uuid, String world);

	/**
	 * Gets the nickname if one is set, otherwise gets the username
	 * @param uuid
	 * @return The nickname of the player if they are online, blank if they are not
	 */
	public abstract String getCurrentName(UUID uuid);

	/**
	 * Offer a value for the current name for this player
	 * Note, if the offer will be used or discarded is dependent on the MODE of the PlayerMetaStore
	 * @param uuid
	 * @param currentName
	 */
	public abstract void offerCurrentName(UUID uuid, String currentName);

	/**
	 * Get the name of an online player
	 * @param uuid
	 * @return The name if they are online, or blank if they are not
	 */
	public abstract String getName(UUID uuid);

	/**
	 * Offer a value for the name for this player
	 * Note, if the offer will be used or discarded is dependent on the MODE of the PlayerMetaStore
	 * @param uuid
	 * @param name
	 */
	public abstract void offerName(UUID uuid, String name);

	/**
	 * Get the display name of an online player
	 * @param uuid
	 * @return The display name if they are online, or blank if they are not
	 */
	public abstract String getDisplayName(UUID uuid);

	/**
	 * Offer a value for the display name for this player
	 * Note, if the offer will be used or discarded is dependent on the MODE of the PlayerMetaStore
	 * @param uuid
	 * @param displayname
	 */
	public abstract void offerDisplayName(UUID uuid, String displayName);

}
