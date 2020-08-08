package xyz.olivermartin.multichat.bungee.commands;

import java.util.Optional;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConsoleManager;
import xyz.olivermartin.multichat.common.MessageType;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

/**
 * 'Help Me' Command
 * <p>Allows players to request help from all online staff members</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class HelpMeCommand extends Command {

	public HelpMeCommand() {
		super("mchelpme", "multichat.chat.helpme", ProxyConfigs.ALIASES.getAliases("mchelpme"));
	}

	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			ProxyConfigs.MESSAGES.sendMessage(sender, "command_helpme_only_players");
			return;
		}

		if (args.length < 1) {
			ProxyConfigs.MESSAGES.sendMessage(sender, "command_helpme_desc");
			ProxyConfigs.MESSAGES.sendMessage(sender, "command_helpme_usage");
			return;
		}

		ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

		if (ChatControl.isMuted(proxiedPlayer.getUniqueId(), MessageType.HELPME)) {
			ProxyConfigs.MESSAGES.sendMessage(proxiedPlayer, "mute_cannot_send_message");
			return;
		}

		// TODO: Probably should do this differently
		String message = proxiedPlayer.getName() + ": " + String.join(" ", args);

		if (ChatControl.handleSpam(proxiedPlayer, message, MessageType.HELPME)) {
			return;
		}

		Optional<String> crm = ChatControl.applyChatRules(proxiedPlayer, message, MessageType.HELPME);
		if (!crm.isPresent())
			return;

		String finalMessage = crm.get();

		ProxyServer.getInstance().getPlayers().stream()
		.filter(target -> target.hasPermission("multichat.staff"))
		.forEach(target -> ProxyConfigs.MESSAGES.sendMessage(target, "command_helpme_format", finalMessage));

		ConsoleManager.logHelpMe(message);
		ProxyConfigs.MESSAGES.sendMessage(sender, "command_helpme_sent");
	}
}
