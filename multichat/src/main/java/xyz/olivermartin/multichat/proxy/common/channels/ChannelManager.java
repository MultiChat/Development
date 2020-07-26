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

	// Local Channel Management
	private LocalChannel local;

	// Proxy Channel Management
	private Map<String, ProxyChannel> proxyChannels;
	private GlobalStaticProxyChannel global;

	// Player preferences for channels
	private Map<UUID, String> selectedChannels;
	private Map<UUID, Set<String>> hiddenChannels;

	public ChannelManager() {
		proxyChannels = new HashMap<String, ProxyChannel>();
		selectedChannels = new HashMap<UUID, String>();
		hiddenChannels = new HashMap<UUID, Set<String>>();
	}

	public Optional<ProxyChannel> getProxyChannel(String channelId) {
		return Optional.ofNullable(proxyChannels.get(channelId));
	}

	public String getChannel(ProxiedPlayer player) {

		DebugManager.log("Getting channel for: " + player.getName());
		UUID uuid = player.getUniqueId();

		if (selectedChannels.containsKey(uuid)) {
			DebugManager.log("Their UUID has a selected channel...");
			String channel = selectedChannels.get(uuid);
			DebugManager.log("Their channel=" + channel);
			return channel;
		} else {
			DebugManager.log("They don't yet have a selected channel");
			ContextManager cm = MultiChatProxy.getInstance().getContextManager();
			String defaultChannel = cm.getContext(player).getDefaultChannel();
			DebugManager.log("Default channel for their context is..." + defaultChannel);
			select(player.getUniqueId(), defaultChannel);
			return defaultChannel;
		}
	}

	public ChannelMode getChannelMode(UUID uuid) {

		if (selectedChannels.containsKey(uuid)) {
			if (selectedChannels.get(uuid).equals("local")) return ChannelMode.LOCAL;
		}

		return ChannelMode.PROXY;

	}

	public GlobalStaticProxyChannel getGlobalChannel() {
		return this.global;
	}

	public LocalChannel getLocalChannel() {
		return this.local;
	}

	public void setGlobalChannel(GlobalStaticProxyChannel global) {
		this.global = global;
		proxyChannels.remove("global");
		proxyChannels.put("global", global);
	}

	public void setLocalChannel(LocalChannel local) {
		this.local = local;
	}

	public boolean existsProxyChannel(String channelId) {
		return proxyChannels.containsKey(channelId);
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
		if (existsProxyChannel(channelId) || channelId.equals("local")) {
			selectedChannels.put(uuid, channelId);
			return true;
		} else {
			return false;
		}
	}

}
