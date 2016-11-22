package com.olivermartin410.plugins;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HelpMeCommand extends Command {

	public HelpMeCommand()
	  {
	    super("helpme", "multichat.chat.helpme", new String[0]);
	  }
	  
	  public void execute(CommandSender sender, String[] args)
	  {
		 
		  if ( sender instanceof ProxiedPlayer ) {
		  if (args.length < 1)
		    {
		      sender.sendMessage(new ComponentBuilder("Request help from a staff member!").color(ChatColor.DARK_RED).create());
		      sender.sendMessage(new ComponentBuilder("Usage: /HelpMe <Message>").color(ChatColor.RED).create());
		    }
		    else
		    { 
		      String Message = "";
		      for (String arg : args) {
		        Message = Message + arg + " ";
		      }
		      
		      chatMessage(sender.getName() + ": " + Message);
		    
		    }
		  } else {
			  
			  sender.sendMessage(new ComponentBuilder("Only players can request help!").color(ChatColor.DARK_RED).create());
			  
		  }
		  
	  }
	  
	  public static void chatMessage(String Message)
	  {
	    ChatManipulation chatfix = new ChatManipulation();
	    
	    String OriginalMessage = Message;
	    
	    String URLBIT = chatfix.getURLBIT(OriginalMessage);
	    for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
	    	if (onlineplayer.hasPermission("multichat.staff")) {
	    		onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&c&l<< &4HELPME &c&l>> &f&o" + Message)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
	    	}
	    }
	    System.out.println("\033[31m[MultiChat][HELPME] " + OriginalMessage);
	  }
	
}
