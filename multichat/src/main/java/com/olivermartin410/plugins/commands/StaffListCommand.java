package com.olivermartin410.plugins.commands;

import java.util.Iterator;

import com.olivermartin410.plugins.BungeeComm;
import com.olivermartin410.plugins.MultiChat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Staff List Command
 * <p>Allows the user to view a list of all online staff, sorted by their server</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class StaffListCommand extends Command {

	private static String[] aliases = new String[] {};

	public StaffListCommand() {
		super("staff", "multichat.staff.list", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		String server;

		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&a&lOnline Staff")));

		for (Iterator<String> localIterator1 = ProxyServer.getInstance().getServers().keySet().iterator(); localIterator1.hasNext();) {

			server = (String)localIterator1.next();
			sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&a" + server)));

			for (ProxiedPlayer onlineplayer2 : ProxyServer.getInstance().getPlayers()) {

				if ((onlineplayer2.hasPermission("multichat.staff"))) {

					if (onlineplayer2.getServer().getInfo().getName().equals(server)) {

						if (MultiChat.configman.config.getBoolean("fetch_spigot_display_names") == true) {
							BungeeComm.sendMessage(onlineplayer2.getName(), onlineplayer2.getServer().getInfo());
						}

						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b- " + onlineplayer2.getDisplayName())).create());

					}
				}
			}
		}
	}
}
