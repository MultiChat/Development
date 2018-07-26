package com.olivermartin410.plugins.commands;

import com.olivermartin410.plugins.MultiChat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class GroupListCommand
  extends Command
{
  public GroupListCommand()
  {
    super("groups", "multichat.staff.listgroups", new String[0]);
  }
   
  public void execute(CommandSender sender, String[] args)
  {
    sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&a&lGroup List:")).create());
    for (String groupname : MultiChat.groupchats.keySet()) {
      sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b- " + groupname)).create());
    }
  }
}

