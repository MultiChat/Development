package xyz.olivermartin.multichat.bungee.commands;

import java.util.Iterator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.Bulletins;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChatUtil;

/**
 * Bulletin Command
 * <p>Allows the user to create, start and stop bulletins</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class BulletinCommand extends Command {

	private static String[] aliases = new String[] {"bulletins"};

	public BulletinCommand() {
		super("bulletin", "multichat.bulletin", aliases);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (args.length < 1) {

			showCommandUsage(sender);

		} else if (args.length == 1) {

			if (args[0].toLowerCase().equals("stop")) {

				Bulletins.stopBulletins();
				MessageManager.sendMessage(sender, "command_bulletin_stopped");

			} else if (args[0].toLowerCase().equals("list")) {

				int counter = 0;
				Iterator<String> it = Bulletins.getIterator();

				MessageManager.sendMessage(sender, "command_bulletin_list");
				while (it.hasNext()) {
					counter++;
					MessageManager.sendSpecialMessage(sender, "command_bulletin_list_item", counter + ": " + it.next());
				}

			} else {

				showCommandUsage(sender);

			}

		} else if (args.length == 2) {

			if (args[0].toLowerCase().equals("remove")) {

				try {

					Bulletins.removeBulletin(Integer.parseInt(args[1]) - 1);
					MessageManager.sendMessage(sender, "command_bulletin_removed");

				} catch (Exception e) {
					MessageManager.sendMessage(sender, "command_bulletin_invalid_usage");
				}

			} else if (args[0].toLowerCase().equals("start") ) {

				try {
					Bulletins.startBulletins(Integer.parseInt(args[1]));
					MessageManager.sendMessage(sender, "command_bulletin_started");
				} catch (Exception e) {
					MessageManager.sendMessage(sender, "command_bulletin_invalid_usage");
				}

			} else if (args[0].toLowerCase().equals("add") ) {

				Bulletins.addBulletin(args[1]);
				MessageManager.sendMessage(sender, "command_bulletin_added");

			} else {

				showCommandUsage(sender);

			}

		} else if (args.length > 2) {

			if (args[0].toLowerCase().equals("add")) {

				String message = MultiChatUtil.getMessageFromArgs(args, 1);

				Bulletins.addBulletin(message);
				MessageManager.sendMessage(sender, "command_bulletin_added");
			}

		} else {

			showCommandUsage(sender);

		}

	}

	private void showCommandUsage(CommandSender sender) {

		MessageManager.sendMessage(sender, "command_bulletin_usage");
		sender.sendMessage(new ComponentBuilder("/bulletin add <message>").color(ChatColor.AQUA).create());
		sender.sendMessage(new ComponentBuilder("/bulletin remove <index>").color(ChatColor.AQUA).create());
		sender.sendMessage(new ComponentBuilder("/bulletin start <interval in minutes>").color(ChatColor.AQUA).create());
		sender.sendMessage(new ComponentBuilder("/bulletin stop").color(ChatColor.AQUA).create());
		sender.sendMessage(new ComponentBuilder("/bulletin list").color(ChatColor.AQUA).create());

	}
}