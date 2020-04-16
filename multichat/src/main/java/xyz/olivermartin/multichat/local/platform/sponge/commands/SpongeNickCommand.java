package xyz.olivermartin.multichat.local.platform.sponge.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.commands.MultiChatLocalCommandSender;
import xyz.olivermartin.multichat.local.commands.NickCommand;
import xyz.olivermartin.multichat.local.platform.sponge.MultiChatLocalSpongePlayer;

public class SpongeNickCommand extends NickCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		MultiChatLocalCommandSender mccs = new MultiChatLocalSpongeCommandSender(src);

		if (!mccs.isPlayer()) {
			mccs.sendBadMessage("Only players can use this command!");
			return CommandResult.success();
		}

		MultiChatLocalPlayer senderPlayer = new MultiChatLocalSpongePlayer((Player)src);

		Optional<Player> opTarget = args.<Player>getOne("player");

		if (!opTarget.isPresent()) {
			mccs.sendBadMessage("That player could not be found!");
			return CommandResult.success();
		}

		Player target = opTarget.get();

		if (target == null) {
			mccs.sendBadMessage("That player could not be found!");
			return CommandResult.success();
		}

		String nickname = args.<String>getOne("message").get();

		MultiChatLocalPlayer targetPlayer = new MultiChatLocalSpongePlayer(target);

		boolean status = executeNickCommand(targetPlayer, senderPlayer, nickname);

		if (status) {
			return CommandResult.success();
		} else {
			mccs.sendBadMessage("Usage: /nick <player> <nickname/off>");
			return CommandResult.empty();
		}

	}

}
