package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.Events;
import xyz.olivermartin.multichat.bungee.MessageManager;

public class MultiChatBypassCommand extends Command {

	public MultiChatBypassCommand() {
		super("multichatbypass", "multichat.bypass", ConfigManager.getInstance().getHandler("config.yml").getConfig().contains("multichatbypasscommand") ? (String[]) ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("multichatbypasscommand").toArray(new String[0]) : new String[0]);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (! (sender instanceof ProxiedPlayer)) {
			return;
		}

		ProxiedPlayer player = (ProxiedPlayer) sender;

		if (args.length >= 1) {

			MessageManager.sendMessage(sender, "command_multichatbypass_usage");

		} else {

			if (Events.mcbPlayers.contains(player.getUniqueId())) {

				Events.mcbPlayers.remove(player.getUniqueId());
				MessageManager.sendMessage(sender, "command_multichatbypass_disabled");

			} else {

				Events.mcbPlayers.add(player.getUniqueId());
				MessageManager.sendMessage(sender, "command_multichatbypass_enabled");

			}

		}

	}



}
