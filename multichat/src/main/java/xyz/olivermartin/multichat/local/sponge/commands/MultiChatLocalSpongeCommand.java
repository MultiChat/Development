package xyz.olivermartin.multichat.local.sponge.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import xyz.olivermartin.multichat.local.common.commands.MultiChatLocalCommand;
import xyz.olivermartin.multichat.local.common.commands.MultiChatLocalCommandSender;

public class MultiChatLocalSpongeCommand extends MultiChatLocalCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource sender, CommandContext args) throws CommandException {

		MultiChatLocalCommandSender mccs = new MultiChatLocalSpongeCommandSender(sender);

		String[] strArgs = new String[1];
		strArgs[0] = args.<String>getOne("command").get();

		boolean status = executeMultiChatLocalCommand(mccs, strArgs);

		if (status) {
			return CommandResult.success();
		} else {
			mccs.sendBadMessage("Usage: /multichatlocal <reload/debug/migratetosql>");
			return CommandResult.empty();
		}

	}

}
