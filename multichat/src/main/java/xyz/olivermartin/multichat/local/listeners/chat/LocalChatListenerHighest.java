package xyz.olivermartin.multichat.local.listeners.chat;

import xyz.olivermartin.multichat.local.LocalPseudoChannel;
import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.config.LocalConfig;
import xyz.olivermartin.multichat.local.storage.LocalDataStore;

public abstract class LocalChatListenerHighest {

	protected abstract String processExternalPlaceholders(MultiChatLocalPlayer player, String messageFormat);

	protected abstract String translateColourCodes(String format);

	protected void handleChatMessage(MultiChatLocalPlayerChatEvent event) {

		LocalDataStore store = MultiChatLocal.getInstance().getDataStore();
		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();

		// Deal with coloured chat
		if (store.colourMap.containsKey(event.getPlayer().getUniqueId())) {

			boolean colour = store.colourMap.get(event.getPlayer().getUniqueId());

			if (colour) {
				event.setMessage(translateColourCodes(event.getMessage()));
			}

		}

		synchronized (config.getMultichatPlaceholders()) {
			for (String key : config.getMultichatPlaceholders().keySet()) {

				String value = config.getMultichatPlaceholders().get(key);
				value = MultiChatLocal.getInstance().getPlaceholderManager().processMultiChatPlaceholders(event.getPlayer().getUniqueId(), value);

				// If we are hooked with PAPI then use their placeholders!
				value = processExternalPlaceholders(event.getPlayer(), value);

				if (event.getFormat().contains(key)) {
					event.setFormat(event.getFormat().replace(key, value));
				}
			}
		}

		// Deal with ignores and channel members
		if (store.playerChannels.containsKey(event.getPlayer().getUniqueId())) {

			String channelName = store.playerChannels.get(event.getPlayer().getUniqueId());

			// HACK for /local <message> and /global<message>

			if (store.chatQueues.containsKey(event.getPlayer().getName().toLowerCase())) {
				String tempChannel = store.chatQueues.get(event.getPlayer().getName().toLowerCase()).peek();
				channelName = tempChannel.startsWith("!SINGLE L MESSAGE!") ? "local" : "global";
			}

			// END HACK

			if (store.channelObjects.containsKey(channelName)) {

				LocalPseudoChannel channelObject = store.channelObjects.get(channelName);

				event.removeIgnoredPlayersAndNonChannelMembersFromRecipients(channelObject);

			}

		}

		if (store.playerChannels.containsKey(event.getPlayer().getUniqueId())) {

			if (!store.globalChatServer) {
				return;
			}

			if (store.chatQueues.containsKey(event.getPlayer().getName().toLowerCase())) {
				// Hack for /global /local direct messaging...
				String tempChannel = store.chatQueues.get(event.getPlayer().getName().toLowerCase()).peek();
				if(tempChannel.startsWith("!SINGLE L MESSAGE!")) {
					return;
				}
			} else if (store.playerChannels.get(event.getPlayer().getUniqueId()).equals("local") || (!store.globalChatServer)) {

				// If its a local chat message (or we can't use global chat here) then we dont need to do anything else!
				return;
			}

		}

		if (config.isForceMultiChatFormat()) {

			String format;

			if (!config.isOverrideGlobalFormat()) {

				// If we aren't overriding then use the main global format
				format = store.globalChatFormat;

			} else {

				// Otherwise use the locally defined one in the config file
				format = config.getOverrideGlobalFormatFormat();

			}

			// Build chat format
			format = MultiChatLocal.getInstance().getPlaceholderManager().buildChatFormat(event.getPlayer().getUniqueId(), format);

			format = processExternalPlaceholders(event.getPlayer(), format);

			format = format.replace("%1$s", "!!!1!!!");
			format = format.replace("%2$s", "!!!2!!!");

			format = format.replace("%", "%%");

			format = format.replace("!!!1!!!", "%1$s");
			format = format.replace("!!!2!!!", "%2$s");

			// If we are a global chat server, then we want to set the format!
			if (store.globalChatServer) event.setFormat(translateColourCodes(format));

		}

	}

}
