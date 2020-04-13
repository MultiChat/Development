package xyz.olivermartin.multichat.local.platform.spigot.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.listeners.LocalWorldChangeListener;
import xyz.olivermartin.multichat.local.platform.spigot.MultiChatLocalSpigotPlayer;

public class LocalSpigotWorldChangeListener extends LocalWorldChangeListener implements Listener{

	@EventHandler
	public void onWorldChange(final PlayerChangedWorldEvent event) {

		MultiChatLocalPlayer mclp = new MultiChatLocalSpigotPlayer(event.getPlayer());
		String world = event.getPlayer().getWorld().getName();
		updatePlayerWorld(mclp, world);

	}

}
