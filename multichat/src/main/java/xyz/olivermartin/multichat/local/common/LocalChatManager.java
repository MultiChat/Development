package xyz.olivermartin.multichat.local.common;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.common.TranslateMode;
import xyz.olivermartin.multichat.local.common.config.LocalConfig;
import xyz.olivermartin.multichat.local.common.config.RegexChannelForcer;
import xyz.olivermartin.multichat.local.common.storage.LocalDataStore;

public abstract class LocalChatManager {

	public boolean isForceMultiChatFormat() {
		return MultiChatLocal.getInstance().getConfigManager().getLocalConfig().isForceMultiChatFormat();
	}

	public boolean isOverrideMultiChatFormat() {
		return MultiChatLocal.getInstance().getConfigManager().getLocalConfig().isOverrideAllMultiChatFormatting();
	}

	public synchronized boolean isGlobalChatServer() {
		return MultiChatLocal.getInstance().getDataStore().isGlobalChatServer();
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

		Map<UUID,String> playerChannels = store.getPlayerChannels();

		synchronized(playerChannels) {

			if (playerChannels.containsKey(uuid)) {
				channel = playerChannels.get(uuid);
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Got selected player channel as " + channel);
			} else {
				channel = "global";
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Player was not in channel map, so using global...");
			}

		}

		return channel;

	}

	public void queueRecipients(UUID uuid, Set<UUID> recipients) {

		Map<UUID, Queue<Set<UUID>>> recipientQueues = MultiChatLocal.getInstance().getDataStore().getRecipientQueues();

		synchronized (recipientQueues) {

			if (recipientQueues.containsKey(uuid)) {

				Queue<Set<UUID>> q = recipientQueues.get(uuid);
				q.add(recipients);

			} else {

				Queue<Set<UUID>> q = new LinkedList<Set<UUID>>();
				q.add(recipients);
				recipientQueues.put(uuid, q);

			}

		}

	}

	public void queueChatChannel(String playerName, String channel) {

		Map<String, Queue<String>> chatQueues = MultiChatLocal.getInstance().getDataStore().getChatQueues();

		synchronized (chatQueues) {

			if (chatQueues.containsKey(playerName.toLowerCase())) {

				Queue<String> chatQueue = chatQueues.get(playerName.toLowerCase());
				chatQueue.add(channel);

			} else {

				Queue<String> chatQueue = new LinkedList<String>();
				chatQueue.add(channel);
				chatQueues.put(playerName.toLowerCase(), chatQueue);

			}

		}

	}

	public Set<UUID> getRecipientsFromRecipientQueue(UUID uuid) {

		LocalDataStore store = MultiChatLocal.getInstance().getDataStore();
		Map<UUID, Queue<Set<UUID>>> recipientQueues = store.getRecipientQueues();
		Set<UUID> recipients;

		synchronized (recipientQueues) {

			recipients = recipientQueues.get(uuid).poll();

			if (recipientQueues.get(uuid).size() < 1) {
				recipientQueues.remove(uuid);
			}

		}

		return recipients;

	}

	private String getChannelFromChatQueue(MultiChatLocalPlayer player, boolean pollQueue) {

		LocalDataStore store = MultiChatLocal.getInstance().getDataStore();
		Map<String, Queue<String>> chatQueues = store.getChatQueues();
		String channel;

		synchronized (chatQueues) {

			if (chatQueues.containsKey(player.getName().toLowerCase())) {

				// Hack for /global /local direct messaging...

				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Player in chat queue...");

				String tempChannel;

				if (pollQueue) {
					tempChannel = chatQueues.get(player.getName().toLowerCase()).poll();

					if (chatQueues.get(player.getName().toLowerCase()).size() < 1) {
						chatQueues.remove(player.getName().toLowerCase());
					}

				} else {
					tempChannel = chatQueues.get(player.getName().toLowerCase()).peek();
				}

				MultiChatLocal.getInstance().getConsoleLogger().debug("What did we get from the chat queue? Is it null?: " + (tempChannel==null));

				MultiChatLocal.getInstance().getConsoleLogger().debug("It was: " + tempChannel);

				channel = tempChannel;

			} else {

				// Get normally selected channel
				channel = getSelectedChatChannel(player.getUniqueId());

			}

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

		case "global":

			// Global Chat

			if (!config.isOverrideGlobalFormat()) {

				// If we aren't overriding then use the main global format
				format = MultiChatLocal.getInstance().getDataStore().getGlobalChatFormat();

			} else {

				// Otherwise use the locally defined one in the config file
				format = config.getOverrideGlobalFormatFormat();

			}

			break;

		default:

			format = MultiChatLocal.getInstance().getDataStore().getChannelFormats().getOrDefault(channel, MultiChatLocal.getInstance().getDataStore().getGlobalChatFormat());
			break;

		}

		return format;

	}

