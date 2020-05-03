package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.Collection;
import java.util.Iterator;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlayer;
import xyz.olivermartin.multichat.proxy.common.ProxyPlayerManager;
import xyz.olivermartin.multichat.proxy.common.store.ProxyChannelsDataStore;

public abstract class AbstractChannel implements Channel {

	private String id;
	private String name;
	private ChannelType type;

	private boolean blocked;

	public AbstractChannel(String id, String name, ChannelType type) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.blocked = false;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public ChannelType getType() {
		return this.type;
	}

	protected abstract boolean isPermittedToSendMessage(MultiChatProxyPlayer sender);

	protected abstract boolean canAlwaysChat(MultiChatProxyPlayer sender);

	protected abstract Collection<MultiChatProxyPlayer> removeNonPermittedViewers(Collection<MultiChatProxyPlayer> currentViewers);

	protected Collection<MultiChatProxyPlayer> removeHiddenViewers(Collection<MultiChatProxyPlayer> currentViewers) {

		Iterator<MultiChatProxyPlayer> it = currentViewers.iterator();
		ProxyChannelsDataStore channelsDataStore = MultiChatProxy.getInstance().getDataStoreManager().getChannelsDataStore().get();

		while (it.hasNext()) {
			MultiChatProxyPlayer viewer = it.next();
			if (channelsDataStore.hasHiddenChannel(viewer.getUniqueId(), getId())) {
				it.remove();
			}
		}

		return currentViewers;

	}

	@Override
	public void setPlayerMessagesBlocked(boolean blockPlayerMessages) {
		this.blocked = blockPlayerMessages;
	}

	protected Collection<MultiChatProxyPlayer> removeSameServer(Collection<MultiChatProxyPlayer> currentViewers, MultiChatProxyPlayer sender) {

		Iterator<MultiChatProxyPlayer> it = currentViewers.iterator();

		while (it.hasNext()) {
			MultiChatProxyPlayer viewer = it.next();
			if (viewer.getServer().equals(sender.getServer())) {
				it.remove();
			}
		}

		return currentViewers;

	}

	@Override
	public ChannelMessageStatus sendPlayerDistributionMessage(MultiChatProxyPlayer player, String message) {

		if (!isPermittedToSendMessage(player)) return ChannelMessageStatus.NOT_PERMITTED;

		if (this.blocked && !canAlwaysChat(player)) return ChannelMessageStatus.BLOCKED;

		ProxyPlayerManager playerManager = MultiChatProxy.getInstance().getPlayerManager();
		Collection<MultiChatProxyPlayer> onlinePlayers = playerManager.getOnlinePlayers();

		onlinePlayers = removeNonPermittedViewers(onlinePlayers);
		
		// TODO Remove ignored players

		onlinePlayers = removeHiddenViewers(onlinePlayers);

		onlinePlayers = removeSameServer(onlinePlayers, player);
		
		// TODO Add staff members with spy on

		for (MultiChatProxyPlayer p : onlinePlayers) {
			// Plain message means the formatting should already be done...
			p.sendPlainMessage(message);
		}

		return ChannelMessageStatus.SENT;

	}

	@Override
	public ChannelMessageStatus sendPlayerCompleteMessage(MultiChatProxyPlayer player, String message) {

		if (!isPermittedToSendMessage(player)) return ChannelMessageStatus.NOT_PERMITTED;

		if (this.blocked && !canAlwaysChat(player)) return ChannelMessageStatus.BLOCKED;

		ProxyPlayerManager playerManager = MultiChatProxy.getInstance().getPlayerManager();
		Collection<MultiChatProxyPlayer> onlinePlayers = playerManager.getOnlinePlayers();

		onlinePlayers = removeNonPermittedViewers(onlinePlayers);
		
		// TODO Remove ignored players

		onlinePlayers = removeHiddenViewers(onlinePlayers);
		
		// TODO Add staff members with spy on

		for (MultiChatProxyPlayer p : onlinePlayers) {
			// Plain message means the formatting should already be done...
			p.sendPlainMessage(message);
		}

		return ChannelMessageStatus.SENT;

	}

	@Override
	public ChannelMessageStatus sendConsoleMessage(String message) {

		ProxyPlayerManager playerManager = MultiChatProxy.getInstance().getPlayerManager();
		Collection<MultiChatProxyPlayer> onlinePlayers = playerManager.getOnlinePlayers();

		onlinePlayers = removeNonPermittedViewers(onlinePlayers);

		onlinePlayers = removeHiddenViewers(onlinePlayers);

		for (MultiChatProxyPlayer p : onlinePlayers) {
			// Plain message means the formatting should already be done...
			p.sendPlainMessage(message);
		}

		return ChannelMessageStatus.SENT;

	}

	@Override
	public ChannelMessageStatus sendRawMessage(String message) {

		ProxyPlayerManager playerManager = MultiChatProxy.getInstance().getPlayerManager();
		Collection<MultiChatProxyPlayer> onlinePlayers = playerManager.getOnlinePlayers();

		onlinePlayers = removeNonPermittedViewers(onlinePlayers);

		onlinePlayers = removeHiddenViewers(onlinePlayers);

		for (MultiChatProxyPlayer p : onlinePlayers) {
			// Plain message means the formatting should already be done...
			p.sendPlainMessage(message);
		}

		return ChannelMessageStatus.SENT;

	}

}
