package xyz.olivermartin.multichat.spigotbridge.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import xyz.olivermartin.multichat.spigotbridge.MetaManager;
import xyz.olivermartin.multichat.spigotbridge.MultiChatSpigot;
import xyz.olivermartin.multichat.spigotbridge.SpigotCommunicationManager;
import xyz.olivermartin.multichat.spigotbridge.events.InducedAsyncPlayerChatEvent;

public class ChatListenerHighest implements Listener {

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onChat(final AsyncPlayerChatEvent event) {

		// IF ITS A MULTICHAT MESSAGE THEN ITS TIME TO UNCANCEL IT! (This is so plugins like DiscordSRV can grab it)
		if (event instanceof InducedAsyncPlayerChatEvent) {
			event.setCancelled(false);
			return;
		}

		// IF ITS ALREADY CANCELLED WE CAN IGNORE IT
		if (event.isCancelled()) return;

		// IF WE ARE MANAGING GLOBAL CHAT THEN WE NEED TO MANAGE IT!
		if (MultiChatSpigot.globalChatServer) {
			// Lets send Bungee the latest info!
			MetaManager.getInstance().updatePlayerMeta(event.getPlayer().getName(), MultiChatSpigot.setDisplayNameLastVal, MultiChatSpigot.displayNameFormatLastVal);
			event.setCancelled(true); //This is needed to stop the double message, but interferes with plugins like FactionsOne which for some reason use HIGHEST priority
			SpigotCommunicationManager.getInstance().sendPluginChatChannelMessage("multichat:chat", event.getPlayer().getUniqueId(), event.getMessage(), event.getFormat());
		}

	}

}
