package xyz.olivermartin.multichat.spongebridge;

import org.spongepowered.api.entity.living.player.Player;

public class SpongePlaceholderManager {

	public static String buildChatFormat(Player player, String format) {

		String nickname;
		String prefix = "";
		String suffix = "";

		if (MultiChatSponge.nicknames.containsKey(player.getUniqueId())) {
			if (MultiChatSponge.showNicknamePrefix) {
				nickname = MultiChatSponge.nicknamePrefix + MultiChatSponge.nicknames.get(player.getUniqueId());
			} else {
				nickname = MultiChatSponge.nicknames.get(player.getUniqueId());
			}
		} else {
			nickname =  player.getName();
		}
		
		DebugManager.log("PLAYERS NICKNAME / REALNAME IS: " + nickname);

		if (player.getOption("prefix").isPresent()) {
			prefix = player.getOption("prefix").get();
			DebugManager.log("LOADED PREFIX: " + prefix);
			DebugManager.log("PREFIX CONTAINS § SYMBOL? : " + prefix.contains("§"));
		}

		if (player.getOption("suffix").isPresent()) {
			suffix = player.getOption("suffix").get();
			DebugManager.log("LOADED SUFFIX: " + suffix);
			DebugManager.log("SUFFIX CONTAINS § SYMBOL? : " + suffix.contains("§"));
		}

		// Replace the displayname placeholder with the displayname format
		format = format.replace("%DISPLAYNAME%", MultiChatSponge.displayNameFormatLastVal);

		format = format.replace("%NAME%", player.getName());
		format = format.replace("%PREFIX%", prefix);
		format = format.replace("%SUFFIX%", suffix);
		format = format.replace("%NICK%", nickname);
		format = format.replace("%WORLD%", player.getWorld().getName());
		format = format.replace("%SERVER%", MultiChatSponge.serverName);
		format = format.replace("%SERVER%", MultiChatSponge.serverName);

		DebugManager.log("PLACEHOLDER MANAGER IS RETURNING THIS FORMAT: " + format);
		
		return format;

	}

}
