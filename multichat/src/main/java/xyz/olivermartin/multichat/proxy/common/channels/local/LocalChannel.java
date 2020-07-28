package xyz.olivermartin.multichat.proxy.common.channels.local;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;

public class LocalChannel {

	private String desc;
	private String format;
	private List<String> aliases;

	private ChannelManager manager;

	public LocalChannel(String desc, String format, List<String> aliases, ChannelManager manager) {

		this.desc = desc;
		this.format = format;
		this.aliases = aliases;

		this.manager = manager;

	}

	public String getDescription() {
		return this.desc;
	}

	public String getFormat() {
		return this.format;
	}

	public List<String> getAliases() {
		return this.aliases;
	}

	public ChannelManager getManager() {
		return this.manager;
	}

	public void distributeMessage(ProxiedPlayer sender, String message, String format, Set<UUID> otherRecipients) {

		// If the sender can't speak, or is between servers, then return
		if (sender.getServer() == null) return;

		String joined = format + message;

		for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {

			// Skip sending to this player if they shouldn't receive the message
			if (receiver.getServer() == null // Receiver is between servers
					|| manager.isHidden(receiver.getUniqueId(), "local")) // Receiver has hidden this channel
				continue;

			// If receiver is sender then ignore them
			if (receiver.getUniqueId().equals(sender.getUniqueId())) continue;

			// If receiver is NOT in the other recipients list then leave processing (as this is local only) (unless they are spying)
			if (!otherRecipients.contains(receiver.getUniqueId())
					&& !manager.isLocalSpy(receiver)) continue;

			// If receiver ignores sender
			if (ChatControl.ignores(sender.getUniqueId(), receiver.getUniqueId(), "global_chat")) {
				ChatControl.sendIgnoreNotifications(receiver, sender, "global_chat");
				continue;
			}

			String finalMessage = joined;

			if (manager.isLocalSpy(receiver)
					&& !otherRecipients.contains(receiver.getUniqueId())) {
				finalMessage = MultiChatProxy.getInstance().getChatManager().getLocalSpyMessage(sender, format, message);
			}

			if (MultiChat.legacyServers.contains(receiver.getServer().getInfo().getName())) {
				receiver.sendMessage(TextComponent.fromLegacyText(MultiChatUtil.approximateHexCodes(finalMessage)));
			} else {
				receiver.sendMessage(TextComponent.fromLegacyText(finalMessage));
			}

		}

	}

	public void sendMessage(ProxiedPlayer sender, String message) {
		ProxyLocalCommunicationManager.sendPlayerDirectChatMessage("local", sender.getName(), message, sender.getServer().getInfo());
	}

	public void broadcastRawMessage(CommandSender sender, String server, String message) {

		message = MultiChatUtil.reformatRGB(message);
		message = ChatColor.translateAlternateColorCodes('&', message);

		for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {

			// Skip sending to this player if they shouldn't receive the message
			if (receiver.getServer() == null // Receiver is between servers
					|| manager.isHidden(receiver.getUniqueId(), "local")) // Receiver has hidden this channel
				continue;

			// If not on specified server then return (unless spying)
			if (!receiver.getServer().getInfo().getName().equals(server) && !manager.isLocalSpy(receiver)) continue;

			String finalMessage = message;

			if (manager.isLocalSpy(receiver) && !receiver.getServer().getInfo().getName().equals(server)) {
				finalMessage = MultiChatProxy.getInstance().getChatManager().getLocalSpyMessage(sender, message);
			}

			if (MultiChat.legacyServers.contains(receiver.getServer().getInfo().getName())) {
				receiver.sendMessage(TextComponent.fromLegacyText(MultiChatUtil.approximateHexCodes(finalMessage)));
			} else {
				receiver.sendMessage(TextComponent.fromLegacyText(finalMessage));
			}

		}

	}

}
