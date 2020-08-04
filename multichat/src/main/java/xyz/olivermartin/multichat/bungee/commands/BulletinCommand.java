package xyz.olivermartin.multichat.bungee.commands;

import java.util.Arrays;
import java.util.Iterator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.Bulletins;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;

/**
 * Bulletin Command
 * <p>Allows the user to create, start and stop bulletins</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class BulletinCommand extends Command {

    public BulletinCommand() {
        super("mcbulletin", "multichat.bulletin", ConfigManager.getInstance().getHandler(ConfigFile.ALIASES).getConfig().getStringList("bulletin").toArray(new String[0]));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            showCommandUsage(sender);
            return;
        }

        String arg = args[0].toLowerCase();
        switch (arg) {
            case "list": {
                // TODO: Refactor Bulletins to change this part properly
                int counter = 0;
                Iterator<String> it = Bulletins.getIterator();

                MessageManager.sendMessage(sender, "command_bulletin_list");
                while (it.hasNext()) {
                    counter++;
                    MessageManager.sendSpecialMessage(sender, "command_bulletin_list_item", counter + ": +++" + it.next(), true);
                }
                break;
            }
            case "add": {
                if (args.length < 2) {
                    showCommandUsage(sender);
                    return;
                }

                Bulletins.addBulletin(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                MessageManager.sendMessage(sender, "command_bulletin_added");
                break;
            }
            case "remove": {
                if (args.length < 2) {
                    showCommandUsage(sender);
                    return;
                }

                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException ignored) {
                    MessageManager.sendMessage(sender, "command_bulletin_invalid_usage");
                    return;
                }

                Bulletins.removeBulletin(id - 1);
                MessageManager.sendMessage(sender, "command_bulletin_removed");
                break;
            }
            case "start": {
                if (args.length < 2) {
                    showCommandUsage(sender);
                    return;
                }

                int bulletinDelay;
                try {
                    bulletinDelay = Integer.parseInt(args[1]);
                } catch (NumberFormatException ignored) {
                    MessageManager.sendMessage(sender, "command_bulletin_invalid_usage");
                    return;
                }

                Bulletins.startBulletins(bulletinDelay);
                MessageManager.sendMessage(sender, "command_bulletin_started");
                break;
            }
            case "stop": {
                Bulletins.stopBulletins();
                MessageManager.sendMessage(sender, "command_bulletin_stopped");
                break;
            }
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