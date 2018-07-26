package com.olivermartin410.plugins.commands;

import java.util.Iterator;

import com.olivermartin410.plugins.Bulletins;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class BulletinCommand extends Command {

	static String[] aliases = {"bulletins"};
	 
	public BulletinCommand() {
		super("bulletin", "multichat.bulletin", aliases);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		
		
		if (args.length < 1)
	    {
	      sender.sendMessage(new ComponentBuilder("Usage:").color(ChatColor.GREEN).create());
	      sender.sendMessage(new ComponentBuilder("/bulletin add <message>").color(ChatColor.AQUA).create());
	      sender.sendMessage(new ComponentBuilder("/bulletin remove <index>").color(ChatColor.AQUA).create());
	      sender.sendMessage(new ComponentBuilder("/bulletin start <interval in minutes>").color(ChatColor.AQUA).create());
	      sender.sendMessage(new ComponentBuilder("/bulletin stop").color(ChatColor.AQUA).create());
	      sender.sendMessage(new ComponentBuilder("/bulletin list").color(ChatColor.AQUA).create());
	    }
	    else if (args.length == 1) 
	    {
	    	if (args[0].toLowerCase().equals("stop")) {
	    		Bulletins.stopBulletins();
	    		sender.sendMessage(new ComponentBuilder("Bulletins stopped").color(ChatColor.AQUA).create());
	    	}
	    	else if (args[0].toLowerCase().equals("list")) {
	    		int counter = 0;
	    		Iterator<String> it = Bulletins.getIterator();
	  	      sender.sendMessage(new ComponentBuilder("List of bulletin messages with index:").color(ChatColor.GREEN).create());
	    		while (it.hasNext()) {
	    			counter++;
	    			sender.sendMessage(new ComponentBuilder(counter + ": " + it.next()).color(ChatColor.AQUA).create());
	    		}
	    	} else {
	    		sender.sendMessage(new ComponentBuilder("Usage:").color(ChatColor.GREEN).create());
			      sender.sendMessage(new ComponentBuilder("/bulletin add <message>").color(ChatColor.AQUA).create());
			      sender.sendMessage(new ComponentBuilder("/bulletin remove <index>").color(ChatColor.AQUA).create());
			      sender.sendMessage(new ComponentBuilder("/bulletin start <interval in minutes>").color(ChatColor.AQUA).create());
			      sender.sendMessage(new ComponentBuilder("/bulletin stop").color(ChatColor.AQUA).create());
			      sender.sendMessage(new ComponentBuilder("/bulletin list").color(ChatColor.AQUA).create());
	    	}
	    	
	    }
	    else if (args.length == 2) {
	    	
	    	if (args[0].toLowerCase().equals("remove")) {
	    		
	    		try {
	    			Bulletins.removeBulletin(Integer.parseInt(args[1]) - 1);
	    			sender.sendMessage(new ComponentBuilder("Removed bulletin").color(ChatColor.AQUA).create());
	    		} catch (Exception e) {
	    			sender.sendMessage(new ComponentBuilder("Invalid command usage!").color(ChatColor.RED).create());
	    		}
	    	} else if (args[0].toLowerCase().equals("start") ) {
	    		try {
	    			Bulletins.startBulletins(Integer.parseInt(args[1]));
	    			sender.sendMessage(new ComponentBuilder("Started bulletin").color(ChatColor.AQUA).create());
	    		} catch (Exception e) {
	    			sender.sendMessage(new ComponentBuilder("Invalid command usage!").color(ChatColor.RED).create());
	    		}
	    		
	    	} else if (args[0].toLowerCase().equals("add") ) {
	    		
	    		Bulletins.addBulletin(args[1]);
	    		sender.sendMessage(new ComponentBuilder("Added to bulletin").color(ChatColor.AQUA).create());
	    		
	    	} else {
	    		
	  	      sender.sendMessage(new ComponentBuilder("Usage:").color(ChatColor.GREEN).create());
		      sender.sendMessage(new ComponentBuilder("/bulletin add <message>").color(ChatColor.AQUA).create());
		      sender.sendMessage(new ComponentBuilder("/bulletin remove <index>").color(ChatColor.AQUA).create());
		      sender.sendMessage(new ComponentBuilder("/bulletin start <interval in minutes>").color(ChatColor.AQUA).create());
		      sender.sendMessage(new ComponentBuilder("/bulletin stop").color(ChatColor.AQUA).create());
		      sender.sendMessage(new ComponentBuilder("/bulletin list").color(ChatColor.AQUA).create());
	    		
	    	}
	    
	    }
	    else if (args.length > 2) {
	    	
	    	if (args[0].toLowerCase().equals("add")) {
	   			
	    				int counter = 0;
	    		        String Message = "";
	    		        for (String arg : args) {
	    		          if (!(counter == 1)) {
	    		            counter++;
	    		          } else {
	    		            Message = Message + arg + " ";
	    		          }
	    		        }
	    			
	    		        Bulletins.addBulletin(Message);
	    		        sender.sendMessage(new ComponentBuilder("Added to bulletin").color(ChatColor.AQUA).create());
	    	}
	    	
	    } else {
	    	
		      sender.sendMessage(new ComponentBuilder("Usage:").color(ChatColor.GREEN).create());
		      sender.sendMessage(new ComponentBuilder("/bulletin add <message>").color(ChatColor.AQUA).create());
		      sender.sendMessage(new ComponentBuilder("/bulletin remove <index>").color(ChatColor.AQUA).create());
		      sender.sendMessage(new ComponentBuilder("/bulletin start <interval in minutes>").color(ChatColor.AQUA).create());
		      sender.sendMessage(new ComponentBuilder("/bulletin stop").color(ChatColor.AQUA).create());
		      sender.sendMessage(new ComponentBuilder("/bulletin list").color(ChatColor.AQUA).create());
	    	
	    }
		
		
	}
	
}