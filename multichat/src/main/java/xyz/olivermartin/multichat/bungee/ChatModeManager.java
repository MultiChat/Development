package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

		// TODO Send plugin channel message to local servers

	}

	public void setGlobal(UUID uuid) {

		globalPlayers.put(uuid, true);

		// TODO
		Channel.setChannel(uuid, Channel.getGlobalChannel());

		// TODO Send plugin channel message to local servers

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
