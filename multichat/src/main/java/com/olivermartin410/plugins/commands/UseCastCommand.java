package com.olivermartin410.plugins.commands;

import com.olivermartin410.plugins.CastControl;
import com.olivermartin410.plugins.MultiChat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class UseCastCommand extends Command {

	public UseCastCommand() {
		super("usecast", "multichat.cast.admin", new String[0]);
	}

	public void displayUsage(CommandSender sender) {
		sender.sendMessage(new ComponentBuilder("Usage:").color(ChatColor.GREEN).create());
		sender.sendMessage(new ComponentBuilder("/usecast <name> <message>").color(ChatColor.AQUA).create());
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (args.length < 2) {
			displayUsage(sender);
			return;
		}
		
		if (CastControl.existsCast(args[0])) {
			
			boolean starter = false;
			String Message = "";
			for (String part : args) {
				if (!starter) {
					starter = true;
				} else {
					Message = Message + part + " ";
				}
			}
			
			CastControl.sendCast(args[0],Message,MultiChat.globalChat);
			
		} else {
			sender.sendMessage(new ComponentBuilder("Sorry, no such cast found: " + args[0].toUpperCase()).color(ChatColor.RED).create());
			return;
		}
		
	}
	
}
