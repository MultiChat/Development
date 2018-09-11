package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChat;

/**
 * SocialSpy Command
 * <p>Allows staff members to view private messages sent by players</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class SocialSpyCommand extends Command {

	private static String[] aliases = (String[])ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("socialspycommand").toArray(new String[0]);

	public SocialSpyCommand() {
		super("socialspy", "multichat.staff.spy", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		if ((sender instanceof ProxiedPlayer)) {

			if (args.length < 1) {

				if (MultiChat.socialspy.contains(((ProxiedPlayer)sender).getUniqueId())) {
					MultiChat.socialspy.remove(((ProxiedPlayer)sender).getUniqueId());
					MessageManager.sendMessage(sender, "command_socialspy_disabled");
				} else {
					MultiChat.socialspy.add(((ProxiedPlayer)sender).getUniqueId());
					MessageManager.sendMessage(sender, "command_socialspy_enabled");
				}

			} else {
				MessageManager.sendMessage(sender, "command_socialspy_usage");
				MessageManager.sendMessage(sender, "command_socialspy_desc");
			}

		} else {
			MessageManager.sendMessage(sender, "command_socialspy_only_players");
		}
	}
}
