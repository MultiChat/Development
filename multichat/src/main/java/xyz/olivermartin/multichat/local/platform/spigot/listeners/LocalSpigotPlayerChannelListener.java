package xyz.olivermartin.multichat.local.platform.spigot.listeners;

import java.io.IOException;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.listeners.LocalBungeeObjectMessage;
import xyz.olivermartin.multichat.local.listeners.LocalPlayerChannelListener;
import xyz.olivermartin.multichat.local.platform.spigot.MultiChatLocalSpigotPlayer;

public class LocalSpigotPlayerChannelListener extends LocalPlayerChannelListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		if (!channel.equals("multichat:ch")) return;

		try {
			LocalBungeeObjectMessage lbm = new SpigotBungeeObjectMessage(message);

			handleMessage(lbm);

		} catch (IOException e) {
			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred reading the object stream in the local channel listener...");
			return;
		}

	}

	@Override
	protected Optional<MultiChatLocalPlayer> getPlayerFromName(String playername) {

		Player bukkitPlayer;

		bukkitPlayer = Bukkit.getPlayer(playername);

		if (bukkitPlayer == null) {
			return Optional.empty();
		} else {
			return Optional.of(new MultiChatLocalSpigotPlayer(bukkitPlayer));
		}

	}

}
