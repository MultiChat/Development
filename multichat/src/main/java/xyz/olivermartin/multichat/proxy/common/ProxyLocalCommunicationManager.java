package xyz.olivermartin.multichat.proxy.common;

import net.md_5.bungee.api.config.ServerInfo;
import xyz.olivermartin.multichat.bungee.DebugManager;
import xyz.olivermartin.multichat.common.communication.CommChannels;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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
		 * - dn = display name info
		 * - legacy = legacy server info
		 */

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {

			ObjectOutputStream out = new ObjectOutputStream(stream);

			boolean globalChatServer = ProxyConfigs.CONFIG.isGlobal() && ProxyConfigs.CONFIG.isGlobalServer(server.getName());
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
		 * - dn = display name info
		 * - legacy = legacy server info
		 */

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {

			ObjectOutputStream out = new ObjectOutputStream(stream);

			boolean setDisplayName = ProxyConfigs.CONFIG.isSetDisplayName();
			String displayNameFormat = ProxyConfigs.CONFIG.getDisplayNameFormat();

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

	public static void sendLegacyServerData(ServerInfo server) {

		DebugManager.log("About to send to multichat:sdata on the legacy id");

		/*
		 * This is for the sdata channel id: legacy
		 * 
		 * Other ids are:
		 * - global = Global chat info
		 * - dn = display name info
		 * - legacy = legacy server info
		 */

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {

			ObjectOutputStream out = new ObjectOutputStream(stream);

			boolean isLegacy = ProxyConfigs.CONFIG.isLegacyServer(server.getName());

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

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {
			ObjectOutputStream oout = new ObjectOutputStream(stream);

			// Players name
			oout.writeUTF(playerName);
			// Channel part
			oout.writeUTF(channel);
			oout.writeUTF(channelFormat);
			oout.writeBoolean(colour);
			oout.writeBoolean(rgb);
			oout.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData(CommChannels.PLAYER_DATA, stream.toByteArray());

		DebugManager.log("Sent message on multichat:pdata channel!");

	}

}
