package xyz.olivermartin.multichat.bungee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.bungee.events.MultiChatBroadcastIRCEvent;
import xyz.olivermartin.multichat.bungee.events.MultiChatGlobalIRCEvent;

/**
 * Chat Stream
 * <p>A class to represent a chat stream and control the messages sent etc.</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ChatStream {

	boolean whitelistMembers;
	protected List<UUID> members;

	boolean whitelistServers;
	protected List<String> servers;

	protected String name;
	protected String format;

	public static Map<UUID,ChatStream> currentStreams = new HashMap<UUID,ChatStream>();

	public static void setStream (UUID uuid,ChatStream stream) {
		ChatStream.currentStreams.put(uuid,stream);
	}

	public static ChatStream getStream (UUID uuid) {
		return ChatStream.currentStreams.get(uuid);
	}

	public static void removePlayer (UUID uuid) {
		ChatStream.currentStreams.remove(uuid);
	}

	public ChatStream(String name,  String format, boolean whitelistServers, boolean whitelistMembers) {

		this.name = name;
		this.whitelistServers = whitelistServers;
		this.format = format;
		this.servers = new ArrayList<String>();
		this.members = new ArrayList<UUID>();
		this.whitelistMembers = whitelistMembers;

	}

	public void addServer(String server) {

		if (!servers.contains(server)) {
			servers.add(server);
		}

	}

	public void addMember(UUID member) {

		if (!members.contains(member)) {
			members.add(member);
		}

	}

	public String getName() {
		return this.name;
	}

	public String getFormat() {
		return this.format;
	}

	public void sendMessage(ProxiedPlayer sender, String message) {
		
		// Alert IRC plugins
		ProxyServer.getInstance().getPluginManager().callEvent(new MultiChatGlobalIRCEvent(sender, message, format));

		for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {
			if ( (whitelistMembers && members.contains(receiver.getUniqueId())) || (!whitelistMembers && !members.contains(receiver.getUniqueId()))) {
				if ( (whitelistServers && servers.contains(receiver.getServer().getInfo().getName())) || (!whitelistServers && !servers.contains(receiver.getServer().getInfo().getName()))) {
					//TODO hiding & showing streams
					if ( (MultiChat.globalplayers.get(sender.getUniqueId()) == false
							&& sender.getServer().getInfo().getName().equals(receiver.getServer().getInfo().getName())) ||
							(MultiChat.globalplayers.get(receiver.getUniqueId()) == false
							&& sender.getServer().getInfo().getName().equals(receiver.getServer().getInfo().getName())) ||
							(MultiChat.globalplayers.get(sender.getUniqueId()).equals(true) && MultiChat.globalplayers.get(receiver.getUniqueId()))) {

						if (!ChatControl.ignores(sender.getUniqueId(), receiver.getUniqueId(), "global_chat")) {
							receiver.sendMessage(buildFormat(sender,receiver,format,message));
						} else {
							ChatControl.sendIgnoreNotifications(receiver, sender, "global_chat");
						}

					}
				}
			}
		}

		ProxyServer.getInstance().getConsole().sendMessage(buildFormatConsole(sender,format,message));

	}

	public void sendMessage(String message) {
		
		// Alert IRC plugins
		ProxyServer.getInstance().getPluginManager().callEvent(new MultiChatBroadcastIRCEvent("cast", message));
		
		for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {
			if ( (whitelistMembers && members.contains(receiver.getUniqueId())) || (!whitelistMembers && !members.contains(receiver.getUniqueId()))) {
				if ( (whitelistServers && servers.contains(receiver.getServer().getInfo().getName())) || (!whitelistServers && !servers.contains(receiver.getServer().getInfo().getName()))) {
					//TODO hiding & showing streams

					receiver.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));


				}
			}
		}
		//TODO
		System.out.println("\033[33m[MultiChat][CHAT]" + message);

	}

	public BaseComponent[] buildFormat(ProxiedPlayer sender, ProxiedPlayer receiver, String format, String message) {

		String newFormat = format;

		newFormat = newFormat.replace("%DISPLAYNAME%", sender.getDisplayName());
		newFormat = newFormat.replace("%NAME%", sender.getName());

		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(sender.getUniqueId());
		if (opm.isPresent()) {
			newFormat = newFormat.replace("%PREFIX%", opm.get().prefix);
			newFormat = newFormat.replace("%SUFFIX%", opm.get().suffix);
			newFormat = newFormat.replace("%NICK%", opm.get().nick);
		}

		newFormat = newFormat.replace("%DISPLAYNAMET%", receiver.getDisplayName());
		newFormat = newFormat.replace("%NAMET%", receiver.getName());

		Optional<PlayerMeta> opmt = PlayerMetaManager.getInstance().getPlayer(receiver.getUniqueId());
		if (opmt.isPresent()) {
			newFormat = newFormat.replace("%PREFIXT%", opmt.get().prefix);
			newFormat = newFormat.replace("%SUFFIXT%", opmt.get().suffix);
			newFormat = newFormat.replace("%NICKT%", opmt.get().nick);
		}

		newFormat = newFormat.replace("%SERVER%", sender.getServer().getInfo().getName());
		newFormat = newFormat.replace("%SERVERT%", receiver.getServer().getInfo().getName());

		if (MultiChat.globalplayers.get(sender.getUniqueId()).equals(false)) {
			newFormat = newFormat.replace("%MODE%", "Local");
			newFormat = newFormat.replace("%M%", "L");
		}

		if (MultiChat.globalplayers.get(sender.getUniqueId()).equals(true)) {
			newFormat = newFormat.replace("%MODE%", "Global");
			newFormat = newFormat.replace("%M%", "G");
		}

		newFormat = newFormat + "%MESSAGE%";

		BaseComponent[] toSend;

		if (sender.hasPermission("multichat.chat.colour") || sender.hasPermission("multichat.chat.color")) {

			newFormat = newFormat.replace("%MESSAGE%", message);
			toSend = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', newFormat));

		} else {

			newFormat = newFormat.replace("%MESSAGE%", "");
			toSend = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', newFormat) + message);
		}

		return toSend;

	}

	public BaseComponent[] buildFormat(String name, String displayName, String server, ProxiedPlayer receiver, String format, String message) {

		String newFormat = format;

		newFormat = newFormat.replace("%DISPLAYNAME%", displayName);
		newFormat = newFormat.replace("%NAME%", name);
		newFormat = newFormat.replace("%DISPLAYNAMET%", receiver.getDisplayName());
		newFormat = newFormat.replace("%NAMET%", receiver.getName());

		Optional<PlayerMeta> opmt = PlayerMetaManager.getInstance().getPlayer(receiver.getUniqueId());
		if (opmt.isPresent()) {
			newFormat = newFormat.replace("%PREFIXT%", opmt.get().prefix);
			newFormat = newFormat.replace("%SUFFIXT%", opmt.get().suffix);
			newFormat = newFormat.replace("%NICKT%", opmt.get().nick);
		}

		newFormat = newFormat.replace("%SERVER%", server);
		newFormat = newFormat.replace("%SERVERT%", receiver.getServer().getInfo().getName());

		newFormat = newFormat.replace("%MODE%", "Global");
		newFormat = newFormat.replace("%M%", "G");

		newFormat = newFormat + "%MESSAGE%";

		BaseComponent[] toSend;

		newFormat = newFormat.replace("%MESSAGE%", message);
		toSend = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', newFormat));

		return toSend;

	}

	public BaseComponent[] buildFormatConsole(ProxiedPlayer sender, String format, String message) {

		String newFormat = format;

		newFormat = newFormat.replace("%DISPLAYNAME%", sender.getDisplayName());
		newFormat = newFormat.replace("%NAME%", sender.getName());

		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(sender.getUniqueId());
		if (opm.isPresent()) {
			newFormat = newFormat.replace("%PREFIX%", opm.get().prefix);
			newFormat = newFormat.replace("%SUFFIX%", opm.get().suffix);
			newFormat = newFormat.replace("%NICK%", opm.get().nick);
		}

		newFormat = newFormat.replace("%DISPLAYNAMET%", "CONSOLE");
		newFormat = newFormat.replace("%NAMET%", "CONSOLE");
		newFormat = newFormat.replace("%SERVER%", sender.getServer().getInfo().getName());
		newFormat = newFormat.replace("%SERVERT%", "CONSOLE");

		if (MultiChat.globalplayers.get(sender.getUniqueId()).equals(false)) {
			newFormat = newFormat.replace("%MODE%", "Local");
			newFormat = newFormat.replace("%M%", "L");
		}

		if (MultiChat.globalplayers.get(sender.getUniqueId()).equals(true)) {
			newFormat = newFormat.replace("%MODE%", "Global");
			newFormat = newFormat.replace("%M%", "G");
		}

		newFormat = newFormat + "%MESSAGE%";

		BaseComponent[] toSend;

		if (sender.hasPermission("multichat.chat.colour") || sender.hasPermission("multichat.chat.color")) {

			newFormat = newFormat.replace("%MESSAGE%", message);
			toSend = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&6[MultiChat][CHAT] " + newFormat));

		} else {

			newFormat = newFormat.replace("%MESSAGE%", "");
			toSend = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&6[MultiChat][CHAT] " + newFormat) + message);
		}

		return toSend;

	}

	public BaseComponent[] buildFormatConsole(String name, String displayName, String server, String format, String message) {

		String newFormat = format;

		newFormat = newFormat.replace("%DISPLAYNAME%", displayName);
		newFormat = newFormat.replace("%NAME%", name);
		newFormat = newFormat.replace("%DISPLAYNAMET%", "CONSOLE");
		newFormat = newFormat.replace("%NAMET%", "CONSOLE");
		newFormat = newFormat.replace("%SERVER%", server);
		newFormat = newFormat.replace("%SERVERT%", "CONSOLE");

		newFormat = newFormat.replace("%MODE%", "Global");
		newFormat = newFormat.replace("%M%", "G");

		newFormat = newFormat + "%MESSAGE%";

		BaseComponent[] toSend;

		newFormat = newFormat.replace("%MESSAGE%", message);
		toSend = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&6[MultiChat][CHAT] " + newFormat));

		return toSend;

	}
}
