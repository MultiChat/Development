package com.olivermartin410.plugins;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ACCommand extends Command {

	  public ACCommand()
	  {
	    super("ac", "multichat.staff.admin", new String[0]);
	  }
	  
	  public void execute(CommandSender sender, String[] args)
	  {
	    boolean toggleresult;
	    if (args.length < 1)
	    {
	      if ((sender instanceof ProxiedPlayer))
	      {
	        ProxiedPlayer player = (ProxiedPlayer)sender;
	        
	        toggleresult = Events.toggleAC(player.getUniqueId());
	        if (toggleresult == true) {
	          sender.sendMessage(new ComponentBuilder("Admin chat toggled on!").color(ChatColor.LIGHT_PURPLE).create());
	        } else {
	          sender.sendMessage(new ComponentBuilder("Admin chat toggled off!").color(ChatColor.RED).create());
	        }
	      }
	      else
	      {
	        sender.sendMessage(new ComponentBuilder("Only players can toggle the chat!").color(ChatColor.RED).create());
	      }
	    }
	    else if ((sender instanceof ProxiedPlayer))
	    {
	    	String Message = "";
	        for (String arg : args) {
	          Message = Message + arg + " ";
	        }
	        ProxiedPlayer player = (ProxiedPlayer)sender;
	        StaffChatManager chatman = new StaffChatManager();
	        chatman.sendAdminMessage(player.getName(), player.getDisplayName(), player.getServer().getInfo().getName(), Message);
	        //chatMessage(Message, (ProxiedPlayer)sender);
	        chatman = null;
	    }
	    else
	    {
	    	String Message = "";
	        for (String arg : args) {
	          Message = Message + arg + " ";
	        }
	        StaffChatManager chatman = new StaffChatManager();
	        chatman.sendAdminMessage("CONSOLE", "CONSOLE", "#", Message);
	        //chatMessage(Message, (ProxiedPlayer)sender);
	        chatman = null;
	    }
	  }
	  
	  /*public static void chatMessage(String Message, ProxiedPlayer player)
	  {
	    String OriginalMessage = Message;
	    
	    ChatManipulation chatfix = new ChatManipulation();
	    
	    String URLBIT = chatfix.getURLBIT(OriginalMessage);
	    
	    String MessageFormat = MultiChat.configman.config.getString("adminchat.format");
	    for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
	      if (onlineplayer.hasPermission("multichat.staff.admin"))
	      {
	        Message = chatfix.replaceAdminChatVars(MessageFormat, player, OriginalMessage, onlineplayer);
	        String finalmessage = chatfix.FixFormatCodes(Message);
	        
	        onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', finalmessage)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
	      }
	    }
	    System.out.println("\033[35m[StaffChat] /ac {" + player.getName() + "}  " + OriginalMessage);
	  }*/
	
}
