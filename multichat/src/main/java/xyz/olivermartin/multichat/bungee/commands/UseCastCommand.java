package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.CastControl;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

import java.util.Arrays;

/**
 * Use Cast Command
 * <p>A command designed to allow you to use a cast from the console</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class UseCastCommand extends Command {

    public UseCastCommand() {
        super("mcusecast", "multichat.cast.admin", ProxyConfigs.ALIASES.getAliases("mcusecast"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_usecast_usage");
            // TODO: This should probably be in the configurable usecast message
            sender.sendMessage(new ComponentBuilder("/usecast <name> <message>").color(ChatColor.AQUA).create());
            return;
        }

        String castName = args[0];
        if (!CastControl.existsCast(castName)) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_usecast_does_not_exist", castName);
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        CastControl.sendCast(castName, message, MultiChatProxy.getInstance().getChannelManager().getGlobalChannel(), sender);
    }
}
