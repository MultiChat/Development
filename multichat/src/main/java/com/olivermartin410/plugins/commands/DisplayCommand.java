package com.olivermartin410.plugins.commands;

import com.olivermartin410.plugins.ChatManipulation;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class DisplayCommand
  extends Command
{
  public DisplayCommand()
  {
    super("display", "multichat.staff.display", new String[0]);
  }
  
  public void execute(CommandSender sender, String[] args)
  {
    if (args.length < 1)
    {
      sender.sendMessage(new ComponentBuilder("Display a message to the entire network").color(ChatColor.DARK_AQUA).create());
      sender.sendMessage(new ComponentBuilder("Usage /Display <Message>").color(ChatColor.AQUA).create());
    }
    else
    {
      String Message = "";
      for (String arg : args) {
        Message = Message + arg + " ";
      }
      chatMessage(Message);
    }
  }
  
  public static void chatMessage(String Message)
  {
    ChatManipulation chatfix = new ChatManipulation();
    
    String OriginalMessage = Message;
    
    Message = chatfix.FixFormatCodes(Message);
    
    String URLBIT = chatfix.getURLBIT(OriginalMessage);
    for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
      onlineplayer.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&f" + Message)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, URLBIT)).create());
    }
    System.out.println("\033[33m[MultiChat][Display] " + OriginalMessage);
  }
}
