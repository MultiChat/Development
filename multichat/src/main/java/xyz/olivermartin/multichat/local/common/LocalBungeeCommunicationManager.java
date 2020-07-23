package xyz.olivermartin.multichat.local.common;

import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.common.communication.CommChannels;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;

/**
 * Allows MultiChatLocal to communicate with a Bungeecord Proxy
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public abstract class LocalBungeeCommunicationManager extends LocalProxyCommunicationManager {

	protected LocalBungeeCommunicationManager(MultiChatLocalPlatform localPlatform) {
		super(localPlatform, MultiChatProxyPlatform.BUNGEE);
	}

	protected abstract boolean sendUUIDAndString(String channel, UUID uuid, String value);

	protected abstract boolean sendUUIDAndStringAndString(String channel, UUID uuid, String value1, String value2);

	protected abstract boolean sendUUIDAndStringAndStringAndString(String channel, UUID uuid, String value1, String value2, String value3);

	protected abstract boolean sendPlatformChatMessage(String channel, UUID uuid, String chatChannel, String message, String format, Set<UUID> otherRecipients);

	protected abstract boolean sendStringAndString(String channel, String string1, String string2);

	protected abstract boolean sendString(String channel, String string);

	@Override
	public void sendMetaUpdate(UUID uuid, String metaId, String metaValue) {
		sendUUIDAndStringAndString(CommChannels.PLAYER_META, uuid, metaId, metaValue);
	}

	@Override
	public void sendProxyExecuteMessage(String command) {
		sendString(CommChannels.SERVER_ACTION, command);
	}

	@Override
	public void sendProxyExecutePlayerMessage(String command, String player) {
		sendStringAndString(CommChannels.PLAYER_ACTION, command, player);
	}

	@Override
	public void sendPlayerChatMessage(UUID uuid, String channel, String message, String format, Set<UUID> otherRecipients) {
		sendPlatformChatMessage(CommChannels.PLAYER_CHAT, uuid, channel, message, format, otherRecipients);
	}

}
