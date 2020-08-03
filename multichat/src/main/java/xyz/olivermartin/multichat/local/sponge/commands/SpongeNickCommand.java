package xyz.olivermartin.multichat.local.sponge.commands;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.commands.MultiChatLocalCommandSender;
import xyz.olivermartin.multichat.local.common.commands.NickCommand;
import xyz.olivermartin.multichat.local.sponge.MultiChatLocalSpongePlayer;

public class SpongeNickCommand extends NickCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		MultiChatLocalCommandSender mccs = new MultiChatLocalSpongeCommandSender(src);

		Optional<String> opTargetName = args.<String>getOne("player");

		if (!opTargetName.isPresent()) {
			mccs.sendBadMessage("That player could not be found!");
			return CommandResult.success();
		}

		Optional<UUID> targetUniqueId = MultiChatLocal.getInstance().getNameManager().getUUIDFromName(opTargetName.get());

		if (!targetUniqueId.isPresent()) {
			mccs.sendBadMessage("That player could not be found!");
			return CommandResult.success();
		}

		String nickname = args.<String>getOne("message").get();

		boolean status;

		if (!mccs.isPlayer()) {
			status = executeConsoleNickCommand(targetUniqueId.get(), mccs, nickname);
		} else {
			MultiChatLocalPlayer senderPlayer = new MultiChatLocalSpongePlayer((Player)src);
			status = executeNickCommand(targetUniqueId.get(), senderPlayer, nickname);
		}

		if (status) {
			return CommandResult.success();
		} else {
			mccs.sendBadMessage("Usage: /nick <player> <nickname/off>");
			return CommandResult.empty();
		}

	}

}
