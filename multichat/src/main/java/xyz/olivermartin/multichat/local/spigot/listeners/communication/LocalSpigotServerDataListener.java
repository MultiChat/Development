package xyz.olivermartin.multichat.local.spigot.listeners.communication;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import xyz.olivermartin.multichat.common.communication.CommChannels;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeObjectMessage;
import xyz.olivermartin.multichat.local.common.listeners.communication.LocalServerDataListener;
import xyz.olivermartin.multichat.local.spigot.listeners.SpigotBungeeObjectMessage;

public class LocalSpigotServerDataListener extends LocalServerDataListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		if (!channel.equals(CommChannels.getServerData())) return;

		try {

			LocalBungeeObjectMessage lbm = new SpigotBungeeObjectMessage(message);
			handleMessage(lbm);

		} catch (IOException e) {
			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred reading the object stream in the local server data listener...");
			return;
		}

	}

}
