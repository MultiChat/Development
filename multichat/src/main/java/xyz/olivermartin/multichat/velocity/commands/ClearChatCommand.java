package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import xyz.olivermartin.multichat.velocity.ConfigManager;
import xyz.olivermartin.multichat.velocity.MessageManager;
import xyz.olivermartin.multichat.velocity.MultiChat;

/**
 * Clear Chat Command
 * <p>Allows the user to clear their personal chat, the server chat, the global chat, or all servers' chat</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ClearChatCommand extends Command {

	private static final String[] aliases = new String[] {"chatclear","wipechat","killchat"};

	public ClearChatCommand() {
		super("clearchat",  aliases);
	}

	private void clearChatSelf(CommandSource sender) {
		for (int i = 1 ; i<151 ; i++ ) {
			sender.sendMessage(Component.empty());
		}
		MessageManager.sendMessage(sender, "command_clearchat_self");
	}

	private void clearChatServer(CommandSource sender) {
		for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {
			if (onlineplayer.getCurrentServer().get().getServerInfo().getName().equals(((Player) sender).getCurrentServer().get().getServerInfo().getName() )) {
				for (int i = 1 ; i<151 ; i++ ) {
					onlineplayer.sendMessage(Component.empty());
				}
				MessageManager.sendMessage(onlineplayer, "command_clearchat_server");
			}
		}
	}

	private void clearChatGlobal() {

		for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {
			if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("no_global").getList(String::valueOf).contains(onlineplayer.getCurrentServer().get().getServerInfo().getName()) ) {
				for (int i = 1 ; i<151 ; i++ ) {
					onlineplayer.sendMessage(Component.empty());
				}
				MessageManager.sendMessage(onlineplayer, "command_clearchat_global");
			}
		}

	}

	private void clearChatAll() {
		for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {
			for (int i = 1 ; i<151 ; i++ ) {
				onlineplayer.sendMessage(Component.empty());
			}
			MessageManager.sendMessage(onlineplayer, "command_clearchat_all");
		}
	}

	public boolean hasPermission(Invocation invocation) {
		return invocation.source().hasPermission("multichat.chat.clear");
	}

	public void execute(Invocation invocation) {
		var args = invocation.arguments();
		var sender = invocation.source();

		if (args.length < 1) {

			clearChatSelf(sender);

		} else {
			if (args.length == 1) {

				if (args[0].equalsIgnoreCase("self")) {

					clearChatSelf(sender);

				} else if (args[0].equalsIgnoreCase("all") ) {

					if (sender.hasPermission("multichat.chat.clear.all")) {

						clearChatAll();

					} else {
						MessageManager.sendSpecialMessage(sender, "command_clearchat_no_permission", "ALL");
					}

				} else if (args[0].equalsIgnoreCase("server") ) {

					if (sender.hasPermission("multichat.chat.clear.server")) {

						clearChatServer(sender);

					} else {
						MessageManager.sendSpecialMessage(sender, "command_clearchat_no_permission", "SERVER");
					}

				} else if (args[0].equalsIgnoreCase("global") ) {

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
