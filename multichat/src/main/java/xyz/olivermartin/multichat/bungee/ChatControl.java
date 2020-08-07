package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.common.MessageType;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.config.ProxyChatControl;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

// TODO: Refactor ChatControl some more at some point
public class ChatControl {

	static {
		mutedPlayers = new HashSet<>();
		ignoreMap = new HashMap<>();
		spamMap = new HashMap<>();
	}

	private static Set<UUID> mutedPlayers;
	private static Map<UUID, Set<UUID>> ignoreMap;
	private static Map<UUID, PlayerSpamInfo> spamMap;

	public static Set<UUID> getMutedPlayers() {
		return mutedPlayers;
	}

	public static void setMutedPlayers(Set<UUID> mutedPlayers) {
		ChatControl.mutedPlayers = mutedPlayers;
	}

	public static Map<UUID, Set<UUID>> getIgnoreMap() {
		return ignoreMap;
	}

	public static void setIgnoreMap(Map<UUID, Set<UUID>> ignoreMap) {
		ChatControl.ignoreMap = ignoreMap;
	}

	/**
	 * @param commandSender the sender that rules should be applied for or null
	 * @param input The input message
	 * @param messageType the type of message to be checked against
	 * @return The message to send with rules applied, or empty if the chat message should be cancelled
	 */
	public static Optional<String> applyChatRules(CommandSender commandSender, String input, MessageType messageType) {
		input = ProxyConfigs.CHAT_CONTROL.applyRegexRules(commandSender, input, messageType);
		if (commandSender != null && ProxyConfigs.CHAT_CONTROL.regexActionsCancel(commandSender, input, messageType))
			return Optional.empty();
		return Optional.of(input);
	}

	public static boolean isMuted(UUID uuid, MessageType messageType) {
		return ProxyConfigs.CHAT_CONTROL.isMute()
				&& mutedPlayers.contains(uuid)
				&& ProxyConfigs.CHAT_CONTROL.applyMuteTo(messageType);
	}

	public static boolean isMutedAnywhere(UUID uuid) {
		return ProxyConfigs.CHAT_CONTROL.isMute() && mutedPlayers.contains(uuid);
	}

	public static void mute(UUID uuid) {

		mutedPlayers.add(uuid);

	}

	public static void unmute(UUID uuid) {

		mutedPlayers.remove(uuid);

	}

	/**
	 * Tests if the target is ignoring the sender, and hence should not receive the message
	 * @param sender The player trying to send a message
	 * @param target The player who will see the message
	 * @return TRUE if the target ignores the sender and the message should not be sent, FALSE otherwise
	 */
	public static boolean ignores(UUID sender, UUID target, MessageType messageType) {
		return ignoresAnywhere(sender, target) && ProxyConfigs.CHAT_CONTROL.applyIgnoreTo(messageType);
	}

	/**
	 * Tests if the target is ignoring the sender, and hence should not receive the message
	 * @param sender The player trying to send a message
	 * @param target The player who will see the message
	 * @return TRUE if the target ignores the sender and the message should not be sent, FALSE otherwise
	 */
	public static boolean ignoresAnywhere(UUID sender, UUID target) {
		Set<UUID> ignoredPlayers = ignoreMap.get(target);
		return ignoredPlayers != null && ignoredPlayers.contains(sender);
	}

	public static void ignore(UUID ignorer, UUID ignoree) {

		Set<UUID> ignoredPlayers;

		if (ignoreMap.containsKey(ignorer)) {

			ignoredPlayers = ignoreMap.get(ignorer);

		} else {

			ignoredPlayers = new HashSet<>();

		}

		ignoredPlayers.add(ignoree);
		ignoreMap.put(ignorer, ignoredPlayers);

	}

	public static void unignore(UUID ignorer, UUID ignoree) {

		Set<UUID> ignoredPlayers;

		if (ignoreMap.containsKey(ignorer)) {

			ignoredPlayers = ignoreMap.get(ignorer);

		} else {

			return;

		}

		ignoredPlayers.remove(ignoree);

		if (ignoredPlayers.size() < 1) {
			ignoreMap.remove(ignorer);
		} else {
			ignoreMap.put(ignorer, ignoredPlayers);
		}

	}

	public static void unignoreAll(UUID ignorer) {

		ignoreMap.remove(ignorer);

	}

	public static void sendIgnoreNotifications(CommandSender ignorer, CommandSender ignoree, String chatType) {
		if (ProxyConfigs.CHAT_CONTROL.isNotifyIgnore()) {
			MessageManager.sendSpecialMessage(ignorer, "ignore_target", ignoree.getName());
		}

		if (!chatType.equals("private_messages")) return;

		MessageManager.sendMessage(ignoree, "ignore_sender");

	}

