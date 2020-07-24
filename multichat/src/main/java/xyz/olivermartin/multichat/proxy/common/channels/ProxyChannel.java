package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface ProxyChannel {

	/**
	 * Gets the ID of this proxy channel
	 * @return the id
	 */
	public String getId();

	/**
	 * Gets the info for this proxy channel
	 * @return the info
	 */
	public ProxyChannelInfo getInfo();

	/**
	 * Gets the channel manager for this channel
	 * @return the manager
	 */
	public ChannelManager getManager();

	/**
	 * <p>Distributes the chat message to all remaining players</p>
	 * <p>MultiChat works by first handling the message formatting on each local server.</p>
	 * <p>This message is ONLY sent to the sender locally... It is then forwarded to the proxy to distribute.</p>
	 * @param sender The sender of the message
	 * @param message The message to send
	 * @param format The format before the message part
	 * @param otherRecipients The recipients the message was intended for on the local server (excluding the sender)
	 */
	public void distributeMessage(ProxiedPlayer sender, String message, String format, Set<UUID> otherRecipients);

	/**
	 * <p>Sends a chat message from a player to the local server to be sent</p>
	 * <p>MultiChat works by first handling the message formatting on each local server.</p>
	 * <p>This message is ONLY sent to the sender locally... It is then forwarded to the proxy to distribute.</p>
	 * @param sender The sender of the message
	 * @param message The message to send
	 */
	public void sendMessage(ProxiedPlayer sender, String message);

	/**
	 * <p>Broadcasts a raw message to this channel</p>
	 * <p>This sends a message directly to all permitted viewers on the proxy only</p>
	 * @param sender The sender of the message
	 * @param message The message to send
	 */
	public void broadcastRawMessage(CommandSender sender, String message);

}
