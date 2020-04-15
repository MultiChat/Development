package xyz.olivermartin.multichat.local.listeners.chat;

import xyz.olivermartin.multichat.local.LocalChatManager;
import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;

public abstract class LocalChatListenerLowest {

	protected void handleChatMessage(MultiChatLocalPlayerChatEvent event) {

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Handling chat message...");

		LocalChatManager chatManager = MultiChatLocal.getInstance().getChatManager();

		MultiChatLocalPlayer player = event.getPlayer();
		String channel = chatManager.peekAtChatChannel(player);
		String format = event.getFormat();

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Channel for this message is " + channel);

		if (!chatManager.isGlobalChatServer()) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Not a global chat server, so setting channel to local!");
			channel = "local";
		}

		if (channel.equals("local") && !chatManager.isSetLocalFormat()) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Its local chat and we aren't setting the format for that, so return now!");
			return;
		}

		format = chatManager.getChannelFormat(channel);
		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Got the format for this channel as:" + format);

		// Build chat format
		format = MultiChatLocal.getInstance().getPlaceholderManager().buildChatFormat(player.getUniqueId(), format);

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Built to become: " + format);

		format = chatManager.processExternalPlaceholders(player, format);

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Processing external placeholders to become: " + format);

		format = format.replace("%1$s", "!!!1!!!");
		format = format.replace("%2$s", "!!!2!!!");

		format = format.replace("%", "%%");

		format = format.replace("!!!1!!!", "%1$s");
		format = format.replace("!!!2!!!", "%2$s");

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Did some magic formatting to end up as: " + format);

		event.setFormat(chatManager.translateColourCodes(format));

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Set the format of the message. Finished processing at the lowest level!");

	}

}
