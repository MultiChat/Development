package com.olivermartin410.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class Events
  implements Listener
{
  static List<UUID> MCToggle = new ArrayList<UUID>();
  static List<UUID> ACToggle = new ArrayList<UUID>();
  static List<UUID> GCToggle = new ArrayList<UUID>();
  public static Map<UUID, UUID> PMToggle = new HashMap<UUID, UUID>();
  
  public static boolean toggleMC(UUID uuid)
  {
    if (MCToggle.contains(uuid))
    {
      MCToggle.remove(uuid);
      return false;
    }
    if (ACToggle.contains(uuid)) {
      ACToggle.remove(uuid);
    }
    if (GCToggle.contains(uuid)) {
      GCToggle.remove(uuid);
    }
    if (PMToggle.containsKey(uuid)) {
      PMToggle.remove(uuid);
    }
    MCToggle.add(uuid);
    return true;
  }
  
  public static boolean toggleAC(UUID uuid)
  {
    if (ACToggle.contains(uuid))
    {
      ACToggle.remove(uuid);
      return false;
    }
    if (MCToggle.contains(uuid)) {
      MCToggle.remove(uuid);
    }
    if (GCToggle.contains(uuid)) {
      GCToggle.remove(uuid);
    }
    if (PMToggle.containsKey(uuid)) {
      PMToggle.remove(uuid);
    }
    ACToggle.add(uuid);
    return true;
  }
  
  public static boolean toggleGC(UUID uuid)
  {
    if (GCToggle.contains(uuid))
    {
      GCToggle.remove(uuid);
      return false;
    }
    if (MCToggle.contains(uuid)) {
      MCToggle.remove(uuid);
    }
    if (ACToggle.contains(uuid)) {
      ACToggle.remove(uuid);
    }
    if (PMToggle.containsKey(uuid)) {
      PMToggle.remove(uuid);
    }
    GCToggle.add(uuid);
    return true;
  }
  
  public static boolean togglePM(UUID uuid, UUID uuidt)
  {
    if (PMToggle.containsKey(uuid))
    {
      PMToggle.remove(uuid);
      return false;
    }
    if (MCToggle.contains(uuid)) {
      MCToggle.remove(uuid);
    }
    if (ACToggle.contains(uuid)) {
      ACToggle.remove(uuid);
    }
    if (GCToggle.contains(uuid)) {
      GCToggle.remove(uuid);
    }
    PMToggle.put(uuid, uuidt);
    return true;
  }
  
  @EventHandler(priority=64)
  public void onChat(ChatEvent event)
  {
    ProxiedPlayer player = (ProxiedPlayer)event.getSender();
    if (MCToggle.contains(player.getUniqueId()))
    {
      String Message = event.getMessage();
      if (!event.isCommand())
      {
        event.setCancelled(true);
        
        StaffChatManager chatman = new StaffChatManager();
        chatman.sendModMessage(player.getName(),player.getDisplayName(), player.getServer().getInfo().getName(), Message);
        chatman = null;
        //MCCommand.chatMessage(Message, player);
      }
    }
    if (ACToggle.contains(player.getUniqueId()))
    {
      String Message = event.getMessage();
      if (!event.isCommand())
      {
        event.setCancelled(true);
        
        StaffChatManager chatman = new StaffChatManager();
        chatman.sendAdminMessage(player.getName(),player.getDisplayName(), player.getServer().getInfo().getName(), Message);
        chatman = null;
        //ACCommand.chatMessage(Message, player);
      }
    }
    if (GCToggle.contains(player.getUniqueId()))
    {
      String Message = event.getMessage();
      if (!event.isCommand())
      {
        event.setCancelled(true);
        if (MultiChat.viewedchats.get(player.getUniqueId()) != null)
        {
          String chatname = ((String)MultiChat.viewedchats.get(player.getUniqueId())).toLowerCase();
          if (MultiChat.groupchats.containsKey(chatname))
          {
            TGroupChatInfo chatinfo = (TGroupChatInfo)MultiChat.groupchats.get(chatname);
            
            String PlayerName = player.getName();
            if ((chatinfo.getFormal() == true) && 
              (chatinfo.getAdmins().contains(player.getUniqueId()))) {
              PlayerName = "&o" + PlayerName;
            }
            GCCommand.chatMessage(Message, PlayerName, chatinfo);
          }
          else
          {
            player.sendMessage(new ComponentBuilder("You have toggled group chat but selected group doesn't exist!").color(ChatColor.RED).create());
            player.sendMessage(new ComponentBuilder("Please select the chat you wish to message using /group <group name> or disable the toggle with /gc").color(ChatColor.RED).create());
          }
        }
        else
        {
          player.sendMessage(new ComponentBuilder("You have toggled group chat but you have no group selected!").color(ChatColor.RED).create());
          player.sendMessage(new ComponentBuilder("Please select the chat you wish to message using /group <group name> or disable the toggle with /gc").color(ChatColor.RED).create());
        }
      }
    }
    String messageoutformat;
    if (PMToggle.containsKey(player.getUniqueId()))
    {
      String Message = event.getMessage();
      if (!event.isCommand())
      {
        event.setCancelled(true);
        
        ChatManipulation chatfix = new ChatManipulation();
        
        String URLBIT = chatfix.getURLBIT(event.getMessage());
        if (ProxyServer.getInstance().getPlayer((UUID)PMToggle.get(player.getUniqueId())) != null)
        {
          ProxiedPlayer target = ProxyServer.getInstance().getPlayer((UUID)PMToggle.get(player.getUniqueId()));
          
          BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());
          BungeeComm.sendMessage(target.getName(), target.getServer().getInfo());
          if (!MultiChat.configman.config.getStringList("no_pm").contains(player.getServer().getInfo().getName()))
          {
            if (!MultiChat.configman.config.getStringList("no_pm").contains(target.getServer().getInfo().getName()))
            {
              messageoutformat = MultiChat.configman.config.getString("pmout");
              String messageinformat = MultiChat.configman.config.getString("pmin");
              String messagespyformat = MultiChat.configman.config.getString("pmspy");
              
              String finalmessage = chatfix.replaceMsgVars(messageoutformat, Message, player, target);
              finalmessage = chatfix.FixFormatCodes(finalmessage);
              player.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', finalmessage)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
              
              finalmessage = chatfix.replaceMsgVars(messageinformat, Message, player, target);
              finalmessage = chatfix.FixFormatCodes(finalmessage);
              target.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', finalmessage)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
              
              finalmessage = chatfix.replaceMsgVars(messagespyformat, event.getMessage(), player, target);
              finalmessage = chatfix.FixFormatCodes(finalmessage);
              for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
                if ((onlineplayer.hasPermission("multichat.staff.spy")) && (MultiChat.socialspy.contains(onlineplayer.getUniqueId())) && 
                  (onlineplayer.getUniqueId() != player.getUniqueId()) && (onlineplayer.getUniqueId() != target.getUniqueId())) {
                  onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', finalmessage)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
                }
              }
              if (MultiChat.lastmsg.containsKey(player.getUniqueId())) {
                MultiChat.lastmsg.remove(player.getUniqueId());
              }
              MultiChat.lastmsg.put(player.getUniqueId(), target.getUniqueId());
              if (MultiChat.lastmsg.containsKey(target.getUniqueId())) {
                MultiChat.lastmsg.remove(target.getUniqueId());
              }
              MultiChat.lastmsg.put(target.getUniqueId(), player.getUniqueId());
              
              System.out.println("\033[31m[MultiChat] SOCIALSPY {" + player.getName() + " -> " + target.getName() + "}  " + event.getMessage());
            }
            else
            {
              player.sendMessage(new ComponentBuilder("Sorry private messages are disabled on the target player's server!").color(ChatColor.RED).create());
            }
          }
          else {
            player.sendMessage(new ComponentBuilder("Sorry private messages are disabled on this server!").color(ChatColor.RED).create());
          }
        }
        else
        {
          player.sendMessage(new ComponentBuilder("Sorry this player is not online!").color(ChatColor.RED).create());
        }
        chatfix = null;
      }
    }
    if ((!event.isCancelled()) && (!event.isCommand())) {
      if (MultiChat.configman.config.getBoolean("global") == true) {
        if (!MultiChat.configman.config.getStringList("no_global").contains(player.getServer().getInfo().getName()))
        {
          if (MultiChat.configman.config.getBoolean("fetch_spigot_display_names") == true) {
            BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());
          }
          if ((!MultiChat.frozen) || (player.hasPermission("multichat.chat.always")))
          {
            String Message = event.getMessage();
            
            ChatManipulation chatfix = new ChatManipulation();
            /*if ((player.hasPermission("multichat.chat.color")) || (player.hasPermission("multichat.chat.colour"))) {
              Message = chatfix.FixFormatCodes(Message);
            }*/
            String chatformat = MultiChat.configman.config.getString("globalformat");
            
            chatformat = chatfix.replaceChatVars(chatformat, player);
            
            String URLBIT = chatfix.getURLBIT(event.getMessage());
            
            if ((player.hasPermission("multichat.chat.color")) || (player.hasPermission("multichat.chat.colour"))) {
                Message = chatfix.FixFormatCodes(chatformat + Message);
              }
            
            chatfix = null;
            for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
              if (!MultiChat.configman.config.getStringList("no_global").contains(onlineplayer.getServer().getInfo().getName())) {
                if (((Boolean)MultiChat.globalplayers.get(onlineplayer.getUniqueId())).booleanValue() == true)
                {
                  if ((!player.hasPermission("multichat.chat.color")) && (!player.hasPermission("multichat.chat.colour"))) {
                    onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', chatformat) + Message).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
                  } else {
                    onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', Message)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
                  }
                }
                else if (player.getServer().getInfo().getName() == onlineplayer.getServer().getInfo().getName()) {
                  if ((!player.hasPermission("multichat.chat.color")) && (!player.hasPermission("multichat.chat.colour"))) {
                    onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', chatformat) + Message).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
                  } else {
                    onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', Message)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
                  }
                }
              }
            }
            System.out.println("\033[33m[MultiChat][CHAT]" + player.getName() + ": " + event.getMessage());
          }
          else
          {
            player.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bSorry chat has been &3&lFROZEN")).create());
          }
          event.setCancelled(true);
        }
      }
    }
  }
  
  @EventHandler
  public void onLogin(PostLoginEvent event)
  {
    ProxiedPlayer player = event.getPlayer();
    UUID uuid = player.getUniqueId();
    if (player.hasPermission("multichat.staff.mod")) {
      if (!MultiChat.modchatpreferences.containsKey(uuid))
      {
        TChatInfo chatinfo = new TChatInfo();
        chatinfo.setChatColor(MultiChat.configman.config.getString("modchat.ccdefault").toCharArray()[0]);
        chatinfo.setNameColor(MultiChat.configman.config.getString("modchat.ncdefault").toCharArray()[0]);
        
        MultiChat.modchatpreferences.put(uuid, chatinfo);
      }
    }
    if (player.hasPermission("multichat.staff.admin")) {
      if (!MultiChat.adminchatpreferences.containsKey(uuid))
      {
        TChatInfo chatinfo = new TChatInfo();
        chatinfo.setChatColor(MultiChat.configman.config.getString("adminchat.ccdefault").toCharArray()[0]);
        chatinfo.setNameColor(MultiChat.configman.config.getString("adminchat.ncdefault").toCharArray()[0]);
        
        MultiChat.adminchatpreferences.put(uuid, chatinfo);
      }
    }
    if (!MultiChat.viewedchats.containsKey(uuid))
    {
      MultiChat.viewedchats.put(uuid, null);
      
      System.out.println("[MultiChat] Registered player " + player.getName());
    }
    if (!MultiChat.globalplayers.containsKey(uuid))
    {
      MultiChat.globalplayers.put(uuid, Boolean.valueOf(true));
      
      System.out.println("[MultiChat] Created new global chat entry for " + player.getName());
    }
    if (UUIDNameManager.existsUUID(uuid)) {
      UUIDNameManager.removeUUID(uuid);
    }
    UUIDNameManager.addNew(uuid, player.getName());
    System.out.println("[MultiChat] Refresed UUID-Name lookup: " + uuid.toString());
    
    /*if ( MultiChat.jmconfigman.config.getBoolean("showjoin") == true ) {
    	
    	String joinformat = MultiChat.jmconfigman.config.getString("networkjoin");
    	ChatManipulation chatman = new ChatManipulation();
    	joinformat = chatman.replaceJoinMsgVars(joinformat, player.getName(), player.getDisplayName(), player.getServer().getInfo().getName());
    	
    	for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
    		if (!player.hasPermission("multichat.staff.silentjoin")) {
    			onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', joinformat)).create());
    		} else {
    			if (onlineplayer.hasPermission("multichat.staff.silentjoin") ) {
    				onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', joinformat)).create());
    			}
    		}
    	}
  }*/
    
    if ( MultiChat.jmconfigman.config.getBoolean("showjoin") == true ) {
      	
      	String joinformat = MultiChat.jmconfigman.config.getString("serverjoin");
      	String silentformat = MultiChat.jmconfigman.config.getString("silentjoin");
      	ChatManipulation chatman = new ChatManipulation();
      	joinformat = chatman.replaceJoinMsgVars(joinformat, player.getName());
      	silentformat = chatman.replaceJoinMsgVars(silentformat, player.getName());
      	
      	for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
      		if (!player.hasPermission("multichat.staff.silentjoin")) {
      			onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', joinformat)).create());
      		} else {
      			if (onlineplayer.hasPermission("multichat.staff.silentjoin") ) {
      				onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', silentformat)).create());
      			}
      		}
      	
	      	}
      		
      	}
    
    
  }
  
  @EventHandler
  public void onLogout(PlayerDisconnectEvent event)
  {
	ProxiedPlayer player = event.getPlayer();
    UUID uuid = event.getPlayer().getUniqueId();
    if (MCToggle.contains(uuid)) {
      MCToggle.remove(uuid);
    }
    if (ACToggle.contains(uuid)) {
      ACToggle.remove(uuid);
    }
    if (GCToggle.contains(uuid)) {
      GCToggle.remove(uuid);
    }
    if (MultiChat.viewedchats.containsKey(uuid))
    {
      MultiChat.viewedchats.remove(uuid);
      
      System.out.println("[MultiChat] Un-Registered player " + event.getPlayer().getName());
      
      if ( MultiChat.jmconfigman.config.getBoolean("showquit") == true ) {
	      	
	      	String joinformat = MultiChat.jmconfigman.config.getString("networkquit");
	      	String silentformat = MultiChat.jmconfigman.config.getString("silentquit");
	      	ChatManipulation chatman = new ChatManipulation();
	      	joinformat = chatman.replaceJoinMsgVars(joinformat, player.getName());
	      	silentformat = chatman.replaceJoinMsgVars(silentformat, player.getName());
	      	
	      	for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
	      		if (!player.hasPermission("multichat.staff.silentjoin")) {
	      			onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', joinformat)).create());
	      		} else {
	      			if (onlineplayer.hasPermission("multichat.staff.silentjoin") ) {
	      				onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', silentformat)).create());
	      			}
	      		}
	      	
		      	}
	      		
	      	}
	    }
      
    }
  
  
  /*@EventHandler
  public void onServerSwitch(ServerSwitchEvent event) {
	  
	  ProxiedPlayer player = event.getPlayer();
	  
	  if ( MultiChat.jmconfigman.config.getBoolean("showjoin") == true ) {
	      	
	      	String joinformat = MultiChat.jmconfigman.config.getString("serverjoin");
	      	String silentformat = MultiChat.jmconfigman.config.getString("silentjoin");
	      	ChatManipulation chatman = new ChatManipulation();
	      	joinformat = chatman.replaceJoinMsgVars(joinformat, player.getName(), player.getDisplayName(), player.getServer().getInfo().getName());
	      	silentformat = chatman.replaceJoinMsgVars(silentformat, player.getName(), player.getDisplayName(), player.getServer().getInfo().getName());
	      	
	      	if (MultiChat.jmconfigman.config.getBoolean("showglobal") == true ) {
	      	
	      	for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
	      		if (!player.hasPermission("multichat.staff.silentjoin")) {
	      			onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', joinformat)).create());
	      		} else {
	      			if (onlineplayer.hasPermission("multichat.staff.silentjoin") ) {
	      				onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', silentformat)).create());
	      			}
	      		}
	      	}
	      	
	      	} else {
	      		
	      		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
	      			if (player.getServer().getInfo().getName() == onlineplayer.getServer().getInfo().getName()) {
		      		if (!player.hasPermission("multichat.staff.silentjoin")) {
		      			onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', joinformat)).create());
		      		} else {
		      			if (onlineplayer.hasPermission("multichat.staff.silentjoin") ) {
		      				onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', silentformat)).create());
		      			}
		      		}
	      		}
		      	}
	      		
	      	}
	    } 
	  
  }*/
  
}

