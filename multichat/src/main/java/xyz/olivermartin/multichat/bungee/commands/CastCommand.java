package xyz.olivermartin.multichat.bungee.commands;

import java.util.Iterator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.CastControl;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChatUtil;

/**
 * Cast Command
 * <p> The Custom broadcAST (CAST) command allows you to create your own customised broadcast formats </p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class CastCommand extends Command {

	public CastCommand() {
		super("mccast", "multichat.cast.admin", (String[]) ConfigManager.getInstance().getHandler("aliases.yml").getConfig().getStringList("cast").toArray(new String[0]));
	}

	public void showCommandUsage(CommandSender sender) {
		MessageManager.sendMessage(sender, "command_cast_usage");
		sender.sendMessage(new ComponentBuilder("/cast add <name> <format>").color(ChatColor.AQUA).create());
		sender.sendMessage(new ComponentBuilder("/cast remove <name>").color(ChatColor.AQUA).create());
		sender.sendMessage(new ComponentBuilder("/cast list").color(ChatColor.AQUA).create());
		sender.sendMessage(new ComponentBuilder("/<castname> <message>").color(ChatColor.AQUA).create());
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (args.length < 1) {

			showCommandUsage(sender);

		} else if (args.length == 1) {

			if (args[0].toLowerCase().equals("list")) {

				Iterator<String> it = CastControl.castList.keySet().iterator();
				String currentItem;

				MessageManager.sendMessage(sender, "command_cast_list");
				while (it.hasNext()) {
					currentItem = it.next();
					MessageManager.sendSpecialMessage(sender, "command_cast_list_item", currentItem + ": " + CastControl.castList.get(currentItem));
				}

			} else {
				showCommandUsage(sender);
			}

		} else if (args.length == 2) {

			if (args[0].toLowerCase().equals("remove")) {

				if (CastControl.existsCast(args[1])) {

					CastControl.removeCast(args[1]);
					MessageManager.sendSpecialMessage(sender, "command_cast_removed", args[1].toUpperCase());

				} else {

					MessageManager.sendSpecialMessage(sender, "command_cast_does_not_exist", args[1].toUpperCase());
				}

			} else {

				showCommandUsage(sender);

			}

		} else if (args.length == 3) {

			if (args[0].toLowerCase().equals("add")) {

				if (!(CastControl.existsCast(args[1])) && !args[1].equalsIgnoreCase("cast")) {

					CastControl.addCast(args[1], args[2]);
					MessageManager.sendSpecialMessage(sender, "command_cast_added", args[1].toUpperCase());

				} else {

					MessageManager.sendSpecialMessage(sender, "command_cast_added_error", args[1].toUpperCase());
				}

			} else {

				showCommandUsage(sender);

			}

		} else if (args.length >= 3) {

			if (args[0].toLowerCase().equals("add")) {

				String message = MultiChatUtil.getMessageFromArgs(args, 2);

				if (!CastControl.existsCast(args[1])) {

					CastControl.addCast(args[1], message);
					MessageManager.sendSpecialMessage(sender, "command_cast_added", args[1].toUpperCase());

				} else {

					MessageManager.sendSpecialMessage(sender, "command_cast_added_error", args[1].toUpperCase());
				}

			} else {

				showCommandUsage(sender);

			}

		} else {

			showCommandUsage(sender);
		}
	}
}
