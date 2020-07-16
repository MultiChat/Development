package xyz.olivermartin.multichat.local.common;

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

	protected final String pxeChannel = "multichat:pxe";
	protected final String ppxeChannel = "multichat:ppxe";
	protected final String chatChannel = "multichat:chat";

	protected LocalBungeeCommunicationManager(MultiChatLocalPlatform localPlatform) {
		super(localPlatform, MultiChatProxyPlatform.BUNGEE);
	}

	protected abstract boolean sendUUIDAndString(String channel, UUID uuid, String value);

	protected abstract boolean sendUUIDAndStringAndString(String channel, UUID uuid, String value1, String value2);

	protected abstract boolean sendStringAndString(String channel, String string1, String string2);

	protected abstract boolean sendString(String channel, String string);

	@Override
	public void sendMetaUpdate(UUID uuid, String metaId, String metaValue) {
		sendUUIDAndStringAndString(CommChannels.getPlayerMeta(), uuid, metaId, metaValue);
	}

	@Override
	public void sendProxyExecuteMessage(String command) {
		sendString(pxeChannel, command);
	}

	@Override
	public void sendProxyExecutePlayerMessage(String command, String player) {
		sendStringAndString(ppxeChannel, command, player);
	}

	@Override
	public void sendChatMessage(UUID uuid, String message, String format) {
		sendUUIDAndStringAndString(chatChannel, uuid, message, format);
	}

}
