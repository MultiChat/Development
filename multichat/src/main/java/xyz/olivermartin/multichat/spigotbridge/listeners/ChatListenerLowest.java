package xyz.olivermartin.multichat.spigotbridge.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.clip.placeholderapi.PlaceholderAPI;
import xyz.olivermartin.multichat.spigotbridge.MultiChatSpigot;
import xyz.olivermartin.multichat.spigotbridge.events.InducedAsyncPlayerChatEvent;

public class ChatListenerLowest implements Listener {

	@EventHandler(priority=EventPriority.LOWEST)
	public void onChat2(final AsyncPlayerChatEvent event) {

		// IF ITS ALREADY CANCELLED THEN WE CAN IGNORE IT!
		if (event.isCancelled()) return;

		if (event instanceof InducedAsyncPlayerChatEvent) {
			// IF IT IS A MULTICHAT MESSAGE THEN CANCEL IT AND RETURN!
			event.setCancelled(true);
			return;
		}

		// IF WE ARE MANAGING GLOBAL CHAT THEN SET THE FORMAT!
		String format;

		if (MultiChatSpigot.hookedPAPI()) {
			format = PlaceholderAPI.setPlaceholders(event.getPlayer(), MultiChatSpigot.globalChatFormat);
		} else {
			format = MultiChatSpigot.globalChatFormat;
		}

		if (MultiChatSpigot.globalChatServer) event.setFormat(ChatColor.translateAlternateColorCodes('&', format.replaceAll("%", "%%")));
		//if (globalChatServer) event.setFormat(ChatColor.translateAlternateColorCodes('&', globalChatFormat.replaceAll("%", "%%").replace("%%DISPLAYNAME%%","%s")) + "%s");

	}

}
