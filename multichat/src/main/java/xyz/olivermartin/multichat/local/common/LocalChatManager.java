package xyz.olivermartin.multichat.local.common;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;

import xyz.olivermartin.multichat.local.common.config.LocalConfig;
import xyz.olivermartin.multichat.local.common.config.RegexChannelForcer;
import xyz.olivermartin.multichat.local.common.storage.LocalDataStore;

public abstract class LocalChatManager {

	public boolean isForceMultiChatFormat() {
		return MultiChatLocal.getInstance().getConfigManager().getLocalConfig().isForceMultiChatFormat();
	}

	public boolean isGlobalChatServer() {
		return MultiChatLocal.getInstance().getDataStore().globalChatServer;
	}

	public boolean isSetLocalFormat() {
		return MultiChatLocal.getInstance().getConfigManager().getLocalConfig().isSetLocalFormat();
	}

	/**
	 * Gets the raw channel name the player has selected to chat into. This does not check if the channel is enabled in their location
	 * @param uuid
	 * @return
	 */
	public String getSelectedChatChannel(UUID uuid) {

		LocalDataStore store = MultiChatLocal.getInstance().getDataStore();

		String channel;

		if (store.playerChannels.containsKey(uuid)) {
			channel = store.playerChannels.get(uuid);
			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Got selected player channel as " + channel);
		} else {
			channel = "global";
			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Player was not in channel map, so using global...");
		}

		return channel;

	}

	public void queueChatChannel(String playerName, String channel) {

		if (MultiChatLocal.getInstance().getDataStore().chatQueues.containsKey(playerName.toLowerCase())) {

			Queue<String> chatQueue = MultiChatLocal.getInstance().getDataStore().chatQueues.get(playerName.toLowerCase());
			chatQueue.add(channel);

		} else {

			Queue<String> chatQueue = new LinkedList<String>();
			chatQueue.add(channel);
			MultiChatLocal.getInstance().getDataStore().chatQueues.put(playerName.toLowerCase(), chatQueue);

		}

	}

	private String getChannelFromChatQueue(MultiChatLocalPlayer player, boolean pollQueue) {

		LocalDataStore store = MultiChatLocal.getInstance().getDataStore();

		String channel;

		if (store.chatQueues.containsKey(player.getName().toLowerCase())) {

			// Hack for /global /local direct messaging...

			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Player in chat queue...");

			String tempChannel;

			if (pollQueue) {
				tempChannel = store.chatQueues.get(player.getName().toLowerCase()).poll();

				if (store.chatQueues.get(player.getName().toLowerCase()).size() < 1) {
					store.chatQueues.remove(player.getName().toLowerCase());
				}

			} else {
				tempChannel = store.chatQueues.get(player.getName().toLowerCase()).peek();
			}

			MultiChatLocal.getInstance().getConsoleLogger().debug("What did we get from the chat queue? Is it null?: " + (tempChannel==null));

			MultiChatLocal.getInstance().getConsoleLogger().debug("It was: " + tempChannel);

			channel = tempChannel;

			/*if (tempChannel.startsWith("!SINGLE L MESSAGE!")) {

				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] This is a local (direct) message");
				channel = "local";

			} else {

				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] This is a global (direct) message");
				channel = "global";

			}*/

		} else {

			// Get normally selected channel

			channel = getSelectedChatChannel(player.getUniqueId());

		}

		return channel;

	}

	public String peekAtChatChannel(MultiChatLocalPlayer player) {
		return getChannelFromChatQueue(player, false);
	}

	public String pollChatChannel(MultiChatLocalPlayer player) {
		return getChannelFromChatQueue(player, true);
	}

	public String getChannelFormat(String channel) {

		String format;
		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();

		switch (channel.toLowerCase()) {

		case "local":

			// Local Chat

			format = config.getLocalChatFormat();

			break;

		default:

			// Global Chat

			if (!config.isOverrideGlobalFormat()) {

				// If we aren't overriding then use the main global format
				format = MultiChatLocal.getInstance().getDataStore().globalChatFormat;

			} else {

				// Otherwise use the locally defined one in the config file
				format = config.getOverrideGlobalFormatFormat();

			}

			break;

		}

		return format;

	}

	public boolean canChatInColour(UUID uuid) {

		LocalDataStore store = MultiChatLocal.getInstance().getDataStore();

		if (store.colourMap.containsKey(uuid)) {

			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Player is in the colour map!");

			boolean colour = store.colourMap.get(uuid);

			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Can they use colours? --> " + colour);

			return colour;

		} else {

			MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Player was NOT in the colour map! That probably isn't good!");

			return false;

		}

	}

	public abstract String translateColourCodes(String message);

	public abstract String processExternalPlaceholders(MultiChatLocalPlayer player, String message);

	public String processMultiChatConfigPlaceholders(MultiChatLocalPlayer player, String message) {

		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();

		synchronized (config.getMultichatPlaceholders()) {

			for (String key : config.getMultichatPlaceholders().keySet()) {

				String value = config.getMultichatPlaceholders().get(key);
				value = MultiChatLocal.getInstance().getPlaceholderManager().processMultiChatPlaceholders(player.getUniqueId(), value);

				// If we are hooked with PAPI then use their placeholders!
				value = processExternalPlaceholders(player, value);

				if (message.contains(key)) {
					message = message.replace(key, value);
				}
			}

		}

		return message;

	}

	public Optional<LocalPseudoChannel> getChannelObject(String channelName) {

		if (MultiChatLocal.getInstance().getDataStore().channelObjects.containsKey(channelName)) {
			return Optional.of(MultiChatLocal.getInstance().getDataStore().channelObjects.get(channelName));
		} else {
			return Optional.empty();
		}

	}

	public String getRegexForcedChannel(String currentChannel, String messageFormat) {

		List<RegexChannelForcer> regexChannelForcers =
				MultiChatLocal.getInstance().getConfigManager().getLocalConfig().getRegexChannelForcers();

		String channel = currentChannel;

		for (RegexChannelForcer rcf : regexChannelForcers) {
			if (rcf.matchesRegex(messageFormat)) {
				channel = rcf.getChannel();
				break;
			}
		}

		return channel;

	}

}
