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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.bukkit.scheduler.BukkitRunnable;

public class SpigotComm
  extends JavaPlugin
  implements PluginMessageListener, Listener
{
  public void onEnable()
  {
    getServer().getMessenger().registerOutgoingPluginChannel(this, "MultiChat");
    getServer().getMessenger().registerIncomingPluginChannel(this, "MultiChat", this);
    getServer().getPluginManager().registerEvents(this, this);
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
  
  public void sendStaffMessage(String message)
  {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(stream);
    try
    {
      out.writeUTF(message);
    }
    catch (IOException e)
    {
      System.out.println("[MultiChatBridge] Failed to contact bungeecord");
      e.printStackTrace();
    }
    ((PluginMessageRecipient)getServer().getOnlinePlayers().toArray()[0]).sendPluginMessage(this, "MultiChatStaff", stream.toByteArray());
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
        sendMessage(Bukkit.getPlayer(playername).getDisplayName(), playername);
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
  	//event.setJoinMessage(null);
    new BukkitRunnable()
    {
      public void run()
      {

        SpigotComm.this.sendMessage(event.getPlayer().getDisplayName(), event.getPlayer().getName());
      }
    }
    
      .runTaskLater(this, 10L);
  }
  
}
  
  /*@EventHandler
  public void onQuit(final PlayerQuitEvent event)
  {
  	event.setQuitMessage(null);
  }
  
}*/



