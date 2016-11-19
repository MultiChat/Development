package com.olivermartin410.plugins;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class FreezeChatCommand
  extends Command
{
  public FreezeChatCommand()
  {
    super("freezechat", "multichat.chat.freeze", new String[0]);
  }
  
  public void execute(CommandSender sender, String[] args)
  {
    if (MultiChat.frozen == true)
    {
      for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
        onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b&lChat was &3&lTHAWED &b&lby &a&l" + sender.getName())).create());
      }
      MultiChat.frozen = false;
    }
    else
    {
      for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
        onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b&lChat was &3&lFROZEN &b&lby &a&l" + sender.getName())).create());
      }
      MultiChat.frozen = true;
    }
  }
}

