package xyz.olivermartin.multichat.local.spigot.listeners.communication;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import xyz.olivermartin.multichat.common.communication.CommChannels;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeMessage;
import xyz.olivermartin.multichat.local.common.listeners.communication.LocalServerActionListener;
import xyz.olivermartin.multichat.local.spigot.listeners.SpigotBungeeMessage;

public class LocalSpigotServerActionListener extends LocalServerActionListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		if (!channel.equals(CommChannels.getServerAction())) return;

		LocalBungeeMessage lbm = new SpigotBungeeMessage(message);

		handleMessage(lbm);

	}

	@Override
	protected void executeCommandAsConsole(String command) {
		Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command); 
	}

}
