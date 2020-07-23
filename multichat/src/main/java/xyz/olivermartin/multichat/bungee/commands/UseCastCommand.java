package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.CastControl;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;

/**
 * Use Cast Command
 * <p>A command designed to allow you to use a cast from the console</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class UseCastCommand extends Command {

	public UseCastCommand() {
		super("mcusecast", "multichat.cast.admin", (String[])ConfigManager.getInstance().getHandler("aliases.yml").getConfig().getStringList("usecast").toArray(new String[0]));
	}

	public void displayUsage(CommandSender sender) {
		MessageManager.sendMessage(sender, "command_usecast_usage");
		sender.sendMessage(new ComponentBuilder("/usecast <name> <message>").color(ChatColor.AQUA).create());
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (args.length < 2) {
			displayUsage(sender);
			return;
		}

		if (CastControl.existsCast(args[0])) {

			String message = MultiChatUtil.getMessageFromArgs(args, 1);

			CastControl.sendCast(args[0], message, MultiChatProxy.getInstance().getChannelManager().getGlobalChannel(), sender);

		} else {

			MessageManager.sendSpecialMessage(sender, "command_usecast_does_not_exist", args[0].toUpperCase());
			return;

		}
	}
}
