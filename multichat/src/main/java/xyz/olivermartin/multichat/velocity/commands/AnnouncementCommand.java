package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import xyz.olivermartin.multichat.velocity.Announcements;
import xyz.olivermartin.multichat.velocity.MessageManager;
import xyz.olivermartin.multichat.velocity.MultiChatUtil;

import java.util.Iterator;
import java.util.Map;

/**
 * Announcement Command
 * <p>Allows the user to create, remove or use announcements</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class AnnouncementCommand extends Command {

    private static final String[] aliases = new String[]{"announce"};

    public AnnouncementCommand() {
        super("announcement", aliases);
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.announce");
    }

    public void execute(Invocation invocation) {

        var args = invocation.arguments();
        var sender = invocation.source();

        if (args.length < 1) {

            showCommandUsage(sender);

        } else if (args.length == 1) {

            if (args[0].equalsIgnoreCase("list")) {

                Map<String, String> announcementList = Announcements.getAnnouncementList();
                Iterator<String> it = announcementList.keySet().iterator();

                MessageManager.sendMessage(sender, "command_announcement_list");

                String currentItem;
                while (it.hasNext()) {
                    currentItem = it.next();
                    MessageManager.sendSpecialMessage(sender, "command_announcement_list_item", currentItem + ": " + announcementList.get(currentItem));
                }

            } else if (Announcements.existsAnnouncemnt(args[0].toLowerCase())) {

                Announcements.playAnnouncement(args[0].toLowerCase());

            } else {

                MessageManager.sendSpecialMessage(sender, "command_announcement_does_not_exist", args[0].toUpperCase());

            }

        } else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("remove")) {

                if (Announcements.removeAnnouncement(args[1].toLowerCase())) {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_removed", args[1].toUpperCase());
                } else {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_does_not_exist", args[1].toUpperCase());
                }

            } else if (args[0].equalsIgnoreCase("stop")) {

                if (Announcements.stopAnnouncement(args[1].toLowerCase())) {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_stopped", args[1].toUpperCase());
                } else {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_stopped_error", args[1].toUpperCase());
                }

            } else {

                showCommandUsage(sender);
            }

        } else if (args.length == 3) {

            if (isInteger(args[2])) {

                if (args[0].equalsIgnoreCase("start")) {

                    if (Announcements.startAnnouncement(args[1].toLowerCase(), Integer.parseInt(args[2]))) {
                        MessageManager.sendSpecialMessage(sender, "command_announcement_started", args[1].toUpperCase());
                    } else {
                        MessageManager.sendSpecialMessage(sender, "command_announcement_started_error", args[1].toUpperCase());
                    }

                } else {

                    showCommandUsage(sender);

                }

            } else if (args[0].equalsIgnoreCase("add")) {

                if (Announcements.addAnnouncement(args[1].toLowerCase(), args[2])) {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_added", args[1].toUpperCase());
                } else {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_added_error", args[1].toUpperCase());
                }

            } else {

                showCommandUsage(sender);

            }

        } else {
            if (args[0].equalsIgnoreCase("add")) {
                String message = MultiChatUtil.getMessageFromArgs(args, 2);
                if (Announcements.addAnnouncement(args[1].toLowerCase(), message)) {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_added", args[1].toUpperCase());
                } else {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_added_error", args[1].toUpperCase());
                }
            } else {
                showCommandUsage(sender);
            }
        }

    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    private void showCommandUsage(CommandSource sender) {

        MessageManager.sendMessage(sender, "command_announcement_usage");
        sender.sendMessage(Component.text("/announcement add <name> <message>").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("/announcement remove <name>").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("/announcement start <name> <interval in minutes>").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("/announcement stop <name>").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("/announcement list").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("/announce <name>").color(NamedTextColor.AQUA));

    }

}
