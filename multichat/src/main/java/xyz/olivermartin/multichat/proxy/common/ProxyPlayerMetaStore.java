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
	 * Get the prefix of an online player
	 * @param uuid
	 * @return The prefix if they are online, or blank if they are not
	 */
	public abstract String getPrefix(UUID uuid);

	/**
	 * Get the suffix of an online player
	 * @param uuid
	 * @return The suffix if they are online, or blank if they are not
	 */
	public abstract String getSuffix(UUID uuid);

	/**
	 * Get the world of an online player
	 * @param uuid
	 * @return The world if they are online, or blank if they are not
	 */
	public abstract String getWorld(UUID uuid);

	/**
	 * Gets the nickname if one is set, otherwise gets the username
	 * @param uuid
	 * @return The nickname of the player if they are online, blank if they are not
	 */
	public abstract String getCurrentName(UUID uuid);

	/**
	 * Get the name of an online player
	 * @param uuid
	 * @return The name if they are online, or blank if they are not
	 */
	public abstract String getName(UUID uuid);

	/**
	 * Get the displayname of an online player
	 * @param uuid
	 * @return The displayname if they are online, or blank if they are not
	 */
	public abstract String getDisplayName(UUID uuid);

}
