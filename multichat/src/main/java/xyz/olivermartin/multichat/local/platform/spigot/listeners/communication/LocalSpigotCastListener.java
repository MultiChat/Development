package xyz.olivermartin.multichat.local.platform.spigot.listeners.communication;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import xyz.olivermartin.multichat.local.listeners.LocalBungeeMessage;
import xyz.olivermartin.multichat.local.listeners.communication.LocalCastListener;
import xyz.olivermartin.multichat.local.platform.spigot.listeners.SpigotBungeeMessage;

public class LocalSpigotCastListener extends LocalCastListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		if (!channel.equals("multichat:chat")) return;

		LocalBungeeMessage lbm = new SpigotBungeeMessage(message);

		handleMessage(lbm);

	}

	@Override
	protected void broadcastRawMessageToChat(String message) {
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

}
