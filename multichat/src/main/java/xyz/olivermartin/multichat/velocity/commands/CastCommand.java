package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import xyz.olivermartin.multichat.velocity.CastControl;
import xyz.olivermartin.multichat.velocity.MessageManager;
import xyz.olivermartin.multichat.velocity.MultiChatUtil;

import java.util.Iterator;

/**
 * Cast Command
 * <p> The Custom broadcAST (CAST) command allows you to create your own customised broadcast formats </p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class CastCommand extends Command {

	private static final String[] aliases = new String[] {};

	public CastCommand() {
		super("cast",  aliases);
	}

	public void showCommandUsage(CommandSource sender) {
		MessageManager.sendMessage(sender, "command_cast_usage");
		sender.sendMessage(Component.text("/cast add <name> <format>").color(NamedTextColor.AQUA));
		sender.sendMessage(Component.text("/cast remove <name>").color(NamedTextColor.AQUA));
		sender.sendMessage(Component.text("/cast list").color(NamedTextColor.AQUA));
		sender.sendMessage(Component.text("/<castname> <message>").color(NamedTextColor.AQUA));
	}

	public boolean hasPermission(Invocation invocation) {
		return invocation.source().hasPermission("multichat.cast.admin");
	}

	public void execute(Invocation invocation) {

		var args = invocation.arguments();
		var sender = invocation.source();

		if (args.length < 1) {

			showCommandUsage(sender);

		} else if (args.length == 1) {

			if (args[0].equalsIgnoreCase("list")) {

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

			if (args[0].equalsIgnoreCase("remove")) {

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

			if (args[0].equalsIgnoreCase("add")) {

				if (!(CastControl.existsCast(args[1])) && !args[1].equalsIgnoreCase("cast")) {

					CastControl.addCast(args[1], args[2]);
					MessageManager.sendSpecialMessage(sender, "command_cast_added", args[1].toUpperCase());

				} else {

					MessageManager.sendSpecialMessage(sender, "command_cast_added_error", args[1].toUpperCase());
				}

			} else {

				showCommandUsage(sender);

			}

		} else {
			if (args[0].equalsIgnoreCase("add")) {

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
		}
	}
}
