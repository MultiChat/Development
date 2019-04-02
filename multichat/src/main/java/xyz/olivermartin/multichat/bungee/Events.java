package xyz.olivermartin.multichat.bungee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.olivermartin410.plugins.TChatInfo;
import com.olivermartin410.plugins.TGroupChatInfo;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import xyz.olivermartin.multichat.bungee.commands.GCCommand;

/**
 * Events Manager
 * <p>Manages the majority of the event listeners, chat message, login and logout</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class Events implements Listener {

	public static List<UUID> mcbPlayers = new ArrayList<UUID>();

	private static List<UUID> MCToggle = new ArrayList<UUID>();
	private static List<UUID> ACToggle = new ArrayList<UUID>();
	private static List<UUID> GCToggle = new ArrayList<UUID>();
	public static Map<UUID, UUID> PMToggle = new HashMap<UUID, UUID>();

	public static Set<UUID> hiddenStaff = new HashSet<UUID>();

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

		ProxiedPlayer player = (ProxiedPlayer) event.getSender();

		// If player is bypassing MultiChat
		if (mcbPlayers.contains(player.getUniqueId())) {
			return;
		}

		///
		if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean("fetch_spigot_display_names") == true) {
			BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());
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

				if (MultiChat.viewedchats.get(player.getUniqueId()) != null) {

					String chatName = ((String)MultiChat.viewedchats.get(player.getUniqueId())).toLowerCase();

					if (MultiChat.groupchats.containsKey(chatName)) {

						TGroupChatInfo chatInfo = (TGroupChatInfo)MultiChat.groupchats.get(chatName);
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

					BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());
					BungeeComm.sendMessage(target.getName(), target.getServer().getInfo());

					if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_pm").contains(player.getServer().getInfo().getName())) {

						if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_pm").contains(target.getServer().getInfo().getName())) {

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

		if (event.isCommand()) {

			String[] parts = event.getMessage().split(" ");

			if (CastControl.castList.containsKey(parts[0].substring(1).toLowerCase())) {

				if (event.getSender() instanceof ProxiedPlayer) {

					ProxiedPlayer playerSender = (ProxiedPlayer) event.getSender();

					if (playerSender.hasPermission("multichat.cast." + parts[0].substring(1).toLowerCase())
							|| playerSender.hasPermission("multichat.cast.admin")) {

						String message = MultiChatUtil.getMessageFromArgs(parts, 1);

						CastControl.sendCast(parts[0].substring(1),message,Channel.getChannel(playerSender.getUniqueId()), playerSender);

						event.setCancelled(true);

					}

				} else {

					String message = MultiChatUtil.getMessageFromArgs(parts, 1);

					CastControl.sendCast(parts[0].substring(1), message, Channel.getGlobalChannel(), ProxyServer.getInstance().getConsole());

					event.setCancelled(true);

				}
			}
		}

		if ((!event.isCancelled()) && (!event.isCommand())) {

			if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean("global") == true) {

				if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_global").contains(player.getServer().getInfo().getName())) {

					if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean("fetch_spigot_display_names") == true) {
						BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());
					}

					if ((!MultiChat.frozen) || (player.hasPermission("multichat.chat.always"))) {

						String message = event.getMessage();

						if (ChatControl.isMuted(player.getUniqueId(), "global_chat")) {
							MessageManager.sendMessage(player, "mute_cannot_send_message");
							event.setCancelled(true);
							return;
						}

						DebugManager.log(player.getName() + "- about to check for spam");

						if (ChatControl.handleSpam(player, message, "global_chat")) {
							DebugManager.log(player.getName() + " - chat message being cancelled due to spam");
							event.setCancelled(true);
							return;
						}

						Optional<String> crm;

						crm = ChatControl.applyChatRules(message, "global_chat", player.getName());

						if (crm.isPresent()) {
							message = crm.get();
							event.setMessage(message);
						} else {
							event.setCancelled(true);
							return;
						}
						
						if (!player.hasPermission("multichat.chat.link")) {
							message = ChatControl.replaceLinks(message);
							event.setMessage(message);
						}

						// Let server know players channel preference
						BungeeComm.sendPlayerChannelMessage(player.getName(), Channel.getChannel(player.getUniqueId()).getName(), Channel.getChannel(player.getUniqueId()), player.getServer().getInfo(), (player.hasPermission("multichat.chat.colour")||player.hasPermission("multichat.chat.color")));

						// Message passes through to spigot here

						if (hiddenStaff.contains(player.getUniqueId())) {
							hiddenStaff.remove(player.getUniqueId());
						}

					} else {
						MessageManager.sendMessage(player, "freezechat_frozen");
						event.setCancelled(true);
					}

				}
			}
		}
	}

	@EventHandler
	public void onLogin(PostLoginEvent event) {

		ProxiedPlayer player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		boolean firstJoin = false;

		if (player.hasPermission("multichat.staff.mod")) {

			if (!MultiChat.modchatpreferences.containsKey(uuid)) {

				TChatInfo chatinfo = new TChatInfo();
				chatinfo.setChatColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("modchat.ccdefault").toCharArray()[0]);
				chatinfo.setNameColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("modchat.ncdefault").toCharArray()[0]);
				MultiChat.modchatpreferences.put(uuid, chatinfo);

			}
		}

		if (player.hasPermission("multichat.staff.admin")) {

			if (!MultiChat.adminchatpreferences.containsKey(uuid)) {

				TChatInfo chatinfo = new TChatInfo();
				chatinfo.setChatColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("adminchat.ccdefault").toCharArray()[0]);
				chatinfo.setNameColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("adminchat.ncdefault").toCharArray()[0]);
				MultiChat.adminchatpreferences.put(uuid, chatinfo);

			}
		}

		PlayerMetaManager.getInstance().registerPlayer(uuid, event.getPlayer().getName());

		if (!MultiChat.viewedchats.containsKey(uuid)) {

			MultiChat.viewedchats.put(uuid, null);
			ConsoleManager.log("Registered player " + player.getName());

		}

		if (!ChatModeManager.getInstance().existsPlayer(uuid)) {

			boolean globalMode;
			if (!MultiChat.defaultChannel.equalsIgnoreCase("local")) {
				globalMode = true;
			} else {
				globalMode = false;
			}
			ChatModeManager.getInstance().registerPlayer(uuid, globalMode);
			firstJoin = true;
			//ConsoleManager.log("Created new global chat entry for " + player.getName());

		}
		
		if (MultiChat.forceChannelOnJoin) {
			
			boolean globalMode;
			if (!MultiChat.defaultChannel.equalsIgnoreCase("local")) {
				globalMode = true;
			} else {
				globalMode = false;
			}
			ChatModeManager.getInstance().registerPlayer(uuid, globalMode);
			
		}

		// Set player to appropriate channels
		if (ChatModeManager.getInstance().isGlobal(uuid)) {
			Channel.setChannel(player.getUniqueId(), Channel.getGlobalChannel());
		} else {
			Channel.setChannel(player.getUniqueId(), Channel.getLocalChannel());
		}

		//BungeeComm.sendPlayerChannelMessage(player.getName(), Channel.getChannel(player.getUniqueId()).getName(), Channel.getChannel(player.getUniqueId()), player.getServer().getInfo());

		if (UUIDNameManager.existsUUID(uuid)) {
			UUIDNameManager.removeUUID(uuid);
		}

		UUIDNameManager.addNew(uuid, player.getName());

		ConsoleManager.log("Refreshed UUID-Name lookup: " + uuid.toString());

		if ( ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getBoolean("showjoin") == true ) {

			String joinformat = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getString("serverjoin");
			String silentformat = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getString("silentjoin");
			String welcomeMessage = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getString("welcome_message");

			ChatManipulation chatman = new ChatManipulation();

			joinformat = chatman.replaceJoinMsgVars(joinformat, player.getName());
			silentformat = chatman.replaceJoinMsgVars(silentformat, player.getName());
			welcomeMessage = chatman.replaceJoinMsgVars(welcomeMessage, player.getName());

			for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

				if (!player.hasPermission("multichat.staff.silentjoin")) {

					if (firstJoin && ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getBoolean("welcome")) {
						onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', welcomeMessage)));
					}
					onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', joinformat)));

				} else {

					hiddenStaff.add(player.getUniqueId());

					if (onlineplayer.hasPermission("multichat.staff.silentjoin") ) {
						onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', silentformat)));
					}

				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogout(PlayerDisconnectEvent event) {

		ProxiedPlayer player = event.getPlayer();
		UUID uuid = event.getPlayer().getUniqueId();

		if (hiddenStaff.contains(uuid)) {
			hiddenStaff.remove(uuid);
		}

		if (mcbPlayers.contains(uuid)) {
			mcbPlayers.remove(uuid);
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

		Configuration config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

		if (config.getBoolean("session_ignore")) {
			ChatControl.unignoreAll(uuid);
		}

		// Reset their spam data on logout (nothing is stored persistantly)
		ChatControl.spamPardonPlayer(uuid);

		///
		Channel.removePlayer(player.getUniqueId());
		///

		if (MultiChat.viewedchats.containsKey(uuid)) {
			MultiChat.viewedchats.remove(uuid);
		}

		PlayerMetaManager.getInstance().unregisterPlayer(uuid);

		ConsoleManager.log("Un-Registered player " + player.getName());

		if (!Channel.getGlobalChannel().isMember(player.getUniqueId())) {
			Channel.getGlobalChannel().removeMember(uuid);
		}

		if (!Channel.getLocalChannel().isMember(player.getUniqueId())) {
			Channel.getLocalChannel().removeMember(uuid);
		}

		if ( ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getBoolean("showquit") == true ) {

			String joinformat = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getString("networkquit");
			String silentformat = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getString("silentquit");

			ChatManipulation chatman = new ChatManipulation();

			joinformat = chatman.replaceJoinMsgVars(joinformat, player.getName());
			silentformat = chatman.replaceJoinMsgVars(silentformat, player.getName());

			for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

				if (!player.hasPermission("multichat.staff.silentjoin")) {

					onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', joinformat)).create());

				} else {

					if (onlineplayer.hasPermission("multichat.staff.silentjoin") ) {
						onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', silentformat)).create());
					}
				}
			}
		}
	}


	@EventHandler(priority = EventPriority.LOWEST)
	public void onServerSwitch(ServerSwitchEvent event) {
		// Tell the new server the player's channel preference
		ProxyServer.getInstance().getScheduler().schedule(MultiChat.getInstance(), new Runnable() {

			public void run() {

				try {
					BungeeComm.sendPlayerChannelMessage(event.getPlayer().getName(), Channel.getChannel(event.getPlayer().getUniqueId()).getName(), Channel.getChannel(event.getPlayer().getUniqueId()), event.getPlayer().getServer().getInfo(), (event.getPlayer().hasPermission("multichat.chat.colour")|| event.getPlayer().hasPermission("multichat.chat.color")));
				}

				catch (NullPointerException ex) { /* EMPTY */ }
			}

		}, 500L, TimeUnit.MILLISECONDS);

	}

}
