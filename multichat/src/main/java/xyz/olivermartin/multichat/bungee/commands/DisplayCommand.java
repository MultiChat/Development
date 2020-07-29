package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.ConsoleManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;
import xyz.olivermartin.multichat.bungee.events.PostBroadcastEvent;

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

		message = ChatControl.applyChatRules(message, "display_command", "").get();
		message = MultiChatUtil.reformatRGB(message);
		Configuration config = ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig();

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
			if (onlineplayer.getServer() != null) {
				if (!config.getStringList(ConfigValues.Config.NO_GLOBAL).contains(
						onlineplayer.getServer().getInfo().getName())) {
					if (MultiChat.legacyServers.contains(onlineplayer.getServer().getInfo().getName())) {
						onlineplayer.sendMessage(TextComponent.fromLegacyText(MultiChatUtil.approximateHexCodes(ChatColor.translateAlternateColorCodes('&', message))));
					} else {
						onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
					}
				}
			}
		}

		// Trigger PostBroadcastEvent
		ProxyServer.getInstance().getPluginManager().callEvent(new PostBroadcastEvent("display", message));

		ConsoleManager.logDisplayMessage(message);
	}
}
