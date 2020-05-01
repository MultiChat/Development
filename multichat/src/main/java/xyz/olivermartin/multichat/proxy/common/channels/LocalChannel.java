package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.Collection;
import java.util.Iterator;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlayer;

public class LocalChannel extends AbstractChannel {

	private String server;

	public LocalChannel(String id, String name, String server) {
		super(id, name, ChannelType.LOCAL);
		this.server = server;
	}

	public String getServer() {
		return this.server;
	}

	@Override
	protected boolean isPermittedToSendMessage(MultiChatProxyPlayer sender) {
		return sender.getServer().equalsIgnoreCase(server);
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
			if (!viewer.getServer().equalsIgnoreCase(getServer())) {
				it.remove();
			}
		}

		return currentViewers;
	}

}
