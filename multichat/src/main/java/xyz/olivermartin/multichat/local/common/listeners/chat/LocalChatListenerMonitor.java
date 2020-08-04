package xyz.olivermartin.multichat.local.common.listeners.chat;

import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.common.config.LocalConfig;

public abstract class LocalChatListenerMonitor {

	public void handleChat(MultiChatLocalPlayerChatEvent event) {

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Okay less fun here, we are just the monitor...");

		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();
		LocalChatManager chatManager = MultiChatLocal.getInstance().getChatManager();

		Set<UUID> originalRecipients = chatManager.getRecipientsFromRecipientQueue(event.getPlayer().getUniqueId());

		String channel = chatManager.pollChatChannel(event.getPlayer());

		// Deal with regex channel forcing...
		channel = chatManager.getRegexForcedChannel(channel, event.getFormat());

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - If the message is cancelled, then we will end here...");

		// IF ITS ALREADY CANCELLED WE CAN IGNORE IT
		if (event.isCancelled()) return;

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - The message isn't cancelled!");

		// IF WE ARE MANAGING GLOBAL CHAT THEN WE NEED TO MANAGE IT!

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - TIME TO FORWARD TO PROXY!");

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - First we are sending their meta data...");
		MultiChatLocal.getInstance().getProxyCommunicationManager().updatePlayerMeta(event.getPlayer().getUniqueId());

		String proxyFormat = event.getFormat();
		String proxyMessage = event.getMessage();

		if (MultiChatLocal.getInstance().getPlatform() == MultiChatLocalPlatform.SPIGOT) {

			// Handle the special formatting required for spigot...

			if (!config.isOverrideAllMultiChatFormatting()) {

				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - We were managing the format...");

				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Currently it is " + proxyFormat);

				proxyFormat = proxyFormat.replace("%1$s", MultiChatLocal.getInstance().getMetaManager().getDisplayName(event.getPlayer().getUniqueId()));
				proxyFormat = proxyFormat.replace("%2$s", "");

				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - We replaced the special bits to get: " + proxyFormat);

			} else {

				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Oh dear... we need to send it to the proxy... but we weren't managing the chat...");

				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - The format currently is: " + proxyFormat);

				proxyFormat = proxyFormat.replace("%1$s", MultiChatLocal.getInstance().getMetaManager().getDisplayName(event.getPlayer().getUniqueId()));
				proxyFormat = proxyFormat.replace("%2$s", "");
				proxyFormat = proxyFormat.replaceFirst("\\$s", MultiChatLocal.getInstance().getMetaManager().getDisplayName(event.getPlayer().getUniqueId()));
				proxyFormat = proxyFormat.replaceFirst("\\$s", "");

				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - But we worked some magic to arrive at... " + proxyFormat);

			}

		}

		MultiChatLocal.getInstance().getProxyCommunicationManager().sendPlayerChatMessage(event.getPlayer().getUniqueId(), channel, proxyMessage, proxyFormat, originalRecipients);

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Aaaaand we sent it to the proxy! ALL DONE.");
		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - UUID: " + event.getPlayer().getUniqueId());
		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - MESSAGE (please note this will be shown in colour here even if the player doesn't have colour permissions): " + proxyMessage);
		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - FORMAT: " + proxyFormat);

	}

}
