package xyz.olivermartin.multichat.spigotbridge.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import xyz.olivermartin.multichat.spigotbridge.FileNameManager;
import xyz.olivermartin.multichat.spigotbridge.MetaManager;
import xyz.olivermartin.multichat.spigotbridge.MultiChatSpigot;
import xyz.olivermartin.multichat.spigotbridge.NameManager;
import xyz.olivermartin.multichat.spigotbridge.SQLNameManager;
import xyz.olivermartin.multichat.spigotbridge.SpigotCommunicationManager;
import xyz.olivermartin.multichat.spigotbridge.SpigotConfigManager;

public class CommandHandler implements CommandExecutor {

	private static final Pattern simpleNickname = Pattern.compile("^[a-zA-Z0-9&_]+$");

	private static CommandHandler instance;

	public static CommandHandler getInstance() {
		return instance;
	}

	static {
		instance = new CommandHandler();
	}

	/* --- END STATIC --- */

	private CommandHandler() {
		/* Empty */
	}

	@EventHandler
	public boolean onCommand(CommandSender commandSender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("multichatspigot")) {

			// Show usage
			if (args.length != 1) {
				return false;
			}

			if (args[0].equalsIgnoreCase("reload")) {

				if (commandSender.hasPermission("multichatspigot.reload")) {

					SpigotConfigManager.getInstance().getHandler("spigotconfig.yml").startupConfig();
					Configuration config = SpigotConfigManager.getInstance().getHandler("spigotconfig.yml").getConfig();

					if (config.contains("server_name")) {
						MultiChatSpigot.serverName = config.getString("server_name");
					}

					MultiChatSpigot.overrideGlobalFormat = config.getBoolean("override_global_format");
					MultiChatSpigot.overrideGlobalFormatFormat = config.getString("override_global_format_format");
					MultiChatSpigot.overrideAllMultiChatFormats = config.getBoolean("override_all_multichat_formatting");
					MultiChatSpigot.setLocalFormat = config.getBoolean("set_local_format");
					MultiChatSpigot.localChatFormat = config.getString("local_chat_format");
					MultiChatSpigot.forceMultiChatFormat = config.getBoolean("force_multichat_format");

					MultiChatSpigot.placeholderMap.clear();
					ConfigurationSection placeholders = config.getConfigurationSection("multichat_placeholders");
					if (placeholders != null) {

						for (String placeholder : placeholders.getKeys(false)) {
							MultiChatSpigot.placeholderMap.put("{multichat_" + placeholder + "}", placeholders.getString(placeholder));
						}

					}

					if (config.contains("show_nickname_prefix")) {
						MultiChatSpigot.showNicknamePrefix = config.getBoolean("show_nickname_prefix");
						MultiChatSpigot.nicknamePrefix = config.getString("nickname_prefix");
						MultiChatSpigot.nicknameBlacklist = config.getStringList("nickname_blacklist");
						if (config.contains("nickname_length_limit")) {

							MultiChatSpigot.nicknameMaxLength = config.getInt("nickname_length_limit");
							MultiChatSpigot.nicknameLengthIncludeFormatting = config.getBoolean("nickname_length_limit_formatting");

						}
					}

					commandSender.sendMessage(ChatColor.GREEN + "The plugin has been reloaded!");

					return true;

				} else {
					commandSender.sendMessage(ChatColor.DARK_RED + "You do not have permission to reload the plugin");
					return true;
				}

			} else if (args[0].equalsIgnoreCase("migratetosql")) {

				if (commandSender instanceof Player) {

					commandSender.sendMessage(ChatColor.DARK_RED + "This command can only be executed from the server console for security reasons!");
					return true;

				} else {

					if (! (NameManager.getInstance() instanceof SQLNameManager)) {
						commandSender.sendMessage(ChatColor.DARK_RED + "This command can only be used in SQL mode!");
						return true;
					}

					File f = new File(MultiChatSpigot.configDir, MultiChatSpigot.nameDataFile);
					FileNameManager fnm = new FileNameManager();

					if ((f.exists()) && (!f.isDirectory())) {

						commandSender.sendMessage(ChatColor.GREEN + "Starting load of nickname file data...");

						File file = new File(MultiChatSpigot.configDir, MultiChatSpigot.nameDataFile);
						FileInputStream saveFile;
						try {
							saveFile = new FileInputStream(file);
							fnm.loadNicknameData(saveFile);
							commandSender.sendMessage(ChatColor.GREEN + "Successfully loaded nickname data...");

							Map<UUID,String> mapUUIDName = fnm.getMapUUIDName();
							Map<UUID,String> mapUUIDNick = fnm.getMapUUIDNick();
							Map<String,String> mapNameFormatted = fnm.getMapNameFormatted();
							Map<String,String> mapNickFormatted = fnm.getMapNickFormatted();

							commandSender.sendMessage(ChatColor.GREEN + "Starting migration");

							int count = 0;
							int max = mapUUIDName.size();
							int checkcount = 25;
							int checkpoint = max/100*checkcount;

							for (Entry<UUID, String> entry : mapUUIDName.entrySet()) {

								count++;
								if (count > checkpoint) {
									commandSender.sendMessage(ChatColor.GREEN + "Completed " + checkcount + "% of migration...");
									checkcount += 25;
									checkpoint = max/100*checkcount;
								}

								UUID uuid = entry.getKey();
								String name = entry.getValue();
								String formattedName = mapNameFormatted.get(name);
								String nick;
								String formattedNick;
								if (mapUUIDNick.containsKey(uuid)) {
									nick = mapUUIDNick.get(uuid);
									formattedNick = mapNickFormatted.get(nick);
									if (formattedNick.equals(formattedName)) {
										nick = null;
										formattedNick = null;
									}
								} else {
									nick = null;
									formattedNick = null;
								}



								((SQLNameManager)NameManager.getInstance()).registerMigratedPlayer(uuid, name, formattedName, nick, formattedNick);

							}

							commandSender.sendMessage(ChatColor.GREEN + "Successfully migrated: " + max + " records");

							commandSender.sendMessage(ChatColor.GREEN + "Saving nickname data file...");

							FileOutputStream saveFile2 = new FileOutputStream(file);
							fnm.saveNicknameData(saveFile2);

						} catch (FileNotFoundException e) {
							commandSender.sendMessage(ChatColor.DARK_RED + "[ERROR] Could not load nickname data!");
							e.printStackTrace();
						}

					}

					return true;

				}

			} else {
				// Show usage
				return false;
			}

		} else if (cmd.getName().equalsIgnoreCase("pxe") || cmd.getName().equalsIgnoreCase("pexecute")) {


			/* PROXY EXECUTE */

			// Show usage
			if (args.length < 1) {
				
				return false;
				
			} else {
				
				boolean playerFlag = false;
				String player = ".*";
				
				// Handle flags
				int index = 0;
				
				while (index < args.length) {

					if (args[index].equalsIgnoreCase("-p")) {
						if (index+1 < args.length) {
							playerFlag = true;
							player = args[index+1];
						}
					} else {
						break;
					}

					index = index+2;

				}
				
				if (index >= args.length) {
					return false; // Show usage
				}
				
				String message = "";
				for (String arg : args) {
					if (index > 0) {
						index--;
					} else {
						message = message + arg + " ";
					}
				}
				
				if (playerFlag) {
					
					SpigotCommunicationManager.getInstance().sendProxyExecutePlayerMessage(message, player);
					
				} else {
					
					SpigotCommunicationManager.getInstance().sendProxyExecuteMessage(message);
					
				}
				
				commandSender.sendMessage(ChatColor.GREEN + "SENT COMMAND TO PROXY SERVER");
				
				return true;
				
			}

		}

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

