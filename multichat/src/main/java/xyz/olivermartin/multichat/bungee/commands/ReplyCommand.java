package xyz.olivermartin.multichat.bungee.commands;

import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatManipulation;
import xyz.olivermartin.multichat.bungee.MultiChat;

/**
 * Reply Command
 * <p>Used to quickly reply to your last private message</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ReplyCommand extends Command {

	private static String[] aliases = (String[])MultiChat.configman.config.getStringList("rcommand").toArray(new String[0]);

	public ReplyCommand() {
		super("r", "multichat.chat.msg", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		if (args.length < 1) {

			sender.sendMessage(new ComponentBuilder("Usage: /r <message>").color(ChatColor.AQUA).create());
			sender.sendMessage(new ComponentBuilder("Reply to the person who you private messaged most recently").color(ChatColor.AQUA).create());

		} else if ((sender instanceof ProxiedPlayer)) {

			String message = "";
			for (String arg : args) {
				message = message + arg + " ";
			}

			ChatManipulation chatfix = new ChatManipulation();

			if (MultiChat.lastmsg.containsKey(((ProxiedPlayer)sender).getUniqueId())) {

				if (ProxyServer.getInstance().getPlayer((UUID)MultiChat.lastmsg.get(((ProxiedPlayer)sender).getUniqueId())) != null) {

					ProxiedPlayer target = ProxyServer.getInstance().getPlayer((UUID)MultiChat.lastmsg.get(((ProxiedPlayer)sender).getUniqueId()));

					if (!MultiChat.configman.config.getStringList("no_pm").contains(((ProxiedPlayer)sender).getServer().getInfo().getName())) {

						if (!MultiChat.configman.config.getStringList("no_pm").contains(target.getServer().getInfo().getName())) {

							String messageoutformat = MultiChat.configman.config.getString("pmout");
							String messageinformat = MultiChat.configman.config.getString("pmin");
							String messagespyformat = MultiChat.configman.config.getString("pmspy");

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

							System.out.println("\033[31m[MultiChat] SOCIALSPY {" + sender.getName() + " -> " + target.getName() + "}  " + message);

						} else {
							sender.sendMessage(new ComponentBuilder("Sorry private messages are disabled on the target player's server!").color(ChatColor.RED).create());
						}

					} else {
						sender.sendMessage(new ComponentBuilder("Sorry private messages are disabled on this server!").color(ChatColor.RED).create());
					}

				} else {
					sender.sendMessage(new ComponentBuilder("You have no one to reply to!").color(ChatColor.RED).create());
				}

			} else {
				sender.sendMessage(new ComponentBuilder("You have no one to reply to!").color(ChatColor.RED).create());
			}

			chatfix = null;

		} else {
			sender.sendMessage(new ComponentBuilder("Only players can reply to private messages").color(ChatColor.RED).create());
		}
	}
}
