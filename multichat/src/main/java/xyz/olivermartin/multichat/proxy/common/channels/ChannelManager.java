package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.bungee.DebugManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;

public class ChannelManager {

	private NetworkChannel global;
	private LocalChannel local;

	private Map<String, NetworkChannel> channels;
	private Map<UUID, String> selectedChannels;
	private Map<UUID, Set<String>> hiddenChannels;

	public ChannelManager() {
		channels = new HashMap<String, NetworkChannel>();
		selectedChannels = new HashMap<UUID, String>();
		hiddenChannels = new HashMap<UUID, Set<String>>();
	}

	public Optional<NetworkChannel> getChannel(String channelId) {
		return Optional.ofNullable(channels.get(channelId));
	}

	public NetworkChannel getChannel(ProxiedPlayer player) {

		DebugManager.log("Getting channel for: " + player.getName());

		UUID uuid = player.getUniqueId();
		if (selectedChannels.containsKey(uuid)) {
			DebugManager.log("Their UUID has a selected channel...");
			String channel = selectedChannels.get(uuid);
			DebugManager.log("Their channel=" + channel);
			return channels.get(channel);
		} else {
			DebugManager.log("They don't yet have a selected channel");
			ContextManager cm = MultiChatProxy.getInstance().getContextManager();
			String defaultChannel = cm.getContext(player).getDefaultChannel();
			DebugManager.log("Default channel for their context is..." + defaultChannel);
			select(player.getUniqueId(), defaultChannel);
			return channels.get(defaultChannel);
		}
	}

	public NetworkChannel getGlobalChannel() {
		return this.global;
	}

	public LocalChannel getLocalChannel() {
		return this.local;
	}

	public void setGlobalChannel(GlobalChannel global) {
		this.global = global;
		channels.remove("global");
		channels.put("global", global);
	}

	public void setLocalChannel(LocalChannel local) {
		this.local = local;
	}

	public boolean existsChannel(String channelId) {
		if (channelId.equals("local")) return true;
		return channels.containsKey(channelId);
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

	public boolean select(UUID uuid, String channelId) {
		if (existsChannel(channelId)) {
			selectedChannels.put(uuid, channelId);
			return true;
		} else {
			return false;
		}
	}

}
