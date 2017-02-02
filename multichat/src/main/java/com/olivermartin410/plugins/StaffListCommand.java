package com.olivermartin410.plugins;

import java.util.Iterator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class StaffListCommand
  extends Command
{
  public StaffListCommand()
  {
    super("staff", "multichat.staff.list", new String[0]);
  }
  
  public void execute(CommandSender sender, String[] args)
  {
	  
	  String server;
    sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&a&lOnline Staff")).create());
    for (Iterator<String> localIterator1 = ProxyServer.getInstance().getServers().keySet().iterator(); localIterator1.hasNext();)
    {
      server = (String)localIterator1.next();
      
      sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&a" + server)).create());
      for (ProxiedPlayer onlineplayer2 : ProxyServer.getInstance().getPlayers()) {
        if ((onlineplayer2.hasPermission("multichat.staff"))) {
          if (onlineplayer2.getServer().getInfo().getName().equals(server))
          {
            if (MultiChat.configman.config.getBoolean("fetch_spigot_display_names") == true) {
              BungeeComm.sendMessage(onlineplayer2.getName(), onlineplayer2.getServer().getInfo());
            }
            sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b- " + onlineplayer2.getDisplayName())).create());
          }
        }
      }
    }
  }
}

