package com.olivermartin410.plugins.commands;

import com.olivermartin410.plugins.MultiChat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Freeze Chat Command
 * <p>Allows staff members to block all chat messages being sent</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class FreezeChatCommand extends Command {

	private static String[] aliases = new String[] {};

	public FreezeChatCommand() {
		super("freezechat", "multichat.chat.freeze", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		if (MultiChat.frozen == true) {

			for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
				onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b&lChat was &3&lTHAWED &b&lby &a&l" + sender.getName())).create());
			}

			MultiChat.frozen = false;

		} else {

			for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
				onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b&lChat was &3&lFROZEN &b&lby &a&l" + sender.getName())).create());
			}

			MultiChat.frozen = true;
		}
	}
}
