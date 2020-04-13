package xyz.olivermartin.multichat.local.commands;

import java.util.UUID;
import java.util.regex.Pattern;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.config.LocalConfig;
import xyz.olivermartin.multichat.local.storage.LocalNameManager;

public abstract class NickCommand {

	private static final Pattern simpleNickname = Pattern.compile("^[a-zA-Z0-9&_]+$");

	public boolean executeNickCommand(MultiChatLocalPlayer targetPlayer, MultiChatLocalPlayer sender, String proposedNick) {

		UUID targetUUID = targetPlayer.getUniqueId();

		LocalNameManager lnm = MultiChatLocal.getInstance().getNameManager();

		if (targetPlayer.getUniqueId() != sender.getUniqueId()) {
			if (!sender.hasPermission("multichatlocal.nick.others")) {
				sender.sendBadMessage("You do not have permission to nickname other players!");
				return true;
			}
		}

		if (proposedNick.equalsIgnoreCase("off")) {
			lnm.removeNickname(targetUUID);
			MultiChatLocal.getInstance().getProxyCommunicationManager().updatePlayerMeta(targetUUID);
			sender.sendGoodMessage("The nickname has been removed!");
			return true;
		}

		if (!checkPermissions(targetPlayer, sender, proposedNick)) {
			return true;
		}

		lnm.setNickname(targetUUID, proposedNick);
		MultiChatLocal.getInstance().getProxyCommunicationManager().updatePlayerMeta(targetUUID);

		sender.sendGoodMessage("The nickname has been set!");
		return true;

	}


	private boolean checkPermissions(MultiChatLocalPlayer targetPlayer, MultiChatLocalPlayer sender, String proposedNick) {

		LocalNameManager lnm = MultiChatLocal.getInstance().getNameManager();
		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();

		if (lnm.containsColorCodes(proposedNick) && !(sender.hasPermission("multichatlocal.nick.color") || sender.hasPermission("multichatlocal.nick.colour"))) {
			sender.sendBadMessage("You do not have permission to use nicknames with color codes!");
			return false;
		}

		if (lnm.containsFormatCodes(proposedNick) && !(sender.hasPermission("multichatlocal.nick.format"))) {
			sender.sendBadMessage("You do not have permission to use nicknames with format codes!");
			return false;
		}

		if (!simpleNickname.matcher(proposedNick).matches() && !(sender.hasPermission("multichatlocal.nick.special"))) {
			sender.sendBadMessage("You do not have permission to use nicknames with special characters!");
			return false;
		}

		if (!sender.hasPermission("multichatlocal.nick.anylength")) {

			int length;
			String endOfMessage;

			if (config.isNicknameLengthLimitFormatting()) {
				length = proposedNick.length();
				endOfMessage = "(Including format codes)";
			} else {
				length = lnm.stripAllFormattingCodes(proposedNick).length();
				endOfMessage = "(Excluding format codes)";
			}

			if (length > config.getNicknameLengthLimit()) {
				sender.sendBadMessage("Sorry your nickname is too long, max " + config.getNicknameLengthLimit() + " characters! " + endOfMessage);
				return false;
			}

			if (length < config.getNicknameLengthMin()) {
				sender.sendBadMessage("Sorry your nickname is too short, min " + config.getNicknameLengthMin() + " characters! " + endOfMessage);
				return false;
			}

		}

		if (lnm.stripAllFormattingCodes(proposedNick).length() < 1) {
			sender.sendBadMessage("Sorry your nickname cannot be empty!");
			return false;
		}

		String targetNickname = lnm.stripAllFormattingCodes(lnm.getCurrentName(targetPlayer.getUniqueId(), false));
		String targetName = lnm.getName(targetPlayer.getUniqueId());

		if (lnm.existsNickname(proposedNick) && !targetNickname.equalsIgnoreCase(lnm.stripAllFormattingCodes(proposedNick)) ) {
			sender.sendBadMessage("Sorry, this nickname is already in use!");
			return false;
		}

		if (lnm.existsPlayer(proposedNick) && !targetName.equalsIgnoreCase(lnm.stripAllFormattingCodes(proposedNick)) && !sender.hasPermission("multichatlocal.nick.impersonate")) {
			sender.sendBadMessage("Sorry, a player already exists with this name!");
			return false;
		}

		if (! sender.hasPermission("multichatlocal.nick.blacklist")) {

			boolean blacklisted = false;

			for (String bl : config.getNicknameBlacklist()) {
				if (lnm.stripAllFormattingCodes(proposedNick).matches(bl)) {
					blacklisted = true;
					break;
				}
			}

			if (blacklisted) {
				sender.sendBadMessage("Sorry, this name is not allowed!");
				return false;
			}

		}

		return true;

	}

}
