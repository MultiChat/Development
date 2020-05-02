package xyz.olivermartin.multichat.local.common.listeners;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;

public abstract class LocalWorldChangeListener {

	protected void updatePlayerWorld(MultiChatLocalPlayer player, String world) {
		MultiChatLocal.getInstance().getProxyCommunicationManager().sendWorldUpdate(player.getUniqueId(), world);
	}

}
