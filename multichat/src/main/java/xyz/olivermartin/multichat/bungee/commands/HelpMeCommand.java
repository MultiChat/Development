package xyz.olivermartin.multichat.bungee.commands;

import java.util.Optional;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.ConsoleManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.common.MultiChatUtil;

/**
 * 'Help Me' Command
 * <p>Allows players to request help from all online staff members</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class HelpMeCommand extends Command {

	public HelpMeCommand() {
		super("mchelpme", "multichat.chat.helpme", (String[]) ConfigManager.getInstance().getHandler("aliases.yml").getConfig().getStringList("helpme").toArray(new String[0]));
	}

	public void execute(CommandSender sender, String[] args) {

		if ( sender instanceof ProxiedPlayer ) {

			if (args.length < 1) {

				MessageManager.sendMessage(sender, "command_helpme_desc");
				MessageManager.sendMessage(sender, "command_helpme_usage");

			} else { 

				String message = MultiChatUtil.getMessageFromArgs(args);

				if ( sendMessage(sender.getName() + ": " + message, sender.getName()) ) {
					MessageManager.sendMessage(sender, "command_helpme_sent");
				}

			}

		} else {
			MessageManager.sendMessage(sender, "command_helpme_only_players");
		}
	}

	public static boolean sendMessage(String message, String username) {

		Optional<String> crm;

		ProxiedPlayer potentialPlayer = ProxyServer.getInstance().getPlayer(username);
		if (potentialPlayer != null) {
			if (ChatControl.isMuted(potentialPlayer.getUniqueId(), "helpme")) {
				MessageManager.sendMessage(potentialPlayer, "mute_cannot_send_message");
				return false;
			}

			if (ChatControl.handleSpam(potentialPlayer, message, "helpme")) {
				return false;
			}
		}

		crm = ChatControl.applyChatRules(message, "helpme", username);

		if (crm.isPresent()) {
			message = crm.get();
		} else {
			return false;
		}

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
			if (onlineplayer.hasPermission("multichat.staff")) {
				MessageManager.sendSpecialMessage(onlineplayer, "command_helpme_format", message);
			}
		}

		ConsoleManager.logHelpMe(message);

		return true;

	}
}
