package xyz.olivermartin.multichat.local.common.commands;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.storage.LocalNameManager;

public abstract class RealnameCommand {

	public boolean executeRealnameCommand(MultiChatLocalCommandSender sender, String[] args) {

		if (!sender.isPlayer()) {
			sender.sendBadMessage("Only players can use this command!");
			return true;
		}

		if (args.length != 1) {
			return false;
		}

		LocalNameManager lnm = MultiChatLocal.getInstance().getNameManager();

		if (lnm.existsNickname(args[0])) {

			Optional<String> player;
			player = lnm.getNameFromNickname(args[0]);

			if (player.isPresent()) {
				sender.sendGoodMessage("Nickname: '" + args[0] + "' Belongs to player: '" + player.get() + "'");
			} else {
				sender.sendBadMessage("No one could be found with nickname: " + args[0]);
			}

			return true;

		} else if (sender.hasPermission("multichatlocal.realname.partial")) {

			Optional<Set<UUID>> matches = lnm.getPartialNicknameMatches(args[0]);

			if (matches.isPresent()) {

				int limit = 10;

				sender.sendInfoMessageA("No one could be found with the exact nickname: " + args[0]);
				sender.sendInfoMessageB("The following were found as partial matches:");

				for (UUID uuid : matches.get()) {

					if (limit > 0 || sender.hasPermission("multichatlocal.realname.nolimit")) {
						sender.sendGoodMessage("Nickname: '" + lnm.getCurrentName(uuid, false) + "' Belongs to player: '" + lnm.getName(uuid) + "'");
						limit--;
					} else {
						sender.sendInfoMessageA("Only the first 10 results have been shown, please try a more specific query!");
						break;
					}

				}

			} else {

				sender.sendBadMessage("No one could be found with nickname: " + args[0]);

			}

			return true;

		} else {

			sender.sendBadMessage("No one could be found with nickname: " + args[0]);
			return true;

		}

	}

}
