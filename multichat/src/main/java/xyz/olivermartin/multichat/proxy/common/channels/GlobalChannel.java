package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlayer;

public class GlobalChannel extends AbstractChannel {

	private Set<String> serverList;
	private boolean blacklist;

	public GlobalChannel(String id, String name) {
		super(id, name, ChannelType.GLOBAL);
		this.serverList = new HashSet<String>();
		this.blacklist = true;
	}

	public GlobalChannel(String id, String name, boolean blacklist, Set<String> serverList) {
		super(id, name, ChannelType.GLOBAL);
		this.serverList = new HashSet<String>();
		for (String server : serverList) {
			this.serverList.add(server.toLowerCase());
		}
		this.blacklist = blacklist;
	}

	public void addServerToList(String server) {
		this.serverList.add(server.toLowerCase());
	}

	public void removeServerFromList(String server) {
		this.serverList.remove(server.toLowerCase());
	}

	public boolean isPermittedServer(String server) {
		if (blacklist) {
			return !serverList.contains(server.toLowerCase());
		} else {
			return serverList.contains(server.toLowerCase());
		}
	}

	@Override
	protected boolean isPermittedToSendMessage(MultiChatProxyPlayer sender) {
		return isPermittedServer(sender.getServer());
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
			if (!isPermittedServer(viewer.getServer())) {
				it.remove();
			}
		}

		return currentViewers;
	}

}
