package xyz.olivermartin.multichat.spongebridge.commands;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import xyz.olivermartin.multichat.spongebridge.SpongeNameManager;

public class SpongeRealnameCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource sender, CommandContext args) throws CommandException {

		if (sender instanceof Player) {
			sender = (Player) sender;
		} else {
			sender.sendMessage(Text.of("Only players can use this command!"));
			return CommandResult.success();
		}

		String nickname = args.<String>getOne("nickname").get();

		if (SpongeNameManager.getInstance().existsNickname(nickname)) {

			Optional<String> player;
			player = SpongeNameManager.getInstance().getNameFromNickname(nickname);

			if (player.isPresent()) {
				sender.sendMessage(Text.of("Nickname: '" + nickname + "' Belongs to player: '" + player.get() +"'"));
			} else {
				sender.sendMessage(Text.of("No one could be found with nickname: " + nickname));
			}

			return CommandResult.success();

		} else if (sender.hasPermission("multichatsponge.realname.partial")) {

			Optional<Set<UUID>> matches = SpongeNameManager.getInstance().getPartialNicknameMatches(nickname);

			if (matches.isPresent()) {

				int limit = 10;

				sender.sendMessage(Text.of("No one could be found with the exact nickname: " + nickname));
				sender.sendMessage(Text.of("The following were found as partial matches: "));

				for (UUID uuid : matches.get()) {

					if (limit > 0 || sender.hasPermission("multichatsponge.realname.nolimit")) {
						sender.sendMessage(Text.of("Nickname: '" + SpongeNameManager.getInstance().getCurrentName(uuid, false) + "' Belongs to player: '" + SpongeNameManager.getInstance().getName(uuid) + "'"));
						limit--;
					} else {
						sender.sendMessage(Text.of("Only the first 10 results have been shown, please try a more specific query!"));
						break;
					}

				}

			} else {

				sender.sendMessage(Text.of("No one could be found with nickname: " + nickname));

			}

			return CommandResult.success();

		} else {

			sender.sendMessage(Text.of("No one could be found with nickname: " + nickname));
			return CommandResult.success();

		}

	}

}
