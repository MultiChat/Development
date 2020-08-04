package xyz.olivermartin.multichat.local.common.listeners.chat;

import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;

public abstract class LocalChatListenerHighest {

	public void handleChat(MultiChatLocalPlayerChatEvent event) {

		// IF ITS ALREADY CANCELLED WE CAN IGNORE IT
		if (event.isCancelled()) return;

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Now is where the fun starts... Welcome to the highest level!");

		LocalChatManager chatManager = MultiChatLocal.getInstance().getChatManager();

		if (chatManager.canChatInRGBColour(event.getPlayer().getUniqueId())) {
			event.setMessage(chatManager.translateColorCodes(event.getMessage(),true));
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Translated their message to include the colours (RGB) and set back in the event as: " + event.getMessage());
		} else if (chatManager.canChatInSimpleColour(event.getPlayer().getUniqueId())) {
			event.setMessage(chatManager.translateColorCodes(event.getMessage(),false));
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Translated their message to include the colours (SIMPLE ONLY) and set back in the event as: " + event.getMessage());
		}

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Now we will process MultiChat placeholders!");

		event.setFormat(chatManager.processMultiChatConfigPlaceholders(event.getPlayer(), event.getFormat()));

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - The resulting format was... " + event.getFormat());

		String channel = chatManager.peekAtChatChannel(event.getPlayer());

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Channel for this message before forcing is: " + channel);

		// Deal with regex channel forcing...
		channel = chatManager.getRegexForcedChannel(channel, event.getFormat());

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Channel for this message after forcing is: " + channel);

		// Deal with ignores and channel members

		Set<UUID> intendedRecipients = event.getOtherRecipients();

		chatManager.queueRecipients(event.getPlayer().getUniqueId(), intendedRecipients);

		event.removeOtherPlayers();

		if (!chatManager.isGlobalChatServer() || channel.equalsIgnoreCase("local")) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - We are speaking into local chat, so at this point we are returning! Bye!");
			return;
		}

		if (chatManager.isForceMultiChatFormat()) {

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - OKAYYY! We are forcing our format! All other plugins shall now crumble!");

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Currently it is starting out as... " + event.getFormat());

			String format;

			format = chatManager.getChannelFormat(channel);
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Got the format for this channel as:" + format);

			// Build chat format
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Rebuilding the chat format...");
			format = MultiChatLocal.getInstance().getPlaceholderManager().buildChatFormat(event.getPlayer().getUniqueId(), format);

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Now we have: " + format);

			format = chatManager.processExternalPlaceholders(event.getPlayer(), format);

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Processed external placeholders to get: " + format);

			if (MultiChatLocal.getInstance().getPlatform() == MultiChatLocalPlatform.SPIGOT) {
				// Handle Spigot displayname formatting etc.
				format = format.replace("%1$s", "!!!1!!!");
				format = format.replace("%2$s", "!!!2!!!");
				format = format.replace("%", "%%");
				format = format.replace("!!!1!!!", "%1$s");
				format = format.replace("!!!2!!!", "%2$s");
			} else {
				format = format.replace("%", "%%");
			}

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Did some magic to get..." + format);

			event.setFormat(chatManager.translateColorCodes(format, true));
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - FORMAT HAS BEEN SET AS: " + event.getFormat());

		}

	}
}