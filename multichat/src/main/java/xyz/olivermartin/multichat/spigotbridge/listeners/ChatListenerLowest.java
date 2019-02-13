package xyz.olivermartin.multichat.spigotbridge.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.clip.placeholderapi.PlaceholderAPI;
import xyz.olivermartin.multichat.spigotbridge.MultiChatSpigot;
import xyz.olivermartin.multichat.spigotbridge.SpigotPlaceholderManager;
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

		String format;
		Player p = event.getPlayer();
		String channel;

		if (MultiChatSpigot.playerChannels.containsKey(p)) {
			channel = MultiChatSpigot.playerChannels.get(p);
		} else {
			channel = "global";
		}


		if (channel.equals("local")) {

			// Local chat

			if (MultiChatSpigot.setLocalFormat) {

				format = MultiChatSpigot.localChatFormat;
				format = SpigotPlaceholderManager.replaceLocalChatPlaceholders(p, format);

			} else {
				return;
			}

		} else {

			// Global chat

			// If we aren't setting the format then we can leave now!
			if (MultiChatSpigot.overrideAllMultiChatFormats) return;

			if (!MultiChatSpigot.overrideGlobalFormat) {

				// If we aren't overriding then use the main global format
				format = MultiChatSpigot.globalChatFormat;

			} else {

				// Otherwise use the locally defined one in the config file
				format = MultiChatSpigot.overrideGlobalFormatFormat;

			}

		}

		// If we are hooked with PAPI then use their placeholders!
		if (MultiChatSpigot.hookedPAPI()) {
			format = PlaceholderAPI.setPlaceholders(event.getPlayer(), format);
		}

		// If we are a global chat server, then we want to set the format!
		if (MultiChatSpigot.globalChatServer) event.setFormat(ChatColor.translateAlternateColorCodes('&', format.replaceAll("%", "%%")));

	}

}
