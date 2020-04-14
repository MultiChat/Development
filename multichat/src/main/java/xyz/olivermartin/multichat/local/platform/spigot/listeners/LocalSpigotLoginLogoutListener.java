package xyz.olivermartin.multichat.local.platform.spigot.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.listeners.LocalLoginLogoutListener;
import xyz.olivermartin.multichat.local.platform.spigot.MultiChatLocalSpigotPlayer;

public class LocalSpigotLoginLogoutListener extends LocalLoginLogoutListener implements Listener {

	@EventHandler
	public void onLogin(final PlayerJoinEvent event) {
		MultiChatLocalPlayer mclp = new MultiChatLocalSpigotPlayer(event.getPlayer());
		handleLoginEvent(mclp);
	}

	@EventHandler
	public void onLogout(PlayerQuitEvent event) {
		MultiChatLocalPlayer mclp = new MultiChatLocalSpigotPlayer(event.getPlayer());
		handleLogoutEvent(mclp);
	}

	@Override
	protected boolean isPlayerStillOnline(MultiChatLocalPlayer player) {
		return (Bukkit.getServer().getPlayer(player.getUniqueId()) != null);
	}

	@Override
	protected void runTaskLater(long delay, LocalLoginListenerTask taskToRun) {

		new BukkitRunnable() {
			public void run() {
				taskToRun.run();
			}
		}.runTaskLater(Bukkit.getPluginManager().getPlugin(MultiChatLocal.getInstance().getPluginName()), delay);

	}

}
