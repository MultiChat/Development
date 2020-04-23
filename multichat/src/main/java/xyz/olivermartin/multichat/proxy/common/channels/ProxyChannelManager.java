package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class ProxyChannelManager {

	private Map<String, Channel> channels;

	public Collection<Channel> getChannels() {
		return this.channels.values();
	}

	public Optional<Channel> getChannel(String id) {
		if (channels.containsKey(id)) return Optional.of(this.channels.get(id));
		return Optional.empty();
	}

	public void registerChannel(Channel channel) {
		channels.put(channel.getId(), channel);
	}

	public void unregisterChannel(String id) {
		channels.remove(id);
	}

}
