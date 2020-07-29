package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

/**
 * SocialSpy Command
 * <p>Allows staff members to view private messages sent by players</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class SocialSpyCommand extends Command {

	public SocialSpyCommand() {
		super("mcsocialspy", "multichat.staff.spy", (String[])ConfigManager.getInstance().getHandler(ConfigFile.ALIASES).getConfig().getStringList("socialspy").toArray(new String[0]));
	}

	public void execute(CommandSender sender, String[] args) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		if ((sender instanceof ProxiedPlayer)) {

			if (args.length < 1) {

				if (ds.getSocialSpy().contains(((ProxiedPlayer)sender).getUniqueId())) {
					ds.getSocialSpy().remove(((ProxiedPlayer)sender).getUniqueId());
					MessageManager.sendMessage(sender, "command_socialspy_disabled");
				} else {
					ds.getSocialSpy().add(((ProxiedPlayer)sender).getUniqueId());
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
