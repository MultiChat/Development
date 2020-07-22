package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ChannelManager {

	private NetworkChannel global;
	//private Something local;

	private Map<String, NetworkChannel> channels;
	private Map<UUID, Set<String>> hiddenChannels;

	public ChannelManager() {
		channels = new HashMap<String, NetworkChannel>();
		hiddenChannels = new HashMap<UUID, Set<String>>();
	}

	public NetworkChannel getGlobalChannel() {
		return this.global;
	}

	/*public Something getLocalChannel() {
		return this.local;
	}*/

	public void setGlobalChannel(GlobalChannel global) {
		this.global = global;
		channels.remove("global");
		channels.put("global", global);
	}

	public void hide(UUID uuid, String channelId) {
		Set<String> hidden = hiddenChannels.getOrDefault(uuid, new HashSet<String>());
		hidden.add(channelId);
	}

	public void show(UUID uuid, String channelId) {
		Set<String> hidden = hiddenChannels.getOrDefault(uuid, new HashSet<String>());
		hidden.remove(channelId);
		if (hidden.size() == 0) hiddenChannels.remove(uuid);
	}

	public boolean isHidden(UUID uuid, String channelId) {
		if (!hiddenChannels.containsKey(uuid)) return false;
		Set<String> hidden = hiddenChannels.get(uuid);
		return hidden.contains(channelId);
	}

}
