package xyz.olivermartin.multichat.bungee;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;

public class ChatControl {

	static {
		mutedPlayers = new HashSet<UUID>();
	}

	private static Set<UUID> mutedPlayers;

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

}
