package xyz.olivermartin.multichat.bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.regex.PatternSyntaxException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import xyz.olivermartin.multichat.common.communication.CommChannels;

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
			// Players name
			out.writeUTF(message);

			// Should display name be set?
			Configuration configYML = ConfigManager.getInstance().getHandler("config.yml").getConfig();
			if (configYML.contains("set_display_name")) {
				if (configYML.getBoolean("set_display_name")) {
					out.writeUTF("T");
				} else {
					out.writeUTF("F");
				}
			} else {
				out.writeUTF("T");
			}

			// Display name format
			if (configYML.contains("display_name_format")) {
				out.writeUTF(configYML.getString("display_name_format"));
			} else {
				out.writeUTF("%PREFIX%%NICK%%SUFFIX%");
			}

			// Is this server a global chat server?
			if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean("global") == true
					&& !ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_global").contains(server.getName())) {
				out.writeUTF("T");
			} else {
				out.writeUTF("F");
			}

			// Send the global format
			out.writeUTF(Channel.getGlobalChannel().getFormat());

		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData("multichat:comm", stream.toByteArray());

	}

	public static void sendCommandMessage(String command, ServerInfo server) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);

		try {

			// Command
			out.writeUTF(command);

		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData("multichat:act", stream.toByteArray());

	}

	public static void sendPlayerCommandMessage(String command, String playerRegex, ServerInfo server) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);

		try {

			// Command
			out.writeUTF(playerRegex);
			out.writeUTF(command);

		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData("multichat:pact", stream.toByteArray());

	}

	public static void sendServerChatMessage(String channel, String message, ServerInfo server) {

		// This has been repurposed to send casts to local chat streams!

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);


		try {
			// message part
			out.writeUTF(channel);
			out.writeUTF(message);


		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData(CommChannels.getServerChat(), stream.toByteArray());

	}

	public static void sendIgnoreMap(ServerInfo server) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		//DataOutputStream out = new DataOutputStream(stream);
		try {
			ObjectOutputStream oout = new ObjectOutputStream(stream);

			oout.writeObject(ChatControl.getIgnoreMap());

		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData("multichat:ignore", stream.toByteArray());

	}

	public static void sendPlayerChannelMessage(String playerName, String channel, Channel channelObject, ServerInfo server, boolean colour, boolean rgb) {

		sendIgnoreMap(server);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		//DataOutputStream out = new DataOutputStream(stream);
		try {
			ObjectOutputStream oout = new ObjectOutputStream(stream);

			// Players name
			oout.writeUTF(playerName);
			// Channel part
			oout.writeUTF(channel);
			oout.writeBoolean(colour);
			oout.writeBoolean(rgb);
			oout.writeBoolean(channelObject.isWhitelistMembers());
			oout.writeObject(channelObject.getMembers());

		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData("multichat:ch", stream.toByteArray());

		DebugManager.log("Sent message on multichat:ch channel!");

	}

	@EventHandler
	public static void onPluginMessage(PluginMessageEvent ev) {

		if (! (ev.getTag().equals("multichat:comm") || ev.getTag().equals("multichat:pxe") || ev.getTag().equals("multichat:ppxe")) ) {
			return;
		}

		if (!(ev.getSender() instanceof Server)) {
			return;
		}

		if (ev.getTag().equals("multichat:comm")) {

			// TODO Remove - legacy
			return;

		}

		if (ev.getTag().equals("multichat:pxe")) {

			ev.setCancelled(true);

			DebugManager.log("[multichat:pxe] Got an incoming pexecute message!");

			ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
			DataInputStream in = new DataInputStream(stream);

			try {

				String command = in.readUTF();
				DebugManager.log("[multichat:pxe] Command is: " + command);
				ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (ev.getTag().equals("multichat:ppxe")) {

			ev.setCancelled(true);

			DebugManager.log("[multichat:ppxe] Got an incoming pexecute message (for a player)!");

			ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
			DataInputStream in = new DataInputStream(stream);

			try {

				String command = in.readUTF();
				String playerRegex = in.readUTF();

				DebugManager.log("[multichat:ppxe] Command is: " + command);
				DebugManager.log("[multichat:ppxe] Player regex is: " + playerRegex);

				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {

					if (p.getName().matches(playerRegex)) {

						ProxyServer.getInstance().getPluginManager().dispatchCommand(p, command);

					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (PatternSyntaxException e2) {
				MessageManager.sendMessage(ProxyServer.getInstance().getConsole(), "command_execute_regex");
			}

		}

	}
}
