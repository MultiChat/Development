package xyz.olivermartin.multichat.local.common.listeners;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;

public abstract class LocalLoginLogoutListener {

	protected abstract boolean isPlayerStillOnline(MultiChatLocalPlayer player);

	protected void handleLoginEvent(MultiChatLocalPlayer player) {

		MultiChatLocal.getInstance().getNameManager().registerPlayer(player.getUniqueId(), player.getName());

		if (!MultiChatLocal.getInstance().getDataStore().playerChannels.containsKey(player.getUniqueId())) {
			MultiChatLocal.getInstance().getDataStore().playerChannels.put(player.getUniqueId(), "global");
		}

		if (!MultiChatLocal.getInstance().getDataStore().colourMap.containsKey(player.getUniqueId())) {
			MultiChatLocal.getInstance().getDataStore().colourMap.put(player.getUniqueId(), false);
		}

		MultiChatLocal.getInstance().getProxyCommunicationManager().updatePlayerMeta(player.getUniqueId());

	}

	protected void handleLogoutEvent(MultiChatLocalPlayer player) {

		MultiChatLocal.getInstance().getNameManager().unregisterPlayer(player.getUniqueId());
		MultiChatLocal.getInstance().getDataStore().playerChannels.remove(player.getUniqueId());
		MultiChatLocal.getInstance().getDataStore().colourMap.remove(player.getUniqueId());

	}

}
