package com.olivermartin410.plugins;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LocalCommand
  extends Command
{
  public LocalCommand()
  {
    super("local", "multichat.chat.mode", new String[0]);
  }
   
  public void execute(CommandSender sender, String[] args)
  {
    if ((sender instanceof ProxiedPlayer))
    {
      MultiChat.globalplayers.remove(((ProxiedPlayer)sender).getUniqueId());
      MultiChat.globalplayers.put(((ProxiedPlayer)sender).getUniqueId(), Boolean.valueOf(false));
      sender.sendMessage(new ComponentBuilder("LOCAL CHAT ENABLED").color(ChatColor.DARK_AQUA).create());
      sender.sendMessage(new ComponentBuilder("You will only see messages from players on the same server!").color(ChatColor.AQUA).create());
    }
    else
    {
      sender.sendMessage(new ComponentBuilder("Only players can change their chat state").color(ChatColor.RED).create());
    }
  }
}
