package com.olivermartin410.plugins;

import java.util.HashSet;
import java.util.Set;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class MsgCommand
  extends Command implements TabExecutor
{
  static String[] aliases = (String[])MultiChat.configman.config.getStringList("msgcommand").toArray(new String[0]);
  
  public MsgCommand()
  {
    super("msg", "multichat.chat.msg", aliases);
  }
  
  public void execute(CommandSender sender, String[] args)
  {
    if (args.length < 1)
    {
      sender.sendMessage(new ComponentBuilder("Usage: /msg <player> [message]").color(ChatColor.AQUA).create());
      sender.sendMessage(new ComponentBuilder("Using /msg <player> with no message will toggle chat to go to that player").color(ChatColor.AQUA).create());
    }
    else
    {
      boolean toggleresult;
      if (args.length == 1)
      {
        if (ProxyServer.getInstance().getPlayer(args[0]) != null)
        {
          ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
          if ((sender instanceof ProxiedPlayer))
          {
            ProxiedPlayer player = (ProxiedPlayer)sender;
            
            toggleresult = Events.togglePM(player.getUniqueId(), target.getUniqueId());
            if (toggleresult == true) {
              sender.sendMessage(new ComponentBuilder("Private chat toggled on! [You -> " + target.getName() + "] (Type the same command to disable the toggle)").color(ChatColor.YELLOW).create());
            } else {
              sender.sendMessage(new ComponentBuilder("Private chat toggled off!").color(ChatColor.RED).create());
            }
          }
          else
          {
            sender.sendMessage(new ComponentBuilder("Only players can toggle the chat!").color(ChatColor.RED).create());
          }
        }
        else
        {
        	ProxiedPlayer player = (ProxiedPlayer)sender;
        	if ( Events.PMToggle.containsKey(player.getUniqueId())) {
        		Events.PMToggle.remove(player.getUniqueId());
        		sender.sendMessage(new ComponentBuilder("Private chat toggled off!").color(ChatColor.RED).create());
        	} else {
          sender.sendMessage(new ComponentBuilder("Sorry this person is not online!").color(ChatColor.RED).create());
        	}
        }
        	
      }
      else if ((sender instanceof ProxiedPlayer))
      {
        boolean starter = false;
        String Message = "";
        for (String arg : args) {
          if (!starter) {
            starter = true;
          } else {
            Message = Message + arg + " ";
          }
        }
        String OriginalMessage = Message;
        
        ChatManipulation chatfix = new ChatManipulation();
        
        String URLBIT = chatfix.getURLBIT(OriginalMessage);
        if (ProxyServer.getInstance().getPlayer(args[0]) != null)
        {
          ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
          if (MultiChat.configman.config.getBoolean("fetch_spigot_display_names") == true) {
        	  BungeeComm.sendMessage(sender.getName(), ((ProxiedPlayer)sender).getServer().getInfo());
              BungeeComm.sendMessage(target.getName(), target.getServer().getInfo());
           }
          
          if (!MultiChat.configman.config.getStringList("no_pm").contains(((ProxiedPlayer)sender).getServer().getInfo().getName()))
          {
            if (!MultiChat.configman.config.getStringList("no_pm").contains(target.getServer().getInfo().getName()))
            {
              String messageoutformat = MultiChat.configman.config.getString("pmout");
              String messageinformat = MultiChat.configman.config.getString("pmin");
              String messagespyformat = MultiChat.configman.config.getString("pmspy");
              
              String finalmessage = chatfix.replaceMsgVars(messageoutformat, Message, (ProxiedPlayer)sender, target);
              finalmessage = chatfix.FixFormatCodes(finalmessage);
              sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', finalmessage)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
              
              finalmessage = chatfix.replaceMsgVars(messageinformat, Message, (ProxiedPlayer)sender, target);
              finalmessage = chatfix.FixFormatCodes(finalmessage);
              target.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', finalmessage)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
              
              finalmessage = chatfix.replaceMsgVars(messagespyformat, OriginalMessage, (ProxiedPlayer)sender, target);
              finalmessage = chatfix.FixFormatCodes(finalmessage);
              for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
                if ((onlineplayer.hasPermission("multichat.staff.spy")) && (MultiChat.socialspy.contains(onlineplayer.getUniqueId())) && 
                  (onlineplayer.getUniqueId() != ((ProxiedPlayer)sender).getUniqueId()) && (onlineplayer.getUniqueId() != target.getUniqueId())) {
                  onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', finalmessage)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
                }
              }
              if (MultiChat.lastmsg.containsKey(((ProxiedPlayer)sender).getUniqueId())) {
                MultiChat.lastmsg.remove(((ProxiedPlayer)sender).getUniqueId());
              }
              MultiChat.lastmsg.put(((ProxiedPlayer)sender).getUniqueId(), target.getUniqueId());
              if (MultiChat.lastmsg.containsKey(target.getUniqueId())) {
                MultiChat.lastmsg.remove(target.getUniqueId());
              }
              MultiChat.lastmsg.put(target.getUniqueId(), ((ProxiedPlayer)sender).getUniqueId());
              
              System.out.println("\033[31m[MultiChat] SOCIALSPY {" + sender.getName() + " -> " + target.getName() + "}  " + OriginalMessage);
            }
            else
            {
              sender.sendMessage(new ComponentBuilder("Sorry private messages are disabled on the target player's server!").color(ChatColor.RED).create());
            }
          }
          else {
            sender.sendMessage(new ComponentBuilder("Sorry private messages are disabled on this server!").color(ChatColor.RED).create());
          }
        }
        else
        {
          sender.sendMessage(new ComponentBuilder("Sorry this player is not online!").color(ChatColor.RED).create());
        }
        chatfix = null;
      }
      else
      {
        sender.sendMessage(new ComponentBuilder("Only players can send private messages").color(ChatColor.RED).create());
      }
    }
  }
  
  @Override
  public Iterable<String> onTabComplete(CommandSender sender, String[] args)
      {
	  	
	  Set<String> matches = new HashSet<>();
	          if ( args.length == 1 )
	          {
	              String search = args[0].toLowerCase();
	              for ( ProxiedPlayer player : ProxyServer.getInstance().getPlayers() )
	              {
	                  if ( player.getName().toLowerCase().startsWith( search ) )
	                  {
	                      matches.add( player.getName() );
	                  }
	              }
	          }
	          
	          return matches;
      }
  
}

