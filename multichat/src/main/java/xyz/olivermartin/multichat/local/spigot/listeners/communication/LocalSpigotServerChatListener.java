package xyz.olivermartin.multichat.local.spigot.listeners.communication;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import xyz.olivermartin.multichat.common.communication.CommChannels;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeMessage;
import xyz.olivermartin.multichat.local.common.listeners.communication.LocalServerChatListener;
import xyz.olivermartin.multichat.local.spigot.listeners.SpigotBungeeMessage;

public class LocalSpigotServerChatListener extends LocalServerChatListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		if (!channel.equals(CommChannels.SERVER_CHAT)) return;

		LocalBungeeMessage lbm = new SpigotBungeeMessage(message);

		handleMessage(lbm);

	}

	@Override
	protected void broadcastRawMessageToChat(String message) {
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

}
