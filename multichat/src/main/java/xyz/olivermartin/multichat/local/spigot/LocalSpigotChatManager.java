package xyz.olivermartin.multichat.local.spigot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.clip.placeholderapi.PlaceholderAPI;
import xyz.olivermartin.multichat.bungee.MultiChatUtil;
import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.spigot.hooks.LocalSpigotPAPIHook;

public class LocalSpigotChatManager extends LocalChatManager {

	@Override
	public String translateColourCodes(String message, boolean rgb) {

		if (rgb) {
			message = MultiChatLocal.getInstance().getChatManager().reformatRGB(message);

			// LEGACY HACK
			if (MultiChatLocal.getInstance().getDataStore().isLegacy()) {
				message = message.replaceAll("&(?=[a-f0-9k-orx])", "§");
				message = MultiChatUtil.approximateHexCodes(message);
			}

			return ChatColor.translateAlternateColorCodes('&', message);
		} else {
			message = message.replaceAll("&(?=[a-f0-9k-or])", "§");
			return message;
		}

	}

	@Override
	public String processExternalPlaceholders(MultiChatLocalPlayer player, String message) {
		// If we are hooked with PAPI then use their placeholders!
		message = ChatColor.translateAlternateColorCodes('&', message);

		// Unicode between U+E000 and U+F8FF is designated for private use. Unlikely to run into issues.
		message = message.replace("%1$s", "\uEF0D").replace("%2$s", "\uEF0F");

		if (LocalSpigotPAPIHook.getInstance().isHooked()) {
			message = PlaceholderAPI.setPlaceholders(Bukkit.getPlayer(player.getUniqueId()), message);
		}

		message = message.replace("\uEF0D", "%1$s").replace("\uEF0F", "%2$s");

		return message;
	}

}