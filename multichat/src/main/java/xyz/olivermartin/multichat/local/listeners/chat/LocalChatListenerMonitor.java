package xyz.olivermartin.multichat.local.listeners.chat;

import xyz.olivermartin.multichat.local.LocalChatManager;
import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.config.LocalConfig;

public abstract class LocalChatListenerMonitor {

	protected void handleChatMessage(MultiChatLocalPlayerChatEvent event) {

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Okay less fun here, we are just the monitor...");

		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();
		LocalChatManager chatManager = MultiChatLocal.getInstance().getChatManager();

		String channel = chatManager.pollChatChannel(event.getPlayer());

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - If the message is cancelled, then we will end here...");

		// IF ITS ALREADY CANCELLED WE CAN IGNORE IT
		if (event.isCancelled()) return;

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - The message isn't cancelled!");

		// IF ITS LOCAL CHAT WE CAN IGNORE IT
		if (!chatManager.isGlobalChatServer() || channel.equalsIgnoreCase("local")) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - We are speaking into local chat, so at this point we are returning! Bye!");
			return;
		}

		// IF WE ARE MANAGING GLOBAL CHAT THEN WE NEED TO MANAGE IT!

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - We are in global chat... SO TIME TO FORWARD TO PROXY!");

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - First we are sending their meta data...");
		MultiChatLocal.getInstance().getProxyCommunicationManager().updatePlayerMeta(event.getPlayer().getUniqueId());

		if (!config.isOverrideAllMultiChatFormatting()) {

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - We were managing the format...");

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Currently it is " + event.getFormat());

			String toSendFormat;
			toSendFormat = event.getFormat().replace("%1$s", MultiChatLocal.getInstance().getMetaManager().getDisplayName(event.getPlayer().getUniqueId()));
			toSendFormat = toSendFormat.replace("%2$s", "");

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - We replaced the special bits to get: " + toSendFormat);

			MultiChatLocal.getInstance().getProxyCommunicationManager().sendChatMessage(event.getPlayer().getUniqueId(), event.getMessage(), toSendFormat);

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Aaaaand we sent it to the proxy! ALL DONE.");
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - UUID: " + event.getPlayer().getUniqueId());
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - MESSAGE (please note this will be shown in colour here even if the player doesn't have colour permissions): " + event.getMessage());
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - FORMAT: " + toSendFormat);

		} else {

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
