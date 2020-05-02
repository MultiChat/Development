package xyz.olivermartin.multichat.local.spigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.commands.MultiChatLocalCommandSender;
import xyz.olivermartin.multichat.local.common.commands.NickCommand;
import xyz.olivermartin.multichat.local.spigot.MultiChatLocalSpigotPlayer;

public class SpigotNickCommand extends NickCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!command.getName().equalsIgnoreCase("nick")) return false;

		MultiChatLocalCommandSender mccs = new MultiChatLocalSpigotCommandSender(sender);

		if (!mccs.isPlayer()) {
			mccs.sendBadMessage("Only players can use this command!");
			return true;
		}

		MultiChatLocalPlayer senderPlayer = new MultiChatLocalSpigotPlayer((Player)sender);

		if (args.length < 1 || args.length > 2) {
			return false;
		}

		MultiChatLocalPlayer targetPlayer;

		if (args.length == 1) {

			targetPlayer = senderPlayer;

			return executeNickCommand(targetPlayer, senderPlayer, args[0]);

		} else {

			Player target = sender.getServer().getPlayer(args[0]);

			if (target == null) {
				mccs.sendBadMessage(args[0] + " is not currently online so cannot be nicknamed!");
				return true;
			}

			targetPlayer = new MultiChatLocalSpigotPlayer(target);

			return executeNickCommand(targetPlayer, senderPlayer, args[1]);

		}

	}

}
