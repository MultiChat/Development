package xyz.olivermartin.multichat.proxy.common;

import java.util.Optional;

import org.bukkit.ChatColor;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.DebugManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.PlayerMeta;
import xyz.olivermartin.multichat.bungee.PlayerMetaManager;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;

public class ProxyChatManager {

	public ProxyChatManager() {
		/* EMPTY */
	}

	/**
	 * Check if the player has permission to use simple colour codes in chat
	 * @param player The player to check
	 * @return true if they have permission
	 */
	public boolean hasSimpleColourPermission(ProxiedPlayer player) {
		return player.hasPermission("multichat.chat.colour.simple")
				|| player.hasPermission("multichat.chat.color.simple")
				|| hasRGBColourPermission(player);
	}

	/**
	 * Check if the player has permission to use RGB colour codes in chat
	 * @param player The player to check
	 * @return true if they have permission
	 */
	public boolean hasRGBColourPermission(ProxiedPlayer player) {
		return player.hasPermission("multichat.chat.colour.rgb")
				|| player.hasPermission("multichat.chat.color.rgb")
				|| hasLegacyColourPermission(player);
	}

	/**
	 * Check if the player has permission to use all colour codes in chat (legacy permission)
	 * @param player The player to check
	 * @return true if they have permission
	 */
	public boolean hasLegacyColourPermission(ProxiedPlayer player) {
		return player.hasPermission("multichat.chat.colour")
				|| player.hasPermission("multichat.chat.color");
	}

	/**
	 * <p>Check if this player is allowed to send a chat message</p>
	 * <p>Possible reasons for not being allowed are:
	 * <ul>
	 *     <li>Chat is frozen</li>
	 *     <li>Player is muted by MultiChat</li>
	 *     <li>Player is spamming</li>
	 * </ul>
	 * </p>
	 * @param player The player to check
	 * @param message The message they are trying to send
	 * @return true if they are allowed to send a message
	 */
	private boolean canPlayerSendChat(ProxiedPlayer player, String message) {

		// Check if chat is frozen
		if (MultiChatProxy.getInstance().getDataStore().isChatFrozen() && !player.hasPermission("multichat.chat.always")) {
			MessageManager.sendMessage(player, "freezechat_frozen");
			return false;
		}

		// Check if they are muted by MultiChat
		if (ChatControl.isMuted(player.getUniqueId(), "global_chat")) {
			MessageManager.sendMessage(player, "mute_cannot_send_message");
			return false;
		}

		// Check if they are spamming
		if (ChatControl.handleSpam(player, message, "global_chat")) {
			DebugManager.log(player.getName() + " - chat message being cancelled due to spam");
			return false;
		}

		return true;

	}

	/**
	 * <p>Pre-processes a chat message before sending it</p>
	 * <p>This includes the following:
	 * <ul>
	 *     <li>Applying regex rules and actions</li>
	 *     <li>Filtering links if they player does not have permission</li>
	 * </ul>
	 * </p>
	 * @param player The player to check
	 * @param message The message they are trying to send
	 * @return the new processed string if they are allowed to send the message, or empty if the message should be cancelled
	 */
	private Optional<String> preProcessMessage(ProxiedPlayer player, String message) {

		Optional<String> crm;

		crm = ChatControl.applyChatRules(message, "global_chat", player.getName());

		if (crm.isPresent()) {
			message = crm.get();
		} else {
			return Optional.empty();
		}

		if (!player.hasPermission("multichat.chat.link")) {
			message = ChatControl.replaceLinks(message);
		}

		return Optional.of(message);

	}

	/**
	 * <p>Handles the process of getting a message ready to send from the proxy</p>
	 * <p>This includes the following:
	 * <ul>
	 *     <li>Makes sure chat is not frozen</li>
	 *     <li>Makes sure the player is not muted by MultiChat</li>
	 *     <li>Makes sure the player is not spamming</li>
	 *     <li>Applies regex rules and actions to message</li>
	 *     <li>Filters links if they player does not have permission</li>
	 * </ul>
	 * </p>
	 * @param player The player to check
	 * @param message The message they are trying to send
	 * @return the new processed string if they are allowed to send the message, or empty if the message should be cancelled
	 */
	public Optional<String> handleChatMessage(ProxiedPlayer player, String message) {

		if (!canPlayerSendChat(player, message)) {
			return Optional.empty();
		}

		return preProcessMessage(player, message);

	}

	public String getLocalSpyMessage(ProxiedPlayer player, String format, String message) {

		String spyFormat = ConfigManager.getInstance().getHandler("config.yml").getConfig()
				.getString(ConfigValues.Config.LOCAL_SPY_FORMAT, "&8[&7SPY&8] %FORMAT%");

		spyFormat = spyFormat.replace("%FORMAT%", format);
		spyFormat = spyFormat.replace("%DISPLAYNAME%", player.getDisplayName());
		spyFormat = spyFormat.replace("%NAME%", player.getName());

		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(player.getUniqueId());
		if (opm.isPresent()) {
			spyFormat = spyFormat.replace("%PREFIX%", opm.get().prefix);
			spyFormat = spyFormat.replace("%SUFFIX%", opm.get().suffix);
			spyFormat = spyFormat.replace("%NICK%", opm.get().nick);
			spyFormat = spyFormat.replace("%WORLD%", opm.get().world);
		}

		spyFormat = spyFormat.replace("%SERVER%", player.getServer().getInfo().getName());
		spyFormat = ChatColor.translateAlternateColorCodes('&', spyFormat);

		// Append message
		spyFormat = spyFormat + message;

		return spyFormat;

	}

	public String getLocalSpyMessage(CommandSender sender, String message) {

		String spyFormat = ConfigManager.getInstance().getHandler("config.yml").getConfig()
				.getString(ConfigValues.Config.LOCAL_SPY_FORMAT, "&8[&7SPY&8] %FORMAT%");

		spyFormat = spyFormat.replace("%FORMAT%", "");
		spyFormat = spyFormat.replace("%DISPLAYNAME%", sender.getName());
		spyFormat = spyFormat.replace("%NAME%", sender.getName());

		spyFormat = spyFormat.replace("%PREFIX%", "");
		spyFormat = spyFormat.replace("%SUFFIX%", "");
		spyFormat = spyFormat.replace("%NICK%",  sender.getName());
		spyFormat = spyFormat.replace("%WORLD%", "");

		spyFormat = spyFormat.replace("%SERVER%", "");
		spyFormat = ChatColor.translateAlternateColorCodes('&', spyFormat);

		return spyFormat + message;

	}

}
