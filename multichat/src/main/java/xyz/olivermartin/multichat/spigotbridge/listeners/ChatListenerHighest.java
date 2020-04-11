package xyz.olivermartin.multichat.spigotbridge.listeners;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.clip.placeholderapi.PlaceholderAPI;
import xyz.olivermartin.multichat.spigotbridge.MultiChatSpigot;
import xyz.olivermartin.multichat.spigotbridge.PseudoChannel;
import xyz.olivermartin.multichat.spigotbridge.SpigotPlaceholderManager;

public class ChatListenerHighest implements Listener {

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onChat(final AsyncPlayerChatEvent event) {

		// IF ITS ALREADY CANCELLED WE CAN IGNORE IT
		if (event.isCancelled()) return;

		// Deal with coloured chat
		if (MultiChatSpigot.colourMap.containsKey(event.getPlayer().getUniqueId())) {
			
			System.out.println("Player in colour map.."); //TODO REMOVE

			boolean colour = MultiChatSpigot.colourMap.get(event.getPlayer().getUniqueId());

			if (colour) {
				System.out.println("They have the colour permission! Translating!"); // TODO REMOVE
				event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
			}

		}
		
		Bukkit.getConsoleSender().sendMessage("Event currently is... " + event.getMessage()); // TODO REMOVE!

		synchronized (MultiChatSpigot.placeholderMap) {
			for (String key : MultiChatSpigot.placeholderMap.keySet()) {
				
				String value = MultiChatSpigot.placeholderMap.get(key);
				value = SpigotPlaceholderManager.buildMultiChatPlaceholder(event.getPlayer(), value);
				
				// If we are hooked with PAPI then use their placeholders!
				if (MultiChatSpigot.hookedPAPI()) {
					value = PlaceholderAPI.setPlaceholders(event.getPlayer(), value);
				}
				
				if (event.getFormat().contains(key)) {
					event.setFormat(event.getFormat().replace(key, value));
				}
			}
		}

		// Deal with ignores and channel members
		if (MultiChatSpigot.playerChannels.containsKey(event.getPlayer())) {

			String channelName = MultiChatSpigot.playerChannels.get(event.getPlayer());

			// HACK for /local <message> and /global<message>
			
			if (MultiChatSpigot.chatQueues.containsKey(event.getPlayer().getName().toLowerCase())) {
				String tempChannel = MultiChatSpigot.chatQueues.get(event.getPlayer().getName().toLowerCase()).peek();
				channelName = tempChannel.startsWith("!SINGLE L MESSAGE!") ? "local" : "global";
			}
			
			// END HACK
			
			if (MultiChatSpigot.channelObjects.containsKey(channelName)) {

				PseudoChannel channelObject = MultiChatSpigot.channelObjects.get(channelName);

				Set<UUID> ignoredPlayers;

				Iterator<Player> it = event.getRecipients().iterator();

				while (it.hasNext()) {

					Player p = it.next();

					ignoredPlayers = MultiChatSpigot.ignoreMap.get(p.getUniqueId());

					if ( (channelObject.whitelistMembers && channelObject.members.contains(p.getUniqueId())) || (!channelObject.whitelistMembers && !channelObject.members.contains(p.getUniqueId()))) {

						// Then this player is okay!
						if (ignoredPlayers != null) {

							if (ignoredPlayers.contains(event.getPlayer().getUniqueId())) {

								it.remove();

							}

						}

					} else {

						it.remove();

					}
				}

			}

		}

		if (MultiChatSpigot.playerChannels.containsKey(event.getPlayer())) {
			
			if (!MultiChatSpigot.globalChatServer) {
				return;
			}
			
			if (MultiChatSpigot.chatQueues.containsKey(event.getPlayer().getName().toLowerCase())) {
				// Hack for /global /local direct messaging...
				String tempChannel = MultiChatSpigot.chatQueues.get(event.getPlayer().getName().toLowerCase()).peek();
				if(tempChannel.startsWith("!SINGLE L MESSAGE!")) {
					return;
				}
			} else if (MultiChatSpigot.playerChannels.get(event.getPlayer()).equals("local") || (!MultiChatSpigot.globalChatServer)) {

				// If its a local chat message (or we can't use global chat here) then we dont need to do anything else!
				return;
			}

		}

		if (MultiChatSpigot.forceMultiChatFormat) {

			String format;

			if (!MultiChatSpigot.overrideGlobalFormat) {

				// If we aren't overriding then use the main global format
				format = MultiChatSpigot.globalChatFormat;

			} else {

				// Otherwise use the locally defined one in the config file
				format = MultiChatSpigot.overrideGlobalFormatFormat;

			}

			// Build chat format
			format = SpigotPlaceholderManager.buildChatFormat(event.getPlayer(), format);

			// If we are hooked with PAPI then use their placeholders!
			if (MultiChatSpigot.hookedPAPI()) {
				format = PlaceholderAPI.setPlaceholders(event.getPlayer(), format);
			}

			format = format.replace("%1$s", "!!!1!!!");
			format = format.replace("%2$s", "!!!2!!!");

			format = format.replace("%", "%%");

			format = format.replace("!!!1!!!", "%1$s");
			format = format.replace("!!!2!!!", "%2$s");

			// If we are a global chat server, then we want to set the format!
			if (MultiChatSpigot.globalChatServer) event.setFormat(ChatColor.translateAlternateColorCodes('&', format));

		}

	}

}
