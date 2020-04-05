package xyz.olivermartin.multichat.spongebridge.commands;

import java.util.Collection;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import xyz.olivermartin.multichat.spongebridge.MultiChatSponge;

public class SpongeProxyExecuteCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource sender, CommandContext rawArgs) throws CommandException {

		Collection<String> listArgs = rawArgs.getAll("message");
		String[] args = (String[]) listArgs.toArray();

		// Show usage
		if (args.length < 1) {

			sender.sendMessage(Text.of("Usage: /pexecute [-p <player>] <command>"));
			return CommandResult.success();

		} else {

			boolean playerFlag = false;
			String player = ".*";

			// Handle flags
			int index = 0;

			while (index < args.length) {

				if (args[index].equalsIgnoreCase("-p")) {
					if (index+1 < args.length) {
						playerFlag = true;
						player = args[index+1];
					}
				} else {
					break;
				}

				index = index+2;

			}

			if (index >= args.length) {
				sender.sendMessage(Text.of("Usage: /pexecute [-p <player>] <command>"));
				return CommandResult.success();
			}

			String message = "";
			for (String arg : args) {
				if (index > 0) {
					index--;
				} else {
					message = message + arg + " ";
				}
			}

			if (Sponge.getServer().getOnlinePlayers().size() < 1) {
				sender.sendMessage(Text.of("Sorry, this command is only possible if at least one player is online!"));
				return CommandResult.success();
			}

			Player facilitatingPlayer = (Player)Sponge.getServer().getOnlinePlayers().toArray()[0];

			if (playerFlag) {

				MultiChatSponge.sendProxyExecutePlayerMessage(facilitatingPlayer, message, player);

			} else {

				MultiChatSponge.sendProxyExecuteMessage(facilitatingPlayer, message);

			}

			sender.sendMessage(Text.of("SENT COMMAND TO PROXY SERVER"));
			return CommandResult.success();

		}

	}

}
