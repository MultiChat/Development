package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.ConsoleManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.bungee.events.PostBroadcastEvent;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.ProxyJsonUtils;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;

/**
 * Display Command
 * <p>Displays a message to every player connected to the BungeeCord network</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class DisplayCommand extends Command {

	public DisplayCommand() {
		super("mcdisplay", "multichat.staff.display", (String[]) ConfigManager.getInstance().getHandler(ConfigFile.ALIASES).getConfig().getStringList("display").toArray(new String[0]));
	}

	public void execute(CommandSender sender, String[] args) {

		if (args.length < 1) {

			MessageManager.sendMessage(sender, "command_display_desc");
			MessageManager.sendMessage(sender, "command_display_usage");

		} else {

			String message = MultiChatUtil.getMessageFromArgs(args);

			displayMessage(message);
		}
	}

	public static void displayMessage(String message) {

		Configuration config = ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig();

		message = ChatControl.applyChatRules(message, "display_command", "").get();
		message = MultiChatUtil.translateColorCodes(message);

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if (onlineplayer.getServer() == null) continue;

			if (config.getStringList(ConfigValues.Config.NO_GLOBAL).contains(
					onlineplayer.getServer().getInfo().getName())) continue;

			if (MultiChat.legacyServers.contains(onlineplayer.getServer().getInfo().getName())) {
				onlineplayer.sendMessage(ProxyJsonUtils.parseMessage(MultiChatUtil.approximateRGBColorCodes(message)));
			} else {
				onlineplayer.sendMessage(ProxyJsonUtils.parseMessage(message));
			}

		}

		// Trigger PostBroadcastEvent
		ProxyServer.getInstance().getPluginManager().callEvent(new PostBroadcastEvent("display", message));

		ConsoleManager.logDisplayMessage(message);

	}
}
