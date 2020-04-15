package xyz.olivermartin.multichat.local.listeners.chat;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.config.LocalConfig;
import xyz.olivermartin.multichat.local.storage.LocalDataStore;

public abstract class LocalChatListenerMonitor {

	protected void handleChatMessage(MultiChatLocalPlayerChatEvent event) {

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Okay less fun here, we are just the monitor...");

		String queueValue = "";
		LocalDataStore store = MultiChatLocal.getInstance().getDataStore();
		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();

		if (store.chatQueues.containsKey(event.getPlayer().getName().toLowerCase())) {

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - They are in a chat queue... Let's handle that hack...");

			// Hack for /global /local direct messaging...
			String tempChannel = store.chatQueues.get(event.getPlayer().getName().toLowerCase()).poll();

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - (We have also now removed their message from that chat queue)");

			if (store.chatQueues.get(event.getPlayer().getName().toLowerCase()).size() < 1) {
				store.chatQueues.remove(event.getPlayer().getName().toLowerCase());
			}

			if(tempChannel.startsWith("!SINGLE L MESSAGE!")) {
				queueValue = "local";
			} else {
				queueValue = "global";
			}

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Queue value was: " + queueValue);
		}

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - If the message is cancelled, then we will end here...");

		// IF ITS ALREADY CANCELLED WE CAN IGNORE IT
		if (event.isCancelled()) return;

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - The message isn't cancelled!");

		// IF ITS LOCAL CHAT WE CAN IGNORE IT
		if (store.playerChannels.containsKey(event.getPlayer().getUniqueId())) {

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - The player is in the channel map!");

			if (!store.globalChatServer) {
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - We aren't a global chat server, so do not need to do this. Bye!");
				return;
			}

			if (!queueValue.equals("")) {
				// Hack for /global /local direct messaging...
				if (queueValue.equalsIgnoreCase("local")) {
					MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - It was for local chat... so returning...");
					return;
				}
			} else if (store.playerChannels.get(event.getPlayer().getUniqueId()).equals("local") || (!store.globalChatServer)) {
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - It was for local chat... so returning...");
				return;
			}
		} else {

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Player was not in the channel map, that isn't good!");

		}

		// IF WE ARE MANAGING GLOBAL CHAT THEN WE NEED TO MANAGE IT!
		if (store.globalChatServer) {

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - We are in global chat... SO TIME TO FORWARD TO PROXY!");

			// Lets send Bungee the latest info!
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - First we are sending their meta data...");
			MultiChatLocal.getInstance().getProxyCommunicationManager().updatePlayerMeta(event.getPlayer().getUniqueId());
			// event.setCancelled(true); // Needed to stop double message
			if (!config.isOverrideAllMultiChatFormatting()) {
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - We were managing the format...");
				String toSendFormat;
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Currently it is " + event.getFormat());
				toSendFormat = event.getFormat().replace("%1$s", MultiChatLocal.getInstance().getMetaManager().getDisplayName(event.getPlayer().getUniqueId()));
				toSendFormat = toSendFormat.replace("%2$s", "");

				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - We replaced the special bits to get: " + toSendFormat);

				MultiChatLocal.getInstance().getProxyCommunicationManager().sendChatMessage(event.getPlayer().getUniqueId(), event.getMessage(), toSendFormat);

				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Aaaaand we sent it to the proxy! ALL DONE.");
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - UUID: " + event.getPlayer().getUniqueId());
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - MESSAGE (please note this will be shown in colour here even if the player doesn't have colour permissions): " + event.getMessage());
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - FORMAT: " + toSendFormat);
			} else {
				// Lets try and apply the other plugins formats correctly...
				// THIS IS DONE ON A BEST EFFORT BASIS!
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Oh dear... we need to send it to the proxy... but we weren't managing the chat...");
				String format = event.getFormat();
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - The format currently is: " + format);
				format = format.replace("%1$s", MultiChatLocal.getInstance().getMetaManager().getDisplayName(event.getPlayer().getUniqueId()));
				format = format.replace("%2$s", "");
				format = format.replaceFirst("\\$s", MultiChatLocal.getInstance().getMetaManager().getDisplayName(event.getPlayer().getUniqueId()));
				format = format.replaceFirst("\\$s", "");

				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - But we worked some magic to arrive at... " + format);
				MultiChatLocal.getInstance().getProxyCommunicationManager().sendChatMessage(event.getPlayer().getUniqueId(), event.getMessage(), format);
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Aaaaand we sent it to the proxy! ALL DONE.");
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - UUID: " + event.getPlayer().getUniqueId());
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - MESSAGE (please note this will be shown in colour here even if the player doesn't have colour permissions): " + event.getMessage());
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - FORMAT: " + format);

			}
		}

	}

}
