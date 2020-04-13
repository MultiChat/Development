package xyz.olivermartin.multichat.local.platform.spigot.listeners;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.listeners.LocalBungeeObjectMessage;
import xyz.olivermartin.multichat.local.listeners.LocalIgnoreListener;

public class LocalSpigotIgnoreListener extends LocalIgnoreListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		if (!channel.equals("multichat:ignore")) return;

		try {
			LocalBungeeObjectMessage lbm = new SpigotBungeeObjectMessage(message);

			handleMessage(lbm);

		} catch (IOException e) {
			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred reading the object stream in the local ignore listener...");
			return;
		}

	}

}
