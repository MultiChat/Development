package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatModeManager;
import xyz.olivermartin.multichat.bungee.MessageManager;

/**
 * Global Command
 * <p>Causes players to see messages sent from all servers in the global chat</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class GlobalCommand extends Command {

	private static String[] aliases = new String[] {};

	public GlobalCommand() {
		super("global", "multichat.chat.mode", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		if ((sender instanceof ProxiedPlayer)) {

			ChatModeManager.getInstance().setGlobal(((ProxiedPlayer)sender).getUniqueId());

			MessageManager.sendMessage(sender, "command_global_enabled_1");
			MessageManager.sendMessage(sender, "command_global_enabled_2");

		} else {
			MessageManager.sendMessage(sender, "command_global_only_players");
		}
	}
}
