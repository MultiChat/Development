package xyz.olivermartin.multichat.local.common.commands;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.storage.LocalNameManager;

public abstract class UsernameCommand {

	public boolean executeUsernameCommand(MultiChatLocalCommandSender sender, String[] args) {

		if (!sender.isPlayer()) {
			sender.sendBadMessage("Only players can use this command!");
			return true;
		}

		if (args.length != 1) {
			return false;
		}

		LocalNameManager lnm = MultiChatLocal.getInstance().getNameManager();

		if (lnm.existsPlayer(args[0])) {

			Optional<String> player;
			player = lnm.getFormattedNameFromName(args[0]);

			if (player.isPresent()) {
				sender.sendGoodMessage("User exists with name: '" + player.get() + "'");
			} else {
				sender.sendBadMessage("No one could be found with username: " + args[0]);
			}

			return true;

		} else if (sender.hasPermission("multichatlocal.username.partial")) {

			Optional<Set<UUID>> matches = lnm.getPartialNameMatches(args[0]);

			if (matches.isPresent()) {

				int limit = 10;

				sender.sendInfoMessageA("No one could be found with the exact username: " + args[0]);
				sender.sendInfoMessageB("The following were found as partial matches:");

				for (UUID uuid : matches.get()) {

					if (limit > 0 || sender.hasPermission("multichatlocal.username.nolimit")) {
						sender.sendGoodMessage("- '" + lnm.getName(uuid) + "'");
						limit--;
					} else {
						sender.sendInfoMessageA("Only the first 10 results have been shown, please try a more specific query!");
						break;
					}

				}

			} else {

				sender.sendBadMessage("No one could be found with username: " + args[0]);

			}

			return true;

		} else {

			sender.sendBadMessage("No one could be found with username: " + args[0]);
			return true;

		}

	}

}
