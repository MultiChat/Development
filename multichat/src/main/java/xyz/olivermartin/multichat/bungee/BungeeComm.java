package xyz.olivermartin.multichat.bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Bungee Communication Manager
 * <p>Manages all plug-in messaging channels on the BungeeCord side</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class BungeeComm implements Listener {

	public static void sendMessage(String message, ServerInfo server) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);

		try {
			out.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData("multichat:comm", stream.toByteArray());

	}

	@EventHandler
	public static void onPluginMessage(PluginMessageEvent ev) {

		if (!ev.getTag().equals("multichat:comm")) {
			return;
		}

		if (!(ev.getSender() instanceof Server)) {
			return;
		}

		if (ev.getTag().equals("multichat:comm")) {

			ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
			DataInputStream in = new DataInputStream(stream);

			try {

				String playerDisplayName = in.readUTF();
				String playerName = in.readUTF();
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);

				if (player == null) return;

				synchronized (player) {

					/*
					 * TODO Add option to NOT set the bungee display name
					 * (While maintaining the fetching prefixes and correct display of them)
					 * (Useful for older servers where char limit in place for display names)
					 */

					if (ConfigManager.getInstance().getBoolean("fetch_spigot_display_names") == true && player != null) {
						player.setDisplayName(playerDisplayName.replaceAll("&(?=[a-f,0-9,k-o,r])", "§"));
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
