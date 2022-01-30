package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import xyz.olivermartin.multichat.velocity.Bulletins;
import xyz.olivermartin.multichat.velocity.MessageManager;
import xyz.olivermartin.multichat.velocity.MultiChatUtil;

import java.util.Iterator;

/**
 * Bulletin Command
 * <p>Allows the user to create, start and stop bulletins</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class BulletinCommand extends Command {

    private static final String[] aliases = new String[]{"bulletins"};

    public BulletinCommand() {
        super("bulletin", aliases);
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.bulletin");
    }

    public void execute(Invocation invocation) {

        var args = invocation.arguments();
        var sender = invocation.source();

        if (args.length < 1) {

            showCommandUsage(sender);

        } else if (args.length == 1) {

            if (args[0].equalsIgnoreCase("stop")) {

                Bulletins.stopBulletins();
                MessageManager.sendMessage(sender, "command_bulletin_stopped");

            } else if (args[0].equalsIgnoreCase("list")) {

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

            if (args[0].equalsIgnoreCase("remove")) {

                try {

                    Bulletins.removeBulletin(Integer.parseInt(args[1]) - 1);
                    MessageManager.sendMessage(sender, "command_bulletin_removed");

                } catch (Exception e) {
                    MessageManager.sendMessage(sender, "command_bulletin_invalid_usage");
                }

            } else if (args[0].equalsIgnoreCase("start")) {

                try {
                    Bulletins.startBulletins(Integer.parseInt(args[1]));
                    MessageManager.sendMessage(sender, "command_bulletin_started");
                } catch (Exception e) {
                    MessageManager.sendMessage(sender, "command_bulletin_invalid_usage");
                }

            } else if (args[0].equalsIgnoreCase("add")) {

                Bulletins.addBulletin(args[1]);
                MessageManager.sendMessage(sender, "command_bulletin_added");

            } else {

                showCommandUsage(sender);

            }

        } else {

            if (args[0].equalsIgnoreCase("add")) {

                String message = MultiChatUtil.getMessageFromArgs(args, 1);

                Bulletins.addBulletin(message);
                MessageManager.sendMessage(sender, "command_bulletin_added");
            }

        }

    }

    private void showCommandUsage(CommandSource sender) {

        MessageManager.sendMessage(sender, "command_bulletin_usage");
        sender.sendMessage(Component.text("/bulletin add <message>").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("/bulletin remove <index>").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("/bulletin start <interval in minutes>").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("/bulletin stop").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("/bulletin list").color(NamedTextColor.AQUA));

    }
}