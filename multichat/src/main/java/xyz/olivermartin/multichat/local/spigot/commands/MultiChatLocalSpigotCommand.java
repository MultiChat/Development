package xyz.olivermartin.multichat.local.spigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import xyz.olivermartin.multichat.local.common.commands.MultiChatLocalCommand;
import xyz.olivermartin.multichat.local.common.commands.MultiChatLocalCommandSender;

public class MultiChatLocalSpigotCommand extends MultiChatLocalCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!command.getName().equalsIgnoreCase("multichatlocal")) return false;

		MultiChatLocalCommandSender mccs = new MultiChatLocalSpigotCommandSender(sender);

		return executeMultiChatLocalCommand(mccs, args);

	}

}
