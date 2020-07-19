package xyz.olivermartin.multichat.proxy.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import xyz.olivermartin.multichat.bungee.Channel;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.DebugManager;
import xyz.olivermartin.multichat.common.communication.CommChannels;

/**
 * Proxy -> Local communication manager
 * <p>Manages all plugin messaging channels on the BungeeCord side</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ProxyLocalCommunicationManager {

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

		server.sendData(CommChannels.getServerAction(), stream.toByteArray());

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

		server.sendData(CommChannels.getPlayerAction(), stream.toByteArray());

	}

	public static void sendPlayerDirectChatMessage(String channel, String player, String chatMessage, ServerInfo server) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);

		try {

			out.writeUTF(channel);
			out.writeUTF(player);
			out.writeUTF(chatMessage);

		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData(CommChannels.getPlayerChat(), stream.toByteArray());

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

}
