package xyz.olivermartin.multichat.bungee.commands;

import com.google.gson.JsonParser;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.config.Configuration;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.ConsoleManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.bungee.events.PostBroadcastEvent;
import xyz.olivermartin.multichat.common.MultiChatUtil;
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

	public static boolean isValidJson(String json) {

		try {

			return new JsonParser().parse(json).getAsJsonObject() != null;

		} catch (Throwable ignored) {

			try {
				return new JsonParser().parse(json).getAsJsonArray() != null;
			} catch (Throwable ignored2) {
				return false;
			}

		}

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

		boolean json = isValidJson(message);
		if (!json) message = MultiChatUtil.reformatRGB(message);

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if (onlineplayer.getServer() == null) continue;

			if (config.getStringList(ConfigValues.Config.NO_GLOBAL).contains(
					onlineplayer.getServer().getInfo().getName())) continue;

			if (json) {
				onlineplayer.sendMessage(ComponentSerializer.parse(message));
			} else {
				if (MultiChat.legacyServers.contains(onlineplayer.getServer().getInfo().getName())) {
					onlineplayer.sendMessage(TextComponent.fromLegacyText(MultiChatUtil.approximateHexCodes(ChatColor.translateAlternateColorCodes('&', message))));
				} else {
					onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
				}
			}

		}

		// Trigger PostBroadcastEvent
		ProxyServer.getInstance().getPluginManager().callEvent(new PostBroadcastEvent("display", message));

		ConsoleManager.logDisplayMessage(message);

	}
}
