package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.MultiChat;

/**
 * Clear Chat Command
 * <p>Allows the user to clear their personal chat, the server chat, the global chat, or all servers' chat</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ClearChatCommand extends Command {

	private static String[] aliases = new String[] {"chatclear","wipechat","killchat"};

	public ClearChatCommand() {
		super("clearchat", "multichat.chat.clear", aliases);
	}

	private void clearChatSelf(CommandSender sender) {

		for (int i = 1 ; i<151 ; i++ ) {
			sender.sendMessage(new ComponentBuilder("").create());
		}
		sender.sendMessage(new ComponentBuilder("- Chat Cleared -").color(ChatColor.AQUA).create());

	}

	private void clearChatServer(CommandSender sender) {

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
			if (onlineplayer.getServer().getInfo().getName().equals(((ProxiedPlayer) sender).getServer().getInfo().getName() )) {
				for (int i = 1 ; i<151 ; i++ ) {
					onlineplayer.sendMessage(new ComponentBuilder("").create());
				}
				onlineplayer.sendMessage(new ComponentBuilder("- Server Chat Cleared -").color(ChatColor.AQUA).create());
			}
		}

	}

	private void clearChatGlobal() {

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
			if (!MultiChat.configman.config.getStringList("no_global").contains(onlineplayer.getServer().getInfo().getName()) ) {
				for (int i = 1 ; i<151 ; i++ ) {
					onlineplayer.sendMessage(new ComponentBuilder("").create());
				}
				onlineplayer.sendMessage(new ComponentBuilder("- Global Chat Cleared -").color(ChatColor.AQUA).create());
			}
		}

	}

	private void clearChatAll() {

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
			for (int i = 1 ; i<151 ; i++ ) {
				onlineplayer.sendMessage(new ComponentBuilder("").create());
			}
			onlineplayer.sendMessage(new ComponentBuilder("- All Chat Cleared -").color(ChatColor.AQUA).create());
		}

	}

	public void execute(CommandSender sender, String[] args) {

		if (args.length < 1) {

			clearChatSelf(sender);

		}
		else
		{
			if (args.length == 1) {

				if (args[0].toLowerCase().equals("self")) {

					clearChatSelf(sender);

				} else if (args[0].toLowerCase().equals("all") ) {

					if (sender.hasPermission("multichat.chat.clear.all")) {

						clearChatAll();

					} else {
						sender.sendMessage(new ComponentBuilder("You do not have permission to clear ALL chat").color(ChatColor.RED).create());
					}

				} else if (args[0].toLowerCase().equals("server") ) {

					if (sender.hasPermission("multichat.chat.clear.server")) {

						clearChatServer(sender);

					} else {
						sender.sendMessage(new ComponentBuilder("You do not have permission to clear SERVER chat").color(ChatColor.RED).create());
					}

				} else if (args[0].toLowerCase().equals("global") ) {

					if (sender.hasPermission("multichat.chat.clear.global")) {

						clearChatGlobal();

					} else {
						sender.sendMessage(new ComponentBuilder("You do not have permission to clear GLOBAL chat").color(ChatColor.RED).create());
					}

				}

			} else {
				sender.sendMessage(new ComponentBuilder("Usage: /clearchat [self/server/global/all]").color(ChatColor.RED).create());
			}
		}
	}
}
