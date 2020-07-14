package xyz.olivermartin.multichat.local.spigot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.clip.placeholderapi.PlaceholderAPI;
import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.spigot.hooks.LocalSpigotPAPIHook;

public class LocalSpigotChatManager extends LocalChatManager {

	@Override
	public String translateColourCodes(String message, boolean rgb) {

		if (rgb) {
			message = MultiChatLocal.getInstance().getChatManager().reformatRGB(message);
			return ChatColor.translateAlternateColorCodes('&', message);
		} else {
			message = message.replaceAll("&(?=[a-f,0-9,k-o,r])", "§");
			return message;
		}

	}

	@Override
	public String processExternalPlaceholders(MultiChatLocalPlayer player, String message) {

		// If we are hooked with PAPI then use their placeholders!
		if (LocalSpigotPAPIHook.getInstance().isHooked()) {
			message = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(player.getUniqueId()), message);
		}

		return message;
	}

}
