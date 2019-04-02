package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class ChatControl {

	static {
		mutedPlayers = new HashSet<UUID>();
		ignoreMap = new HashMap<UUID, Set<UUID>>();
		spamMap = new HashMap<UUID, PlayerSpamInfo>();
	}

	private static Set<UUID> mutedPlayers;
	private static Map<UUID, Set<UUID>> ignoreMap;
	private static Map<UUID, PlayerSpamInfo> spamMap;
	
	public static boolean controlLinks = false;
	public static String linkMessage = "[LINK REMOVED]";

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
	 * 
	 * @param input The input message
	 * @param chatType The type of chat the message was sent in
	 * @return The message to send with rules applied, or empty if the chat message should be cancelled
	 */
	@SuppressWarnings("rawtypes")
	public static Optional<String>applyChatRules(String input, String chatType, String playerName) {

		Configuration config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();
		boolean cancel = false;

		if (config.contains("apply_rules_to." + chatType)) {
			if (config.getBoolean("apply_rules_to." + chatType)) {

				List rules = config.getList("regex_rules");

				if (rules != null) {
					for (Object rule : rules) {
						Map dictionary = (Map) rule;
						input = input.replaceAll(String.valueOf( dictionary.get("look_for")), String.valueOf(dictionary.get("replace_with") ));
					}
				}

			}
		}

		if (config.contains("apply_actions_to." + chatType)) {
			if (config.getBoolean("apply_actions_to." + chatType)) {

				List actions = config.getList("regex_actions");

				if (actions != null) {
					for (Object action : actions) {
						Map dictionary = (Map) action;

						if (input.matches(String.valueOf(dictionary.get("look_for")))) {

							if ((Boolean) dictionary.get("cancel")) {
								cancel = true;
							}

							if ((Boolean) dictionary.get("spigot")) {

								ServerInfo server = ProxyServer.getInstance().getPlayer(playerName).getServer().getInfo();
								BungeeComm.sendCommandMessage(String.valueOf(dictionary.get("command")).replaceAll("%PLAYER%", playerName), server);

							} else {
								ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), String.valueOf(dictionary.get("command")).replaceAll("%PLAYER%", playerName)); 
							}


						}

					}
				}

			}
		}

		if (cancel) {
			return Optional.empty();
		} else {
			return Optional.of(input);
		}

	}

	public static boolean isMuted(UUID uuid, String chatType) {

		Configuration config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

		if (!config.getBoolean("mute")) return false;

		if (!mutedPlayers.contains(uuid)) return false;

		if (!config.contains("apply_mute_to." + chatType)) return false;

		if (!config.getBoolean("apply_mute_to." + chatType)) return false;

		return true;

	}

	public static boolean isMutedAnywhere(UUID uuid) {

		Configuration config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

		if (!config.getBoolean("mute")) return false;

		if (!mutedPlayers.contains(uuid)) return false;

		return true;

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
	public static boolean ignores(UUID sender, UUID target, String chatType) {

		Configuration config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

		if (!ignoreMap.containsKey(target)) return false;

		Set<UUID> ignoredPlayers = ignoreMap.get(target);

		if (ignoredPlayers == null) return false;

		if (!ignoredPlayers.contains(sender)) return false;

		if (!config.contains("apply_ignore_to." + chatType)) return false;

		if (!config.getBoolean("apply_ignore_to." + chatType)) return false;


		return true;

	}

	/**
	 * Tests if the target is ignoring the sender, and hence should not receive the message
	 * @param sender The player trying to send a message
	 * @param target The player who will see the message
	 * @return TRUE if the target ignores the sender and the message should not be sent, FALSE otherwise
	 */
	public static boolean ignoresAnywhere(UUID sender, UUID target) {

		if (!ignoreMap.containsKey(target)) return false;

		Set<UUID> ignoredPlayers = ignoreMap.get(target);

		if (ignoredPlayers == null) return false;

		if (!ignoredPlayers.contains(sender)) return false;

		return true;

	}

	public static void ignore(UUID ignorer, UUID ignoree) {

		Set<UUID> ignoredPlayers;

		if (ignoreMap.containsKey(ignorer)) {

			ignoredPlayers = ignoreMap.get(ignorer);

		} else {

			ignoredPlayers = new HashSet<UUID>();

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

		Configuration config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

		if (config.getBoolean("notify_ignore")) {
			MessageManager.sendSpecialMessage(ignorer, "ignore_target", ignoree.getName());
		}

		if (!chatType.equals("private_messages")) return;

		MessageManager.sendMessage(ignoree, "ignore_sender");

	}

	/**
	 * If sessional ignore is enabled, removes any offline players from the ignore map
	 */
	public static void reload() {

		Configuration config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

		if (config.getBoolean("session_ignore")) {

			for (UUID uuid : ignoreMap.keySet()) {

				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

				if (player == null) ignoreMap.remove(uuid);

			}

		}

	}
	
	public static String replaceLinks(String message) {
		if (!controlLinks) return message;
		return message.replaceAll("((https|http):\\/\\/)?(www\\.)?([-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.)+[a-zA-Z]{2,4}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)", linkMessage);
	}

	public static void spamPardonPlayer(UUID uuid) {
		spamMap.remove(uuid);
	}

	/**
	 * 
	 * @return true if the player is spamming and the message should be blocked
	 */
	public static boolean handleSpam(ProxiedPlayer player, String message, String chatType) {
		
		DebugManager.log(player.getName() + " - checking for spam...");

		Configuration config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();
		
		if (player.hasPermission("multichat.spam.bypass")) return false;
		
		DebugManager.log(player.getName() + " - does not have bypass perm...");

		if (!config.getBoolean("anti_spam")) return false;
		
		DebugManager.log(player.getName() + " - anti spam IS enabled...");

		if (!config.contains("apply_anti_spam_to." + chatType)) return false;

		if (!config.getBoolean("apply_anti_spam_to." + chatType)) return false;
		
		DebugManager.log(player.getName() + " - anti spam IS enabled for " + chatType + "...");

		if (!spamMap.containsKey(player.getUniqueId())) spamMap.put(player.getUniqueId(), new PlayerSpamInfo());

		PlayerSpamInfo spamInfo = spamMap.get(player.getUniqueId());

		boolean spam = spamInfo.checkSpam(message);

		if (spam) {
			
			DebugManager.log(player.getName() + " - PLAYER IS SPAMMING!");

			MessageManager.sendSpecialMessage(player, "anti_spam_cooldown", String.valueOf(spamInfo.getCooldownSeconds()));
			
			DebugManager.log(player.getName() + " - sent cooldown message to player...");

			if (spamInfo.getSpamTriggerCount() >= config.getInt("anti_spam_trigger")) {
				
				DebugManager.log(player.getName() + " - they have set off the trigger...");

				spamInfo.resetSpamTriggerCount();

				if (config.getBoolean("anti_spam_action")) {
					
					DebugManager.log(player.getName() + " - trigger IS enabled...");

					if (config.getBoolean("anti_spam_spigot")) {
						ServerInfo server = player.getServer().getInfo();
						BungeeComm.sendCommandMessage(config.getString("anti_spam_command").replaceAll("%PLAYER%", player.getName()), server);
					} else {
						ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), config.getString("anti_spam_command").replaceAll("%PLAYER%", player.getName())); 
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
		long messageTimeBuffer[] = {0L, 0L, 0L};
		int sameMessageCounter = 0;
		String lastMessage = "";

		/**
		 * 
		 * @return true if the user is spamming and message should be cancelled
		 */
		public boolean checkSpam(String message) {

			boolean spam = false;
			long currentTime = System.currentTimeMillis();
			Configuration config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

			// If the user triggered anti-spam, check if they are still on cooldown
			if (currentTime - lastSpamTime < (1000 * config.getInt("anti_spam_cooldown"))) return true;

			long deltaTime = currentTime - messageTimeBuffer[2];

			if (lastMessage.equalsIgnoreCase(message)) {
				// This is a hard coded test. If the same message is sent 4 times in a row, it is spam...
				// However; this extra bit states that if it has been longer than 10 times the usual spam time
				// then this should not be considered spam. And hence the counter is reset.
				if ((currentTime - messageTimeBuffer[0]) < (1000 * config.getInt("anti_spam_time")*10)) {
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
			if (deltaTime < (1000 * config.getInt("anti_spam_time"))
					|| !(sameMessageCounter + 1 < config.getInt("spam_same_message"))) {
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
			Configuration config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();
			return config.getInt("anti_spam_cooldown") - ((System.currentTimeMillis() - lastSpamTime)/1000);
		}

	}

}
