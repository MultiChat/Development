package xyz.olivermartin.multichat.local.common.listeners.chat;

import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.LocalConsoleLogger;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;

public abstract class LocalChatListenerHighest {

	public void handleChat(MultiChatLocalPlayerChatEvent event) {

		LocalConsoleLogger logger = MultiChatLocal.getInstance().getConsoleLogger();

		logger.debug("&8[&9CHAT-L2&8]&7 ", "Processing a chat message...");
		logger.debug("&8[&9CHAT-L2&8]&7 ", "SENDER = '" + event.getPlayer().getName() + "'");
		logger.debug("&8[&9CHAT-L2&8]&7 ", "ORIGINAL MESSAGE = '" + event.getMessage() + "'");
		logger.debug("&8[&9CHAT-L2&8]&7 ", "ORIGINAL FORMAT = '" + event.getFormat() + "'");

		// IF ITS ALREADY CANCELLED WE CAN IGNORE IT
		if (event.isCancelled()) {
			logger.debug("&8[&9CHAT-L2&8]&7 ", "Message is already cancelled - FINISH");
			return;
		}

		LocalChatManager chatManager = MultiChatLocal.getInstance().getChatManager();

		if (chatManager.canChatInRGBColour(event.getPlayer().getUniqueId())) {
			event.setMessage(chatManager.translateColorCodes(event.getMessage(),true));
			logger.debug("&8[&9CHAT-L2&8]&7 ", "COLOR PERMISSIONS = RGB");
		} else if (chatManager.canChatInSimpleColour(event.getPlayer().getUniqueId())) {
			event.setMessage(chatManager.translateColorCodes(event.getMessage(),false));
			logger.debug("&8[&9CHAT-L2&8]&7 ", "COLOR PERMISSIONS = SIMPLE");
		} else {
			logger.debug("&8[&9CHAT-L2&8]&7 ", "COLOR PERMISSIONS = NONE");
		}

		logger.debug("&8[&9CHAT-L2&8]&7 ", "MESSAGE (after color processing) = '" + event.getMessage() + "'");

		event.setFormat(chatManager.processMultiChatConfigPlaceholders(event.getPlayer(), event.getFormat()));

		logger.debug("&8[&9CHAT-L2&8]&7 ", "FORMAT (after MultiChat placeholders) = '" + event.getFormat() + "'");

		String channel = chatManager.peekAtChatChannel(event.getPlayer());

		logger.debug("&8[&9CHAT-L2&8]&7 ", "CHANNEL (before forcing) = '" + channel + "'");

		// Deal with regex channel forcing...
		channel = chatManager.getRegexForcedChannel(channel, event.getFormat());

		logger.debug("&8[&9CHAT-L2&8]&7 ", "CHANNEL (after forcing) = '" + channel + "'");

		// Deal with ignores and channel members

		Set<UUID> intendedRecipients = event.getOtherRecipients();

		chatManager.queueRecipients(event.getPlayer().getUniqueId(), intendedRecipients);

		event.removeOtherPlayers();

		logger.debug("&8[&9CHAT-L2&8]&7 ", "Removed all recipients except for sender");

		if (!chatManager.isGlobalChatServer() || channel.equalsIgnoreCase("local")) {
			logger.debug("&8[&9CHAT-L2&8]&7 ", "This is a local chat message - FINISH");
			return;
		}

		if (chatManager.isForceMultiChatFormat()) {

			logger.debug("&8[&9CHAT-L2&8]&7 ", "MultiChat force format is enabled, so we will now force our own format");

			String format;

			format = chatManager.getChannelFormat(channel);

			logger.debug("&8[&9CHAT-L2&8]&7 ", "CHANNEL FORMAT = '" + format + "'");

			// Build chat format
			format = MultiChatLocal.getInstance().getPlaceholderManager().buildChatFormat(event.getPlayer().getUniqueId(), format);

			logger.debug("&8[&9CHAT-L2&8]&7 ", "CHANNEL FORMAT (built) = '" + format + "'");

			format = chatManager.processExternalPlaceholders(event.getPlayer(), format) + "%2$s";

			logger.debug("&8[&9CHAT-L2&8]&7 ", "CHANNEL FORMAT (final with external placeholders) = '" + format + "'");

			event.setFormat(format);

			logger.debug("&8[&9CHAT-L2&8]&7 ", "Format has been set");

		}

		logger.debug("&8[&9CHAT-L2&8]&7 ", "Processing completed - FINISH");

	}
}