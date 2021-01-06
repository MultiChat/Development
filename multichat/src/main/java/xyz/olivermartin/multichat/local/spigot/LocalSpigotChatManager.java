package xyz.olivermartin.multichat.local.spigot;

import org.bukkit.Bukkit;

import me.clip.placeholderapi.PlaceholderAPI;
import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.spigot.hooks.LocalSpigotPAPIHook;

public class LocalSpigotChatManager extends LocalChatManager {

	@Override
	public String processExternalPlaceholders(MultiChatLocalPlayer player, String message) {

		// If we are hooked with PAPI then use their placeholders!
		if (LocalSpigotPAPIHook.getInstance().isHooked()) {
			message = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(player.getUniqueId()), message);
		}

		return message;
	}

}
