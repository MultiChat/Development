package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.MultiChat;

/**
 * SocialSpy Command
 * <p>Allows staff members to view private messages sent by players</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class SocialSpyCommand extends Command {

	private static String[] aliases = (String[])MultiChat.configman.config.getStringList("socialspycommand").toArray(new String[0]);

	public SocialSpyCommand() {
		super("socialspy", "multichat.staff.spy", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		if ((sender instanceof ProxiedPlayer)) {

			if (args.length < 1) {

				if (MultiChat.socialspy.contains(((ProxiedPlayer)sender).getUniqueId())) {
					MultiChat.socialspy.remove(((ProxiedPlayer)sender).getUniqueId());
					sender.sendMessage(new ComponentBuilder("Social Spy Disabled").color(ChatColor.RED).create());
				} else {
					MultiChat.socialspy.add(((ProxiedPlayer)sender).getUniqueId());
					sender.sendMessage(new ComponentBuilder("Social Spy Enabled").color(ChatColor.AQUA).create());
				}

			} else {
				sender.sendMessage(new ComponentBuilder("Usage: /socialspy").color(ChatColor.AQUA).create());
				sender.sendMessage(new ComponentBuilder("Toggles if the user has social spy enabled or disabled").color(ChatColor.AQUA).create());
			}

		} else {
			sender.sendMessage(new ComponentBuilder("Only players can toggle socialspy").color(ChatColor.RED).create());
		}
	}
}
