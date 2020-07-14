package xyz.olivermartin.multichat.local.common.listeners;

import java.util.Map;
import java.util.UUID;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;

public abstract class LocalLoginLogoutListener {

	protected abstract boolean isPlayerStillOnline(MultiChatLocalPlayer player);

	protected void handleLoginEvent(MultiChatLocalPlayer player) {

		MultiChatLocal.getInstance().getNameManager().registerPlayer(player.getUniqueId(), player.getName());

		Map<UUID, String> playerChannels = MultiChatLocal.getInstance().getDataStore().getPlayerChannels();
		synchronized (playerChannels) {
			if (!playerChannels.containsKey(player.getUniqueId())) {
				playerChannels.put(player.getUniqueId(), "global");
			}
		}

		Map<UUID, Boolean> simpleColourMap = MultiChatLocal.getInstance().getDataStore().getSimpleColourMap();
		synchronized (simpleColourMap) {
			if (!simpleColourMap.containsKey(player.getUniqueId())) {
				simpleColourMap.put(player.getUniqueId(), false);
			}
		}

		Map<UUID, Boolean> rgbColourMap = MultiChatLocal.getInstance().getDataStore().getRGBColourMap();
		synchronized (rgbColourMap) {
			if (!rgbColourMap.containsKey(player.getUniqueId())) {
				rgbColourMap.put(player.getUniqueId(), false);
			}
		}

		MultiChatLocal.getInstance().getProxyCommunicationManager().updatePlayerMeta(player.getUniqueId());

	}

	protected void handleLogoutEvent(MultiChatLocalPlayer player) {

		MultiChatLocal.getInstance().getNameManager().unregisterPlayer(player.getUniqueId());

		Map<UUID, String> playerChannels = MultiChatLocal.getInstance().getDataStore().getPlayerChannels();
		synchronized (playerChannels) {
			playerChannels.remove(player.getUniqueId());
		}

		Map<UUID, Boolean> simpleColourMap = MultiChatLocal.getInstance().getDataStore().getSimpleColourMap();
		synchronized (simpleColourMap) {
			simpleColourMap.remove(player.getUniqueId());
		}

		Map<UUID, Boolean> rgbColourMap = MultiChatLocal.getInstance().getDataStore().getRGBColourMap();
		synchronized (rgbColourMap) {
			rgbColourMap.remove(player.getUniqueId());
		}


	}

}
