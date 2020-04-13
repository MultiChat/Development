package xyz.olivermartin.multichat.local.listeners;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;

public abstract class LocalWorldChangeListener {

	protected void updatePlayerWorld(MultiChatLocalPlayer player, String world) {
		MultiChatLocal.getInstance().getProxyCommunicationManager().sendWorldUpdate(player.getUniqueId(), world);
	}

}