				if (NameManager.getInstance().containsColorCodes(args[0]) && !(sender.hasPermission("multichatspigot.nick.color") || sender.hasPermission("multichatspigot.nick.colour"))) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with color codes!");
					return true;
				}

				if (NameManager.getInstance().containsFormatCodes(args[0]) && !(sender.hasPermission("multichatspigot.nick.format"))) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with format codes!");
					return true;
				}

				if (!simpleNickname.matcher(args[0]).matches() && !(sender.hasPermission("multichatspigot.nick.special"))) {
					sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with special characters!");
					return true;
				}

				if (MultiChatSpigot.nicknameLengthIncludeFormatting) {
					// Include formatting codes in the nickname length
					if (args[0].length() > MultiChatSpigot.nicknameMaxLength && !sender.hasPermission("multichatspigot.nick.anylength")) {

						sender.sendMessage(ChatColor.DARK_RED + "Sorry your nickname is too long, max " + MultiChatSpigot.nicknameMaxLength + " characters! (Including format codes)");
						return true;

					}
				} else {
					// Do not include formatting codes in the nickname length
					if (NameManager.getInstance().stripAllFormattingCodes(args[0]).length() > MultiChatSpigot.nicknameMaxLength && !sender.hasPermission("multichatspigot.nick.anylength")) {

						sender.sendMessage(ChatColor.DARK_RED + "Sorry your nickname is too long, max " + MultiChatSpigot.nicknameMaxLength + " characters! (Excluding format codes)");
						return true;

					}
				}

				String targetNickname = NameManager.getInstance().stripAllFormattingCodes(NameManager.getInstance().getCurrentName(targetUUID));
				String targetName = NameManager.getInstance().getName(targetUUID);

				if (NameManager.getInstance().existsNickname(args[0]) && !targetNickname.equalsIgnoreCase(NameManager.getInstance().stripAllFormattingCodes(args[0])) ) { //&& !sender.hasPermission("multichatspigot.nick.duplicate")) {

					sender.sendMessage(ChatColor.DARK_RED + "Sorry, this nickname is already in use!");
					return true;

				}

				if (NameManager.getInstance().existsPlayer(args[0]) && !targetName.equalsIgnoreCase(NameManager.getInstance().stripAllFormattingCodes(args[0])) && !sender.hasPermission("multichatspigot.nick.impersonate")) {

					sender.sendMessage(ChatColor.DARK_RED + "Sorry, a player already exists with this name!");
					return true;

				}

				boolean blacklisted = false;
				for (String bl : MultiChatSpigot.nicknameBlacklist) {
					if (NameManager.getInstance().stripAllFormattingCodes(args[0]).matches(bl)) blacklisted = true;
				}

				if (blacklisted) {

					sender.sendMessage(ChatColor.DARK_RED + "Sorry, this name is not allowed!");
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
				if (!sender.hasPermission("multichatspigot.nick.others")) {
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

			if (NameManager.getInstance().containsColorCodes(args[1]) && !(sender.hasPermission("multichatspigot.nick.color") || sender.hasPermission("multichatspigot.nick.colour"))) {
				sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with color codes!");
				return true;
			}

			if (NameManager.getInstance().containsFormatCodes(args[1]) && !(sender.hasPermission("multichatspigot.nick.format"))) {
				sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with format codes!");
				return true;
			}

			if (!simpleNickname.matcher(args[1]).matches() && !(sender.hasPermission("multichatspigot.nick.special"))) {
				sender.sendMessage(ChatColor.DARK_RED + "You do not have permission to use nicknames with special characters!");
				return true;
			}

			if (MultiChatSpigot.nicknameLengthIncludeFormatting) {
				// Include formatting codes in the nickname length
				if (args[1].length() > MultiChatSpigot.nicknameMaxLength && !sender.hasPermission("multichatspigot.nick.anylength")) {

					sender.sendMessage(ChatColor.DARK_RED + "Sorry your nickname is too long, max " + MultiChatSpigot.nicknameMaxLength + " characters! (Including format codes)");
					return true;

				}
			} else {
				// Do not include formatting codes in the nickname length
				if (NameManager.getInstance().stripAllFormattingCodes(args[1]).length() > MultiChatSpigot.nicknameMaxLength && !sender.hasPermission("multichatspigot.nick.anylength")) {

					sender.sendMessage(ChatColor.DARK_RED + "Sorry your nickname is too long, max " + MultiChatSpigot.nicknameMaxLength + " characters! (Excluding format codes)");
					return true;

				}
			}

			String targetNickname = NameManager.getInstance().stripAllFormattingCodes(NameManager.getInstance().getCurrentName(targetUUID));
			String targetName = NameManager.getInstance().getName(targetUUID);

			if (NameManager.getInstance().existsNickname(args[1]) && !targetNickname.equalsIgnoreCase(NameManager.getInstance().stripAllFormattingCodes(args[1])) ) { //&& !sender.hasPermission("multichatspigot.nick.duplicate")) {

				sender.sendMessage(ChatColor.DARK_RED + "Sorry, this nickname is already in use!");
				return true;

			}

			if (NameManager.getInstance().existsPlayer(args[1]) && !targetName.equalsIgnoreCase(NameManager.getInstance().stripAllFormattingCodes(args[1])) && !sender.hasPermission("multichatspigot.nick.impersonate")) {

				sender.sendMessage(ChatColor.DARK_RED + "Sorry, a player already exists with this name!");
				return true;

			}

			boolean blacklisted = false;
			for (String bl : MultiChatSpigot.nicknameBlacklist) {
				if (NameManager.getInstance().stripAllFormattingCodes(args[1]).matches(bl)) blacklisted = true;
			}

			if (blacklisted) {

				sender.sendMessage(ChatColor.DARK_RED + "Sorry, this name is not allowed!");
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

			} else if (sender.hasPermission("multichatspigot.realname.partial")) {

				Optional<Set<UUID>> matches = NameManager.getInstance().getPartialNicknameMatches(args[0]);

				if (matches.isPresent()) {

					int limit = 10;

					sender.sendMessage(ChatColor.DARK_AQUA + "No one could be found with the exact nickname: " + args[0]);
					sender.sendMessage(ChatColor.AQUA + "The following were found as partial matches:");

					for (UUID uuid : matches.get()) {

						if (limit > 0 || sender.hasPermission("multichatspigot.realname.nolimit")) {
							sender.sendMessage(ChatColor.GREEN + "Nickname: '" + NameManager.getInstance().getCurrentName(uuid, false) + "' Belongs to player: '" + NameManager.getInstance().getName(uuid) + "'");
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

		} else if (cmd.getName().equalsIgnoreCase("username")) {

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

			if (NameManager.getInstance().existsPlayer(args[0])) {

				Optional<String> player;
				player = NameManager.getInstance().getFormattedNameFromName(args[0]);

				if (player.isPresent()) {

					sender.sendMessage(ChatColor.GREEN + "User exists with name: '" + player.get() + "'");

				} else {

					sender.sendMessage(ChatColor.DARK_RED + "No one could be found with name: " + args[0]);

				}

				return true;

			} else if (sender.hasPermission("multichatspigot.username.partial")) {

				Optional<Set<UUID>> matches = NameManager.getInstance().getPartialNameMatches(args[0]);

				if (matches.isPresent()) {

					int limit = 10;

					sender.sendMessage(ChatColor.DARK_AQUA + "No one could be found with the exact username: " + args[0]);
					sender.sendMessage(ChatColor.AQUA + "The following were found as partial matches:");

					for (UUID uuid : matches.get()) {

						if (limit > 0 || sender.hasPermission("multichatspigot.username.nolimit")) {
							sender.sendMessage(ChatColor.GREEN + "- '" + NameManager.getInstance().getName(uuid) + "'");
							limit--;
						} else {
							sender.sendMessage(ChatColor.DARK_GREEN + "Only the first 10 results have been shown, please try a more specific query!");
							break;
						}

					}

				} else {

					sender.sendMessage(ChatColor.DARK_RED + "No one could be found with username: " + args[0]);

				}

				return true;

			} else {

				sender.sendMessage(ChatColor.DARK_RED + "No one could be found with username: " + args[0]);
				return true;

			}

		} 
		return false;
	}

}
