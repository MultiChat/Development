package com.olivermartin410.plugins;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GlobalCommand
  extends Command
{
  public GlobalCommand()
  {
    super("global", "multichat.chat.mode", new String[0]);
  }
  
  public void execute(CommandSender sender, String[] args)
  {
    if ((sender instanceof ProxiedPlayer))
    {
      MultiChat.globalplayers.remove(((ProxiedPlayer)sender).getUniqueId());
      MultiChat.globalplayers.put(((ProxiedPlayer)sender).getUniqueId(), Boolean.valueOf(true));
      sender.sendMessage(new ComponentBuilder("GLOBAL CHAT ENABLED").color(ChatColor.DARK_AQUA).create());
      sender.sendMessage(new ComponentBuilder("You will see messages from players on all servers!").color(ChatColor.AQUA).create());
    }
    else
    {
      sender.sendMessage(new ComponentBuilder("Only players can change their chat state").color(ChatColor.RED).create());
    }
  }
}

