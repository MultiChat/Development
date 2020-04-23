package xyz.olivermartin.multichat.proxy.common.channels;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlayer;

public interface Channel {

	/**
	 * The ID used for this channel. Must be a unique string.
	 * @return ID
	 */
	public String getId();

	/**
	 * Gets the formatted name of this channel to use when displayed.
	 * This does not have to be unique.
	 * @return The channel name
	 */
	public String getName();

	/**
	 * Gets the type of this channel
	 * @return The type of the channel.
	 */
	public ChannelType getType();

	/**
	 * Send a message from a player to this channel to be distributed to remaining servers
	 * 
	 * <p>This means the message will be displayed on all servers that are NOT the one the player is currently on.</p>
	 * <p>The message should be handled for the originating server by MultiChatLocal</p>
	 * <p>If you do NOT want this functionality, use sendPlayerCompleteMessage</p>
	 * 
	 * @param player The player sending the message
	 * @param message The message, fully formatted and ready to send
	 * @return True if the message can be sent, False otherwise
	 */
	public ChannelMessageStatus sendPlayerDistributionMessage(MultiChatProxyPlayer player, String message);

	/**
	 * Send a message from a player to this channel to be distributed to ALL servers
	 * 
	 * <p>This means the message will be displayed on ALL servers from the Proxy side</p>
	 * <p>The message will not be picked up by local servers chat streams</p>
	 * <p>If you do NOT want this functionality, use sendPlayerDistributionMessage</p>
	 * 
	 * @param player The player sending the message
	 * @param message The message, fully formatted and ready to send
	 * @return True if the message can be sent, False otherwise
	 */
	public ChannelMessageStatus sendPlayerCompleteMessage(MultiChatProxyPlayer player, String message);

	/**
	 * Send a message to a channel as the server console.
	 * @param message The message, fully formatted and ready to send
	 * @return True if the message can be sent, False otherwise
	 */
	public ChannelMessageStatus sendConsoleMessage(String message);

	/**
	 * Send a message to a channel.
	 * @param message The message, fully formatted and ready to send
	 * @return True if the message can be sent, False otherwise
	 */
	public ChannelMessageStatus sendRawMessage(String message);

	/**
	 * Block player chat to this channel, except those with override permissions.
	 */
	public void setPlayerMessagesBlocked(boolean blockPlayerMessages);

}
