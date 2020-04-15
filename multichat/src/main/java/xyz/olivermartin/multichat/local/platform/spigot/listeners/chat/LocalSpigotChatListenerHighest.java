package xyz.olivermartin.multichat.local.platform.spigot.listeners.chat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import xyz.olivermartin.multichat.local.listeners.chat.LocalChatListenerHighest;
import xyz.olivermartin.multichat.local.listeners.chat.MultiChatLocalPlayerChatEvent;

public class LocalSpigotChatListenerHighest extends LocalChatListenerHighest implements Listener {

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onChat(final AsyncPlayerChatEvent event) {

		// IF ITS ALREADY CANCELLED WE CAN IGNORE IT
		if (event.isCancelled()) return;

		MultiChatLocalPlayerChatEvent mcce = new MultiChatLocalSpigotPlayerChatEvent(event);

		handleChatMessage(mcce);

	}

}
