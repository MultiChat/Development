package xyz.olivermartin.multichat.proxy.common.listeners.communication;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.regex.PatternSyntaxException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.common.communication.CommChannels;

/**
 * Listener for communication over the Player Action communication channel
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ProxyPlayerActionListener implements Listener {

	@EventHandler
	public static void onPluginMessage(PluginMessageEvent event) {

		// Ignore if sent to a different channel
		if (!event.getTag().equals(CommChannels.getPlayerAction())) return;

		event.setCancelled(true);

		ByteArrayInputStream stream = new ByteArrayInputStream(event.getData());
		DataInputStream in = new DataInputStream(stream);

		String command;
		String playerRegex;

		try {
			command = in.readUTF();
			playerRegex = in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try {

			for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {

				if (p.getName().matches(playerRegex)) {
					ProxyServer.getInstance().getPluginManager().dispatchCommand(p, command);
				}

			}

		} catch (PatternSyntaxException e) {
			MessageManager.sendMessage(ProxyServer.getInstance().getConsole(), "command_execute_regex");
		}

	}

}