	/**
	 * If sessional ignore is enabled, removes any offline players from the ignore map
	 */
	public static void reload() {
		if (!ProxyConfigs.CHAT_CONTROL.isSessionIgnore())
			return;

		ignoreMap.keySet().removeIf(uuid -> ProxyServer.getInstance().getPlayer(uuid) == null);
	}

	public static void spamPardonPlayer(UUID uuid) {
		spamMap.remove(uuid);
	}

	/**
	 * 
	 * @return true if the player is spamming and the message should be blocked
	 */
	public static boolean handleSpam(ProxiedPlayer player, String message, MessageType messageType) {

		DebugManager.log(player.getName() + " - checking for spam...");

		ProxyChatControl config = ProxyConfigs.CHAT_CONTROL;

		if (player.hasPermission("multichat.spam.bypass")) return false;

		DebugManager.log(player.getName() + " - does not have bypass perm...");

		if (!config.isAntiSpam()) return false;

		DebugManager.log(player.getName() + " - anti spam IS enabled...");

		if (!config.applyAntiSpamTo(messageType)) return false;

		DebugManager.log(player.getName() + " - anti spam IS enabled for " + messageType.toString() + "...");

		if (!spamMap.containsKey(player.getUniqueId())) spamMap.put(player.getUniqueId(), new PlayerSpamInfo());

		PlayerSpamInfo spamInfo = spamMap.get(player.getUniqueId());

		boolean spam = spamInfo.checkSpam(message);

		if (spam) {

			DebugManager.log(player.getName() + " - PLAYER IS SPAMMING!");

			MessageManager.sendSpecialMessage(player, "anti_spam_cooldown", String.valueOf(spamInfo.getCooldownSeconds()));

			DebugManager.log(player.getName() + " - sent cooldown message to player...");

			if (spamInfo.getSpamTriggerCount() >= config.getAntiSpamTrigger()) {

				DebugManager.log(player.getName() + " - they have set off the trigger...");

				spamInfo.resetSpamTriggerCount();

				if (config.isAntiSpamAction()) {

					DebugManager.log(player.getName() + " - trigger IS enabled...");

					if (config.isAntiSpamSpigot()) {
						ServerInfo server = player.getServer().getInfo();
						ProxyLocalCommunicationManager.sendCommandMessage(config.getAntiSpamCommand().replaceAll("%PLAYER%", player.getName()), server);
					} else {
						ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), config.getAntiSpamCommand().replaceAll("%PLAYER%", player.getName()));
					}

				}

			}

		}

		DebugManager.log(player.getName() + " - returning " + spam);

		return spam;

	}

	public static class PlayerSpamInfo {

		int spamTriggerCount = 0;
		long lastSpamTime = 0L;
		long[] messageTimeBuffer = {0L, 0L, 0L};
		int sameMessageCounter = 0;
		String lastMessage = "";

		/**
		 * 
		 * @return true if the user is spamming and message should be cancelled
		 */
		public boolean checkSpam(String message) {

			boolean spam = false;
			long currentTime = System.currentTimeMillis();
			int antiSpamTime = ProxyConfigs.CHAT_CONTROL.getAntiSpamTime();

			// If the user triggered anti-spam, check if they are still on cooldown
			if (currentTime - lastSpamTime < (1000 * ProxyConfigs.CHAT_CONTROL.getAntiSpamCoolDown())) return true;

			long deltaTime = currentTime - messageTimeBuffer[2];

			if (lastMessage.equalsIgnoreCase(message)) {
				// This is a hard coded test. If the same message is sent 4 times in a row, it is spam...
				// However; this extra bit states that if it has been longer than 10 times the usual spam time
				// then this should not be considered spam. And hence the counter is reset.
				if ((currentTime - messageTimeBuffer[0]) < (1000 * antiSpamTime*10)) {
					sameMessageCounter++;
				} else {
					sameMessageCounter = 0;
				}
			} else {
				sameMessageCounter = 0;
				lastMessage = message;
			}

			rotateMessages(currentTime);

			// Max messages in time limit or same message in row check
			if (deltaTime < (1000 * antiSpamTime)
					|| !(sameMessageCounter + 1 < ProxyConfigs.CHAT_CONTROL.getSpamSameMessage())) {
				spam = true;
				lastSpamTime = currentTime;
				spamTriggerCount++;
			}

			return spam;
		}

		private void rotateMessages(long currentTime) {
			messageTimeBuffer[2] = messageTimeBuffer[1];
			messageTimeBuffer[1] = messageTimeBuffer[0];
			messageTimeBuffer[0] = currentTime;
		}

		public int getSpamTriggerCount() {
			return spamTriggerCount;
		}

		public void resetSpamTriggerCount() {
			spamTriggerCount = spamTriggerCount - 1;
		}

		public long getCooldownSeconds() {
			return ProxyConfigs.CHAT_CONTROL.getAntiSpamCoolDown() - ((System.currentTimeMillis() - lastSpamTime)/1000);
		}

	}

}
