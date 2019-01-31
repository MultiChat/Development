package xyz.olivermartin.multichat.bungee.commands;

import java.util.Optional;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ChatManipulation;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.ConsoleManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChat;

/**
 * Reply Command
 * <p>Used to quickly reply to your last private message</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ReplyCommand extends Command {

	public ReplyCommand() {
		super("r", "multichat.chat.msg", (String[])ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("rcommand").toArray(new String[0]));
	}

	public void execute(CommandSender sender, String[] args) {

		if (args.length < 1) {

			MessageManager.sendMessage(sender, "command_reply_usage");
			MessageManager.sendMessage(sender, "command_reply_desc");

		} else if ((sender instanceof ProxiedPlayer)) {

			String message = "";
			for (String arg : args) {
				message = message + arg + " ";
			}

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

			ChatManipulation chatfix = new ChatManipulation();

			if (MultiChat.lastmsg.containsKey(((ProxiedPlayer)sender).getUniqueId())) {

				if (ProxyServer.getInstance().getPlayer((UUID)MultiChat.lastmsg.get(((ProxiedPlayer)sender).getUniqueId())) != null) {

					ProxiedPlayer target = ProxyServer.getInstance().getPlayer((UUID)MultiChat.lastmsg.get(((ProxiedPlayer)sender).getUniqueId()));

					if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_pm").contains(((ProxiedPlayer)sender).getServer().getInfo().getName())) {

						if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_pm").contains(target.getServer().getInfo().getName())) {

							if (ChatControl.ignores(((ProxiedPlayer)sender).getUniqueId(), target.getUniqueId(), "private_messages")) {
								ChatControl.sendIgnoreNotifications(target, sender, "private_messages");
								return;
							}

							String messageoutformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmout");
							String messageinformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmin");
							String messagespyformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmspy");

							String finalmessage = chatfix.replaceMsgVars(messageoutformat, message, (ProxiedPlayer)sender, target);
							sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

							finalmessage = chatfix.replaceMsgVars(messageinformat, message, (ProxiedPlayer)sender, target);
							target.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

							finalmessage = chatfix.replaceMsgVars(messagespyformat, message, (ProxiedPlayer)sender, target);

							for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

								if ((onlineplayer.hasPermission("multichat.staff.spy"))
										&& (MultiChat.socialspy.contains(onlineplayer.getUniqueId()))
										&& (onlineplayer.getUniqueId() != ((ProxiedPlayer)sender).getUniqueId())
										&& (onlineplayer.getUniqueId() != target.getUniqueId())) {

									onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

								}

							}

							if (MultiChat.lastmsg.containsKey(((ProxiedPlayer)sender).getUniqueId())) {
								MultiChat.lastmsg.remove(((ProxiedPlayer)sender).getUniqueId());
							}

							MultiChat.lastmsg.put(((ProxiedPlayer)sender).getUniqueId(), target.getUniqueId());

							if (MultiChat.lastmsg.containsKey(target.getUniqueId())) {
								MultiChat.lastmsg.remove(target.getUniqueId());
							}

							MultiChat.lastmsg.put(target.getUniqueId(), ((ProxiedPlayer)sender).getUniqueId());

							ConsoleManager.logSocialSpy(sender.getName(), target.getName(), message);
							//System.out.println("\033[31m[MultiChat] SOCIALSPY {" + sender.getName() + " -> " + target.getName() + "}  " + message);

						} else {
							MessageManager.sendMessage(sender, "command_msg_disabled_target");
						}

					} else {
						MessageManager.sendMessage(sender, "command_msg_disabled_sender");
					}

				} else if ( MultiChat.lastmsg.get( ((ProxiedPlayer)sender ).getUniqueId()).equals(new UUID(0L, 0L)) ) {

					// Console target stuff

					if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_pm").contains(((ProxiedPlayer)sender).getServer().getInfo().getName())) {

						String messageoutformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmout");
						String messageinformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmin");
						String messagespyformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmspy");

						String finalmessage = chatfix.replaceMsgConsoleTargetVars(messageoutformat, message, (ProxiedPlayer)sender);
						sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

						finalmessage = chatfix.replaceMsgConsoleTargetVars(messageinformat, message, (ProxiedPlayer)sender);
						ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

						finalmessage = chatfix.replaceMsgConsoleTargetVars(messagespyformat, message, (ProxiedPlayer)sender);

						for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

							if ((onlineplayer.hasPermission("multichat.staff.spy"))
									&& (MultiChat.socialspy.contains(onlineplayer.getUniqueId()))
									&& (onlineplayer.getUniqueId() != ((ProxiedPlayer)sender).getUniqueId())) {

								onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

							}

						}

						if (MultiChat.lastmsg.containsKey(((ProxiedPlayer)sender).getUniqueId())) {
							MultiChat.lastmsg.remove(((ProxiedPlayer)sender).getUniqueId());
						}

						MultiChat.lastmsg.put(((ProxiedPlayer)sender).getUniqueId(), new UUID(0L, 0L));

						if (MultiChat.lastmsg.containsKey(new UUID(0L, 0L))) {
							MultiChat.lastmsg.remove(new UUID(0L, 0L));
						}

						MultiChat.lastmsg.put(new UUID(0L, 0L), ((ProxiedPlayer)sender).getUniqueId());

					} else {
						MessageManager.sendMessage(sender, "command_msg_disabled_sender");
					}

					// End console target stuff

				} else {
					MessageManager.sendMessage(sender, "command_reply_no_one_to_reply_to");
				}

			} else {
				MessageManager.sendMessage(sender, "command_reply_no_one_to_reply_to");
			}

			chatfix = null;

		} else {

			// New console reply

			String message = "";
			for (String arg : args) {
				message = message + arg + " ";
			}

			ChatManipulation chatfix = new ChatManipulation();

			if (MultiChat.lastmsg.containsKey(new UUID(0L,0L))) {

				if (ProxyServer.getInstance().getPlayer((UUID)MultiChat.lastmsg.get((new UUID(0L,0L)))) != null) {

					ProxiedPlayer target = ProxyServer.getInstance().getPlayer((UUID)MultiChat.lastmsg.get((new UUID(0L,0L))));

					if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_pm").contains(target.getServer().getInfo().getName())) {

						String messageoutformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmout");
						String messageinformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmin");
						String messagespyformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("pmspy");

						String finalmessage = chatfix.replaceMsgConsoleSenderVars(messageoutformat, message, target);
						sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

						finalmessage = chatfix.replaceMsgConsoleSenderVars(messageinformat, message, target);
						target.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

						finalmessage = chatfix.replaceMsgConsoleSenderVars(messagespyformat, message, target);

						for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

							if ((onlineplayer.hasPermission("multichat.staff.spy"))
									&& (MultiChat.socialspy.contains(onlineplayer.getUniqueId()))
									&& (onlineplayer.getUniqueId() != target.getUniqueId())) {

								onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', finalmessage)));

							}

						}

						if (MultiChat.lastmsg.containsKey(new UUID(0L,0L))) {
							MultiChat.lastmsg.remove(new UUID(0L,0L));
						}

						MultiChat.lastmsg.put(new UUID(0L,0L), target.getUniqueId());

						if (MultiChat.lastmsg.containsKey(target.getUniqueId())) {
							MultiChat.lastmsg.remove(target.getUniqueId());
						}

						MultiChat.lastmsg.put(target.getUniqueId(), new UUID(0L,0L));

					} else {
						MessageManager.sendMessage(sender, "command_msg_disabled_target");
					}

				} else {
					MessageManager.sendMessage(sender, "command_reply_no_one_to_reply_to");
				}

			} else {
				MessageManager.sendMessage(sender, "command_reply_no_one_to_reply_to");
			}

			chatfix = null;

			// End new console stuff

		}
	}
}
