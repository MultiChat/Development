package xyz.olivermartin.multichat.spigotbridge.listeners;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import xyz.olivermartin.multichat.spigotbridge.MetaManager;
import xyz.olivermartin.multichat.spigotbridge.MultiChatSpigot;
import xyz.olivermartin.multichat.spigotbridge.NameManager;

public class CommandListener implements Listener {

	private static final Pattern simpleNickname = Pattern.compile("^[a-zA-Z0-9&_]+$");

	@EventHandler
	public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("nick")) {

			Player sender;

			if (commandSender instanceof Player) {
				sender = (Player) commandSender;
			} else {
				commandSender.sendMessage(ChatColor.DARK_RED + "Only players can use this command!");
				return true;
			}

			if (args.length < 1 || args.length > 2) {
				// When onCommand() returns false, the help message associated with that command is displayed.
				return false;
			}

			if (args.length == 1) {

				UUID targetUUID = sender.getUniqueId();

				if (args[0].equalsIgnoreCase("off")) {
					NameManager.getInstance().removeNickname(targetUUID);
					MetaManager.getInstance().updatePlayerMeta(sender.getName(), MultiChatSpigot.setDisplayNameLastVal, MultiChatSpigot.displayNameFormatLastVal);
					sender.sendMessage("You have had your nickname removed!");
					return true;
				}

				if (NameManager.getInstance().containsColorCodes(args[0]) && !(sender.hasPermission("multichatbridge.nick.color") || sender.hasPermission("multichatbridge.nick.colour"))) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with color codes!");
					return true;
				}

				if (NameManager.getInstance().containsFormatCodes(args[0]) && !(sender.hasPermission("multichatbridge.nick.format"))) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with format codes!");
					return true;
				}

				if (!simpleNickname.matcher(args[0]).matches() && !(sender.hasPermission("multichatbridge.nick.special"))) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with special characters!");
					return true;
				}

				if (NameManager.getInstance().stripAllFormattingCodes(args[0]).length() > 20 && !sender.hasPermission("multichatbridge.nick.anylength")) {

					sender.sendMessage(ChatColor.DARK_RED + "Sorry your nickname is too long, max 20 characters! (Excluding format codes)");
					return true;

				}

				NameManager.getInstance().setNickname(targetUUID, args[0]);
				MetaManager.getInstance().updatePlayerMeta(sender.getName(), MultiChatSpigot.setDisplayNameLastVal, MultiChatSpigot.displayNameFormatLastVal);

				sender.sendMessage("You have been nicknamed!");

				return true;

			}

			Player target = sender.getServer().getPlayer(args[0]);

			// Make sure the player is online.
			if (target == null) {
				sender.sendMessage(ChatColor.DARK_RED + args[0] + " is not currently online so cannot be nicknamed!");
				return true;
			}

			if (target != sender) {
				if (!sender.hasPermission("multichatbridge.nick.others")) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to nickname other players!");
					return true;
				}
			}

			UUID targetUUID = target.getUniqueId();

			if (args[1].equalsIgnoreCase("off")) {
				NameManager.getInstance().removeNickname(targetUUID);
				MetaManager.getInstance().updatePlayerMeta(target.getName(), MultiChatSpigot.setDisplayNameLastVal, MultiChatSpigot.displayNameFormatLastVal);
				sender.sendMessage(ChatColor.GREEN + args[0] + " has had their nickname removed!");
				return true;
			}

			if (NameManager.getInstance().containsColorCodes(args[1]) && !(sender.hasPermission("multichatbridge.nick.color") || sender.hasPermission("multichatbridge.nick.colour"))) {
				sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with color codes!");
				return true;
			}

			if (NameManager.getInstance().containsFormatCodes(args[1]) && !(sender.hasPermission("multichatbridge.nick.format"))) {
				sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with format codes!");
				return true;
			}

			if (!simpleNickname.matcher(args[1]).matches() && !(sender.hasPermission("multichatbridge.nick.special"))) {
				sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with special characters!");
				return true;
			}

			if (NameManager.getInstance().stripAllFormattingCodes(args[1]).length() > 20 && !sender.hasPermission("multichatbridge.nick.anylength")) {

				sender.sendMessage(ChatColor.DARK_RED + "Sorry your nickname is too long, max 20 characters! (Excluding format codes)");
				return true;

			}

			NameManager.getInstance().setNickname(targetUUID, args[1]);
			MetaManager.getInstance().updatePlayerMeta(target.getName(), MultiChatSpigot.setDisplayNameLastVal, MultiChatSpigot.displayNameFormatLastVal);

			sender.sendMessage(ChatColor.GREEN + args[0] + " has been nicknamed!");

			return true;

		} else if (cmd.getName().equalsIgnoreCase("realname")) {

			Player sender;

			if (commandSender instanceof Player) {
				sender = (Player) commandSender;
			} else {
				commandSender.sendMessage(ChatColor.DARK_RED + "Only players can use this command!");
				return true;
			}

			if (args.length != 1) {
				// When onCommand() returns false, the help message associated with that command is displayed.
				return false;
			}

			if (NameManager.getInstance().existsNickname(args[0])) {

				Optional<String> player;
				player = NameManager.getInstance().getNameFromNickname(args[0]);

				if (player.isPresent()) {

					sender.sendMessage(ChatColor.GREEN + "Nickname: '" + args[0] + "' Belongs to player: '" + player.get() + "'");

				} else {

					sender.sendMessage(ChatColor.DARK_RED + "No one could be found with nickname: " + args[0]);

				}

				return true;

			} else if (sender.hasPermission("multichatbridge.realname.partial")) {

				Optional<Set<UUID>> matches = NameManager.getInstance().getPartialNicknameMatches(args[0]);

				if (matches.isPresent()) {

					int limit = 10;

					sender.sendMessage(ChatColor.DARK_AQUA + "No one could be found with the exact nickname: " + args[0]);
					sender.sendMessage(ChatColor.AQUA + "The following were found as partial matches:");

					for (UUID uuid : matches.get()) {

						if (limit > 0 || sender.hasPermission("multichatbridge.realname.nolimit")) {
							sender.sendMessage(ChatColor.GREEN + "Nickname: '" + NameManager.getInstance().getCurrentName(uuid) + "' Belongs to player: '" + NameManager.getInstance().getName(uuid) + "'");
							limit--;
						} else {
							sender.sendMessage(ChatColor.DARK_GREEN + "Only the first 10 results have been shown, please try a more specific query!");
							break;
						}

					}

				} else {

					sender.sendMessage(ChatColor.DARK_RED + "No one could be found with nickname: " + args[0]);

				}

				return true;

			} else {

				sender.sendMessage(ChatColor.DARK_RED + "No one could be found with nickname: " + args[0]);
				return true;

			}

		}
		return false;
	}

}
