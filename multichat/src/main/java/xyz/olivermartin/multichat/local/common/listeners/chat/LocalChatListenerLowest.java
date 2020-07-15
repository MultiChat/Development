package xyz.olivermartin.multichat.local.common.listeners.chat;

import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;

public abstract class LocalChatListenerLowest {

	public void handleChat(MultiChatLocalPlayerChatEvent event) {

		// IF ITS ALREADY CANCELLED THEN WE CAN IGNORE IT!
		if (event.isCancelled()) return;

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Handling chat message...");

		LocalChatManager chatManager = MultiChatLocal.getInstance().getChatManager();

		MultiChatLocalPlayer player = event.getPlayer();
		String channel = chatManager.peekAtChatChannel(player);
		String format = event.getFormat();

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Channel for this message before forcing is " + channel);

		// Deal with regex channel forcing...
		channel = chatManager.getRegexForcedChannel(channel, format);

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Channel for this message after forcing is " + channel);

		if (!chatManager.isGlobalChatServer()) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Not a global chat server, so setting channel to local!");
			channel = "local";
		}

		if (channel.equals("local") && !chatManager.isSetLocalFormat()) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Its local chat and we aren't setting the format for that, so return now!");
			return;
		}

		if (chatManager.isOverrideMultiChatFormat()) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - We are overriding MultiChat's formatting... So abandon here...");
			return;
		}

		format = chatManager.getChannelFormat(channel);
		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Got the format for this channel as:" + format);

		// Build chat format
		format = MultiChatLocal.getInstance().getPlaceholderManager().buildChatFormat(player.getUniqueId(), format);

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Built to become: " + format);

		format = chatManager.processExternalPlaceholders(player, format);

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Processing external placeholders to become: " + format);

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

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Did some magic formatting to end up as: " + format);

		event.setFormat(chatManager.translateColourCodes(format, true));

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Set the format of the message. Finished processing at the lowest level!");

	}

}
