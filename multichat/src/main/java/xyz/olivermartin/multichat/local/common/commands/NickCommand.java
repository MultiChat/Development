package xyz.olivermartin.multichat.local.common.commands;

import java.util.UUID;
import java.util.regex.Pattern;

import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.common.TranslateMode;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.config.LocalConfig;
import xyz.olivermartin.multichat.local.common.storage.LocalNameManager;

public abstract class NickCommand {

	//private static final Pattern simpleNickname = Pattern.compile("^[a-zA-Z0-9&_]+$");
	private static final Pattern simpleNickname = Pattern.compile("^([a-zA-Z0-9_]|(?i)(\\&[0-9A-FL-ORX]))+$");

	public boolean executeNickCommand(UUID targetUniqueId, MultiChatLocalPlayer sender, String proposedNick) {

		proposedNick = MultiChatUtil.preProcessColorCodes(proposedNick);

		LocalNameManager lnm = MultiChatLocal.getInstance().getNameManager();

		if (targetUniqueId != sender.getUniqueId()) {
			if (!sender.hasPermission("multichatlocal.nick.others")) {
				sender.sendBadMessage("You do not have permission to nickname other players!");
				return true;
			}
		}

		if (proposedNick.equalsIgnoreCase("off")) {
			lnm.removeNickname(targetUniqueId);
			sender.sendGoodMessage("The nickname has been removed!");
			return true;
		}

		if (!checkPermissions(targetUniqueId, sender, proposedNick)) {
			return true;
		}

		if (!checkValidNickname(targetUniqueId, sender, proposedNick)) {
			return true;
		}

		lnm.setNickname(targetUniqueId, proposedNick);

		sender.sendGoodMessage("The nickname has been set!");
		return true;

	}

	public boolean executeConsoleNickCommand(UUID targetUniqueId, MultiChatLocalCommandSender console, String proposedNick) {

		proposedNick = MultiChatUtil.preProcessColorCodes(proposedNick);

		LocalNameManager lnm = MultiChatLocal.getInstance().getNameManager();

		if (proposedNick.equalsIgnoreCase("off")) {
			lnm.removeNickname(targetUniqueId);
			console.sendGoodMessage("The nickname has been removed!");
			return true;
		}

		if (!checkValidNickname(targetUniqueId, console, proposedNick)) {
			return true;
		}

		lnm.setNickname(targetUniqueId, proposedNick);

		console.sendGoodMessage("The nickname has been set!");
		return true;

	}


	private boolean checkPermissions(UUID targetUniqueId, MultiChatLocalPlayer sender, String proposedNick) {

		LocalNameManager lnm = MultiChatLocal.getInstance().getNameManager();
		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();

		if (MultiChatUtil.containsColorCodes(proposedNick, false, TranslateMode.X) && !(sender.hasPermission("multichatlocal.nick.color") || sender.hasPermission("multichatlocal.nick.colour")||sender.hasPermission("multichatlocal.nick.color.rgb") || sender.hasPermission("multichatlocal.nick.colour.rgb"))) {
			sender.sendBadMessage("You do not have permission to use nicknames with rgb color codes!");
			return false;
		}

		if (MultiChatUtil.containsColorCodes(proposedNick, false, TranslateMode.COLOR_SIMPLE) && !(sender.hasPermission("multichatlocal.nick.color") || sender.hasPermission("multichatlocal.nick.colour")||sender.hasPermission("multichatlocal.nick.color.simple") || sender.hasPermission("multichatlocal.nick.colour.simple") ||sender.hasPermission("multichatlocal.nick.color.rgb") || sender.hasPermission("multichatlocal.nick.colour.rgb"))) {
			sender.sendBadMessage("You do not have permission to use nicknames with simple color codes!");
			return false;
		}

		if (MultiChatUtil.containsColorCodes(proposedNick, false, TranslateMode.FORMAT_ALL)) {

			// If the nickname has ANY format codes...

			if (!sender.hasPermission("multichatlocal.nick.format")) {

				// If they don't have the permission for ALL format codes, then we will check individually...

				if (MultiChatUtil.containsColorCodes(proposedNick, false, TranslateMode.FORMAT_BOLD) && !(sender.hasPermission("multichatlocal.nick.format.bold"))) {
					sender.sendBadMessage("You do not have permission to use nicknames with bold format codes!");
					return false;
				}

				if (MultiChatUtil.containsColorCodes(proposedNick, false, TranslateMode.FORMAT_ITALIC) && !(sender.hasPermission("multichatlocal.nick.format.italic"))) {
					sender.sendBadMessage("You do not have permission to use nicknames with italic format codes!");
					return false;
				}

				if (MultiChatUtil.containsColorCodes(proposedNick, false, TranslateMode.FORMAT_UNDERLINE) && !(sender.hasPermission("multichatlocal.nick.format.underline"))) {
					sender.sendBadMessage("You do not have permission to use nicknames with underline format codes!");
					return false;
				}

				if (MultiChatUtil.containsColorCodes(proposedNick, false, TranslateMode.FORMAT_STRIKE) && !(sender.hasPermission("multichatlocal.nick.format.strikethrough"))) {
					sender.sendBadMessage("You do not have permission to use nicknames with strikethrough format codes!");
					return false;
				}

				if (MultiChatUtil.containsColorCodes(proposedNick, false, TranslateMode.FORMAT_OBFUSCATED) && !(sender.hasPermission("multichatlocal.nick.format.obfuscated"))) {
					sender.sendBadMessage("You do not have permission to use nicknames with obfuscated format codes!");
					return false;
				}

				if (MultiChatUtil.containsColorCodes(proposedNick, false, TranslateMode.FORMAT_RESET) && !((sender.hasPermission("multichatlocal.nick.format.reset")||sender.hasPermission("multichatlocal.nick.color")||sender.hasPermission("multichatlocal.nick.colour")||sender.hasPermission("multichatlocal.nick.color.rgb")||sender.hasPermission("multichatlocal.nick.colour.rgb")))) {
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
				length = MultiChatUtil.stripColorCodes(proposedNick, false).length();
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

		String targetName = lnm.getName(targetUniqueId);

		if (lnm.existsPlayer(proposedNick) && !targetName.equalsIgnoreCase(MultiChatUtil.stripColorCodes(proposedNick, false)) && !sender.hasPermission("multichatlocal.nick.impersonate")) {
			sender.sendBadMessage("Sorry, a player already exists with this name!");
			return false;
		}

		if (! sender.hasPermission("multichatlocal.nick.blacklist")) {

			boolean blacklisted = false;

			for (String bl : config.getNicknameBlacklist()) {
				if (MultiChatUtil.stripColorCodes(proposedNick, false).matches(bl)) {
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

	private boolean checkValidNickname(UUID targetUniqueId, MultiChatLocalCommandSender sender, String proposedNick) {

		LocalNameManager lnm = MultiChatLocal.getInstance().getNameManager();

		if (MultiChatUtil.stripColorCodes(proposedNick, false).length() < 1) {
			sender.sendBadMessage("Sorry your nickname cannot be empty!");
			return false;
		}

		String targetNickname = MultiChatUtil.stripColorCodes(lnm.getCurrentName(targetUniqueId, false), false);

		if (lnm.existsNickname(proposedNick) && !targetNickname.equalsIgnoreCase(MultiChatUtil.stripColorCodes(proposedNick, false)) ) {
			sender.sendBadMessage("Sorry, this nickname is already in use!");
			return false;
		}

		return true;

	}

}
