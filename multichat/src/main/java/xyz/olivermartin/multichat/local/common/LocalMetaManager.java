package xyz.olivermartin.multichat.local.common;

import java.util.UUID;

public abstract class LocalMetaManager {

	public LocalMetaManager() { /* EMPTY */ }

	public String getNick(UUID uuid) {
		return MultiChatLocal.getInstance().getChatManager().translateColorCodes(
				MultiChatLocal.getInstance().getNameManager().getCurrentName(uuid), true);
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
	 * Get the display name of an online player
	 * @param uuid
	 * @return The display name if they are online, or blank if they are not
	 */
	public abstract String getDisplayName(UUID uuid);

}
