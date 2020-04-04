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

public class SpongeUsernameCommand implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource sender, CommandContext args) throws CommandException {

		if (sender instanceof Player) {
			sender = (Player) sender;
		} else {
			sender.sendMessage(Text.of("Only players can use this command!"));
			return CommandResult.success();
		}

		String username = args.<String>getOne("username").get();

		if (SpongeNameManager.getInstance().existsPlayer(username)) {

			Optional<String> player;
			player = SpongeNameManager.getInstance().getFormattedNameFromName(username);

			if (player.isPresent()) {
				sender.sendMessage(Text.of("User exists with name: '" + player.get() +"'"));
			} else {
				sender.sendMessage(Text.of("No one could be found with name: " + username));
			}

			return CommandResult.success();

		} else if (sender.hasPermission("multichatsponge.username.partial")) {

			Optional<Set<UUID>> matches = SpongeNameManager.getInstance().getPartialNameMatches(username);

			if (matches.isPresent()) {

				int limit = 10;

				sender.sendMessage(Text.of("No one could be found with the exact username: " + username));
				sender.sendMessage(Text.of("The following were found as partial matches: "));

				for (UUID uuid : matches.get()) {

					if (limit > 0 || sender.hasPermission("multichatsponge.username.nolimit")) {
						sender.sendMessage(Text.of("- '" + SpongeNameManager.getInstance().getName(uuid) + "'"));
						limit--;
					} else {
						sender.sendMessage(Text.of("Only the first 10 results have been shown, please try a more specific query!"));
						break;
					}

				}

			} else {

				sender.sendMessage(Text.of("No one could be found with username: " + username));

			}

			return CommandResult.success();

		} else {

			sender.sendMessage(Text.of("No one could be found with username: " + username));
			return CommandResult.success();

		}

	}

}
