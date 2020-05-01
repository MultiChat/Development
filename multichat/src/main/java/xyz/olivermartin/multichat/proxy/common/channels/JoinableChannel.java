package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlayer;
import xyz.olivermartin.multichat.proxy.common.store.ProxyChannelsDataStore;

public class JoinableChannel extends AbstractChannel {

	private ProxyChannelsDataStore channelsDataStore;
	private boolean blacklist;

	public JoinableChannel(String id, String name, boolean blacklist) {
		super(id, name, ChannelType.JOINABLE);
		this.blacklist = blacklist;
		this.channelsDataStore = MultiChatProxy.getInstance().getDataStoreManager().getChannelsDataStore().get();
	}

	public boolean isChannelMember(UUID uuid) {
		if (blacklist) {
			return !channelsDataStore.isInJoinedChannelList(uuid, this.getId());
		} else {
			return channelsDataStore.isInJoinedChannelList(uuid, this.getId());
		}
	}

	@Override
	protected boolean isPermittedToSendMessage(MultiChatProxyPlayer sender) {
		return isChannelMember(sender.getUniqueId());
	}

	@Override
	protected boolean canAlwaysChat(MultiChatProxyPlayer sender) {
		return sender.hasProxyPermission("multichat.chat.always");
	}

	@Override
	protected Collection<MultiChatProxyPlayer> removeNonPermittedViewers(
			Collection<MultiChatProxyPlayer> currentViewers) {

		Iterator<MultiChatProxyPlayer> it = currentViewers.iterator();

		while (it.hasNext()) {
			MultiChatProxyPlayer viewer = it.next();
			if (!isChannelMember(viewer.getUniqueId())) {
				it.remove();
			}
		}

		return currentViewers;

	}

	public void joinChannel(UUID uuid) {
		if (!blacklist) channelsDataStore.addToJoinedChannelList(uuid, getId());
	}

	public void leaveChannel(UUID uuid) {
		if (blacklist) channelsDataStore.addToJoinedChannelList(uuid, getId());
	}

}
