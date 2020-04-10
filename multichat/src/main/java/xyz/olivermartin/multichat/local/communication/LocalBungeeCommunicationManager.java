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

	protected LocalBungeeCommunicationManager(MultiChatLocalPlatform localPlatform) {
		super(localPlatform, MultiChatProxyPlatform.BUNGEE);
	}

	protected abstract boolean sendUUIDAndString(String channel, UUID uuid, String value);

	@Override
	protected void sendNicknameUpdate(UUID uuid, String nickname) {
		sendUUIDAndString(nicknameChannel, uuid, nickname);
	}

	@Override
	protected void sendWorldUpdate(UUID uuid, String world) {
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

}
