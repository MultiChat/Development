package com.olivermartin410.plugins.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * 'Help Me' Command
 * <p>Allows players to request help from all online staff members</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class HelpMeCommand extends Command {

	private static String[] aliases = new String[] {};

	public HelpMeCommand() {
		super("helpme", "multichat.chat.helpme", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		if ( sender instanceof ProxiedPlayer ) {

			if (args.length < 1) {

				sender.sendMessage(new ComponentBuilder("Request help from a staff member!").color(ChatColor.DARK_RED).create());
				sender.sendMessage(new ComponentBuilder("Usage: /HelpMe <Message>").color(ChatColor.RED).create());

			} else { 

				String message = "";
				for (String arg : args) {
					message = message + arg + " ";
				}

				sendMessage(sender.getName() + ": " + message);
				sender.sendMessage(new ComponentBuilder("Your request for help has been sent to all online staff :)").color(ChatColor.RED).create());

			}

		} else {
			sender.sendMessage(new ComponentBuilder("Only players can request help!").color(ChatColor.DARK_RED).create());
		}
	}

	public static void sendMessage(String message) {

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
			if (onlineplayer.hasPermission("multichat.staff")) {
				onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&c&l<< &4HELPME &c&l>> &f&o" + message)));
			}
		}

		System.out.println("\033[31m[MultiChat][HELPME] " + message);

	}
}
