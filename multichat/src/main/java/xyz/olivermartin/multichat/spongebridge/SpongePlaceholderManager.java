package xyz.olivermartin.multichat.spongebridge;

import org.spongepowered.api.entity.living.player.Player;

public class SpongePlaceholderManager {

	public static String buildChatFormat(Player player, String format) {

		String nickname;
		String prefix = "";
		String suffix = "";

		if (MultiChatSponge.nicknames.containsKey(player.getUniqueId())) {
			nickname = MultiChatSponge.nicknames.get(player.getUniqueId());
		} else {
			nickname =  player.getName();
		}

		if (player.getOption("prefix").isPresent()) {
			prefix = player.getOption("prefix").get();
		}

		if (player.getOption("suffix").isPresent()) {
			suffix = player.getOption("suffix").get();
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


		return format;

	}

}
