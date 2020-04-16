package xyz.olivermartin.multichat.local.platform.sponge.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import xyz.olivermartin.multichat.local.commands.MultiChatLocalCommandSender;
import xyz.olivermartin.multichat.local.commands.ProxyExecuteCommand;

public class SpongeProxyExecuteCommand extends ProxyExecuteCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		MultiChatLocalCommandSender mccs = new MultiChatLocalSpongeCommandSender(src);

		Optional<String> opArgs = args.getOne("message");
		String[] strArgs;

		if (opArgs.isPresent()) {
			strArgs = opArgs.get().split(" ");
		} else {
			mccs.sendBadMessage("Usage: /pexecute [-p <player>] <command>");
			return CommandResult.success();
		}

		boolean status = executeProxyExecuteCommand(mccs, strArgs);

		if (status) {
			return CommandResult.success();
		} else {
			mccs.sendBadMessage("Usage: /pexecute [-p <player>] <command>");
			return CommandResult.empty();
		}

	}

}
