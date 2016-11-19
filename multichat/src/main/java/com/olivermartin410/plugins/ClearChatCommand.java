package com.olivermartin410.plugins;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ClearChatCommand extends Command {

	static String[] aliases = {"chatclear","wipechat","killchat"};
	  
	  public ClearChatCommand()
	  {
	    super("clearchat", "multichat.chat.clear", aliases);
	  }
	  
	  public void execute(CommandSender sender, String[] args)
	  {
		  
		  if (args.length < 1)
		    {
		      for (int i = 1 ; i<151 ; i++ ) {
		    	  sender.sendMessage(new ComponentBuilder("").create());
		      }
		      sender.sendMessage(new ComponentBuilder("- Chat Cleared -").color(ChatColor.AQUA).create());
		    }
		    else
		    {
		    	if (args.length == 1) {
		    		if (args[0].toLowerCase().equals("self")) {
		    			for (int i = 1 ; i<151 ; i++ ) {
		  		    	  sender.sendMessage(new ComponentBuilder("").create());
		  		      }
		  		      sender.sendMessage(new ComponentBuilder("- Chat Cleared -").color(ChatColor.AQUA).create());
		    		} else if (args[0].toLowerCase().equals("all") ){
		    			if (sender.hasPermission("multichat.chat.clear.all")) {
		    				for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
		    					for (int i = 1 ; i<151 ; i++ ) {
		  		  		    	  onlineplayer.sendMessage(new ComponentBuilder("").create());
		  		  		      }
		  		  		      onlineplayer.sendMessage(new ComponentBuilder("- All Chat Cleared -").color(ChatColor.AQUA).create());
		    				}
		    			}
		    		} else if (args[0].toLowerCase().equals("server") ){
		    			if (sender.hasPermission("multichat.chat.clear.server")) {
		    				for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
		    					if (onlineplayer.getServer().getInfo().getName() == ((ProxiedPlayer)sender).getServer().getInfo().getName() ) {
		    					for (int i = 1 ; i<151 ; i++ ) {
		  		  		    	  onlineplayer.sendMessage(new ComponentBuilder("").create());
		    					}
		  		  		      	onlineplayer.sendMessage(new ComponentBuilder("- Server Chat Cleared -").color(ChatColor.AQUA).create());
		    					}
		    				}
		    			}
		    		} else if (args[0].toLowerCase().equals("global") ){
		    			if (sender.hasPermission("multichat.chat.clear.global")) {
		    				for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
		    					if (!MultiChat.configman.config.getStringList("no_global").contains(onlineplayer.getServer().getInfo().getName()) ) {
		    					for (int i = 1 ; i<151 ; i++ ) {
		  		  		    	  onlineplayer.sendMessage(new ComponentBuilder("").create());
		    					}
		  		  		      	onlineplayer.sendMessage(new ComponentBuilder("- Global Chat Cleared -").color(ChatColor.AQUA).create());
		    					}
		    				}
		    			}
		    		}
		    	} else {
		    		sender.sendMessage(new ComponentBuilder("Usage: /clearchat [self/server/global/all]").color(ChatColor.RED).create());
		    	}
		    }
		  
	  }
	
}
