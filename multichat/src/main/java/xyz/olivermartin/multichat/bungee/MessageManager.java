package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class MessageManager {

	private static Map<String,String> messages;

	private static String prefix = "&8&l[&2&lM&a&lC&8&l]&f ";

	public MessageManager() {

		messages = new HashMap<String,String>();

		// *** COMMANDS *** //

		messages.put("command_acc_usage", "&aUsage: /acc <chatcolorcode> <namecolorcode>");
		messages.put("command_acc_only_players", "&cOnly players can change chat colours!");
		messages.put("command_acc_updated", "&aAdmin-Chat colours updated!");
		messages.put("command_acc_invalid", "&cInvalid color codes specified: Must be letters a-f or numbers 0-9");
		messages.put("command_acc_invalid_usage", "&cUsage: /acc <chatcolorcode> <namecolorcode>");

		messages.put("command_ac_toggle_on", "&dAdmin chat toggled on!");
		messages.put("command_ac_toggle_off", "&cAdmin chat toggled off!");
		messages.put("command_ac_only_players", "&cOnly players can toggle the chat!");

		messages.put("command_announcement_list", "&aList of avaliable announcements:");
		messages.put("command_announcement_list_item", "&b%SPECIAL%");
		messages.put("command_announcement_does_not_exist", "&cSorry, no such announcement found: %SPECIAL%");
		messages.put("command_announcement_removed", "&aRemoved announcement: %SPECIAL%");
		messages.put("command_announcement_stopped", "&aStopped announcement: %SPECIAL%");
		messages.put("command_announcement_stopped_error", "&cSorry, unable to stop announcement: %SPECIAL%");
		messages.put("command_announcement_started", "&aStarted announcement: %SPECIAL%");
		messages.put("command_announcement_started_error", "&cSorry, unable to start announcement: %SPECIAL%");
		messages.put("command_announcement_added", "&aAdded announcement: %SPECIAL%");
		messages.put("command_announcement_added_error", "&cSorry, announcement already exists: %SPECIAL%");
		messages.put("command_announcement_usage", "&aUsage:");

	}

	public static String getMessage(String id) {
		if (!messages.containsKey(id)) return "&cNo message defined for: " + id;
		return messages.get(id.toLowerCase());
	}

	public static void sendMessage(CommandSender sender, String id) {
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', prefix + getMessage(id))));
	}

	public static void sendSpecialMessage(CommandSender sender, String id, String special) {
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', prefix + getMessage(id).replaceAll("%SPECIAL%", special))));
	}

}
