package xyz.olivermartin.multichat.local.platform.spigot.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import xyz.olivermartin.multichat.local.listeners.LocalBungeeMessage;
import xyz.olivermartin.multichat.local.listeners.LocalPlayerActionListener;

public class LocalSpigotPlayerActionListener extends LocalPlayerActionListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		if (!channel.equals("multichat:pact")) return;

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
