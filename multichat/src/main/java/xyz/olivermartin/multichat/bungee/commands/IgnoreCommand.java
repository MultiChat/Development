package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;

public class IgnoreCommand extends Command {

	public IgnoreCommand() {
		super("ignore", "multichat.ignore", (String[])ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().getStringList("ignorecommand").toArray(new String[0]));
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (args.length != 1) {

			MessageManager.sendMessage(sender, "ignore_usage");

		} else {

			if (sender instanceof ProxiedPlayer) {

				String username = args[0];

				ProxiedPlayer target = ProxyServer.getInstance().getPlayer(username);

				if (target != null) {

					if (target.hasPermission("multichat.ignore.bypass")) {
						MessageManager.sendMessage(sender, "ignore_bypass");
						return;
					}

					if (!ChatControl.ignoresAnywhere(target.getUniqueId(), ((ProxiedPlayer) sender).getUniqueId())) {
						ChatControl.ignore(((ProxiedPlayer) sender).getUniqueId(), target.getUniqueId());
						MessageManager.sendMessage(sender, "ignore_ignored");
					} else {
						ChatControl.unignore(((ProxiedPlayer) sender).getUniqueId(), target.getUniqueId());
						MessageManager.sendMessage(sender, "ignore_unignored");
					}

				} else {

					MessageManager.sendMessage(sender, "ignore_player_not_found");

				}

			} else {

				MessageManager.sendMessage(sender, "ignore_only_players");

			}

		}

	}

}
