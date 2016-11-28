package com.olivermartin410.plugins;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.bukkit.scheduler.BukkitRunnable;

import net.milkbowl.vault.chat.Chat;

public class SpigotComm
  extends JavaPlugin
  implements PluginMessageListener, Listener
{
	
	public static Chat chat = null;
	public static boolean vault;
	
  public void onEnable()
  {
    getServer().getMessenger().registerOutgoingPluginChannel(this, "MultiChat");
    getServer().getMessenger().registerIncomingPluginChannel(this, "MultiChat", this);
    getServer().getPluginManager().registerEvents(this, this);
    vault = setupChat();
    if (vault) {
    	System.out.println("MultiChat has successfully connected to vault!");
    }
  }
  
  private boolean setupChat()
  {
      RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
      if (chatProvider != null) {
          chat = chatProvider.getProvider();
      }
      
      return (chat != null);
  }
  
  public void onDisable() {}
  
  public void sendMessage(String message, String playername)
  {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(stream);
    try
    {
      out.writeUTF(message);
      out.writeUTF(playername);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    ((PluginMessageRecipient)getServer().getOnlinePlayers().toArray()[0]).sendPluginMessage(this, "MultiChat", stream.toByteArray());
  }
  
  public void onPluginMessageReceived(String channel, Player player, byte[] bytes)
  {
    if (channel.equals("MultiChat"))
    {
      ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
      DataInputStream in = new DataInputStream(stream);
      try
      {
    	  	String playername = in.readUTF();
        	if (vault) {
        		if (Bukkit.getPlayer(playername).getDisplayName().contains(chat.getPlayerPrefix(Bukkit.getPlayer(playername)))) {
        			sendMessage(Bukkit.getPlayer(playername).getDisplayName(), playername);
        		} else {
        			sendMessage(chat.getPlayerPrefix(Bukkit.getPlayer(playername)) + Bukkit.getPlayer(playername).getName() + chat.getPlayerSuffix(Bukkit.getPlayer(playername)), playername);
        		}
        	} else {
        		sendMessage(Bukkit.getPlayer(playername).getDisplayName(), playername);
        	}
        }
      catch (IOException e)
      {
        System.out.println("[MultiChatBridge] Failed to contact bungeecord");
        
        e.printStackTrace();
      }
    }
  }
  
  @EventHandler
  public void onLogin(final PlayerJoinEvent event)
  {
    new BukkitRunnable()
    {
      public void run()
      {
    	  String playername = event.getPlayer().getName();
    	  if (vault) {
      		if (Bukkit.getPlayer(playername).getDisplayName().contains(chat.getPlayerPrefix(Bukkit.getPlayer(playername)))) {
      			sendMessage(Bukkit.getPlayer(playername).getDisplayName(), playername);
      		} else {
      			sendMessage(chat.getPlayerPrefix(Bukkit.getPlayer(playername)) + Bukkit.getPlayer(playername).getName() + chat.getPlayerSuffix(Bukkit.getPlayer(playername)), playername);
      		}
      	} else {
      		sendMessage(Bukkit.getPlayer(playername).getDisplayName(), playername);
      	}
        //SpigotComm.this.sendMessage(event.getPlayer().getDisplayName(), event.getPlayer().getName());
      
      }
    }
    
      .runTaskLater(this, 10L);
  }
  
}



