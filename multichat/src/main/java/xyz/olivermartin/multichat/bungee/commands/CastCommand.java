package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.CastControl;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

import java.util.Arrays;

/**
 * Cast Command
 * <p> The Custom broadcAST (CAST) command allows you to create your own customised broadcast formats </p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class CastCommand extends Command {

    public CastCommand() {
        super("mccast", "multichat.cast.admin", ProxyConfigs.ALIASES.getAliases("mccast"));
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
                MessageManager.sendMessage(sender, "command_cast_list");
                CastControl.castList.forEach((key, value) ->
                        MessageManager.sendSpecialMessage(sender, "command_cast_list_item", key + ": " + value)
                );
                return;
            }
            case "add": {
                if (args.length < 3)
                    break;

                String castName = args[1];
                if (CastControl.existsCast(castName) || castName.equalsIgnoreCase("cast")) {
                    MessageManager.sendSpecialMessage(sender, "command_cast_added_error", castName);
                    return;
                }

                CastControl.addCast(castName, String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
                MessageManager.sendSpecialMessage(sender, "command_cast_added", castName);
                return;
            }
            case "remove": {
                if (args.length < 2)
                    break;

                String castName = args[1];
                if (!CastControl.existsCast(castName)) {
                    MessageManager.sendSpecialMessage(sender, "command_cast_does_not_exist", castName);
                    return;
                }

                CastControl.removeCast(castName);
                MessageManager.sendSpecialMessage(sender, "command_cast_removed", castName);
                return;
            }
        }

        showCommandUsage(sender);
    }

    public void showCommandUsage(CommandSender sender) {
        MessageManager.sendMessage(sender, "command_cast_usage");
        sender.sendMessage(new ComponentBuilder("/cast add <name> <format>").color(ChatColor.AQUA).create());
        sender.sendMessage(new ComponentBuilder("/cast remove <name>").color(ChatColor.AQUA).create());
        sender.sendMessage(new ComponentBuilder("/cast list").color(ChatColor.AQUA).create());
        sender.sendMessage(new ComponentBuilder("/<castname> <message>").color(ChatColor.AQUA).create());
    }
}
