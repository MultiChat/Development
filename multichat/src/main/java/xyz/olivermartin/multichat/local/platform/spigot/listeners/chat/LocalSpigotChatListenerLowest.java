package xyz.olivermartin.multichat.local.platform.spigot.listeners.chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.clip.placeholderapi.PlaceholderAPI;
import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.listeners.chat.LocalChatListenerLowest;
import xyz.olivermartin.multichat.local.listeners.chat.MultiChatLocalPlayerChatEvent;
import xyz.olivermartin.multichat.local.platform.spigot.hooks.LocalSpigotPAPIHook;

public class LocalSpigotChatListenerLowest extends LocalChatListenerLowest implements Listener {

	@EventHandler(priority=EventPriority.LOWEST)
	public void onChat(final AsyncPlayerChatEvent event) {

		// IF ITS ALREADY CANCELLED THEN WE CAN IGNORE IT!
		if (event.isCancelled()) return;

		MultiChatLocalPlayerChatEvent mcce = new MultiChatLocalSpigotPlayerChatEvent(event);

		handleChatMessage(mcce);

	}

	@Override
	protected String processExternalPlaceholders(MultiChatLocalPlayer player, String messageFormat) {

		// If we are hooked with PAPI then use their placeholders!
		if (LocalSpigotPAPIHook.getInstance().isHooked()) {
			messageFormat = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(player.getUniqueId()), messageFormat);
		}

		return messageFormat;

	}

	@Override
	protected String translateColourCodes(String format) {
		return ChatColor.translateAlternateColorCodes('&', format);
	}

}
