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
		
		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Now is where the fun starts... Welcome to the highest level!");

		LocalDataStore store = MultiChatLocal.getInstance().getDataStore();
		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();

		// Deal with coloured chat
		if (store.colourMap.containsKey(event.getPlayer().getUniqueId())) {
			
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Player is in the colour map!");

			boolean colour = store.colourMap.get(event.getPlayer().getUniqueId());
			
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Can they use colours? --> " + colour);

			if (colour) {
				event.setMessage(translateColourCodes(event.getMessage()));
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Translated their message to include the colours and set back in the event as: " + event.getMessage());
			}

		} else {
			
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Player was NOT in the colour map! That probably isn't good! Oh well... let's continue...");
			
		}

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Now we will process MultiChat placeholders!");
		
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
		
		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - The resulting format was... " + event.getFormat());

		// Deal with ignores and channel members
		if (store.playerChannels.containsKey(event.getPlayer().getUniqueId())) {
			
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - The player is in the channel map!");

			String channelName = store.playerChannels.get(event.getPlayer().getUniqueId());
			
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Their channel name is " + channelName);

			// HACK for /local <message> and /global<message>

			if (store.chatQueues.containsKey(event.getPlayer().getName().toLowerCase())) {
				String tempChannel = store.chatQueues.get(event.getPlayer().getName().toLowerCase()).peek();
				channelName = tempChannel.startsWith("!SINGLE L MESSAGE!") ? "local" : "global";
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Oh. Wait. No its not. Its actually " + channelName + ", because they are using local global direct messaging!");
			}

			// END HACK

			if (store.channelObjects.containsKey(channelName)) {
				
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Do we have a channel object to match that name? Yes!");

				LocalPseudoChannel channelObject = store.channelObjects.get(channelName);
				
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Now we are attempting to remove ignored players from the recipient list of the message, and making sure only people who are meant to see the channel (as specified in the channel object), can see it!");

				event.removeIgnoredPlayersAndNonChannelMembersFromRecipients(channelObject);
				
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - And BAM! That was handled by the local platform implementation!");

			} else {
				
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - We didn't find a channel object to match that name... Probably not good!");
				
			}

		} else {
			
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - The player was not in the channel map... That isn't a good sign! We will try to continue anyway...");
			
		}

		if (store.playerChannels.containsKey(event.getPlayer().getUniqueId())) {
			
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Okay, so they are in the channel map still");

			if (!store.globalChatServer) {
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - We are not a global chat server, so at this point we are returning! Bye!");
				return;
			}

			if (store.chatQueues.containsKey(event.getPlayer().getName().toLowerCase())) {
				// Hack for /global /local direct messaging...
				String tempChannel = store.chatQueues.get(event.getPlayer().getName().toLowerCase()).peek();
				if(tempChannel.startsWith("!SINGLE L MESSAGE!")) {
					MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - This message was destined for local chat! So now we return! Bye!");
					return;
				}
			} else if (store.playerChannels.get(event.getPlayer().getUniqueId()).equals("local") || (!store.globalChatServer)) {

				// If its a local chat message (or we can't use global chat here) then we dont need to do anything else!
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - This message was destined for local chat! So now we return! Bye!");
				return;
			}

		}

		if (config.isForceMultiChatFormat()) {
			
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - OKAYYY! We are forcing our format! All other plugins shall now crumble!");

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Currently it is starting out as... " + event.getFormat());
			
			String format;

			if (!config.isOverrideGlobalFormat()) {

				// If we aren't overriding then use the main global format
				format = store.globalChatFormat;
				
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - We will be using the proxy global chat format " + format);

			} else {

				// Otherwise use the locally defined one in the config file
				format = config.getOverrideGlobalFormatFormat();
				
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - We are overriding the proxys global chat format with " + format);

			}

			// Build chat format
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Rebuilding the chat format...");
			format = MultiChatLocal.getInstance().getPlaceholderManager().buildChatFormat(event.getPlayer().getUniqueId(), format);

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Now we have: " + format);
			
			format = processExternalPlaceholders(event.getPlayer(), format);
			
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Processed external placeholders to get: " + format);

			format = format.replace("%1$s", "!!!1!!!");
			format = format.replace("%2$s", "!!!2!!!");

			format = format.replace("%", "%%");

			format = format.replace("!!!1!!!", "%1$s");
			format = format.replace("!!!2!!!", "%2$s");
			
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Did some magic to get..." + format);

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Now we will set the format... But only if we are a global chat server...");
			// If we are a global chat server, then we want to set the format!
			if (store.globalChatServer) {
				event.setFormat(translateColourCodes(format));
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - FORMAT HAS BEEN SET AS: " + event.getFormat());
			} else {
				MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Oops, we weren't a global chat server... So it wasn't set!");
			}

		}

	}

}
