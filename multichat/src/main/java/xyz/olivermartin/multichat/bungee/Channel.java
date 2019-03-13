package xyz.olivermartin.multichat.bungee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.bungee.events.PostBroadcastEvent;
import xyz.olivermartin.multichat.bungee.events.PostGlobalChatEvent;

/**
 * Channel
 * <p>A class to represent a chat channel and control the messages sent etc.</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class Channel {

	private static GlobalChannel global;
	private static LocalChannel local;

	static {

		global = new GlobalChannel("&f%DISPLAYNAME%&f: ");
		local = new LocalChannel();

	}

	public static GlobalChannel getGlobalChannel() {
		return global;
	}

	public static LocalChannel getLocalChannel() {
		return local;
	}

	public static Map<UUID,Channel> playerChannels = new HashMap<UUID, Channel>();

	public static void setChannel (UUID uuid, Channel channel) {
		Channel.playerChannels.put(uuid, channel);
	}

	public static Channel getChannel (UUID uuid) {
		return Channel.playerChannels.get(uuid);
	}

	public static void removePlayer (UUID uuid) {
		Channel.playerChannels.remove(uuid);
	}

	/* END STATIC */

	boolean whitelistMembers;
	protected List<UUID> members;

	boolean whitelistServers;
	protected List<String> servers;

	protected String name;
	protected String format;

	public Channel(String name, String format, boolean whitelistServers, boolean whitelistMembers) {

		this.name = name;
		this.whitelistServers = whitelistServers;
		this.format = format;
		this.servers = new ArrayList<String>();
		this.members = new ArrayList<UUID>();
		this.whitelistMembers = whitelistMembers;

	}

	public boolean isMember(UUID player) {
		if (this.whitelistMembers) {
			return this.members.contains(player);
		} else {
			return !this.members.contains(player);
		}
	}
	
	public void removeMember(UUID player) {
		this.members.remove(player);
	}

	public List<UUID> getMembers() {
		return this.members;
	}

	public boolean isWhitelistMembers() {
		return this.whitelistMembers;
	}

	public void addServer(String server) {
		if (!servers.contains(server)) servers.add(server);
	}

	public void setServers(List<String> servers) {
		this.servers = servers;
	}

	public void clearServers() {
		this.servers = new ArrayList<String>();
	}

	public void addMember(UUID member) {
		if (!members.contains(member)) members.add(member);
	}

	public void setMembers(List<UUID> members) {
		this.members = members;
	}

	public String getName() {
		return this.name;
	}

	public String getFormat() {
		return this.format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void sendMessage(ProxiedPlayer sender, String message, String format) {
		
		DebugManager.log("CHANNEL #" + getName() + ": Got a message for the channel");
		DebugManager.log("CHANNEL #" + getName() + ": SENDER = " + sender.getName());
		DebugManager.log("CHANNEL #" + getName() + ": MESSAGE = " + message);
		DebugManager.log("CHANNEL #" + getName() + ": FORMAT = " + format);

		// Set<String> players = new HashSet<String>();

		for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {

			if (receiver != null) {

				synchronized (receiver) {

					if ( (whitelistMembers && members.contains(receiver.getUniqueId())) || (!whitelistMembers && !members.contains(receiver.getUniqueId()))) {
						if ( (whitelistServers && servers.contains(receiver.getServer().getInfo().getName())) || (!whitelistServers && !servers.contains(receiver.getServer().getInfo().getName()))) {
							//TODO hiding & showing channels
							/*if ( (!ChatModeManager.getInstance().isGlobal(sender.getUniqueId())
									&& sender.getServer().getInfo().getName().equals(receiver.getServer().getInfo().getName())) ||
									(!ChatModeManager.getInstance().isGlobal(receiver.getUniqueId())
											&& sender.getServer().getInfo().getName().equals(receiver.getServer().getInfo().getName())) ||
									(ChatModeManager.getInstance().isGlobal(sender.getUniqueId()) && ChatModeManager.getInstance().isGlobal(receiver.getUniqueId()))) {*/

							if (!ChatControl.ignores(sender.getUniqueId(), receiver.getUniqueId(), "global_chat")) {
								if (!receiver.getServer().getInfo().getName().equals(sender.getServer().getInfo().getName())) {
									receiver.sendMessage(buildFormat(sender,receiver,format,message));
								} else {
									// players.add(receiver.getName());
								}
							} else {
								ChatControl.sendIgnoreNotifications(receiver, sender, "global_chat");
							}

							//}
						}

					}

				}

			}
		}

		/*String playerString;
		if (local) {
			playerString = playerList;
		} else {
			playerString = MultiChatUtil.getStringFromCollection(players);
		}

		String newFormat = buildSpigotFormat(sender,format,message);
		BungeeComm.sendChatMessage(
				sender.getName(),
				newFormat,
				message, 
				(sender.hasPermission("multichat.chat.color") || sender.hasPermission("multichat.chat.colour")),
				playerString,
				sender.getServer().getInfo()
				);*/

		// Trigger PostGlobalChatEvent
		ProxyServer.getInstance().getPluginManager().callEvent(new PostGlobalChatEvent(sender, message, format));

		sendToConsole(sender,format,message);

	}

	public void sendMessage(String message, CommandSender sender) {

		for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {
			if ( (whitelistMembers && members.contains(receiver.getUniqueId())) || (!whitelistMembers && !members.contains(receiver.getUniqueId()))) {
				if ( (whitelistServers && servers.contains(receiver.getServer().getInfo().getName())) || (!whitelistServers && !servers.contains(receiver.getServer().getInfo().getName()))) {
					//TODO hiding & showing streams

					receiver.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));


				}
			}
		}

		// Trigger PostBroadcastEvent
		ProxyServer.getInstance().getPluginManager().callEvent(new PostBroadcastEvent("cast", message));

		ConsoleManager.logDisplayMessage(message);

	}

	public String buildSpigotFormat(ProxiedPlayer sender, String format, String message) {

		String newFormat = format;

		/*newFormat = newFormat.replace("%DISPLAYNAME%", sender.getDisplayName());
		newFormat = newFormat.replace("%NAME%", sender.getName());

		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(sender.getUniqueId());
		if (opm.isPresent()) {
			newFormat = newFormat.replace("%PREFIX%", opm.get().prefix);
			newFormat = newFormat.replace("%SUFFIX%", opm.get().suffix);
			newFormat = newFormat.replace("%NICK%", opm.get().nick);
			newFormat = newFormat.replace("%WORLD%", opm.get().world);
		}

		newFormat = newFormat.replace("%SERVER%", sender.getServer().getInfo().getName());


		if (!ChatModeManager.getInstance().isGlobal(sender.getUniqueId())) {
			newFormat = newFormat.replace("%MODE%", "Local");
			newFormat = newFormat.replace("%M%", "L");
		}

		if (ChatModeManager.getInstance().isGlobal(sender.getUniqueId())) {
			newFormat = newFormat.replace("%MODE%", "Global");
			newFormat = newFormat.replace("%M%", "G");
		}*/

		newFormat = newFormat + "%MESSAGE%";

		return newFormat;

	}

	public BaseComponent[] buildFormat(ProxiedPlayer sender, ProxiedPlayer receiver, String format, String message) {

		String newFormat = format;

		/*newFormat = newFormat.replace("%DISPLAYNAME%", sender.getDisplayName());
		newFormat = newFormat.replace("%NAME%", sender.getName());

		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(sender.getUniqueId());
		if (opm.isPresent()) {
			newFormat = newFormat.replace("%PREFIX%", opm.get().prefix);
			newFormat = newFormat.replace("%SUFFIX%", opm.get().suffix);
			newFormat = newFormat.replace("%NICK%", opm.get().nick);
			newFormat = newFormat.replace("%WORLD%", opm.get().world);
		}

		newFormat = newFormat.replace("%DISPLAYNAMET%", receiver.getDisplayName());
		newFormat = newFormat.replace("%NAMET%", receiver.getName());

		Optional<PlayerMeta> opmt = PlayerMetaManager.getInstance().getPlayer(receiver.getUniqueId());
		if (opmt.isPresent()) {
			newFormat = newFormat.replace("%PREFIXT%", opmt.get().prefix);
			newFormat = newFormat.replace("%SUFFIXT%", opmt.get().suffix);
			newFormat = newFormat.replace("%NICKT%", opmt.get().nick);
			newFormat = newFormat.replace("%WORLDT%", opmt.get().world);
		}

		newFormat = newFormat.replace("%SERVER%", sender.getServer().getInfo().getName());
		newFormat = newFormat.replace("%SERVERT%", receiver.getServer().getInfo().getName());


		if (!ChatModeManager.getInstance().isGlobal(sender.getUniqueId())) {
			newFormat = newFormat.replace("%MODE%", "Local");
			newFormat = newFormat.replace("%M%", "L");
		}

		if (ChatModeManager.getInstance().isGlobal(sender.getUniqueId())) {
			newFormat = newFormat.replace("%MODE%", "Global");
			newFormat = newFormat.replace("%M%", "G");
		}*/

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

	public BaseComponent[] buildFormat(String name, String displayName, String server, String world, ProxiedPlayer receiver, String format, String message) {

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
			newFormat = newFormat.replace("%WORLDT%", opmt.get().world);
		}

		newFormat = newFormat.replace("%SERVER%", server);
		newFormat = newFormat.replace("%SERVERT%", receiver.getServer().getInfo().getName());

		newFormat = newFormat.replace("%WORLD%", world);


		newFormat = newFormat.replace("%MODE%", "Global");
		newFormat = newFormat.replace("%M%", "G");

		newFormat = newFormat + "%MESSAGE%";

		BaseComponent[] toSend;

		newFormat = newFormat.replace("%MESSAGE%", message);
		toSend = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', newFormat));

		return toSend;

	}

	public void sendToConsole(ProxiedPlayer sender, String format, String message) {

		String newFormat = format;

		newFormat = newFormat.replace("%DISPLAYNAME%", sender.getDisplayName());
		newFormat = newFormat.replace("%NAME%", sender.getName());

		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(sender.getUniqueId());
		if (opm.isPresent()) {
			newFormat = newFormat.replace("%PREFIX%", opm.get().prefix);
			newFormat = newFormat.replace("%SUFFIX%", opm.get().suffix);
			newFormat = newFormat.replace("%NICK%", opm.get().nick);
			newFormat = newFormat.replace("%WORLD%", opm.get().world);
		}

		newFormat = newFormat.replace("%DISPLAYNAMET%", "CONSOLE");
		newFormat = newFormat.replace("%NAMET%", "CONSOLE");
		newFormat = newFormat.replace("%SERVER%", sender.getServer().getInfo().getName());
		newFormat = newFormat.replace("%SERVERT%", "CONSOLE");
		newFormat = newFormat.replace("%WORLDT%", "CONSOLE");

		if (!ChatModeManager.getInstance().isGlobal(sender.getUniqueId())) {
			newFormat = newFormat.replace("%MODE%", "Local");
			newFormat = newFormat.replace("%M%", "L");
		}

		if (ChatModeManager.getInstance().isGlobal(sender.getUniqueId())) {
			newFormat = newFormat.replace("%MODE%", "Global");
			newFormat = newFormat.replace("%M%", "G");
		}

		newFormat = newFormat + "%MESSAGE%";

		if (sender.hasPermission("multichat.chat.colour") || sender.hasPermission("multichat.chat.color")) {

			newFormat = newFormat.replace("%MESSAGE%", message);
			ConsoleManager.logChat(newFormat);

		} else {

			newFormat = newFormat.replace("%MESSAGE%", "");
			ConsoleManager.logBasicChat(newFormat, message);

		}

	}

	public void sendToConsole(String name, String displayName, String server, String world, String format, String message) {

		String newFormat = format;

		newFormat = newFormat.replace("%DISPLAYNAME%", displayName);
		newFormat = newFormat.replace("%NAME%", name);
		newFormat = newFormat.replace("%DISPLAYNAMET%", "CONSOLE");
		newFormat = newFormat.replace("%NAMET%", "CONSOLE");
		newFormat = newFormat.replace("%SERVER%", server);
		newFormat = newFormat.replace("%SERVERT%", "CONSOLE");
		newFormat = newFormat.replace("%WORLD%", world);
		newFormat = newFormat.replace("%WORLDT%", "CONSOLE");

		newFormat = newFormat.replace("%MODE%", "Global");
		newFormat = newFormat.replace("%M%", "G");

		newFormat = newFormat + "%MESSAGE%";

		newFormat = newFormat.replace("%MESSAGE%", message);

		ConsoleManager.logChat(newFormat);

	}
}
