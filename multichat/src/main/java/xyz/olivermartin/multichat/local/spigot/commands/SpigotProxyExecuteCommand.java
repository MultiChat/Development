package xyz.olivermartin.multichat.local.spigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import xyz.olivermartin.multichat.local.common.commands.MultiChatLocalCommandSender;
import xyz.olivermartin.multichat.local.common.commands.ProxyExecuteCommand;

public class SpigotProxyExecuteCommand extends ProxyExecuteCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (! (command.getName().equalsIgnoreCase("pxe") || command.getName().equalsIgnoreCase("pexecute"))) return false;

		MultiChatLocalCommandSender mccs = new MultiChatLocalSpigotCommandSender(sender);

		return executeProxyExecuteCommand(mccs, args);

	}

}
