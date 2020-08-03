package xyz.olivermartin.multichat.local.spigot.commands;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
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

		if (args.length == 1) {

			return executeNickCommand(senderPlayer.getUniqueId(), senderPlayer, args[0]);

		} else {

			Optional<UUID> targetUniqueId = MultiChatLocal.getInstance().getNameManager().getUUIDFromName(args[0]);

			if (!targetUniqueId.isPresent()) {
				mccs.sendBadMessage(args[0] + " has never joined the server so cannot be nicknamed!");
				return true;
			}

			return executeNickCommand(targetUniqueId.get(), senderPlayer, args[1]);

		}

	}

}
