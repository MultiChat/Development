package xyz.olivermartin.multichat.local.spigot.listeners.communication;

import java.io.IOException;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import xyz.olivermartin.multichat.common.communication.CommChannels;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeObjectMessage;
import xyz.olivermartin.multichat.local.common.listeners.communication.LocalPlayerDataListener;
import xyz.olivermartin.multichat.local.spigot.MultiChatLocalSpigotPlayer;
import xyz.olivermartin.multichat.local.spigot.listeners.SpigotBungeeObjectMessage;

public class LocalSpigotPlayerDataListener extends LocalPlayerDataListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		if (!channel.equals(CommChannels.PLAYER_DATA)) return;

		try {
			LocalBungeeObjectMessage lbm = new SpigotBungeeObjectMessage(message);

			handleMessage(lbm);

		} catch (IOException e) {
			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred reading the object stream in the local player data listener...");
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
