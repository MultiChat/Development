package xyz.olivermartin.multichat.proxy.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProxyPlayerManager {

	private Map<UUID, MultiChatProxyPlayer> playerMap;

	public ProxyPlayerManager() {
		playerMap = new HashMap<UUID, MultiChatProxyPlayer>();
	}

	public void registerPlayer(MultiChatProxyPlayer player) {
		playerMap.put(player.getUniqueId(), player);
	}

	public void unregisterPlayer(UUID uuid) {
		playerMap.remove(uuid);
		MultiChatProxy.getInstance().getPlayerMetaStore().clearPlayer(uuid);
	}

	public MultiChatProxyPlayer getPlayer(UUID uuid) {
		return playerMap.get(uuid);
	}

	public boolean isOnline(UUID uuid) {
		return playerMap.containsKey(uuid);
	}

}
