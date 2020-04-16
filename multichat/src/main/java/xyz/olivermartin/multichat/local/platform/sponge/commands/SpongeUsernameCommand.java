package xyz.olivermartin.multichat.local.platform.sponge.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import xyz.olivermartin.multichat.local.commands.MultiChatLocalCommandSender;
import xyz.olivermartin.multichat.local.commands.UsernameCommand;

public class SpongeUsernameCommand extends UsernameCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		MultiChatLocalCommandSender mccs = new MultiChatLocalSpongeCommandSender(src);

		String[] strArgs = new String[1];
		strArgs[0] = args.<String>getOne("username").get();

		boolean status = executeUsernameCommand(mccs, strArgs);

		if (status) {
			return CommandResult.success();
		} else {
			mccs.sendBadMessage("Usage: /username <username>");
			return CommandResult.empty();
		}

	}

}
