package xyz.olivermartin.multichat.local.common.listeners.chat;

import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.LocalConsoleLogger;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.common.config.LocalConfig;

public abstract class LocalChatListenerMonitor {

	public void handleChat(MultiChatLocalPlayerChatEvent event) {

		LocalConsoleLogger logger = MultiChatLocal.getInstance().getConsoleLogger();

		logger.debug("&8[&3CHAT-L3&8]&7 ", "Processing a chat message...");
		logger.debug("&8[&3CHAT-L3&8]&7 ", "SENDER = '" + event.getPlayer().getName() + "'");
		logger.debug("&8[&3CHAT-L3&8]&7 ", "ORIGINAL MESSAGE = '" + event.getMessage() + "'");
		logger.debug("&8[&3CHAT-L3&8]&7 ", "ORIGINAL FORMAT = '" + event.getFormat() + "'");

		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();
		LocalChatManager chatManager = MultiChatLocal.getInstance().getChatManager();

		Set<UUID> originalRecipients = chatManager.getRecipientsFromRecipientQueue(event.getPlayer().getUniqueId());
		String channel = chatManager.pollChatChannel(event.getPlayer());

		logger.debug("&8[&3CHAT-L3&8]&7 ", "CHANNEL (before forcing) = '" + channel + "'");

		// Deal with regex channel forcing...
		channel = chatManager.getRegexForcedChannel(channel, event.getFormat());

		logger.debug("&8[&3CHAT-L3&8]&7 ", "CHANNEL (after forcing) = '" + channel + "'");

		// IF ITS ALREADY CANCELLED WE CAN IGNORE IT
		if (event.isCancelled()) {
			logger.debug("&8[&3CHAT-L3&8]&7 ", "Message is already cancelled - FINISH");
			return;
		}

		// IF WE ARE MANAGING GLOBAL CHAT THEN WE NEED TO MANAGE IT!

		MultiChatLocal.getInstance().getProxyCommunicationManager().updatePlayerMeta(event.getPlayer().getUniqueId());

		logger.debug("&8[&3CHAT-L3&8]&7 ", "Player meta data update has just been sent to proxy");

		String proxyFormat = event.getFormat();
		String proxyMessage = event.getMessage();

		if (MultiChatLocal.getInstance().getPlatform() == MultiChatLocalPlatform.SPIGOT) {

			// Handle the special formatting required for spigot...

			if (!config.isOverrideAllMultiChatFormatting()) {

				proxyFormat = proxyFormat.replace("%1$s", MultiChatLocal.getInstance().getMetaManager().getDisplayName(event.getPlayer().getUniqueId()));
				proxyFormat = proxyFormat.replace("%2$s", "");

			} else {

				logger.debug("&8[&3CHAT-L3&8]&7 ", "MultiChat's format has been overridden, so proxy formatting is done on a best-effort basis...");

				proxyFormat = proxyFormat.replace("%1$s", MultiChatLocal.getInstance().getMetaManager().getDisplayName(event.getPlayer().getUniqueId()));
				proxyFormat = proxyFormat.replace("%2$s", "");
				proxyFormat = proxyFormat.replaceFirst("\\$s", MultiChatLocal.getInstance().getMetaManager().getDisplayName(event.getPlayer().getUniqueId()));
				proxyFormat = proxyFormat.replaceFirst("\\$s", "");

			}

		}

		logger.debug("&8[&3CHAT-L3&8]&7 ", "FORMAT (final for proxy) = '" + proxyFormat + "'");
		logger.debug("&8[&3CHAT-L3&8]&7 ", "MESSAGE (final for proxy) = '" + proxyMessage + "'");
		logger.debug("&8[&3CHAT-L3&8]&7 ", "PLAYER UUID = '" + event.getPlayer().getUniqueId() + "'");

		MultiChatLocal.getInstance().getProxyCommunicationManager().sendPlayerChatMessage(event.getPlayer().getUniqueId(), channel, proxyMessage, proxyFormat, originalRecipients);

		logger.debug("&8[&3CHAT-L3&8]&7 ", "Info sent to proxy - FINISH");

	}

}
