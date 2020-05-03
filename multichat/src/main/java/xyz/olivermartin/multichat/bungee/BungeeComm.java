package xyz.olivermartin.multichat.bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.PatternSyntaxException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import xyz.olivermartin.multichat.proxy.bungee.MultiChatProxyBungeeCommandSender;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.config.ProxyMainConfig;

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
			ProxyMainConfig mainConfig = MultiChatProxy.getInstance().getConfigManager().getProxyMainConfig();
			if (mainConfig.isSetDisplayName()) {
				out.writeUTF("T");
			} else {
				out.writeUTF("F");
			}

			// Display name format
			out.writeUTF(mainConfig.getDisplayNameFormat());

			// Is this server a global chat server?
			if (mainConfig.isUseGlobalChat()
					&& !mainConfig.getNoGlobal().contains(server.getName())) {
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

	public static void sendChatMessage(String message, ServerInfo server) {

		// This has been repurposed to send casts to local chat streams!

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(stream);


		try {
			// message part
			out.writeUTF(message);


		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData("multichat:chat", stream.toByteArray());

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

	public static void sendPlayerChannelMessage(String playerName, String channel, Channel channelObject, ServerInfo server, boolean colour) {

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
			oout.writeBoolean(channelObject.isWhitelistMembers());
			oout.writeObject(channelObject.getMembers());

		} catch (IOException e) {
			e.printStackTrace();
		}

		server.sendData("multichat:ch", stream.toByteArray());

		MultiChatProxy.getInstance().getConsoleLogger().debug("Sent message on multichat:ch channel!");

	}

	@EventHandler
	public static void onPluginMessage(PluginMessageEvent ev) {

		if (! (ev.getTag().equals("multichat:comm") || ev.getTag().equals("multichat:chat") || ev.getTag().equals("multichat:prefix") || ev.getTag().equals("multichat:suffix") || ev.getTag().equals("multichat:dn") || ev.getTag().equals("multichat:world") || ev.getTag().equals("multichat:nick") || ev.getTag().equals("multichat:pxe") || ev.getTag().equals("multichat:ppxe")) ) {
			return;
		}

		if (!(ev.getSender() instanceof Server)) {
			return;
		}

		if (ev.getTag().equals("multichat:comm")) {

			// TODO Remove - legacy
			return;

		}

		if (ev.getTag().equals("multichat:chat")) {

			ev.setCancelled(true);

			MultiChatProxy.getInstance().getConsoleLogger().debug("{multichat:chat} Got a plugin message");

			ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
			DataInputStream in = new DataInputStream(stream);

			try {

				UUID uuid = UUID.fromString(in.readUTF());
				MultiChatProxy.getInstance().getConsoleLogger().debug("{multichat:chat} UUID = " + uuid);
				String message = in.readUTF();
				MultiChatProxy.getInstance().getConsoleLogger().debug("{multichat:chat} Message = " + message);
				String format = in.readUTF();

				MultiChatProxy.getInstance().getConsoleLogger().debug("{multichat:chat} Format (before removal of double chars) = " + format);

				format = format.replace("%%","%");

				MultiChatProxy.getInstance().getConsoleLogger().debug("{multichat:chat} Format = " + format);

				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

				if (player == null) {
					MultiChatProxy.getInstance().getConsoleLogger().debug("{multichat:chat} Could not get player! Abandoning chat message... (Is IP-Forwarding on?)");
					return;
				}

				MultiChatProxy.getInstance().getConsoleLogger().debug("{multichat:chat} Got player successfully! Name = " + player.getName());

				//synchronized (player) {

				MultiChatProxy.getInstance().getConsoleLogger().debug("{multichat:chat} Global Channel Available? = " + (Channel.getGlobalChannel() != null));
				Channel.getGlobalChannel().sendMessage(player, message, format);

				//}

			} catch (IOException e) {
				MultiChatProxy.getInstance().getConsoleLogger().debug("{multichat:chat} ERROR READING PLUGIN MESSAGE");
				e.printStackTrace();
			}


			return;

		}

		if (ev.getTag().equals("multichat:nick")) {

			ev.setCancelled(true);

			ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
			DataInputStream in = new DataInputStream(stream);

			try {

				UUID uuid = UUID.fromString(in.readUTF());
				String nick = in.readUTF();
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

				if (player == null) return;

				synchronized (player) {

					/*
					 * Update the nickname stored somewhere and call for an update of the player
					 * display name in that location. (Pending the "true" value of fetch display names)
					 * and a new config option to decide if the display name should be set.
					 */

					Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(uuid);

					if (opm.isPresent()) {

						opm.get().nick = nick;
						PlayerMetaManager.getInstance().updateDisplayName(uuid);

					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (ev.getTag().equals("multichat:prefix")) {

			ev.setCancelled(true);

			ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
			DataInputStream in = new DataInputStream(stream);

			try {

				UUID uuid = UUID.fromString(in.readUTF());
				String prefix = in.readUTF();
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

				if (player == null) return;

				synchronized (player) {

					/*
					 * Update the prefix stored somewhere and call for an update of the player
					 * display name in that location. (Pending the "true" value of fetch display names)
					 * and a new config option to decide if the display name should be set.
					 */

					Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(uuid);

					if (opm.isPresent()) {

						opm.get().prefix = prefix;
						PlayerMetaManager.getInstance().updateDisplayName(uuid);

					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (ev.getTag().equals("multichat:suffix")) {

			ev.setCancelled(true);

			ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
			DataInputStream in = new DataInputStream(stream);

			try {

				UUID uuid = UUID.fromString(in.readUTF());
				String suffix = in.readUTF();
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

				if (player == null) return;

				synchronized (player) {

					/*
					 * Update the suffix stored somewhere and call for an update of the player
					 * display name in that location. (Pending the "true" value of fetch display names)
					 * and a new config option to decide if the display name should be set.
					 */

					Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(uuid);

					if (opm.isPresent()) {

						opm.get().suffix = suffix;
						PlayerMetaManager.getInstance().updateDisplayName(uuid);

					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (ev.getTag().equals("multichat:dn")) {

			ev.setCancelled(true);

			MultiChatProxy.getInstance().getConsoleLogger().debug("[multichat:dn] Got an incoming channel message!");

			ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
			DataInputStream in = new DataInputStream(stream);

			try {

				UUID uuid = UUID.fromString(in.readUTF());
				String spigotDisplayName = in.readUTF();
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

				if (player == null) return;

				synchronized (player) {

					MultiChatProxy.getInstance().getConsoleLogger().debug("[multichat:dn] Player exists!");

					Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(uuid);

					if (opm.isPresent()) {

						MultiChatProxy.getInstance().getConsoleLogger().debug("[multichat:dn] Player meta exists!");

						MultiChatProxy.getInstance().getConsoleLogger().debug("[multichat:dn] The displayname received is: " + spigotDisplayName);

						opm.get().spigotDisplayName = spigotDisplayName;
						PlayerMetaManager.getInstance().updateDisplayName(uuid);

					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (ev.getTag().equals("multichat:world")) {

			ev.setCancelled(true);

			ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
			DataInputStream in = new DataInputStream(stream);

			MultiChatProxy.getInstance().getConsoleLogger().debug("[multichat:world] Got an incoming channel message!");

			try {

				UUID uuid = UUID.fromString(in.readUTF());
				String world = in.readUTF();
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

				if (player == null) return;

				MultiChatProxy.getInstance().getConsoleLogger().debug("[multichat:world] Player is online!");

				synchronized (player) {

					/*
					 * Update the world stored somewhere
					 */

					Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(uuid);

					if (opm.isPresent()) {

						MultiChatProxy.getInstance().getConsoleLogger().debug("[multichat:world] Got their meta data correctly");

						opm.get().world = world;

						MultiChatProxy.getInstance().getConsoleLogger().debug("[multichat:world] Set their world to: " + world);

					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}


		if (ev.getTag().equals("multichat:pxe")) {

			ev.setCancelled(true);

			MultiChatProxy.getInstance().getConsoleLogger().debug("[multichat:pxe] Got an incoming pexecute message!");

			ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
			DataInputStream in = new DataInputStream(stream);

			try {

				String command = in.readUTF();
				MultiChatProxy.getInstance().getConsoleLogger().debug("[multichat:pxe] Command is: " + command);
				ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command);

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (ev.getTag().equals("multichat:ppxe")) {

			ev.setCancelled(true);

			MultiChatProxy.getInstance().getConsoleLogger().debug("[multichat:ppxe] Got an incoming pexecute message (for a player)!");

			ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
			DataInputStream in = new DataInputStream(stream);

			try {

				String command = in.readUTF();
				String playerRegex = in.readUTF();

				MultiChatProxy.getInstance().getConsoleLogger().debug("[multichat:ppxe] Command is: " + command);
				MultiChatProxy.getInstance().getConsoleLogger().debug("[multichat:ppxe] Player regex is: " + playerRegex);

				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {

					if (p.getName().matches(playerRegex)) {

						ProxyServer.getInstance().getPluginManager().dispatchCommand(p, command);

					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (PatternSyntaxException e2) {
				MultiChatProxy.getInstance().getMessageManager().sendMessage(new MultiChatProxyBungeeCommandSender(ProxyServer.getInstance().getConsole()), "command_execute_regex");
			}

		}

	}
}
