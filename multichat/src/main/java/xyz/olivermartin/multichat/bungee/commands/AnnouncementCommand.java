package xyz.olivermartin.multichat.bungee.commands;

import java.util.Arrays;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.Announcements;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;

/**
 * Announcement Command
 * <p>Allows the user to create, remove or use announcements</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class AnnouncementCommand extends Command {

    public AnnouncementCommand() {
        super("mcannouncement", "multichat.announce", ConfigManager.getInstance().getHandler(ConfigFile.ALIASES).getConfig().getStringList("announcement").toArray(new String[0]));
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            showCommandUsage(sender);
            return;
        }

        String arg = args[0].toLowerCase();
        switch (arg) {
            case "list": {
                MessageManager.sendMessage(sender, "command_announcement_list");
                Announcements.getAnnouncementList().forEach((key, value) ->
                        MessageManager.sendSpecialMessage(sender,
                                "command_announcement_list_item",
                                key + ": +++" + value,
                                true
                        )
                );
                return;
            }
            case "add": {
                if (args.length < 3)
                    break;

                String announcementKey = args[1].toLowerCase();
                String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                if (Announcements.addAnnouncement(announcementKey, message)) {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_added", announcementKey);
                } else {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_added_error", announcementKey);
                }
                return;
            }
            case "remove": {
                if (args.length < 2)
                    break;

                String announcementKey = args[1].toLowerCase();
                if (Announcements.removeAnnouncement(announcementKey)) {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_removed", announcementKey);
                } else {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_does_not_exist", announcementKey);
                }
                return;
            }
            case "start": {
                int timer;
                if (args.length < 3 || (timer = parseInt(args[2])) == -1)
                    break;

                String announcementKey = args[1].toLowerCase();
                if (Announcements.startAnnouncement(announcementKey, timer)) {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_started", announcementKey);
                } else {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_started_error", announcementKey);
                }
                return;
            }
            case "stop": {
                if (args.length < 2)
                    break;

                String announcementKey = args[1].toLowerCase();
                if (Announcements.stopAnnouncement(announcementKey)) {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_stopped", announcementKey);
                } else {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_stopped_error", announcementKey);
                }
                return;
            }
            default: {
                if (Announcements.existsAnnouncemnt(arg)) {
                    Announcements.playAnnouncement(arg);
                } else {
                    MessageManager.sendSpecialMessage(sender, "command_announcement_does_not_exist", arg);
                }
                return;
            }
        }

        showCommandUsage(sender);
    }

    private int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    private void showCommandUsage(CommandSender sender) {
        MessageManager.sendMessage(sender, "command_announcement_usage");
        sender.sendMessage(new ComponentBuilder("/announcement add <name> <message>").color(ChatColor.AQUA).create());
        sender.sendMessage(new ComponentBuilder("/announcement remove <name>").color(ChatColor.AQUA).create());
        sender.sendMessage(new ComponentBuilder("/announcement start <name> <interval in minutes>").color(ChatColor.AQUA).create());
        sender.sendMessage(new ComponentBuilder("/announcement stop <name>").color(ChatColor.AQUA).create());
        sender.sendMessage(new ComponentBuilder("/announcement list").color(ChatColor.AQUA).create());
        sender.sendMessage(new ComponentBuilder("/announce <name>").color(ChatColor.AQUA).create());
    }
}
