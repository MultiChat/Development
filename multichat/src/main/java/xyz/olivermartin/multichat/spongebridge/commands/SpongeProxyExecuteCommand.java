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

import xyz.olivermartin.multichat.spongebridge.DebugManager;
import xyz.olivermartin.multichat.spongebridge.MultiChatSponge;

public class SpongeProxyExecuteCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource sender, CommandContext rawArgs) throws CommandException {

		Collection<String> listArgs = rawArgs.getAll("message");
		String[] args = listArgs.toArray(new String[0]);

		DebugManager.log("[PXE] Getting ready for PXE");

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

				DebugManager.log("[PXE] Index = " + index);
				DebugManager.log("[PXE] Current arg is = " + args[index]);

				if (args[index].equalsIgnoreCase("-p")) {

					DebugManager.log("[PXE] IT IS A -p FLAG!");

					if (index+1 < args.length) {
						DebugManager.log("[PXE] And there is another arg too!");
						playerFlag = true;
						player = args[index+1];
						DebugManager.log("[PXE] That means we have a player: " + player);
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
					DebugManager.log("[PXE] Second loop, index = " + index);
					index--;
				} else {
					message = message + arg + " ";
					DebugManager.log("[PXE] Message so far is: " + message);
				}
			}

			if (Sponge.getServer().getOnlinePlayers().size() < 1) {
				sender.sendMessage(Text.of("Sorry, this command is only possible if at least one player is online!"));
				return CommandResult.success();
			}

			Player facilitatingPlayer = (Player)Sponge.getServer().getOnlinePlayers().toArray()[0];

			if (playerFlag) {

				DebugManager.log("[PXE] Player flag true so sending ppxe message!");

				MultiChatSponge.sendProxyExecutePlayerMessage(facilitatingPlayer, message, player);

			} else {

				DebugManager.log("[PXE] Sending regular pxe message!");

				MultiChatSponge.sendProxyExecuteMessage(facilitatingPlayer, message);

			}

			sender.sendMessage(Text.of("SENT COMMAND TO PROXY SERVER"));
			return CommandResult.success();

		}

	}

}
