package xyz.olivermartin.multichat.local.listeners.chat;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.config.LocalConfig;
import xyz.olivermartin.multichat.local.storage.LocalDataStore;

public abstract class LocalChatListenerLowest {

	protected abstract String processExternalPlaceholders(MultiChatLocalPlayer player, String messageFormat);

	protected abstract String translateColourCodes(String format);

	protected void handleChatMessage(MultiChatLocalPlayerChatEvent event) {

		LocalDataStore store = MultiChatLocal.getInstance().getDataStore();
		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();
		MultiChatLocalPlayer player = event.getPlayer();
		String channel;
		String format = event.getFormat();

		if (store.playerChannels.containsKey(player.getUniqueId())) {
			channel = store.playerChannels.get(player.getUniqueId());
		} else {
			channel = "global";
		}

		if (store.chatQueues.containsKey(player.getName().toLowerCase())) {
			// Hack for /global /local direct messaging...
			String tempChannel = store.chatQueues.get(player.getName().toLowerCase()).peek();
			if(tempChannel.startsWith("!SINGLE L MESSAGE!")) {
				channel = "local";
			} else {
				channel = "global";
			}
		} 


		if (channel.equals("local") || (!store.globalChatServer)) {

			// Local chat

			if (config.isSetLocalFormat()) {

				format = config.getLocalChatFormat();
				//format = SpigotPlaceholderManager.buildChatFormat(p, format);

			} else {
				return;
			}

		} else {

			// Global chat

			// If we aren't setting the format then we can leave now!
			if (config.isOverrideAllMultiChatFormatting()) return;

			if (!config.isOverrideGlobalFormat()) {

				// If we aren't overriding then use the main global format
				format = store.globalChatFormat;

			} else {

				// Otherwise use the locally defined one in the config file
				format = config.getOverrideGlobalFormatFormat();

			}

		}

		// Build chat format
		format = MultiChatLocal.getInstance().getPlaceholderManager().buildChatFormat(player.getUniqueId(), format);

		format = processExternalPlaceholders(player, format);

		format = format.replace("%1$s", "!!!1!!!");
		format = format.replace("%2$s", "!!!2!!!");

		format = format.replace("%", "%%");

		format = format.replace("!!!1!!!", "%1$s");
		format = format.replace("!!!2!!!", "%2$s");

		if (channel.equals("local") || (!store.globalChatServer)) {
			// TRY TO FIX ISSUE WITH MULTICHAT NOT FORMATTING LOCAL MESSAGES IF NOT IN GLOBAL MODE if (MultiChatSpigot.globalChatServer) event.setFormat(ChatColor.translateAlternateColorCodes('&', format));
			event.setFormat(translateColourCodes(format));
		} else {
			// If we are a global chat server, then we want to set the format!
			if (store.globalChatServer) event.setFormat(translateColourCodes(format));
		}

	}

}
