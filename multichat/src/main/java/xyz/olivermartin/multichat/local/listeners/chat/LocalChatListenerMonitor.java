package xyz.olivermartin.multichat.local.listeners.chat;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.config.LocalConfig;
import xyz.olivermartin.multichat.local.storage.LocalDataStore;

public abstract class LocalChatListenerMonitor {

	protected void handleChatMessage(MultiChatLocalPlayerChatEvent event) {

		String queueValue = "";
		LocalDataStore store = MultiChatLocal.getInstance().getDataStore();
		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();

		if (store.chatQueues.containsKey(event.getPlayer().getName().toLowerCase())) {
			// Hack for /global /local direct messaging...
			String tempChannel = store.chatQueues.get(event.getPlayer().getName().toLowerCase()).poll();

			if (store.chatQueues.get(event.getPlayer().getName().toLowerCase()).size() < 1) {
				store.chatQueues.remove(event.getPlayer().getName().toLowerCase());
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
		if (store.playerChannels.containsKey(event.getPlayer().getUniqueId())) {

			if (!store.globalChatServer) {
				return;
			}

			if (!queueValue.equals("")) {
				// Hack for /global /local direct messaging...
				if (queueValue.equalsIgnoreCase("local")) {
					return;
				}
			} else if (store.playerChannels.get(event.getPlayer().getUniqueId()).equals("local") || (!store.globalChatServer)) {
				return;
			}
		}

		// IF WE ARE MANAGING GLOBAL CHAT THEN WE NEED TO MANAGE IT!
		if (store.globalChatServer) {
			// Lets send Bungee the latest info!
			MultiChatLocal.getInstance().getProxyCommunicationManager().updatePlayerMeta(event.getPlayer().getUniqueId());
			// event.setCancelled(true); // Needed to stop double message
			if (!config.isOverrideAllMultiChatFormatting()) {
				String toSendFormat;
				toSendFormat = event.getFormat().replace("%1$s", MultiChatLocal.getInstance().getMetaManager().getDisplayName(event.getPlayer().getUniqueId()));
				toSendFormat = toSendFormat.replace("%2$s", "");
				MultiChatLocal.getInstance().getProxyCommunicationManager().sendChatMessage(event.getPlayer().getUniqueId(), event.getMessage(), toSendFormat);
			} else {
				// Lets try and apply the other plugins formats correctly...
				// THIS IS DONE ON A BEST EFFORT BASIS!
				String format = event.getFormat();
				format = format.replace("%1$s", MultiChatLocal.getInstance().getMetaManager().getDisplayName(event.getPlayer().getUniqueId()));
				format = format.replace("%2$s", "");
				format = format.replaceFirst("\\$s", MultiChatLocal.getInstance().getMetaManager().getDisplayName(event.getPlayer().getUniqueId()));
				format = format.replaceFirst("\\$s", "");
				MultiChatLocal.getInstance().getProxyCommunicationManager().sendChatMessage(event.getPlayer().getUniqueId(), event.getMessage(), format);
			}
		}

	}

}
