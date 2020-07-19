package xyz.olivermartin.multichat.bungee.commands;

import java.util.regex.PatternSyntaxException;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;

/**
 * Execute Command
 * <p>Used to execute commands remotely on Spigot servers</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class MultiChatExecuteCommand extends Command {

	public MultiChatExecuteCommand() {
		super("multichatexecute", "multichat.execute", ConfigManager.getInstance().getHandler("config.yml").getConfig().contains("multichatexecutecommand") ? (String[]) ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("multichatexecutecommand").toArray(new String[0]) : new String[] {"mcexecute", "mce" ,"gexecute","gexe","gcommand"});
	}

	public void execute(CommandSender sender, String[] args) {

		if (args.length < 1) {

			MessageManager.sendMessage(sender, "command_execute_usage");

		} else {

			String server = ".*";
			boolean playerFlag = false;
			String player = ".*";

			// Handle flags
			int index = 0;

			while (index < args.length) {

				if (args[index].equalsIgnoreCase("-s")) {
					if (index+1 < args.length) {
						server = args[index+1];
					}
				} else if (args[index].equalsIgnoreCase("-p")) {
					if (index+1 < args.length) {
						playerFlag = true;
						player = args[index+1];
					}
				} else {
					break;
				}

				index = index+2;

			}


			String message = "";
			for (String arg : args) {
				if (index > 0) {
					index--;
				} else {
					message = message + arg + " ";
				}
			}

			message = message.trim();

			try {

				for (ServerInfo s : ProxyServer.getInstance().getServers().values()) {

					if (s.getName().matches(server)) {

						if (playerFlag) {
							for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
								if (p.getName().matches(player)) {
									ProxyLocalCommunicationManager.sendPlayerCommandMessage(message, p.getName(), s);
								}
							}
						} else {
							ProxyLocalCommunicationManager.sendCommandMessage(message, s);
						}
					}

				}

				MessageManager.sendMessage(sender, "command_execute_sent");

			} catch (PatternSyntaxException e) {

				MessageManager.sendMessage(sender, "command_execute_regex");

			}

		}
	}

}