	public boolean canChatInSimpleColour(UUID uuid) {

		LocalDataStore store = MultiChatLocal.getInstance().getDataStore();
		Map<UUID, Boolean> colourMap = store.getSimpleColourMap();

		synchronized (colourMap) {

			if (colourMap.containsKey(uuid)) {

				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Player is in the simple colour map!");

				boolean colour = colourMap.get(uuid);

				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Can they use simple colours? --> " + colour);

				return colour;

			} else {

				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Player was NOT in the simple colour map! That probably isn't good!");

				return false;

			}

		}

	}

	public boolean canChatInRGBColour(UUID uuid) {

		LocalDataStore store = MultiChatLocal.getInstance().getDataStore();
		Map<UUID, Boolean> colourMap = store.getRGBColourMap();

		synchronized (colourMap) {

			if (colourMap.containsKey(uuid)) {

				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Player is in the rgb colour map!");

				boolean colour = colourMap.get(uuid);

				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Can they use rgb colours? --> " + colour);

				return colour;

			} else {

				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Player was NOT in the rgb colour map! That probably isn't good!");

				return false;

			}

		}

	}

	public String translateColourCodes(String message, boolean rgb) {

		if (rgb) {
			message = MultiChatUtil.translateColorCodes(message);
		} else {
			message = MultiChatUtil.translateColorCodes(message, TranslateMode.SIMPLE);
		}

		if (MultiChatLocal.getInstance().getDataStore().isLegacy()) {
			message = MultiChatUtil.approximateRGBColorCodes(message);
		}

		return message;

	}

	public abstract String processExternalPlaceholders(MultiChatLocalPlayer player, String message);

	public String processMultiChatConfigPlaceholders(MultiChatLocalPlayer player, String message) {

		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();

		synchronized (config.getMultichatPlaceholders()) {

			for (String key : config.getMultichatPlaceholders().keySet()) {

				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] MultiChatPlaceholder Key = " + key);

				String value = config.getMultichatPlaceholders().get(key);
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] MultiChatPlaceholder Value = " + value);

				value = MultiChatLocal.getInstance().getPlaceholderManager().processMultiChatPlaceholders(player.getUniqueId(), value);
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Processed Value to get: " + value);

				// If we are hooked with PAPI then use their placeholders!
				value = processExternalPlaceholders(player, value);
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Processed with external placeholders to get: " + value);

				value = translateColourCodes(value, true);
				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] Translated colour codes to get: " + value);

				MultiChatLocal.getInstance().getConsoleLogger().debug("[LocalChatManager] MESSAGE = : " + message);

				if (message.contains(key)) {
					message = message.replace(key, value);
				}
			}

		}

		return message;

	}

	public Optional<LocalPseudoChannel> getChannelObject(String channelName) {

		Map<String, LocalPseudoChannel> channelObjects = MultiChatLocal.getInstance().getDataStore().getChannelObjects();

		synchronized (channelObjects) {

			if (channelObjects.containsKey(channelName)) {
				return Optional.of(channelObjects.get(channelName));
			} else {
				return Optional.empty();
			}

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
