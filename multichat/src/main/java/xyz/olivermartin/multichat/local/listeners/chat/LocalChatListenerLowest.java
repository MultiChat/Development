package xyz.olivermartin.multichat.local.listeners.chat;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.config.LocalConfig;
import xyz.olivermartin.multichat.local.storage.LocalDataStore;

public abstract class LocalChatListenerLowest {

	protected abstract String processExternalPlaceholders(MultiChatLocalPlayer player, String messageFormat);

	protected abstract String translateColourCodes(String format);

	protected void handleChatMessage(MultiChatLocalPlayerChatEvent event) {
		
		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Handling chat message...");

		LocalDataStore store = MultiChatLocal.getInstance().getDataStore();
		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();
		MultiChatLocalPlayer player = event.getPlayer();
		String channel;
		String format = event.getFormat();

		if (store.playerChannels.containsKey(player.getUniqueId())) {
			channel = store.playerChannels.get(player.getUniqueId());
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Got player channel as " + channel);
		} else {
			channel = "global";
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Player was not in channel map, so using globa...");
		}

		if (store.chatQueues.containsKey(player.getName().toLowerCase())) {
			// Hack for /global /local direct messaging...
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Player in chat queue... handling local/global direct hack!");
			String tempChannel = store.chatQueues.get(player.getName().toLowerCase()).peek();
			if(tempChannel.startsWith("!SINGLE L MESSAGE!")) {
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - This is a local (direct) message");
				channel = "local";
			} else {
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - This is a global (direct) message");
				channel = "global";
			}
		} 


		if (channel.equals("local") || (!store.globalChatServer)) {
			
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Starting local chat mode processing...");

			// Local chat

			if (config.isSetLocalFormat()) {
				
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - MultiChat is set to process the local format!");

				format = config.getLocalChatFormat();
				
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Got the format from config as " + format);
				//format = SpigotPlaceholderManager.buildChatFormat(p, format);

			} else {
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - MultiChat is NOT set to process the local format. Returning...");
				return;
			}

		} else {
			
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Starting global chat mode processing...");

			// Global chat

			// If we aren't setting the format then we can leave now!
			if (config.isOverrideAllMultiChatFormatting()) return;
			
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - MultiChat should handle chat formatting...");

			if (!config.isOverrideGlobalFormat()) {
				
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - We are not overriding the global format set on the proxy");

				// If we aren't overriding then use the main global format
				format = store.globalChatFormat;
				
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Format from proxy is: " + format);

			} else {

				// Otherwise use the locally defined one in the config file
				format = config.getOverrideGlobalFormatFormat();
				
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - We are overriding the proxy global format as: " + format);

			}

		}
		
		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Now time to build the chat format (same for local and global)");

		// Build chat format
		format = MultiChatLocal.getInstance().getPlaceholderManager().buildChatFormat(player.getUniqueId(), format);
		
		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Built to become: " + format);

		format = processExternalPlaceholders(player, format);
		
		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Processing external placeholders to become: " + format);

		format = format.replace("%1$s", "!!!1!!!");
		format = format.replace("%2$s", "!!!2!!!");

		format = format.replace("%", "%%");

		format = format.replace("!!!1!!!", "%1$s");
		format = format.replace("!!!2!!!", "%2$s");
		
		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Did some magic formatting to end up as: " + format);

		if (channel.equals("local") || (!store.globalChatServer)) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - We are a local chat server, setting the format of the actual chat message to " + format);
			// TRY TO FIX ISSUE WITH MULTICHAT NOT FORMATTING LOCAL MESSAGES IF NOT IN GLOBAL MODE if (MultiChatSpigot.globalChatServer) event.setFormat(ChatColor.translateAlternateColorCodes('&', format));
			event.setFormat(translateColourCodes(format));
		} else {
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - We are a global chat server, setting the format of the actual chat message to " + format);
			// If we are a global chat server, then we want to set the format!
			if (store.globalChatServer) event.setFormat(translateColourCodes(format));
		}
		
		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Finished processing at the lowest level!");

	}

}
