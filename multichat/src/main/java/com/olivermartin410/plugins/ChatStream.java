package com.olivermartin410.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ChatStream {

	private List<UUID> members;
	private boolean everyone;
	
	private List<String> servers;
	private boolean global;
	
	private String name;
	
	private String format;
	
	public ChatStream(String name, boolean everyone, boolean global, String format) {
		
		this.name = name;
		this.everyone = everyone;
		this.global = global;
		this.format = format;
		this.servers = new ArrayList<String>();
		this.members = new ArrayList<UUID>();
		
	}
	
	public void addServer(String server) throws ChatStreamException {
		
		if (!global) {
			servers.add(server);
		} else {
			throw new ChatStreamException("This group includes all servers by default, cannot add a server!");
		}
		
	}
	
	public void addMember(UUID member) throws ChatStreamException {
		
		if (!everyone) {
			members.add(member);
		} else {
			throw new ChatStreamException("This group includes everyone by default, cannot add a member!");
		}
	}

	public String getName() {
		return this.name;
	}

	public void sendMessage(ProxiedPlayer sender, String message) {
		
		for (ProxiedPlayer receiver : ProxyServer.getInstance().getPlayers()) {
			if (everyone || members.contains(receiver.getUniqueId())) {
				if (global || servers.contains(receiver.getServer().getInfo().getName())) {
					
					String formattedMessage;
					formattedMessage = buildFormat(sender,receiver,format,message);
					receiver.sendMessage(buildMessage(formattedMessage));
					//TODO chat stream control for people who have hidden/shown different streams
				}
			}
		}
		
	}
	
	public void sendMessage(String senderToken, String message) {
		//TODO Build this
	}
	
	public String buildFormat(ProxiedPlayer sender, ProxiedPlayer receiver, String format, String message) {
		
		String newFormat = format;
		
		newFormat = newFormat.replace("%MESSAGE%", message);
	    newFormat = newFormat.replace("%DISPLAYNAME%", sender.getDisplayName());
	    newFormat = newFormat.replace("%NAME%", sender.getName());
	    newFormat = newFormat.replace("%DISPLAYNAMET%", receiver.getDisplayName());
	    newFormat = newFormat.replace("%NAMET%", receiver.getName());
	    newFormat = newFormat.replace("%SERVER%", sender.getServer().getInfo().getName());
	    newFormat = newFormat.replace("%SERVERT%", receiver.getServer().getInfo().getName());
		
		return newFormat;
		
	}
	
	public BaseComponent[] buildMessage(String message) {
		//TODO BUILD THIS
		String url = "";
		return new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', message)).event(new ClickEvent(ClickEvent.Action.OPEN_URL, url)).create();
	}
}
