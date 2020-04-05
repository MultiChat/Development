package xyz.olivermartin.multichat.spigotbridge.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import xyz.olivermartin.multichat.spigotbridge.MetaManager;
import xyz.olivermartin.multichat.spigotbridge.MultiChatSpigot;
import xyz.olivermartin.multichat.spigotbridge.SpigotCommunicationManager;

public class ChatListenerMonitor implements Listener {

	@EventHandler(priority=EventPriority.MONITOR)
	public void onChat(final AsyncPlayerChatEvent event) {

		String queueValue = "";

		if (MultiChatSpigot.chatQueues.containsKey(event.getPlayer().getName().toLowerCase())) {
			// Hack for /global /local direct messaging...
			String tempChannel = MultiChatSpigot.chatQueues.get(event.getPlayer().getName().toLowerCase()).poll();

			if (MultiChatSpigot.chatQueues.get(event.getPlayer().getName().toLowerCase()).size() < 1) {
				MultiChatSpigot.chatQueues.remove(event.getPlayer().getName().toLowerCase());
			}

			if(tempChannel.startsWith("!SINGLE L MESSAGE!")) {
				queueValue = "local";
			} else {
				queueValue = "global";
			}
		}

		// IF ITS ALREADY CANCELLED WE CAN IGNORE IT
		if (event.isCancelled()) return;


		// IF ITS LOCAL CHAT WE CAN IGNORE IT
		if (MultiChatSpigot.playerChannels.containsKey(event.getPlayer())) {

			if (!MultiChatSpigot.globalChatServer) {
				return;
			}

			if (!queueValue.equals("")) {
				// Hack for /global /local direct messaging...
				if (queueValue.equalsIgnoreCase("local")) {
					return;
				}
			} else if (MultiChatSpigot.playerChannels.get(event.getPlayer()).equals("local") || (!MultiChatSpigot.globalChatServer)) {
				return;
			}
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
