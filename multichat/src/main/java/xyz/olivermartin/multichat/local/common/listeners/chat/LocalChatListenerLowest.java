package xyz.olivermartin.multichat.local.common.listeners.chat;

import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.LocalConsoleLogger;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;

public abstract class LocalChatListenerLowest {

	public void handleChat(MultiChatLocalPlayerChatEvent event) {

		LocalConsoleLogger logger = MultiChatLocal.getInstance().getConsoleLogger();

		logger.debug("&8[&1CHAT-L1&8]&7 ", "Processing a chat message...");
		logger.debug("&8[&1CHAT-L1&8]&7 ", "SENDER = '" + event.getPlayer().getName() + "'");
		logger.debug("&8[&1CHAT-L1&8]&7 ", "ORIGINAL MESSAGE = '" + event.getMessage() + "'");
		logger.debug("&8[&1CHAT-L1&8]&7 ", "ORIGINAL FORMAT = '" + event.getFormat() + "'");

		// IF ITS ALREADY CANCELLED THEN WE CAN IGNORE IT!
		if (event.isCancelled()) {
			logger.debug("&8[&1CHAT-L1&8]&7 ", "Message is already cancelled - FINISH");
			return;
		}

		LocalChatManager chatManager = MultiChatLocal.getInstance().getChatManager();

		MultiChatLocalPlayer player = event.getPlayer();
		String channel = chatManager.peekAtChatChannel(player);
		String format = event.getFormat();

		logger.debug("&8[&1CHAT-L1&8]&7 ", "CHANNEL (before forcing) = '" + channel + "'");

		// Deal with regex channel forcing...
		channel = chatManager.getRegexForcedChannel(channel, format);

		logger.debug("&8[&1CHAT-L1&8]&7 ", "CHANNEL (after forcing) = '" + channel + "'");

		if (!chatManager.isGlobalChatServer()) {
			channel = "local";
			logger.debug("&8[&1CHAT-L1&8]&7 ", "CHANNEL (override - no global) = '" + channel + "'");
		}

		if (channel.equals("local") && !chatManager.isSetLocalFormat()) {
			logger.debug("&8[&1CHAT-L1&8]&7 ", "Local chat and MultiChat not setting format - FINISH");
			return;
		}

		if (chatManager.isOverrideMultiChatFormat()) {
			logger.debug("&8[&1CHAT-L1&8]&7 ", "MultiChat formatting is set to be overridden - FINISH");
			return;
		}

		format = chatManager.getChannelFormat(channel);
		logger.debug("&8[&1CHAT-L1&8]&7 ", "CHANNEL FORMAT = '" + format + "'");

		// Build chat format
		format = MultiChatLocal.getInstance().getPlaceholderManager().buildChatFormat(player.getUniqueId(), format);

		logger.debug("&8[&1CHAT-L1&8]&7 ", "CHANNEL FORMAT (built) = '" + format + "'");

		format = chatManager.processExternalPlaceholders(player, format) + "%2$s";

		logger.debug("&8[&1CHAT-L1&8]&7 ", "CHANNEL FORMAT (final with external placeholders) = '" + format + "'");

		event.setFormat(format);

		logger.debug("&8[&1CHAT-L1&8]&7 ", "Format has been set - FINISH");

	}

}
