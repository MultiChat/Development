package xyz.olivermartin.multichat.local.listeners;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;

public abstract class LocalLoginLogoutListener {

	protected abstract boolean isPlayerStillOnline(MultiChatLocalPlayer player);

	protected void handleLoginEvent(MultiChatLocalPlayer player) {

		runTaskLater(10L, new LocalLoginListenerTask(player));

	}

	protected void handleLogoutEvent(MultiChatLocalPlayer player) {

		MultiChatLocal.getInstance().getNameManager().unregisterPlayer(player.getUniqueId());
		MultiChatLocal.getInstance().getDataStore().playerChannels.remove(player.getUniqueId());
		MultiChatLocal.getInstance().getDataStore().colourMap.remove(player.getUniqueId());

	}

	protected abstract void runTaskLater(long delay, LocalLoginListenerTask taskToRun);

	public class LocalLoginListenerTask {

		MultiChatLocalPlayer player;

		public LocalLoginListenerTask(MultiChatLocalPlayer player) {
			this.player = player;
		}

		public void run() {

			MultiChatLocal.getInstance().getNameManager().registerPlayer(player.getUniqueId(), player.getName());

			if (!MultiChatLocal.getInstance().getDataStore().playerChannels.containsKey(player.getUniqueId())) {
				MultiChatLocal.getInstance().getDataStore().playerChannels.put(player.getUniqueId(), "global");
			}

			if (!MultiChatLocal.getInstance().getDataStore().colourMap.containsKey(player.getUniqueId())) {
				MultiChatLocal.getInstance().getDataStore().colourMap.put(player.getUniqueId(), false);
			}

			MultiChatLocal.getInstance().getProxyCommunicationManager().updatePlayerMeta(player.getUniqueId());

		}

	}

}
