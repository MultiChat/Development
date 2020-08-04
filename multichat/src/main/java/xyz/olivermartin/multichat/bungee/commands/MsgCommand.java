package xyz.olivermartin.multichat.bungee.commands;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import de.myzelyam.api.vanish.BungeeVanishAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.Events;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.bungee.PrivateMessageManager;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;

/**
 * Message Command
 * <p>Allows players to send private messages to each other</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class MsgCommand extends Command implements TabExecutor {

	public MsgCommand() {
		super("mcmsg", "multichat.chat.msg", (String[]) ConfigManager.getInstance().getHandler(ConfigFile.ALIASES).getConfig().getStringList("msg").toArray(new String[0]));
	}

	public void execute(CommandSender sender, String[] args) {

		if (args.length < 1) {

			// Show usage (not enough args)

			MessageManager.sendMessage(sender, "command_msg_usage");
			MessageManager.sendMessage(sender, "command_msg_usage_toggle");

		} else {

			boolean toggleresult;

			if (args.length == 1) {

				// 1 arg --> toggle

				Optional<ProxiedPlayer> opTarget = PrivateMessageManager.getInstance().getPartialPlayerMatch(args[0]);

				if (opTarget.isPresent()) {

					ProxiedPlayer target = opTarget.get();

					if ((sender instanceof ProxiedPlayer)) {

						boolean permittedToMessage = true;

						if (MultiChat.premiumVanish && MultiChat.hideVanishedStaffInMsg) {
							if (BungeeVanishAPI.isInvisible(target) && !sender.hasPermission("multichat.chat.msg.vanished")) {
								permittedToMessage = false;
							}
						}

						if (permittedToMessage) {

							ProxiedPlayer player = (ProxiedPlayer)sender;
							toggleresult = Events.togglePM(player.getUniqueId(), target.getUniqueId());

							if (toggleresult == true) {

								Configuration config = ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig();

								if (config.contains(ConfigValues.Config.TOGGLE_PM) ? config.getBoolean(ConfigValues.Config.TOGGLE_PM) == false : false) {

									toggleresult = Events.togglePM(player.getUniqueId(), target.getUniqueId());
									MessageManager.sendMessage(sender, "command_msg_no_toggle");

								} else {
									MessageManager.sendSpecialMessage(sender, "command_msg_toggle_on", target.getName());
								}

							} else {
								MessageManager.sendMessage(sender, "command_msg_toggle_off");
							}

						} else {
							// Vanished staff member
							MessageManager.sendMessage(sender, "command_msg_not_online");
						}

					} else {
						MessageManager.sendMessage(sender, "command_msg_only_players");
					}

				} else {

					ProxiedPlayer player = (ProxiedPlayer) sender;

					if ( Events.PMToggle.containsKey(player.getUniqueId())) {
						Events.PMToggle.remove(player.getUniqueId());
						MessageManager.sendMessage(sender, "command_msg_toggle_off");
					} else {
						MessageManager.sendMessage(sender, "command_msg_not_online");
					}

				}

			} else if ((sender instanceof ProxiedPlayer)) {

				// >1 arg and the sender is a PLAYER

				String message = MultiChatUtil.getMessageFromArgs(args, 1);

				Optional<String> crm;

				if (ChatControl.isMuted(((ProxiedPlayer)sender).getUniqueId(), "private_messages")) {
					MessageManager.sendMessage(sender, "mute_cannot_send_message");
					return;
				}

				if (ChatControl.handleSpam(((ProxiedPlayer)sender), message, "private_messages")) {
					return;
				}

				crm = ChatControl.applyChatRules(message, "private_messages", sender.getName());

				if (crm.isPresent()) {
					message = crm.get();
				} else {
					return;
				}

				Optional<ProxiedPlayer> opTarget = PrivateMessageManager.getInstance().getPartialPlayerMatch(args[0]);

				if (opTarget.isPresent()) {

					ProxiedPlayer target = opTarget.get();

					boolean permittedToMessage = true;

					if (MultiChat.premiumVanish && MultiChat.hideVanishedStaffInMsg) {
						if (BungeeVanishAPI.isInvisible(target) && !sender.hasPermission("multichat.chat.msg.vanished")) {
							permittedToMessage = false;
						}
					}

					if (permittedToMessage) {

						if (ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getBoolean(ConfigValues.Config.FETCH_SPIGOT_DISPLAY_NAMES) == true) {

							ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(sender.getName(), ((ProxiedPlayer)sender).getServer().getInfo());
							ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(target.getName(), target.getServer().getInfo());

						}

						if (!ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getStringList(ConfigValues.Config.NO_PM).contains(((ProxiedPlayer)sender).getServer().getInfo().getName())) {

							if (!ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getStringList(ConfigValues.Config.NO_PM).contains(target.getServer().getInfo().getName())) {

								if (ChatControl.ignores(((ProxiedPlayer)sender).getUniqueId(), target.getUniqueId(), "private_messages")) {
									ChatControl.sendIgnoreNotifications(target, sender, "private_messages");
									return;
								}

								PrivateMessageManager.getInstance().sendMessage(message, (ProxiedPlayer)sender, target);

							} else {
								MessageManager.sendMessage(sender, "command_msg_disabled_target");
							}

						} else {
							MessageManager.sendMessage(sender, "command_msg_disabled_sender");
						}

					} else {
						// Vanished staff member
						MessageManager.sendMessage(sender, "command_msg_not_online");
					}

				} else if (args[0].equalsIgnoreCase("console")) {

					// New console target stuff here!

					if (ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getBoolean(ConfigValues.Config.FETCH_SPIGOT_DISPLAY_NAMES) == true) {

						ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(sender.getName(), ((ProxiedPlayer)sender).getServer().getInfo());

					}

					if (!ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getStringList(ConfigValues.Config.NO_PM).contains(((ProxiedPlayer)sender).getServer().getInfo().getName())) {

						PrivateMessageManager.getInstance().sendMessageConsoleTarget(message, (ProxiedPlayer)sender);

					} else {
						MessageManager.sendMessage(sender, "command_msg_disabled_sender");
					}

					// End of console target stuff

				} else {
					MessageManager.sendMessage(sender, "command_msg_not_online");
				}

			} else {

				// >1 arg and the sender is the CONSOLE

				String message = MultiChatUtil.getMessageFromArgs(args, 1);

				Optional<ProxiedPlayer> opTarget = PrivateMessageManager.getInstance().getPartialPlayerMatch(args[0]);

				if (opTarget.isPresent()) {

					ProxiedPlayer target = opTarget.get();

					if (ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getBoolean(ConfigValues.Config.FETCH_SPIGOT_DISPLAY_NAMES) == true) {

						ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(target.getName(), target.getServer().getInfo());

					}

					if (!ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getStringList(ConfigValues.Config.NO_PM).contains(target.getServer().getInfo().getName())) {

						PrivateMessageManager.getInstance().sendMessageConsoleSender(message, target);

					} else {
						MessageManager.sendMessage(sender, "command_msg_disabled_target");
					}

				} else {
					MessageManager.sendMessage(sender, "command_msg_not_online");
				}

			}
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

		Set<String> matches = new HashSet<>();

		if ( args.length == 1 ) {

			String search = args[0].toLowerCase();

			for ( ProxiedPlayer player : ProxyServer.getInstance().getPlayers() ) {

				if ( player.getName().toLowerCase().startsWith( search ) ) {

					if (!MultiChatProxy.getInstance().getDataStore().getHiddenStaff().contains(player.getUniqueId())) {
						if (MultiChat.premiumVanish) {
							if (!BungeeVanishAPI.isInvisible(player)) {
								matches.add(player.getName());
							}
						} else {
							matches.add(player.getName());
						}
					}

				}

			}
		}

		return matches;
	}
}
