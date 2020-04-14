package xyz.olivermartin.multichat.local.platform.spigot.listeners.communication;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.listeners.LocalBungeeMessage;
import xyz.olivermartin.multichat.local.listeners.communication.LocalPlayerMetaListener;
import xyz.olivermartin.multichat.local.platform.spigot.MultiChatLocalSpigotPlayer;
import xyz.olivermartin.multichat.local.platform.spigot.listeners.SpigotBungeeMessage;

public class LocalSpigotPlayerMetaListener extends LocalPlayerMetaListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		if (!channel.equals("multichat:comm")) return;

		LocalBungeeMessage lbm = new SpigotBungeeMessage(message);

		handleMessage(lbm);

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