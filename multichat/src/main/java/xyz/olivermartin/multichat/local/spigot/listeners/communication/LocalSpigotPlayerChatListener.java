package xyz.olivermartin.multichat.local.spigot.listeners.communication;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import xyz.olivermartin.multichat.common.communication.CommChannels;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeMessage;
import xyz.olivermartin.multichat.local.common.listeners.communication.LocalPlayerChatListener;
import xyz.olivermartin.multichat.local.spigot.listeners.SpigotBungeeMessage;

public class LocalSpigotPlayerChatListener extends LocalPlayerChatListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		if (!channel.equals(CommChannels.getPlayerChat())) return;

		LocalBungeeMessage lbm = new SpigotBungeeMessage(message);

		handleMessage(lbm);

	}

	@Override
	protected void sendChatAsPlayer(String playerName, String rawMessage) {
		Bukkit.getServer().getPlayer(playerName).chat(rawMessage);
	}

}
