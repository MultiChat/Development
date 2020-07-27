package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;

/**
 * Clear Chat Command
 * <p>Allows the user to clear their personal chat, the server chat, the global chat, or all servers' chat</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ClearChatCommand extends Command {

	public ClearChatCommand() {
		super("mcclearchat", "multichat.chat.clear", (String[]) ConfigManager.getInstance().getHandler("aliases.yml").getConfig().getStringList("clearchat").toArray(new String[0]));
	}

	private void clearChatSelf(CommandSender sender) {

		for (int i = 1 ; i<151 ; i++ ) {
			sender.sendMessage(new ComponentBuilder("").create());
		}
		MessageManager.sendMessage(sender, "command_clearchat_self");

	}

	private void clearChatServer(CommandSender sender) {

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
			if (onlineplayer.getServer().getInfo().getName().equals(((ProxiedPlayer) sender).getServer().getInfo().getName() )) {
				for (int i = 1 ; i<151 ; i++ ) {
					onlineplayer.sendMessage(new ComponentBuilder("").create());
				}
				MessageManager.sendMessage(onlineplayer, "command_clearchat_server");
			}
		}

	}

	private void clearChatGlobal() {

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
			if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList(ConfigValues.Config.NO_GLOBAL).contains(onlineplayer.getServer().getInfo().getName()) ) {
				for (int i = 1 ; i<151 ; i++ ) {
					onlineplayer.sendMessage(new ComponentBuilder("").create());
				}
				MessageManager.sendMessage(onlineplayer, "command_clearchat_global");
			}
		}

	}

	private void clearChatAll() {

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
			for (int i = 1 ; i<151 ; i++ ) {
				onlineplayer.sendMessage(new ComponentBuilder("").create());
			}
			MessageManager.sendMessage(onlineplayer, "command_clearchat_all");
		}

	}

	public void execute(CommandSender sender, String[] args) {

		if (args.length < 1) {

			clearChatSelf(sender);

		} else {
			if (args.length == 1) {

				if (args[0].toLowerCase().equals("self")) {

					clearChatSelf(sender);

				} else if (args[0].toLowerCase().equals("all") ) {

					if (sender.hasPermission("multichat.chat.clear.all")) {

						clearChatAll();

					} else {
						MessageManager.sendSpecialMessage(sender, "command_clearchat_no_permission", "ALL");
					}

				} else if (args[0].toLowerCase().equals("server") ) {

					if (sender.hasPermission("multichat.chat.clear.server")) {

						clearChatServer(sender);

					} else {
						MessageManager.sendSpecialMessage(sender, "command_clearchat_no_permission", "SERVER");
					}

				} else if (args[0].toLowerCase().equals("global") ) {

					if (sender.hasPermission("multichat.chat.clear.global")) {

						clearChatGlobal();

					} else {
						MessageManager.sendSpecialMessage(sender, "command_clearchat_no_permission", "GLOBAL");
					}

				}

			} else {
				MessageManager.sendMessage(sender, "command_clearchat_usage");
			}
		}
	}
}
