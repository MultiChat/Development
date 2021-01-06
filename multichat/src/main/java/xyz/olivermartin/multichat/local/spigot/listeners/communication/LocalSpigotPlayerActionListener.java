package xyz.olivermartin.multichat.local.spigot.listeners.communication;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import xyz.olivermartin.multichat.common.communication.CommChannels;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeMessage;
import xyz.olivermartin.multichat.local.common.listeners.communication.LocalPlayerActionListener;
import xyz.olivermartin.multichat.local.spigot.listeners.SpigotBungeeMessage;

public class LocalSpigotPlayerActionListener extends LocalPlayerActionListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		if (!channel.equals(CommChannels.PLAYER_ACTION)) return;

		LocalBungeeMessage lbm = new SpigotBungeeMessage(message);

		handleMessage(lbm);

	}

	@Override
	protected void executeCommandForPlayersMatchingRegex(String playerRegex, String command) {

		for (Player p : Bukkit.getServer().getOnlinePlayers()) {

			if (p.getName().matches(playerRegex)) {
				Bukkit.getServer().dispatchCommand(p, command);
			}

		}

	}

}
