package xyz.olivermartin.multichat.local.spigot.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.listeners.LocalWorldChangeListener;
import xyz.olivermartin.multichat.local.spigot.MultiChatLocalSpigotPlayer;

public class LocalSpigotWorldChangeListener extends LocalWorldChangeListener implements Listener{

	@EventHandler
	public void onWorldChange(final PlayerChangedWorldEvent event) {

		MultiChatLocalPlayer mclp = new MultiChatLocalSpigotPlayer(event.getPlayer());
		String world = event.getPlayer().getWorld().getName();
		updatePlayerWorld(mclp, world);

	}

}
