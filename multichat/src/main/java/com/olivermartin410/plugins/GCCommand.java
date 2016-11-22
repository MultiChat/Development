package com.olivermartin410.plugins;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GCCommand
  extends Command
{
  public GCCommand()
  {
    super("gc", "multichat.group", new String[0]);
  }
   
  public void execute(CommandSender sender, String[] args)
  {
    if (args.length < 1)
    {
      if ((sender instanceof ProxiedPlayer))
      {
        ProxiedPlayer player = (ProxiedPlayer)sender;
        
        boolean toggleresult = Events.toggleGC(player.getUniqueId());
        if (toggleresult == true) {
          sender.sendMessage(new ComponentBuilder("Group chat toggled on!").color(ChatColor.GREEN).create());
        } else {
          sender.sendMessage(new ComponentBuilder("Group chat toggled off!").color(ChatColor.RED).create());
        }
      }
      else
      {
        sender.sendMessage(new ComponentBuilder("Only players can toggle the chat!").color(ChatColor.RED).create());
      }
    }
    else if ((sender instanceof ProxiedPlayer))
    {
      ProxiedPlayer player = (ProxiedPlayer)sender;
      if (MultiChat.viewedchats.get(player.getUniqueId()) != null)
      {
        String partyname = (String)MultiChat.viewedchats.get(player.getUniqueId());
        if (MultiChat.groupchats.containsKey(partyname))
        {
          TGroupChatInfo partyinfo = (TGroupChatInfo)MultiChat.groupchats.get(partyname);
          
          String Message = "";
          for (String arg : args) {
            Message = Message + arg + " ";
          }
          String PlayerName = sender.getName();
          if ((partyinfo.getFormal() == true) && 
            (partyinfo.getAdmins().contains(player.getUniqueId()))) {
            PlayerName = "&o" + PlayerName;
          }
          chatMessage(Message, PlayerName, partyinfo);
        }
        else
        {
          sender.sendMessage(new ComponentBuilder("Sorry your selected chat no longer exists, please select a chat with /group <group name>").color(ChatColor.RED).create());
        }
      }
      else
      {
        sender.sendMessage(new ComponentBuilder("Please select the chat you wish to message using /group <group name>").color(ChatColor.RED).create());
      }
    }
    else
    {
      sender.sendMessage(new ComponentBuilder("Only players can speak in group chats").color(ChatColor.RED).create());
    }
  }
  
  public static void chatMessage(String Message, String PlayerName, TGroupChatInfo partyinfo)
  {
    String OriginalMessage = Message;
    
    ChatManipulation chatfix = new ChatManipulation();
    
    String URLBIT = chatfix.getURLBIT(OriginalMessage);
    
    String MessageFormat = MultiChat.configman.config.getString("groupchat.format");
    
    Message = chatfix.replaceGroupChatVars(MessageFormat, PlayerName, Message, partyinfo.getName());
    
    String finalmessage = chatfix.FixFormatCodes(Message);
    for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
      if (((partyinfo.existsViewer(onlineplayer.getUniqueId())) && (onlineplayer.hasPermission("multichat.group"))) || ((MultiChat.allspy.contains(onlineplayer.getUniqueId())) && (onlineplayer.hasPermission("multichat.staff.spy")))) {
        onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', finalmessage)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
      }
    }
    String partyname = partyinfo.getName();
    
    System.out.println("\033[32m[MultiChat] /gc {" + partyname.toUpperCase() + "} {" + PlayerName + "}  " + OriginalMessage);
  }
}

