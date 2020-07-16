package xyz.olivermartin.multichat.local.common;

import java.util.UUID;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;

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

		sendMetaUpdate(uuid, "nick", MultiChatLocal.getInstance().getMetaManager().getNick(uuid));
		sendMetaUpdate(uuid, "world", MultiChatLocal.getInstance().getMetaManager().getWorld(uuid));
		sendMetaUpdate(uuid, "prefix", MultiChatLocal.getInstance().getMetaManager().getPrefix(uuid));
		sendMetaUpdate(uuid, "suffix", MultiChatLocal.getInstance().getMetaManager().getSuffix(uuid));
		sendMetaUpdate(uuid, "dn", MultiChatLocal.getInstance().getMetaManager().getDisplayName(uuid));

	}

	public abstract void sendMetaUpdate(UUID uuid, String metaId, String metaValue);

	public abstract void sendProxyExecuteMessage(String command);

	public abstract void sendProxyExecutePlayerMessage(String command, String player);

	public abstract void sendChatMessage(UUID uuid, String message, String format);

}
