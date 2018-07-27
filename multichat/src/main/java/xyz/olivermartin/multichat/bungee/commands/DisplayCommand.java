package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Display Command
 * <p>Displays a message to every player connected to the BungeeCord network</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class DisplayCommand extends Command {

	private static String[] aliases = new String[] {};

	public DisplayCommand() {
		super("display", "multichat.staff.display", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		if (args.length < 1) {

			sender.sendMessage(new ComponentBuilder("Display a message to the entire network").color(ChatColor.DARK_AQUA).create());
			sender.sendMessage(new ComponentBuilder("Usage /display <message>").color(ChatColor.AQUA).create());

		} else {

			String message = "";

			for (String arg : args) {
				message = message + arg + " ";
			}

			displayMessage(message);
		}
	}

	public static void displayMessage(String message) {

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
			onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
		}

		System.out.println("\033[33m[MultiChat][Display] " + message);
	}
}
