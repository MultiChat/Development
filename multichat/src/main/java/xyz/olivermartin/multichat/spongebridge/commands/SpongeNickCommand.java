package xyz.olivermartin.multichat.spongebridge.commands;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import xyz.olivermartin.multichat.spongebridge.MultiChatSponge;
import xyz.olivermartin.multichat.spongebridge.SpongeNameManager;

public class SpongeNickCommand implements CommandExecutor {

	private static final Pattern simpleNickname = Pattern.compile("^[a-zA-Z0-9&_]+$");

	@Override
	public CommandResult execute(CommandSource sender, CommandContext args) throws CommandException {

		if (sender instanceof Player) {
			sender = (Player) sender;
		} else {
			sender.sendMessage(Text.of("Only players can use this command!"));
			return CommandResult.success();
		}

		Optional<Player> opTarget = args.<Player>getOne("player");

		if (!opTarget.isPresent()) {
			sender.sendMessage(Text.of("That player could not be found!"));
			return CommandResult.success();
		}

		Player target = opTarget.get();
		String nickname = args.<String>getOne("message").get();

		if (target == null) {
			sender.sendMessage(Text.of("That player could not be found!"));
			return CommandResult.success();
		}

		if (target != sender) {
			if (!sender.hasPermission("multichatsponge.nick.others")) {
				sender.sendMessage(Text.of("You do not have permission to nickname other players!"));
				return CommandResult.success();
			}
		}

		UUID targetUUID = target.getUniqueId();

		if (nickname.equalsIgnoreCase("off")) {
			SpongeNameManager.getInstance().removeNickname(targetUUID);
			MultiChatSponge.updatePlayerMeta(target.getName(), MultiChatSponge.setDisplayNameLastVal, MultiChatSponge.displayNameFormatLastVal);
			sender.sendMessage(Text.of(target.getName() + " has had their nickname removed!"));
			return CommandResult.success();
		}

		if (SpongeNameManager.getInstance().containsColorCodes(nickname) && !(sender.hasPermission("multichatsponge.nick.color") || sender.hasPermission("multichatsponge.nick.colour"))) {
			sender.sendMessage(Text.of("You do not have permission to use nicknames with color codes!"));
			return CommandResult.success();
		}

		if (SpongeNameManager.getInstance().containsFormatCodes(nickname) && !(sender.hasPermission("multichatsponge.nick.format"))) {
			sender.sendMessage(Text.of("You do not have permission to use nicknames with format codes!"));
			return CommandResult.success();
		}

		if (!simpleNickname.matcher(nickname).matches() && !(sender.hasPermission("multichatsponge.nick.special"))) {
			sender.sendMessage(Text.of("You do not have permission to use nicknames with special characters!"));
			return CommandResult.success();
		}

		if (MultiChatSponge.nicknameLengthIncludeFormatting) {
			// Include formatting codes in the nickname length
			if (nickname.length() > MultiChatSponge.nicknameMaxLength && !sender.hasPermission("multichatsponge.nick.anylength")) {
				sender.sendMessage(Text.of("Sorry your nickname is too long, max " + MultiChatSponge.nicknameMaxLength + " characters! (Including format codes)"));
				return CommandResult.success();
			}
			if (nickname.length() < MultiChatSponge.nicknameMinLength && !sender.hasPermission("multichatsponge.nick.anylength")) {
				sender.sendMessage(Text.of("Sorry your nickname is too short, min " + MultiChatSponge.nicknameMinLength + " characters! (Including format codes)"));
				return CommandResult.success();
			}
		} else {
			// Do not include formatting codes in the nickname length
			if (SpongeNameManager.getInstance().stripAllFormattingCodes(nickname).length() > MultiChatSponge.nicknameMaxLength && !sender.hasPermission("multichatsponge.nick.anylength")) {
				sender.sendMessage(Text.of("Sorry your nickname is too long, max " + MultiChatSponge.nicknameMaxLength + " characters! (Excluding format codes)"));
				return CommandResult.success();
			}
			if (SpongeNameManager.getInstance().stripAllFormattingCodes(nickname).length() < MultiChatSponge.nicknameMinLength && !sender.hasPermission("multichatsponge.nick.anylength")) {
				sender.sendMessage(Text.of("Sorry your nickname is too short, min " + MultiChatSponge.nicknameMinLength + " characters! (Excluding format codes)"));
				return CommandResult.success();
			}
		}

		String strippedNickname = SpongeNameManager.getInstance().stripAllFormattingCodes(nickname);

		UserStorageService uss = Sponge.getServiceManager().provideUnchecked(UserStorageService.class);
		Optional<User> lookedUpName = uss.get(strippedNickname);

		// Check if a player name exists already (but not the name of this player)
		if (lookedUpName.isPresent() && !strippedNickname.equalsIgnoreCase(target.getName()) && !sender.hasPermission("multichatsponge.nick.impersonate")) {
			sender.sendMessage(Text.of("Sorry, a player already exists with this name!"));
			return CommandResult.success();
		}

		String targetNickname = SpongeNameManager.getInstance().stripAllFormattingCodes(SpongeNameManager.getInstance().getCurrentName(targetUUID));

		if (SpongeNameManager.getInstance().existsNickname(strippedNickname) && !targetNickname.equalsIgnoreCase(strippedNickname)) {

			sender.sendMessage(Text.of("Sorry, a player already has that nickname!"));
			return CommandResult.success();

		}

		boolean blacklisted = false;
		for (String bl : MultiChatSponge.nicknameBlacklist) {
			if (strippedNickname.matches(bl)) blacklisted = true;
		}

		if (blacklisted) {

			sender.sendMessage(Text.of("Sorry, this name is not allowed!"));
			return CommandResult.success();

		}

		SpongeNameManager.getInstance().setNickname(targetUUID, nickname);
		MultiChatSponge.updatePlayerMeta(target.getName(), MultiChatSponge.setDisplayNameLastVal, MultiChatSponge.displayNameFormatLastVal);

		sender.sendMessage(Text.of(target.getName() + " has been nicknamed!"));
		return CommandResult.success();
	}

}
