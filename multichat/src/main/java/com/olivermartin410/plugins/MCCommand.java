package com.olivermartin410.plugins;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MCCommand
  extends Command
{
  public MCCommand()
  {
    super("mc", "multichat.staff.mod", new String[0]);
  }
  
  public void execute(CommandSender sender, String[] args)
  {
    boolean toggleresult;
    if (args.length < 1)
    {
      if ((sender instanceof ProxiedPlayer))
      {
        ProxiedPlayer player = (ProxiedPlayer)sender;
        
        toggleresult = Events.toggleMC(player.getUniqueId());
        if (toggleresult == true) {
          sender.sendMessage(new ComponentBuilder("Mod chat toggled on!").color(ChatColor.AQUA).create());
        } else {
          sender.sendMessage(new ComponentBuilder("Mod chat toggled off!").color(ChatColor.RED).create());
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
      chatman.sendModMessage(player.getName(), player.getDisplayName(), player.getServer().getInfo().getName(), Message);
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
        chatman.sendModMessage("CONSOLE", "CONSOLE", "#", Message);
        //chatMessage(Message, (ProxiedPlayer)sender);
        chatman = null;
    }
  }
}
  
  /*public static void chatMessage(String Message, ProxiedPlayer player)
  {
    String OriginalMessage = Message;
    
    ChatManipulation chatfix = new ChatManipulation();
    
    String URLBIT = chatfix.getURLBIT(OriginalMessage);
    
    String MessageFormat = MultiChat.configman.config.getString("modchat.format");
    for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
      if (onlineplayer.hasPermission("multichat.staff.mod"))
      {
        Message = chatfix.replaceModChatVars(MessageFormat, player, OriginalMessage, onlineplayer);
        String finalmessage = chatfix.FixFormatCodes(Message);
        
        onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', finalmessage)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
      }
    }
    System.out.println("\033[36m[StaffChat] /mc {" + player.getName() + "}  " + OriginalMessage);
  }
}*/
