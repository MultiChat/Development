package xyz.olivermartin.multichat.proxy.common.channels.proxy;

import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConsoleManager;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.bungee.events.PostBroadcastEvent;
import xyz.olivermartin.multichat.bungee.events.PostGlobalChatEvent;
import xyz.olivermartin.multichat.common.MessageType;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;

public abstract class GenericProxyChannel implements ProxyChannel {

	private String id;
	private ProxyChannelInfo info;
	private ChannelManager manager;

	public GenericProxyChannel(String id, ProxyChannelInfo info, ChannelManager manager) {
		this.id = id;
		this.info = info;
		this.manager = manager;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public ProxyChannelInfo getInfo() {
		return this.info;
	}

	@Override
	public ChannelManager getManager() {
		return this.manager;
	}

	/**
	 * Updates the ChannelInfo used for this channel
	 * @param info The new info for the channel
	 */
	public void updateInfo(ProxyChannelInfo info) {
		this.info = info;
	}

	@Override
	public void distributeMessage(ProxiedPlayer sender, String message, String format, Set<UUID> otherRecipients) {

		// If the sender can't speak, or is between servers, then return
		if (!canSpeak(sender) || sender.getServer() == null) return;

		String senderServer = sender.getServer().getInfo().getName();
		String joined = format + message;

		// TODO This is just a test
		if (sender.hasPermission("multichat.chat.tag")) MultiChatProxy.getInstance().getTagManager().handleTags(message, sender.getName());

		for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {

			// Skip sending to this player if they shouldn't receive the message
			if (receiver.getServer() == null // Receiver is between servers
					|| !canView(receiver) // Receiver is not permitted to view message
					|| manager.isHidden(receiver.getUniqueId(), id)) // Receiver has hidden this channel
				continue;

			// If receiver is on the same server as the sender AND NOT in the other recipients list
			if (receiver.getServer().getInfo().getName().equals(senderServer)
					&& !otherRecipients.contains(receiver.getUniqueId()))
				continue;

			// If receiver ignores sender
			if (ChatControl.ignores(sender.getUniqueId(), receiver.getUniqueId(), MessageType.GLOBAL_CHAT)) {
				ChatControl.sendIgnoreNotifications(receiver, sender, "global_chat");
				continue;
			}

			if (MultiChat.legacyServers.contains(receiver.getServer().getInfo().getName())) {
				receiver.sendMessage(TextComponent.fromLegacyText(MultiChatUtil.approximateRGBColorCodes(joined)));
			} else {
				receiver.sendMessage(TextComponent.fromLegacyText(joined));
			}

		}

		// Trigger PostGlobalChatEvent
		ProxyServer.getInstance().getPluginManager().callEvent(new PostGlobalChatEvent(sender, format, message));

		ConsoleManager.logChat(MultiChatUtil.approximateRGBColorCodes(joined));

	}

	@Override
	public void sendMessage(ProxiedPlayer sender, String message) {
		ProxyLocalCommunicationManager.sendPlayerDirectChatMessage(getId(), sender.getName(), message, sender.getServer().getInfo());
	}

	@Override
	public void broadcastRawMessage(CommandSender sender, String message) {

		// If the sender can't speak then return
		if (!canSpeak(sender)) return;

		message = MultiChatUtil.translateColorCodes(message);

		for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {

			// Skip sending to this player if they shouldn't receive the message
			if (receiver.getServer() == null // Receiver is between servers
					|| !canView(receiver) // Receiver is not permitted to view message
					|| manager.isHidden(receiver.getUniqueId(), id)) // Receiver has hidden this channel
				continue;

			if (MultiChat.legacyServers.contains(receiver.getServer().getInfo().getName())) {
				receiver.sendMessage(TextComponent.fromLegacyText(MultiChatUtil.approximateRGBColorCodes(message)));
			} else {
				receiver.sendMessage(TextComponent.fromLegacyText(message));
			}

		}

		// Trigger PostBroadcastEvent
		ProxyServer.getInstance().getPluginManager().callEvent(new PostBroadcastEvent("cast", message));

		ConsoleManager.logDisplayMessage(message);

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
