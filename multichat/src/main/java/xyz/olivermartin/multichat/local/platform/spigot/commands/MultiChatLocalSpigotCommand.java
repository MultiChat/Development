package xyz.olivermartin.multichat.local.platform.spigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import xyz.olivermartin.multichat.local.commands.MultiChatLocalCommand;
import xyz.olivermartin.multichat.local.commands.MultiChatLocalCommandSender;

public class MultiChatLocalSpigotCommand extends MultiChatLocalCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!command.getName().equalsIgnoreCase("multichatlocal")) return false;

		MultiChatLocalCommandSender mccs = new MultiChatLocalSpigotCommandSender(sender);

		return executeMultiChatLocalCommand(mccs, args);

	}

}
