package com.olivermartin410.plugins.commands;

import java.util.Iterator;
import java.util.Map;

import com.olivermartin410.plugins.Announcements;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class AnnouncementCommand extends Command {

	static String[] aliases = {"announce"};
	  
	  public AnnouncementCommand()
	  {
	    super("announcement", "multichat.announce", aliases);
	  }
	  
	  public void execute(CommandSender sender, String[] args)
	  {
		  
		  if (args.length < 1)
		    {
		      sender.sendMessage(new ComponentBuilder("Usage:").color(ChatColor.GREEN).create());
		      sender.sendMessage(new ComponentBuilder("/announcement add <name> <message>").color(ChatColor.AQUA).create());
		      sender.sendMessage(new ComponentBuilder("/announcement remove <name>").color(ChatColor.AQUA).create());
		      sender.sendMessage(new ComponentBuilder("/announcement start <name> <interval in minutes>").color(ChatColor.AQUA).create());
		      sender.sendMessage(new ComponentBuilder("/announcement stop <name>").color(ChatColor.AQUA).create());
		      sender.sendMessage(new ComponentBuilder("/announcement list").color(ChatColor.AQUA).create());
		      sender.sendMessage(new ComponentBuilder("/announce <name>").color(ChatColor.AQUA).create());
		    }
		    else if (args.length == 1) 
		    {
		    	if (args[0].toLowerCase().equals("list")) {
		    		
		    		Map<String,String> announcementList = Announcements.getAnnouncementList();
		    		Iterator<String> it = announcementList.keySet().iterator();
		  	        sender.sendMessage(new ComponentBuilder("List of avaliable announcements:").color(ChatColor.GREEN).create());
	    			String currentItem;
		    		while (it.hasNext()) {
		    			currentItem = it.next();
		    			sender.sendMessage(new ComponentBuilder(currentItem + ": " + announcementList.get(currentItem)).color(ChatColor.AQUA).create());
		    		}
		    		
		    	} else if (Announcements.existsAnnouncemnt(args[0].toLowerCase())) {
		    		Announcements.playAnnouncement(args[0].toLowerCase());
		    	} else {
		    		sender.sendMessage(new ComponentBuilder("Sorry, no such announcement found: " + args[0].toUpperCase()).color(ChatColor.RED).create());
		    	}
		    }
		    else if (args.length == 2) {
		    	
		    	if (args[0].toLowerCase().equals("remove")) {
		    		
		    		if (Announcements.removeAnnouncement(args[1].toLowerCase()) == true) {
		    			sender.sendMessage(new ComponentBuilder("Removed announcement: " + args[1].toUpperCase()).color(ChatColor.GREEN).create());
		    		} else {
		    			sender.sendMessage(new ComponentBuilder("Sorry, no such announcement found: " + args[1].toUpperCase()).color(ChatColor.RED).create());
		    		}
		    		
		    	} else if (args[0].toLowerCase().equals("stop") ) {
		    		
		    		if (Announcements.stopAnnouncement(args[1].toLowerCase()) == true) {
		    			sender.sendMessage(new ComponentBuilder("Stopped announcement: " + args[1].toUpperCase()).color(ChatColor.GREEN).create());
		    		} else {
		    			sender.sendMessage(new ComponentBuilder("Sorry, unable to stop announcement: " + args[1].toUpperCase()).color(ChatColor.RED).create());
		    		}
		    		
		    	} else {
		    		
		    		sender.sendMessage(new ComponentBuilder("Usage:").color(ChatColor.GREEN).create());
				      sender.sendMessage(new ComponentBuilder("/announcement add <name> <message>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement remove <name>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement start <name> <interval in minutes>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement stop <name>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement list").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announce <name>").color(ChatColor.AQUA).create());
		    		
		    	}
		    	
		    }
		    else if (args.length == 3) {
		    	
		    	if (isInteger(args[2])) {
		    	
		    	if (args[0].toLowerCase().equals("start")) {
		    		
		    		if (Announcements.startAnnouncement(args[1].toLowerCase(), Integer.parseInt(args[2])) == true) {
		    			sender.sendMessage(new ComponentBuilder("Started announcement: " + args[1].toUpperCase()).color(ChatColor.GREEN).create());
		    		} else {
		    			sender.sendMessage(new ComponentBuilder("Sorry, unable to start announcement: " + args[1].toUpperCase()).color(ChatColor.RED).create());
		    		}
		    		
		    	} else {
		    		
		    		sender.sendMessage(new ComponentBuilder("Usage:").color(ChatColor.GREEN).create());
				      sender.sendMessage(new ComponentBuilder("/announcement add <name> <message>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement remove <name>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement start <name> <interval in minutes>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement stop <name>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement list").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announce <name>").color(ChatColor.AQUA).create());
		    		
		    	}
		    	
		    	} else if (args[0].toLowerCase().equals("add")) {
    			
    				if (Announcements.addAnnouncement(args[1].toLowerCase(), args[2]) == true) {
    					sender.sendMessage(new ComponentBuilder("Added announcement: " + args[1].toUpperCase()).color(ChatColor.GREEN).create());
    				} else {
    					sender.sendMessage(new ComponentBuilder("Sorry, announcement already exists: " + args[1].toUpperCase()).color(ChatColor.RED).create());
    				}
    		
    	} else {
		    		
		    		sender.sendMessage(new ComponentBuilder("Usage:").color(ChatColor.GREEN).create());
				      sender.sendMessage(new ComponentBuilder("/announcement add <name> <message>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement remove <name>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement start <name> <interval in minutes>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement stop <name>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement list").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announce <name>").color(ChatColor.AQUA).create());
		    		
		    	}
		    	
		    }
		    else if (args.length >= 3) {
		    	
		    	if (args[0].toLowerCase().equals("add")) {
		   			
		    				int counter = 0;
		    		        String Message = "";
		    		        for (String arg : args) {
		    		          if (!(counter == 2)) {
		    		            counter++;
		    		          } else {
		    		            Message = Message + arg + " ";
		    		          }
		    		        }
		    			
		    				if (Announcements.addAnnouncement(args[1].toLowerCase(), Message) == true) {
		    					sender.sendMessage(new ComponentBuilder("Added announcement: " + args[1].toUpperCase()).color(ChatColor.GREEN).create());
		    				} else {
		    					sender.sendMessage(new ComponentBuilder("Sorry, announcement already exists: " + args[1].toUpperCase()).color(ChatColor.RED).create());
		    				}
		    		
		    	} else {
		    		
		    		sender.sendMessage(new ComponentBuilder("Usage:").color(ChatColor.GREEN).create());
				      sender.sendMessage(new ComponentBuilder("/announcement add <name> <message>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement remove <name>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement start <name> <interval in minutes>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement stop <name>").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announcement list").color(ChatColor.AQUA).create());
				      sender.sendMessage(new ComponentBuilder("/announce <name>").color(ChatColor.AQUA).create());
		    		
		    	}
		    	
		    } else {
		    	
		    	sender.sendMessage(new ComponentBuilder("Usage:").color(ChatColor.GREEN).create());
			      sender.sendMessage(new ComponentBuilder("/announcement add <name> <message>").color(ChatColor.AQUA).create());
			      sender.sendMessage(new ComponentBuilder("/announcement remove <name>").color(ChatColor.AQUA).create());
			      sender.sendMessage(new ComponentBuilder("/announcement start <name> <interval in minutes>").color(ChatColor.AQUA).create());
			      sender.sendMessage(new ComponentBuilder("/announcement stop <name>").color(ChatColor.AQUA).create());
			      sender.sendMessage(new ComponentBuilder("/announcement list").color(ChatColor.AQUA).create());
			      sender.sendMessage(new ComponentBuilder("/announce <name>").color(ChatColor.AQUA).create());
		    	
		    }
		  
	  }
	  
	  public static boolean isInteger(String str)  
	  {  
	    try  
	    {  
	      @SuppressWarnings("unused")
		int n = Integer.parseInt(str);  
	    }  
	    catch(NumberFormatException nfe)  
	    {  
	      return false;  
	    }  
	    return true;  
	  }
	
}
