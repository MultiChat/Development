package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ChatModeManager {

	private static ChatModeManager instance;

	public static ChatModeManager getInstance() {
		return instance;
	}

	static {
		instance = new ChatModeManager();
	}

	/* END STATIC */

	private Map<UUID, Boolean> globalPlayers;

	private ChatModeManager() {
		globalPlayers = new HashMap<UUID, Boolean>();
	}

	public void setLocal(UUID uuid) {

		globalPlayers.put(uuid, false);

		// TODO
		Channel.setChannel(uuid, Channel.getLocalChannel());

		// TODO
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
		if (player == null) return;
		BungeeComm.sendPlayerChannelMessage(player.getName(), Channel.getChannel(uuid).getName(), Channel.getChannel(uuid), player.getServer().getInfo(), (player.hasPermission("multichat.chat.colour")||player.hasPermission("multichat.chat.color")));

	}

	public void setGlobal(UUID uuid) {

		globalPlayers.put(uuid, true);

		// TODO
		Channel.setChannel(uuid, Channel.getGlobalChannel());

		// TODO
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
		if (player == null) return;
		BungeeComm.sendPlayerChannelMessage(player.getName(), Channel.getChannel(uuid).getName(), Channel.getChannel(uuid), player.getServer().getInfo(), (player.hasPermission("multichat.chat.colour")||player.hasPermission("multichat.chat.color")));

	}

	public void registerPlayer(UUID uuid, boolean global) {

		globalPlayers.put(uuid, global);

		// TODO Send plugin channel message to local servers

	}

	public boolean existsPlayer(UUID uuid) {

		return globalPlayers.containsKey(uuid);

	}

	public Map<UUID, Boolean> getData() {
		return globalPlayers;
	}

	public void loadData(Map<UUID, Boolean> data) {
		this.globalPlayers = data;
	}

	public boolean isGlobal(UUID uuid) {
		if (globalPlayers.containsKey(uuid)) {
			return globalPlayers.get(uuid);
		} else {
			return true;
		}
	}

}
