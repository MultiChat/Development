package xyz.olivermartin.multichat.local;

import java.util.UUID;

public abstract class LocalPlaceholderManager {
	
	private MultiChatLocalPlatform platform;
	
	public LocalPlaceholderManager(MultiChatLocalPlatform platform) {
		this.platform = platform;
	}
	
	public MultiChatLocalPlatform getPlatform() {
		return this.platform;
	}

	/**
	 * This method builds the format for a chat message on the respective platform
	 * 
	 * <p>This should respect other plugins where possible, for example replacing displayname with %1$s on spigot.</p>
	 * 
	 * @param uuid
	 * @param format
	 * @return The built chat format
	 */
	public abstract String buildChatFormat(UUID uuid, String format);
	
	/**
	 * This method replaces all placeholders according to MultiChat's rules (doesn't pay attention to other plugins)
	 * 
	 * @param uuid
	 * @param message
	 * @return The message with all MultiChat placeholders replaced
	 */
	public String processMultiChatPlaceholders(UUID uuid, String message) {
		
		message = message.replace("%NAME%", MultiChatLocal.getInstance().getNameManager().getName(uuid));
		message = message.replace("%NICK%", MultiChatLocal.getInstance().getMetaManager().getNick(uuid));
		message = message.replace("%DISPLAYNAME%", MultiChatLocal.getInstance().getMetaManager().getDisplayName(uuid));
		message = message.replace("%PREFIX%", MultiChatLocal.getInstance().getMetaManager().getPrefix(uuid));
		message = message.replace("%SUFFIX%", MultiChatLocal.getInstance().getMetaManager().getSuffix(uuid));
		message = message.replace("%WORLD%", MultiChatLocal.getInstance().getMetaManager().getWorld(uuid));
		message = message.replace("%SERVER%", MultiChatLocal.getInstance().getConfigManager().getLocalConfig().serverName);
		
		return message;
		
	}
	
}
