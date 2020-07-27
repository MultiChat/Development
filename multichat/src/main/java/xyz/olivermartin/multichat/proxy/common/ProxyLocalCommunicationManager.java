package xyz.olivermartin.multichat.proxy.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
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

	public static void sendGlobalServerData(ServerInfo server) {

		DebugManager.log("About to send to multichat:sdata on the global id");

		/*
		 * This is for the sdata channel id: global
		 * 
		 * Other ids are:
		 * - global = Global chat info
		 * - ignore = ignore map info
		 * - dn = display name info
		 * - legacy = legacy server info
		 */

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {

			ObjectOutputStream out = new ObjectOutputStream(stream);

			boolean globalChatServer = ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean("global") == true
					&& !ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_global").contains(server.getName());
			String globalChatFormat = MultiChatProxy.getInstance().getChannelManager().getGlobalChannel().getInfo().getFormat();

			out.writeUTF("global");
			out.writeBoolean(globalChatServer);
			out.writeUTF(globalChatFormat);
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData(CommChannels.SERVER_DATA, stream.toByteArray());

		DebugManager.log("Completed send on multichat:sdata on the global id");

	}

	public static void sendDisplayNameServerData(ServerInfo server) {

		DebugManager.log("About to send to multichat:sdata on the dn id");

		/*
		 * This is for the sdata channel id: dn
		 * 
		 * Other ids are:
		 * - global = Global chat info
		 * - ignore = ignore map info
		 * - dn = display name info
		 * - legacy = legacy server info
		 */

		Configuration configYML = ConfigManager.getInstance().getHandler("config.yml").getConfig();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {

			ObjectOutputStream out = new ObjectOutputStream(stream);

			boolean setDisplayName = configYML.getBoolean("set_display_name", true);
			String displayNameFormat = configYML.getString("display_name_format", "%PREFIX%%NICK%%SUFFIX%");

			out.writeUTF("dn");
			out.writeBoolean(setDisplayName);
			out.writeUTF(displayNameFormat);
			out.flush();

			DebugManager.log("setDisplayName = " + setDisplayName);

		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData(CommChannels.SERVER_DATA, stream.toByteArray());

		DebugManager.log("Completed send to multichat:sdata on the dn id");

	}

	public static void sendIgnoreServerData(ServerInfo server) {

		DebugManager.log("About to send to multichat:sdata on the ignore id");

		/*
		 * This is for the sdata channel id: ignore
		 * 
		 * Other ids are:
		 * - global = Global chat info
		 * - ignore = ignore map info
		 * - dn = display name info
		 * - legacy = legacy server info
		 */

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {

			ObjectOutputStream oout = new ObjectOutputStream(stream);
			oout.writeUTF("ignore");
			oout.writeObject(ChatControl.getIgnoreMap());
			oout.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData(CommChannels.SERVER_DATA, stream.toByteArray());

		DebugManager.log("Completed send to multichat:sdata on the ignore id");

	}

	public static void sendLegacyServerData(ServerInfo server) {

		DebugManager.log("About to send to multichat:sdata on the legacy id");

		/*
		 * This is for the sdata channel id: legacy
		 * 
		 * Other ids are:
		 * - global = Global chat info
		 * - ignore = ignore map info
		 * - dn = display name info
		 * - legacy = legacy server info
		 */

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {

			ObjectOutputStream out = new ObjectOutputStream(stream);

			boolean isLegacy =
					ConfigManager.getInstance().getHandler("config.yml").getConfig()
					.getStringList("legacy_servers")
					.contains(server.getName());

			DebugManager.log("isLegacy = " + isLegacy);

			out.writeUTF("legacy");
			out.writeBoolean(isLegacy);
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData(CommChannels.SERVER_DATA, stream.toByteArray());

		DebugManager.log("Completed send to multichat:sdata on the legacy id");

	}

	public static void sendUpdatePlayerMetaRequestMessage(String playerName, ServerInfo server) {

		DebugManager.log("About to send update player meta request!");
		sendDisplayNameServerData(server);
		sendGlobalServerData(server);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);

		try {

			// Command
			out.writeUTF(playerName);

		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData(CommChannels.PLAYER_META, stream.toByteArray());
		DebugManager.log("Request sent!");

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

		server.sendData(CommChannels.SERVER_ACTION, stream.toByteArray());

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

		server.sendData(CommChannels.PLAYER_ACTION, stream.toByteArray());

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

		server.sendData(CommChannels.PLAYER_CHAT, stream.toByteArray());

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

		server.sendData(CommChannels.SERVER_CHAT, stream.toByteArray());

	}

	public static void sendPlayerDataMessage(String playerName, String channel, String channelFormat, ServerInfo server, boolean colour, boolean rgb) {

		//ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();

		sendIgnoreServerData(server);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		//DataOutputStream out = new DataOutputStream(stream);
		try {
			ObjectOutputStream oout = new ObjectOutputStream(stream);

			// Players name
			oout.writeUTF(playerName);
			// Channel part
			oout.writeUTF(channel);
			oout.writeUTF(channelFormat);
			oout.writeBoolean(colour);
			oout.writeBoolean(rgb);
			//oout.writeBoolean(channelObject.isWhitelistMembers());
			//oout.writeObject(channelObject.getMembers());
			oout.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData(CommChannels.PLAYER_DATA, stream.toByteArray());

		DebugManager.log("Sent message on multichat:pdata channel!");

	}

}
