package xyz.olivermartin.multichat.local.commands;

import java.util.Map.Entry;
import java.util.UUID;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.platform.spigot.LocalSpigotFileNameManager;
import xyz.olivermartin.multichat.local.storage.LocalFileNameManager;
import xyz.olivermartin.multichat.local.storage.LocalFileSystemManager;
import xyz.olivermartin.multichat.local.storage.LocalNameManagerMode;
import xyz.olivermartin.multichat.local.storage.LocalSQLNameManager;

public abstract class MultiChatLocalCommand {

	protected boolean executeMultiChatLocalCommand(MultiChatLocalCommandSender sender, String[] args) {

		if (args.length != 1) {
			return false;
		}

		if (args[0].equalsIgnoreCase("reload")) {

			return reload(sender);

		} else if (args[0].equalsIgnoreCase("migratetosql")) {

			return migrateToSQL(sender);

		} else if (args[0].equalsIgnoreCase("debug")) {

			return debug(sender);

		} else {

			return false;
		}

	}

	private boolean reload(MultiChatLocalCommandSender sender) {

		if (sender.hasPermission("multichatlocal.reload")) {

			MultiChatLocal.getInstance().getConfigManager().getLocalConfig().reload();
			sender.sendGoodMessage("The plugin has been reloaded!");

		} else {

			sender.sendBadMessage("You do not have permission to reload the plugin");

		}

		return true;

	}
	
	private boolean debug(MultiChatLocalCommandSender sender) {

		if (sender.hasPermission("multichatlocal.debug")) {

			boolean result = MultiChatLocal.getInstance().getConsoleLogger().toggleDebug();
			sender.sendGoodMessage("Debug mode set to " + result);

		} else {

			sender.sendBadMessage("You do not have permission to enable/disable debug mode");

		}

		return true;

	}

	private boolean migrateToSQL(MultiChatLocalCommandSender sender) {

		if (sender.isPlayer()) {

			sender.sendBadMessage("This command can only be executed from the server console for security reasons!");
			return true;

		} else {

			if (! (MultiChatLocal.getInstance().getNameManager().getMode() == LocalNameManagerMode.SQL)) {
				sender.sendBadMessage("This command can only be used in SQL mode!");
				return true;
			}

			LocalFileNameManager fnm = new LocalSpigotFileNameManager();
			LocalFileSystemManager lfsm = new LocalFileSystemManager();

			sender.sendGoodMessage("Starting load of nickname file data...");
			lfsm.registerNicknameFile(MultiChatLocalPlatform.SPIGOT, "namedata.dat", MultiChatLocal.getInstance().getConfigDirectory(), fnm);
			lfsm.getNicknameFile().reload();
			sender.sendGoodMessage("Successfully loaded nickname data...");

			sender.sendGoodMessage("Starting migration");

			int count = 0;
			int max = fnm.getMapUUIDName().size();
			int checkcount = 25;
			int checkpoint = checkcount/100*max;

			for (Entry<UUID, String> entry : fnm.getMapUUIDName().entrySet()) {

				count++;
				if (count > checkpoint) {
					sender.sendGoodMessage("Completed " + checkcount + "% of migration...");
					checkcount += 25;
					checkpoint = checkcount/100*max;
				}

				UUID uuid = entry.getKey();
				String name = entry.getValue();
				String formattedName = fnm.getMapNameFormatted().get(name);
				String nick;
				String formattedNick;
				if (fnm.getMapUUIDNick().containsKey(uuid)) {
					nick = fnm.getMapUUIDNick().get(uuid);
					formattedNick = fnm.getMapNickFormatted().get(nick);
					if (formattedNick.equals(formattedName)) {
						nick = null;
						formattedNick = null;
					}
				} else {
					nick = null;
					formattedNick = null;
				}


				((LocalSQLNameManager) MultiChatLocal.getInstance().getNameManager()).registerMigratedPlayer(uuid, name, formattedName, nick, formattedNick);

			}

			sender.sendGoodMessage("Successfully migrated: " + max + " records");

			sender.sendGoodMessage("Saving nickname data file...");

			lfsm.getNicknameFile().save();

		}

		return true;

	}

}
