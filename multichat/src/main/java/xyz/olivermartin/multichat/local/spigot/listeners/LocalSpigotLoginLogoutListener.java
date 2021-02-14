package xyz.olivermartin.multichat.local.spigot.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.config.LocalConfig;
import xyz.olivermartin.multichat.local.common.listeners.LocalLoginLogoutListener;
import xyz.olivermartin.multichat.local.spigot.MultiChatLocalSpigotPlayer;

public class LocalSpigotLoginLogoutListener extends LocalLoginLogoutListener implements Listener {

	public LocalSpigotLoginLogoutListener() {
		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();
		this.defaultChannel = config.getDefaultChannel();
	}

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

}
