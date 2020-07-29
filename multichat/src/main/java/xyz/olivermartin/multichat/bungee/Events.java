package xyz.olivermartin.multichat.bungee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.olivermartin410.plugins.TGroupChatInfo;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import xyz.olivermartin.multichat.bungee.commands.GCCommand;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyChatManager;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelMode;
import xyz.olivermartin.multichat.proxy.common.channels.proxy.ProxyChannel;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

/**
 * Events Manager
 * <p>Manages the majority of the event listeners, chat message, login and logout</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class Events implements Listener {

	public static List<UUID> mcbPlayers = new ArrayList<UUID>();

	public static List<UUID> MCToggle = new ArrayList<UUID>();
	public static List<UUID> ACToggle = new ArrayList<UUID>();
	public static List<UUID> GCToggle = new ArrayList<UUID>();
	public static Map<UUID, UUID> PMToggle = new HashMap<UUID, UUID>();

	public static boolean toggleMC(UUID uuid) {

		if (MCToggle.contains(uuid)) {
			MCToggle.remove(uuid);
			return false;
		}

		if (ACToggle.contains(uuid)) {
			ACToggle.remove(uuid);
		}
		if (GCToggle.contains(uuid)) {
			GCToggle.remove(uuid);
		}
		if (PMToggle.containsKey(uuid)) {
			PMToggle.remove(uuid);
		}

		MCToggle.add(uuid);
		return true;

	}

	public static boolean toggleAC(UUID uuid) {

		if (ACToggle.contains(uuid)) {
			ACToggle.remove(uuid);
			return false;
		}

		if (MCToggle.contains(uuid)) {
			MCToggle.remove(uuid);
		}
		if (GCToggle.contains(uuid)) {
			GCToggle.remove(uuid);
		}
		if (PMToggle.containsKey(uuid)) {
			PMToggle.remove(uuid);
		}

		ACToggle.add(uuid);
		return true;

	}

	public static boolean toggleGC(UUID uuid) {

		if (GCToggle.contains(uuid)) {
			GCToggle.remove(uuid);
			return false;
		}

		if (MCToggle.contains(uuid)) {
			MCToggle.remove(uuid);
		}
		if (ACToggle.contains(uuid)) {
			ACToggle.remove(uuid);
		}
		if (PMToggle.containsKey(uuid)) {
			PMToggle.remove(uuid);
		}

		GCToggle.add(uuid);
		return true;

	}

	public static boolean togglePM(UUID uuid, UUID uuidt) {

		if (PMToggle.containsKey(uuid)) {
			PMToggle.remove(uuid);
			return false;
		}

		if (MCToggle.contains(uuid)) {
			MCToggle.remove(uuid);
		}
		if (ACToggle.contains(uuid)) {
			ACToggle.remove(uuid);
		}
		if (GCToggle.contains(uuid)) {
			GCToggle.remove(uuid);
		}

		PMToggle.put(uuid, uuidt);
		return true;

	}

	@EventHandler(priority=64)
	public void onChat(ChatEvent event) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();
		ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();
		ProxyChatManager chatManager = MultiChatProxy.getInstance().getChatManager();
		ProxiedPlayer player = (ProxiedPlayer) event.getSender();

		// New null pointer checks
		if (player.getServer() == null) {
			DebugManager.log("Player sending chat message has null server! Abandoning...");
			return;
		} else {
			if (player.getServer().getInfo() == null) {
				DebugManager.log("Player sending chat message has null server info! Abandoning...");
				return;
			}
		}

		// If player is bypassing MultiChat
		if (mcbPlayers.contains(player.getUniqueId())) {
			return;
		}

		///
		if (ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getBoolean(ConfigValues.Config.FETCH_SPIGOT_DISPLAY_NAMES) == true) {
			if (player.getServer() != null) {
				ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(player.getName(), player.getServer().getInfo());
			}
		}
		///

		if (MCToggle.contains(player.getUniqueId())) {

			String message = event.getMessage();

			if (!event.isCommand()) {

				StaffChatManager chatman = new StaffChatManager();

				event.setCancelled(true);
				chatman.sendModMessage(player.getName(),player.getDisplayName(), player.getServer().getInfo().getName(), message);
				chatman = null;

			}
		}

		if (ACToggle.contains(player.getUniqueId())) {

			String message = event.getMessage();

			if (!event.isCommand()) {

				StaffChatManager chatman = new StaffChatManager();

				event.setCancelled(true);
				chatman.sendAdminMessage(player.getName(),player.getDisplayName(), player.getServer().getInfo().getName(), message);
				chatman = null;

			}
		}

		if (GCToggle.contains(player.getUniqueId())) {

			String message = event.getMessage();

			if (!event.isCommand()) {

				event.setCancelled(true);

				if (ds.getViewedChats().get(player.getUniqueId()) != null) {

					String chatName = ((String)ds.getViewedChats().get(player.getUniqueId())).toLowerCase();

					if (ds.getGroupChats().containsKey(chatName)) {

						TGroupChatInfo chatInfo = (TGroupChatInfo)ds.getGroupChats().get(chatName);
						String playerName = player.getName();

						if ((chatInfo.getFormal() == true)
								&& (chatInfo.getAdmins().contains(player.getUniqueId()))) {

							playerName = "&o" + playerName;

						}

						GCCommand.sendMessage(message, playerName, chatInfo);

					} else {
						MessageManager.sendMessage(player, "groups_toggled_but_no_longer_exists_1");
						MessageManager.sendMessage(player, "groups_toggled_but_no_longer_exists_2");
					}

				} else {
					MessageManager.sendMessage(player, "groups_toggled_but_no_longer_exists_1");
					MessageManager.sendMessage(player, "groups_toggled_but_no_longer_exists_2");
				}
			}
		}

		if (PMToggle.containsKey(player.getUniqueId())) {

			String message = event.getMessage();

			if (!event.isCommand()) {

				Optional<String> crm;

				event.setCancelled(true);

				if (ChatControl.isMuted(player.getUniqueId(), "private_messages")) {
					MessageManager.sendMessage(player, "mute_cannot_send_message");
					return;
				}

				if (ChatControl.handleSpam(player, message, "private_messages")) {
					return;
				}

				crm = ChatControl.applyChatRules(message, "private_messages", player.getName());

				if (crm.isPresent()) {
					message = crm.get();
				} else {
					return;
				}

				if (ProxyServer.getInstance().getPlayer((UUID)PMToggle.get(player.getUniqueId())) != null) {

					ProxiedPlayer target = ProxyServer.getInstance().getPlayer((UUID)PMToggle.get(player.getUniqueId()));

					ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(player.getName(), player.getServer().getInfo());
					ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(target.getName(), target.getServer().getInfo());

					if (!ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getStringList(ConfigValues.Config.NO_PM).contains(player.getServer().getInfo().getName())) {

						if (!ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getStringList(ConfigValues.Config.NO_PM).contains(target.getServer().getInfo().getName())) {

							if (ChatControl.ignores(player.getUniqueId(), target.getUniqueId(), "private_messages")) {
								ChatControl.sendIgnoreNotifications(target, player, "private_messages");
								return;
							}

							PrivateMessageManager.getInstance().sendMessage(message, player, target);

						} else {
							MessageManager.sendMessage(player, "command_msg_disabled_target");
						}

					} else {
						MessageManager.sendMessage(player, "command_msg_disabled_sender");
					}

				} else {
					MessageManager.sendMessage(player, "command_msg_not_online");
				}

			}
		}

		/* MULTICHAT HEX CODE PRE-PROCESSOR! */

		/*if (player.hasPermission("multichat.chat.color") 
				|| player.hasPermission("multichat.chat.color.rgb")
				|| player.hasPermission("multichat.chat.colour")
				|| player.hasPermission("multichat.chat.colour.rgb")) {
			String msg = event.getMessage();
			msg = msg.replaceAll("(?i)\\&(x|#)([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])", "&x&$2&$3&$4&$5&$6&$7");
			event.setMessage(msg);
		}*/

		/* END PRE-PROCESSOR */

		if (event.isCommand()) {

			String[] parts = event.getMessage().split(" ");

			if (CastControl.castList.containsKey(parts[0].substring(1).toLowerCase())) {

				if (event.getSender() instanceof ProxiedPlayer) {

					ProxiedPlayer playerSender = (ProxiedPlayer) event.getSender();

					if (playerSender.hasPermission("multichat.cast." + parts[0].substring(1).toLowerCase())
							|| playerSender.hasPermission("multichat.cast.admin")) {

						String message = MultiChatUtil.getMessageFromArgs(parts, 1);

						if (channelManager.getChannelMode(player.getUniqueId()) == ChannelMode.LOCAL) {

							CastControl.sendCast(parts[0].substring(1),message,channelManager.getLocalChannel(), player.getServer().getInfo().getName(), playerSender);

						} else {

							ProxyChannel pc = channelManager.getProxyChannel(channelManager.getChannel(player)).get(); // TODO unsafe
							CastControl.sendCast(parts[0].substring(1),message,pc, playerSender);

						}

						event.setCancelled(true);

					}

				} else {

					String message = MultiChatUtil.getMessageFromArgs(parts, 1);

					CastControl.sendCast(parts[0].substring(1), message, channelManager.getGlobalChannel(), ProxyServer.getInstance().getConsole());

					event.setCancelled(true);

				}
			}
		}

		if ((!event.isCancelled()) && (!event.isCommand())) {

			String message = event.getMessage(); // Current message
			
			Optional<String> optionalMessage = chatManager.handleChatMessage(player, message); // Processed message

			if (!optionalMessage.isPresent()) {
				// Player not permitted to send this message, so cancel it
				event.setCancelled(true);
				return;
			}

			message = optionalMessage.get();
			event.setMessage(message);

			DebugManager.log("Does player have ALL colour permission? " + chatManager.hasLegacyColourPermission(player));
			DebugManager.log("Does player have simple colour permission? " + chatManager.hasSimpleColourPermission(player));
			DebugManager.log("Does player have rgb colour permission? " + chatManager.hasRGBColourPermission(player));

			// Let server know players channel preference

			String channelFormat;

			switch (channelManager.getChannel(player)) {

			case "global":
				channelFormat = channelManager.getGlobalChannel().getInfo().getFormat();
				break;
			case "local":
				channelFormat = channelManager.getLocalChannel().getFormat();
				break;
			default:
				if (channelManager.existsProxyChannel(channelManager.getChannel(player))) {
					channelFormat = channelManager.getProxyChannel(channelManager.getChannel(player)).get().getInfo().getFormat();
				} else {
					channelFormat = channelManager.getGlobalChannel().getInfo().getFormat();
				}
				break;
			}

			ProxyLocalCommunicationManager.sendPlayerDataMessage(
					player.getName(),
					channelManager.getChannel(player),
					channelFormat,
					player.getServer().getInfo(),
					chatManager.hasSimpleColourPermission(player),
					chatManager.hasRGBColourPermission(player));

			// Message passes through to spigot here

			if (ds.getHiddenStaff().contains(player.getUniqueId())) {
				ds.getHiddenStaff().remove(player.getUniqueId());
			}

		}

	}

}
