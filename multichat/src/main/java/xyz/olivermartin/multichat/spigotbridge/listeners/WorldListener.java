package xyz.olivermartin.multichat.spigotbridge.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import xyz.olivermartin.multichat.spigotbridge.SpigotCommunicationManager;

public class WorldListener implements Listener {

	@EventHandler
	public void onWorldChange(final PlayerChangedWorldEvent event) {

		SpigotCommunicationManager.getInstance().sendPluginChannelMessage("multichat:world", event.getPlayer().getUniqueId(), event.getPlayer().getWorld().getName());

	}

}
