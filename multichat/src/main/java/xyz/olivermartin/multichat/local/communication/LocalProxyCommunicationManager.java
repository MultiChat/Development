package xyz.olivermartin.multichat.local.communication;

import java.util.UUID;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.proxy.MultiChatProxyPlatform;

/**
 * Allows MultiChatLocal to communicate with the Proxy
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public abstract class LocalProxyCommunicationManager {

	private MultiChatProxyPlatform proxyPlatform;
	private MultiChatLocalPlatform localPlatform;

	protected LocalProxyCommunicationManager(MultiChatLocalPlatform localPlatform, MultiChatProxyPlatform proxyPlatform) {
		this.proxyPlatform = proxyPlatform;
		this.localPlatform = localPlatform;
	}

	public MultiChatLocalPlatform getLocalPlatform() {
		return this.localPlatform;
	}

	public MultiChatProxyPlatform getProxyPlatform() {
		return this.proxyPlatform;
	}

	public void updatePlayerMeta(UUID uuid) {

		sendNicknameUpdate(uuid, MultiChatLocal.getInstance().getMetaManager().getNick(uuid));
		sendWorldUpdate(uuid, MultiChatLocal.getInstance().getMetaManager().getWorld(uuid));
		sendPrefixUpdate(uuid, MultiChatLocal.getInstance().getMetaManager().getPrefix(uuid));
		sendSuffixUpdate(uuid, MultiChatLocal.getInstance().getMetaManager().getSuffix(uuid));
		sendDisplayNameUpdate(uuid, MultiChatLocal.getInstance().getMetaManager().getDisplayName(uuid));

	}

	protected abstract void sendNicknameUpdate(UUID uuid, String nickname);

	protected abstract void sendWorldUpdate(UUID uuid, String world);

	protected abstract void sendPrefixUpdate(UUID uuid, String prefix);

	protected abstract void sendSuffixUpdate(UUID uuid, String suffix);

	protected abstract void sendDisplayNameUpdate(UUID uuid, String displayName);

	public abstract void sendProxyExecuteMessage(String command);

	public abstract void sendProxyExecutePlayerMessage(String command, String player);

}
