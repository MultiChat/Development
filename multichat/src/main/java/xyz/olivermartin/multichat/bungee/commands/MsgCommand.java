package xyz.olivermartin.multichat.bungee.commands;

import de.myzelyam.api.vanish.BungeeVanishAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import xyz.olivermartin.multichat.bungee.*;
import xyz.olivermartin.multichat.common.MessageType;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

import java.util.*;

/**
 * Message Command
 * <p>Allows players to send private messages to each other</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class MsgCommand extends Command implements TabExecutor {

	public MsgCommand() {
		super("mcmsg", "multichat.chat.msg", ProxyConfigs.ALIASES.getAliases("mcmsg"));
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			MessageManager.sendMessage(sender, "command_msg_usage");
			MessageManager.sendMessage(sender, "command_msg_usage_toggle");
			return;
		}

		// Pre-Load target because we need it in both scenarios
		Optional<ProxiedPlayer> optionalTarget = PrivateMessageManager.getInstance().getPartialPlayerMatch(args[0]);

		if (args.length == 1) {
			// Console can not toggle PMs
			if (!(sender instanceof ProxiedPlayer)) {
				MessageManager.sendMessage(sender, "command_msg_only_players");
				return;
			}

			// Check if PMs are allowed to be toggled
			if (!ProxyConfigs.CONFIG.isTogglePm()) {
				MessageManager.sendMessage(sender, "command_msg_no_toggle");
				return;
			}

			ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
			UUID playerUID = proxiedPlayer.getUniqueId();

			// If there is no target, quit current PM toggle or notify sender about it
			if (!optionalTarget.isPresent()) {
				if (Events.PMToggle.containsKey(playerUID)) {
					Events.PMToggle.remove(playerUID);
					MessageManager.sendMessage(sender, "command_msg_toggle_off");
				} else {
					MessageManager.sendMessage(sender, "command_msg_not_online");
				}
				return;
			}

			ProxiedPlayer target = optionalTarget.get();

			// TODO: Make this into a proper hook at some point so we can just call Somewhere.getVanishHook().applies(); or something
			if (MultiChat.premiumVanish
					&& ProxyConfigs.CONFIG.isPvPreventMessage()
					&& BungeeVanishAPI.isInvisible(target)
					&& !sender.hasPermission("multichat.chat.msg.vanished")) {
				MessageManager.sendMessage(sender, "command_msg_not_online");
				return;
			}

			// Toggle PM and send message
			boolean toggleResult = Events.togglePM(playerUID, target.getUniqueId());
			MessageManager.sendSpecialMessage(sender,
					"command_msg_toggle_" + (toggleResult ? "on" : "off"),
					target.getName()
					);
			return;
		}

		// Cache message
		String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

		// Cache config values
		boolean fetchSpigotDisplayNames = ProxyConfigs.CONFIG.isFetchSpigotDisplayNames();

		// Target not online
		if (!optionalTarget.isPresent()) {
			if (!(sender instanceof ProxiedPlayer) || !args[0].equalsIgnoreCase("console")) {
				MessageManager.sendMessage(sender, "command_msg_not_online");
				return;
			}

			// Support to send private messages to console
			ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
			ServerInfo serverInfo = proxiedPlayer.getServer().getInfo();

			// Handle disabled servers for player
			if (ProxyConfigs.CONFIG.isNoPmServer(serverInfo.getName())) {
				MessageManager.sendMessage(sender, "command_msg_disabled_sender");
				return;
			}

			// Update player data on local server
			if (fetchSpigotDisplayNames)
				ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(sender.getName(), serverInfo);

			// Send message to console
			PrivateMessageManager.getInstance().sendMessageConsoleTarget(message, proxiedPlayer);
			return;
		}

		ProxiedPlayer target = optionalTarget.get();
		ServerInfo targetServerInfo = target.getServer().getInfo();

		// Handle disabled servers for target
		if (ProxyConfigs.CONFIG.isNoPmServer(targetServerInfo.getName())) {
			MessageManager.sendMessage(sender, "command_msg_disabled_target");
			return;
		}

		// Update target data on local server
		if (fetchSpigotDisplayNames)
			ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(target.getName(), targetServerInfo);

		// If the command sender is the console, send the message now
		if (!(sender instanceof ProxiedPlayer)) {
			PrivateMessageManager.getInstance().sendMessageConsoleSender(message, target);
			return;
		}

		ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
		UUID playerUID = proxiedPlayer.getUniqueId();
		ServerInfo serverInfo = proxiedPlayer.getServer().getInfo();

		// Handle disabled servers for player
		if (ProxyConfigs.CONFIG.isNoPmServer(serverInfo.getName())) {
			MessageManager.sendMessage(sender, "command_msg_disabled_sender");
			return;
		}

		// Check if player has been muted through MultiChat
		if (ChatControl.isMuted(playerUID, MessageType.PRIVATE_MESSAGES)) {
			MessageManager.sendMessage(sender, "mute_cannot_send_message");
			return;
		}

		// Check if the target ignores the player
		if (ChatControl.ignores(playerUID, target.getUniqueId(), MessageType.PRIVATE_MESSAGES)) {
			ChatControl.sendIgnoreNotifications(target, sender, "private_messages");
			return;
		}

		// Check player for potential spam
		if (ChatControl.handleSpam(proxiedPlayer, message, MessageType.PRIVATE_MESSAGES))
			return;

		// Apply chat rules, if any of them cancel the message, return
		Optional<String> optionalChatControl = ChatControl.applyChatRules(sender, message, MessageType.PRIVATE_MESSAGES);
		if (!optionalChatControl.isPresent())
			return;

		message = optionalChatControl.get();

		if (MultiChat.premiumVanish
				&& ProxyConfigs.CONFIG.isPvPreventMessage()
				&& BungeeVanishAPI.isInvisible(target)
				&& !sender.hasPermission("multichat.chat.msg.vanished")) {
			MessageManager.sendMessage(sender, "command_msg_not_online");
			return;
		}

		// Update player data on local server
		if (fetchSpigotDisplayNames)
			ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(sender.getName(), serverInfo);

		// Finally send the message
		PrivateMessageManager.getInstance().sendMessage(message, proxiedPlayer, target);
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		Set<String> matches = new HashSet<>();

		if (args.length == 1) {
			String search = args[0].toLowerCase();
			Set<UUID> hiddenStaff = MultiChatProxy.getInstance().getDataStore().getHiddenStaff();
			ProxyServer.getInstance().getPlayers().stream()
				.filter(target -> target.getName().toLowerCase().startsWith(search)
						&& !hiddenStaff.contains(target.getUniqueId())
						&& !(MultiChat.premiumVanish && BungeeVanishAPI.isInvisible(target))
						)
			.forEach(target -> matches.add(target.getName()));
		}

		return matches;
	}
}
