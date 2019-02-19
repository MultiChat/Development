package xyz.olivermartin.multichat.spigotbridge;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.milkbowl.vault.chat.Chat;

public class SpigotPlaceholderManager {

	public static String buildChatFormat(Player player, String format) {

		format = format.replace("%NAME%", player.getName());
		format = format.replace("%DISPLAYNAME%", "%1$s"); // This might work?
		if (MultiChatSpigot.hookedVault()) {
			Chat chat = MultiChatSpigot.getVaultChat().get();
			format = format.replace("%PREFIX%", chat.getPlayerPrefix(player));
			format = format.replace("%SUFFIX%", chat.getPlayerSuffix(player));
		}
		format = format.replace("%NICK%", NameManager.getInstance().getCurrentName(player.getUniqueId()));
		format = format.replace("%WORLD%", player.getWorld().getName());
		format = format.replace("%SERVER%", Bukkit.getServerName());

		return format + "%2$s";

	}
	
	public static String buildMultiChatPlaceholder(Player player, String format) {
		
		format = format.replace("%NAME%", player.getName());
		format = format.replace("%DISPLAYNAME%", "%1$s"); // This might work?
		if (MultiChatSpigot.hookedVault()) {
			Chat chat = MultiChatSpigot.getVaultChat().get();
			format = format.replace("%PREFIX%", chat.getPlayerPrefix(player));
			format = format.replace("%SUFFIX%", chat.getPlayerSuffix(player));
		}
		format = format.replace("%NICK%", NameManager.getInstance().getCurrentName(player.getUniqueId()));
		format = format.replace("%WORLD%", player.getWorld().getName());
		format = format.replace("%SERVER%", Bukkit.getServerName());

		return format;
		
	}

}
