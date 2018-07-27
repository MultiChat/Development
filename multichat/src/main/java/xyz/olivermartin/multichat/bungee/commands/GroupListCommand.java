package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.MultiChat;

/**
 * Group List Command
 * <p>Displays a list of all current group chats on the server</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class GroupListCommand extends Command {

	private static String[] aliases = new String[] {};

	public GroupListCommand() {
		super("groups", "multichat.staff.listgroups", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&a&lGroup List:")).create());

		for (String groupname : MultiChat.groupchats.keySet()) {
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b- " + groupname)).create());
		}

	}
}
