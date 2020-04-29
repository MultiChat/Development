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

		Map<UUID, Boolean> colourMap = MultiChatLocal.getInstance().getDataStore().getColourMap();
		synchronized (colourMap) {
			if (!colourMap.containsKey(player.getUniqueId())) {
				colourMap.put(player.getUniqueId(), false);
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

		Map<UUID, Boolean> colourMap = MultiChatLocal.getInstance().getDataStore().getColourMap();
		synchronized (colourMap) {
			colourMap.remove(player.getUniqueId());
		}

	}

}
