package xyz.olivermartin.multichat.local.listeners;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;

public abstract class LocalLoginListener {

	protected abstract boolean isPlayerStillOnline(MultiChatLocalPlayer player);
	
	protected void handleEvent(MultiChatLocalPlayer player) {
		
		runTaskLater(10L, new LocalLoginListenerTask(player));
		
	}
	
	protected abstract void runTaskLater(long delay, LocalLoginListenerTask taskToRun);
	
	public class LocalLoginListenerTask {
		
		MultiChatLocalPlayer player;
		
		public LocalLoginListenerTask(MultiChatLocalPlayer player) {
			this.player = player;
		}
		
		public void run() {
			MultiChatLocal.getInstance().getProxyCommunicationManager().updatePlayerMeta(player.getUniqueId());
		}
		
	}
	
}
