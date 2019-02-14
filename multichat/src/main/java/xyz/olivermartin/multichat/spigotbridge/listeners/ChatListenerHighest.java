package xyz.olivermartin.multichat.spigotbridge.listeners;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.clip.placeholderapi.PlaceholderAPI;
import xyz.olivermartin.multichat.spigotbridge.MetaManager;
import xyz.olivermartin.multichat.spigotbridge.MultiChatSpigot;
import xyz.olivermartin.multichat.spigotbridge.PseudoChannel;
import xyz.olivermartin.multichat.spigotbridge.SpigotCommunicationManager;
import xyz.olivermartin.multichat.spigotbridge.SpigotPlaceholderManager;

public class ChatListenerHighest implements Listener {

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onChat(final AsyncPlayerChatEvent event) {

		// IF ITS ALREADY CANCELLED WE CAN IGNORE IT
		if (event.isCancelled()) return;

		// Deal with ignores and channel members
		if (MultiChatSpigot.playerChannels.containsKey(event.getPlayer())) {

			String channelName = MultiChatSpigot.playerChannels.get(event.getPlayer());

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
								System.out.println("Removed " + p.getName());

							}

						}

					} else {

						it.remove();
						System.out.println("Removed " + p.getName());

					}
				}

			}

		}

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

		// IF WE ARE MANAGING GLOBAL CHAT THEN WE NEED TO MANAGE IT!
		if (MultiChatSpigot.globalChatServer) {
			// Lets send Bungee the latest info!
			MetaManager.getInstance().updatePlayerMeta(event.getPlayer().getName(), MultiChatSpigot.setDisplayNameLastVal, MultiChatSpigot.displayNameFormatLastVal);
			// event.setCancelled(true); // Needed to stop double message
			if (!MultiChatSpigot.overrideAllMultiChatFormats) {
				String toSendFormat;
				toSendFormat = event.getFormat().replace("%1$s", event.getPlayer().getDisplayName());
				toSendFormat = toSendFormat.replace("%2$s", "");
				SpigotCommunicationManager.getInstance().sendPluginChatChannelMessage("multichat:chat", event.getPlayer().getUniqueId(), event.getMessage(), toSendFormat);
			} else {
				// Lets try and apply the other plugins formats correctly...
				// THIS IS DONE ON A BEST EFFORT BASIS!
				String format = event.getFormat();
				format = format.replace("%1$s", event.getPlayer().getDisplayName());
				format = format.replace("%2$s", "");
				format = format.replaceFirst("\\$s", event.getPlayer().getDisplayName());
				format = format.replaceFirst("\\$s", "");
				SpigotCommunicationManager.getInstance().sendPluginChatChannelMessage("multichat:chat", event.getPlayer().getUniqueId(), event.getMessage(), format);
			}
		}

	}

}
