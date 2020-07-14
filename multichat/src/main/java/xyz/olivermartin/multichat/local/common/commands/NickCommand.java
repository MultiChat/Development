package xyz.olivermartin.multichat.local.common.commands;

import java.util.UUID;
import java.util.regex.Pattern;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.config.LocalConfig;
import xyz.olivermartin.multichat.local.common.storage.LocalNameManager;

public abstract class NickCommand {

	//private static final Pattern simpleNickname = Pattern.compile("^[a-zA-Z0-9&_]+$");
	private static final Pattern simpleNickname = Pattern.compile("^([a-zA-Z0-9_]|(?i)(\\&[0-9A-FL-ORX]))+$");

	public boolean executeNickCommand(MultiChatLocalPlayer targetPlayer, MultiChatLocalPlayer sender, String proposedNick) {

		proposedNick = MultiChatLocal.getInstance().getChatManager().reformatRGB(proposedNick);

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

		if (lnm.containsRGBColorCodes(proposedNick) && !(sender.hasPermission("multichatlocal.nick.color") || sender.hasPermission("multichatlocal.nick.colour")||sender.hasPermission("multichatlocal.nick.color.rgb") || sender.hasPermission("multichatlocal.nick.colour.rgb"))) {
			sender.sendBadMessage("You do not have permission to use nicknames with rgb color codes!");
			return false;
		}

		if (lnm.containsSimpleColorCodes(proposedNick) && !(sender.hasPermission("multichatlocal.nick.color") || sender.hasPermission("multichatlocal.nick.colour")||sender.hasPermission("multichatlocal.nick.color.simple") || sender.hasPermission("multichatlocal.nick.colour.simple") ||sender.hasPermission("multichatlocal.nick.color.rgb") || sender.hasPermission("multichatlocal.nick.colour.rgb"))) {
			sender.sendBadMessage("You do not have permission to use nicknames with simple color codes!");
			return false;
		}

		if (lnm.containsFormatCodes(proposedNick)) {

			// If the nickname has ANY format codes...

			if (!sender.hasPermission("multichatlocal.nick.format")) {

				// If they don't have the permission for ALL format codes, then we will check individually...

				if (lnm.containsBoldFormatCodes(proposedNick) && !(sender.hasPermission("multichatlocal.nick.format.bold"))) {
					sender.sendBadMessage("You do not have permission to use nicknames with bold format codes!");
					return false;
				}

				if (lnm.containsItalicFormatCodes(proposedNick) && !(sender.hasPermission("multichatlocal.nick.format.italic"))) {
					sender.sendBadMessage("You do not have permission to use nicknames with italic format codes!");
					return false;
				}

				if (lnm.containsUnderlineFormatCodes(proposedNick) && !(sender.hasPermission("multichatlocal.nick.format.underline"))) {
					sender.sendBadMessage("You do not have permission to use nicknames with underline format codes!");
					return false;
				}

				if (lnm.containsStrikethroughFormatCodes(proposedNick) && !(sender.hasPermission("multichatlocal.nick.format.strikethrough"))) {
					sender.sendBadMessage("You do not have permission to use nicknames with strikethrough format codes!");
					return false;
				}

				if (lnm.containsObfuscatedFormatCodes(proposedNick) && !(sender.hasPermission("multichatlocal.nick.format.obfuscated"))) {
					sender.sendBadMessage("You do not have permission to use nicknames with obfuscated format codes!");
					return false;
				}

				if (lnm.containsResetFormatCodes(proposedNick) && !(sender.hasPermission("multichatlocal.nick.format.reset"))) {
					sender.sendBadMessage("You do not have permission to use nicknames with reset format codes!");
					return false;
				}

			}

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
