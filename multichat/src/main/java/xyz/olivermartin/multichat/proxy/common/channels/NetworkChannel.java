package xyz.olivermartin.multichat.proxy.common.channels;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConsoleManager;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.bungee.events.PostBroadcastEvent;
import xyz.olivermartin.multichat.bungee.events.PostGlobalChatEvent;
import xyz.olivermartin.multichat.common.MultiChatUtil;

public abstract class NetworkChannel {

	private String id;
	private ChannelInfo info;
	private ChannelManager manager;

	public NetworkChannel(String id, ChannelInfo info, ChannelManager manager) {
		this.id = id;
		this.info = info;
		this.manager = manager;
	}

	/**
	 * Gets the ID of this channel
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Gets the info for this channel
	 * @return the info
	 */
	public ChannelInfo getInfo() {
		return this.info;
	}

	/**
	 * Updates the ChannelInfo used for this channel
	 * @param info The new info for the channel
	 */
	public void updateInfo(ChannelInfo info) {
		this.info = info;
	}

	/**
	 * Gets the manager for this channel
	 * @return the manager
	 */
	public ChannelManager getManager() {
		return this.manager;
	}

	public void sendMessage(CommandSender sender, String message) {

		// If the sender can't speak then return
		if (!canSpeak(sender)) return;

		for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {

			// Skip sending to this player if they shouldn't receive the message
			if (receiver.getServer() == null // Receiver is between servers
					|| !canView(receiver) // Receiver is not permitted to view message
					|| manager.isHidden(receiver.getUniqueId(), id)) // Receiver has hidden this channel
				continue;

			if (MultiChat.legacyServers.contains(receiver.getServer().getInfo().getName())) {
				message = MultiChatUtil.approximateHexCodes(message);
			}

			receiver.sendMessage(TextComponent.fromLegacyText(message));

		}

		// Trigger PostBroadcastEvent
		ProxyServer.getInstance().getPluginManager().callEvent(new PostBroadcastEvent("cast", message));

		ConsoleManager.logDisplayMessage(message);

	}

	public void distributeMessage(ProxiedPlayer sender, String message, String format) {

		// If the sender can't speak, or is between servers, then return
		if (!canSpeak(sender) || sender.getServer() == null) return;

		String senderServer = sender.getServer().getInfo().getName();
		String joined = format + message;

		for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {

			// Skip sending to this player if they shouldn't receive the message
			if (receiver.getServer() == null // Receiver is between servers
					|| !canView(receiver) // Receiver is not permitted to view message
					|| manager.isHidden(receiver.getUniqueId(), id) // Receiver has hidden this channel
					|| receiver.getServer().getInfo().getName().equals(senderServer)) // Receiver is on same server as sender
				continue;

			// If receiver ignores sender
			if (ChatControl.ignores(sender.getUniqueId(), receiver.getUniqueId(), "global_chat")) {
				ChatControl.sendIgnoreNotifications(receiver, sender, "global_chat");
				continue;
			}

			if (MultiChat.legacyServers.contains(receiver.getServer().getInfo().getName())) {
				joined = MultiChatUtil.approximateHexCodes(joined);
			}

			receiver.sendMessage(TextComponent.fromLegacyText(joined));

		}

		// Trigger PostGlobalChatEvent
		ProxyServer.getInstance().getPluginManager().callEvent(new PostGlobalChatEvent(sender, format, message));

		ConsoleManager.logChat(MultiChatUtil.approximateHexCodes(joined));

	}

	/**
	 * Checks if this command sender is allowed to speak in the channel
	 * @param sender The command sender
	 * @return true if they are allowed to speak
	 */
	public abstract boolean canSpeak(CommandSender sender);

	/**
	 * Checks if this command sender is allowed to view the channel
	 * @param sender The command sender
	 * @return true if they are allowed to view
	 */
	public abstract boolean canView(CommandSender sender);

}
