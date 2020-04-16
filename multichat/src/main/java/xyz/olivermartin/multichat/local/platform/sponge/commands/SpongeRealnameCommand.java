package xyz.olivermartin.multichat.local.platform.sponge.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import xyz.olivermartin.multichat.local.commands.MultiChatLocalCommandSender;
import xyz.olivermartin.multichat.local.commands.RealnameCommand;

public class SpongeRealnameCommand extends RealnameCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		MultiChatLocalCommandSender mccs = new MultiChatLocalSpongeCommandSender(src);

		String[] strArgs = new String[1];
		strArgs[0] = args.<String>getOne("nickname").get();

		boolean status = executeRealnameCommand(mccs, strArgs);

		if (status) {
			return CommandResult.success();
		} else {
			mccs.sendBadMessage("Usage: /realname <nickname>");
			return CommandResult.empty();
		}

	}

}
