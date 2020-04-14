package xyz.olivermartin.multichat.local.platform.spigot.listeners.chat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.config.LocalConfig;
import xyz.olivermartin.multichat.local.listeners.chat.LocalChatListenerMonitor;
import xyz.olivermartin.multichat.local.storage.LocalDataStore;

public class LocalSpigotChatListenerMonitor extends LocalChatListenerMonitor implements Listener {

	@EventHandler(priority=EventPriority.MONITOR)
	public void onChat(final AsyncPlayerChatEvent event) {

		

	}

}
