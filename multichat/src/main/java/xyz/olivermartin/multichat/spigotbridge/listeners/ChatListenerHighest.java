package xyz.olivermartin.multichat.spigotbridge.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.clip.placeholderapi.PlaceholderAPI;
import xyz.olivermartin.multichat.spigotbridge.MetaManager;
import xyz.olivermartin.multichat.spigotbridge.MultiChatSpigot;
import xyz.olivermartin.multichat.spigotbridge.SpigotCommunicationManager;
import xyz.olivermartin.multichat.spigotbridge.SpigotPlaceholderManager;
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

		if (MultiChatSpigot.playerChannels.containsKey(event.getPlayer())) {

			if (MultiChatSpigot.playerChannels.get(event.getPlayer()).equals("local")) {
				// If its a local chat message then we dont need to do anything else!
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

		boolean local = false;
		String playerList = "";

		// If a plugin has edited the number of participants for the message then we dont want to broadcast it to everyone
		if ( (!MultiChatSpigot.broadcastEditedRecipients) && (event.getRecipients().size() != Bukkit.getServer().getOnlinePlayers().size()) ) {
			local = true;
			for (Player p : event.getRecipients()) {
				if (playerList.equals("")) {
					playerList = playerList + p.getName();
				} else {
					playerList = playerList + " " + p.getName();
				}
			}
		}

		// IF WE ARE MANAGING GLOBAL CHAT THEN WE NEED TO MANAGE IT!
		if (MultiChatSpigot.globalChatServer) {
			// Lets send Bungee the latest info!
			MetaManager.getInstance().updatePlayerMeta(event.getPlayer().getName(), MultiChatSpigot.setDisplayNameLastVal, MultiChatSpigot.displayNameFormatLastVal);
			//TODO event.setCancelled(true); //This is needed to stop the double message, but interferes with plugins like FactionsOne which for some reason use HIGHEST priority
			if (!MultiChatSpigot.overrideAllMultiChatFormats) {
				String toSendFormat;
				toSendFormat = event.getFormat().replace("%1$s", event.getPlayer().getDisplayName());
				toSendFormat = toSendFormat.replace("%2$s", "");
				SpigotCommunicationManager.getInstance().sendPluginChatChannelMessage("multichat:chat", event.getPlayer().getUniqueId(), event.getMessage(), toSendFormat, local, playerList);
			} else {
				// Lets try and apply the other plugins formats correctly...
				// THIS IS DONE ON A BEST EFFORT BASIS!
				String format = event.getFormat();
				format = format.replace("%1$s", event.getPlayer().getDisplayName());
				format = format.replace("%2$s", "");
				format = format.replaceFirst("\\$s", event.getPlayer().getDisplayName());
				format = format.replaceFirst("\\$s", "");
				SpigotCommunicationManager.getInstance().sendPluginChatChannelMessage("multichat:chat", event.getPlayer().getUniqueId(), event.getMessage(), format, local, playerList);
			}
		}

	}

}
