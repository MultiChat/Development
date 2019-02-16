package xyz.olivermartin.multichat.spigotbridge.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.olivermartin.multichat.spigotbridge.MetaManager;
import xyz.olivermartin.multichat.spigotbridge.MultiChatSpigot;

public class LoginListener implements Listener {

	@EventHandler
	public void onLogin(final PlayerJoinEvent event) {

		new BukkitRunnable() {

			public void run() {

				synchronized (event.getPlayer()) {

					if (event.getPlayer() == null) {
						return;
					}

					String playername = event.getPlayer().getName();

					MetaManager.getInstance().updatePlayerMeta(playername, MultiChatSpigot.setDisplayNameLastVal, MultiChatSpigot.displayNameFormatLastVal);

				}

			}

		}.runTaskLater(Bukkit.getPluginManager().getPlugin(MultiChatSpigot.pluginName), 10L);

	}

}
