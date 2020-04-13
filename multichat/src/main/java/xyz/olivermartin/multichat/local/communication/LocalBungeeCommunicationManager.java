package xyz.olivermartin.multichat.local.communication;

import java.util.UUID;

import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.proxy.MultiChatProxyPlatform;

/**
 * Allows MultiChatLocal to communicate with a Bungeecord Proxy
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public abstract class LocalBungeeCommunicationManager extends LocalProxyCommunicationManager {

	protected final String nicknameChannel = "multichat:nick";
	protected final String worldChannel = "multichat:world";
	protected final String prefixChannel = "multichat:prefix";
	protected final String suffixChannel = "multichat:suffix";
	protected final String displayNameChannel = "multichat:dn";
	protected final String pxeChannel = "multichat:pxe";
	protected final String ppxeChannel = "multichat:ppxe";

	protected LocalBungeeCommunicationManager(MultiChatLocalPlatform localPlatform) {
		super(localPlatform, MultiChatProxyPlatform.BUNGEE);
	}

	protected abstract boolean sendUUIDAndString(String channel, UUID uuid, String value);

	protected abstract boolean sendStringAndString(String channel, String string1, String string2);

	protected abstract boolean sendString(String channel, String string);

	@Override
	protected void sendNicknameUpdate(UUID uuid, String nickname) {
		sendUUIDAndString(nicknameChannel, uuid, nickname);
	}

	@Override
	public void sendWorldUpdate(UUID uuid, String world) {
		sendUUIDAndString(worldChannel, uuid, world);
	}

	@Override
	protected void sendPrefixUpdate(UUID uuid, String prefix) {
		sendUUIDAndString(prefixChannel, uuid, prefix);
	}

	@Override
	protected void sendSuffixUpdate(UUID uuid, String suffix) {
		sendUUIDAndString(suffixChannel, uuid, suffix);
	}

	@Override
	protected void sendDisplayNameUpdate(UUID uuid, String displayName) {
		sendUUIDAndString(displayNameChannel, uuid, displayName);
	}

	@Override
	public void sendProxyExecuteMessage(String command) {
		sendString(pxeChannel, command);
	}

	@Override
	public void sendProxyExecutePlayerMessage(String command, String player) {
		sendStringAndString(ppxeChannel, command, player);
	}

}
