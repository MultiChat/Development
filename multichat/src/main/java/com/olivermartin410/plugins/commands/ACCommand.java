package com.olivermartin410.plugins.commands;

import com.olivermartin410.plugins.Events;
import com.olivermartin410.plugins.StaffChatManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Admin-Chat command
 * <p>Allows the user to toggle / send a message to admin-chat</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ACCommand extends Command {

	private static String[] aliases = new String[] {};
	
	public ACCommand() {
		super("ac", "multichat.staff.admin", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		boolean toggleresult;

		if (args.length < 1) {

			if ((sender instanceof ProxiedPlayer)) {

				ProxiedPlayer player = (ProxiedPlayer)sender;
				toggleresult = Events.toggleAC(player.getUniqueId());

				if (toggleresult == true) {
					sender.sendMessage(new ComponentBuilder("Admin chat toggled on!").color(ChatColor.LIGHT_PURPLE).create());
				} else {
					sender.sendMessage(new ComponentBuilder("Admin chat toggled off!").color(ChatColor.RED).create());
				}

			} else {

				sender.sendMessage(new ComponentBuilder("Only players can toggle the chat!").color(ChatColor.RED).create());

			}

		} else if ((sender instanceof ProxiedPlayer)) {

			String message = "";
			for (String arg : args) {
				message = message + arg + " ";
			}

			ProxiedPlayer player = (ProxiedPlayer)sender;
			StaffChatManager chatman = new StaffChatManager();

			chatman.sendAdminMessage(player.getName(), player.getDisplayName(), player.getServer().getInfo().getName(), message);
			chatman = null;

		} else {

			String message = "";
			for (String arg : args) {
				message = message + arg + " ";
			}

			StaffChatManager chatman = new StaffChatManager();
			chatman.sendAdminMessage("CONSOLE", "CONSOLE", "#", message);
			chatman = null;

		}
	}
}
