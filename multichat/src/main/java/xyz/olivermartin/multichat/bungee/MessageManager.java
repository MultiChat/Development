package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

public class MessageManager {

	private static MessageManager instance;

	private static Map<String,String> messages;

	private static String prefix = "&8&l[&2&lM&a&lC&8&l]&f ";

	static {
		instance = new MessageManager();
	}

	public static MessageManager getInstance() {
		return instance;
	}

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
		messages.put("command_announcement_list_item", "&b -> %SPECIAL%");
		messages.put("command_announcement_does_not_exist", "&cSorry, no such announcement found: %SPECIAL%");
		messages.put("command_announcement_removed", "&aRemoved announcement: %SPECIAL%");
		messages.put("command_announcement_stopped", "&aStopped announcement: %SPECIAL%");
		messages.put("command_announcement_stopped_error", "&cSorry, unable to stop announcement: %SPECIAL%");
		messages.put("command_announcement_started", "&aStarted announcement: %SPECIAL%");
		messages.put("command_announcement_started_error", "&cSorry, unable to start announcement: %SPECIAL%");
		messages.put("command_announcement_added", "&aAdded announcement: %SPECIAL%");
		messages.put("command_announcement_added_error", "&cSorry, announcement already exists: %SPECIAL%");
		messages.put("command_announcement_usage", "&aUsage:");

		messages.put("command_bulletin_stopped", "&bBulletins stopped");
		messages.put("command_bulletin_list", "&aList of bulletin messages with index:");
		messages.put("command_bulletin_list_item", "&b -> %SPECIAL%");
		messages.put("command_bulletin_removed", "&bRemoved bulletin");
		messages.put("command_bulletin_started", "&bStarted bulletin");
		messages.put("command_bulletin_added", "&bAdded to bulletin");
		messages.put("command_bulletin_invalid_usage", "&cInvalid command usage!");
		messages.put("command_bulletin_usage", "&aUsage:");
		
		messages.put("command_cast_usage", "&aUsage:");
		messages.put("command_cast_list", "&aList of avaliable casts:");
		messages.put("command_cast_list_item", "&b -> %SPECIAL%");
		messages.put("command_cast_removed", "&aRemoved cast: %SPECIAL%");
		messages.put("command_cast_does_not_exist", "&cSorry, no such cast found: %SPECIAL%");
		messages.put("command_cast_added", "&aAdded cast: %SPECIAL%");
		messages.put("command_cast_added_error", "&cSorry, cast already exists: %SPECIAL%");
		
		messages.put("command_clearchat_self", "&bYour chat has been cleared");
		messages.put("command_clearchat_server", "&bServer chat has been cleared");
		messages.put("command_clearchat_global", "&bGlobal chat has been cleared");
		messages.put("command_clearchat_all", "&bAll chat has been cleared");
		messages.put("command_clearchat_no_permission", "&cYou do not have permission to clear %SPECIAL% chat");
		messages.put("command_clearchat_usage", "&cUsage: /clearchat [self/server/global/all]");

	}

	public static String getMessage(String id) {
		if (!messages.containsKey(id)) return "&cERROR - Please report to plugin developer - No message defined for: " + id;
		return messages.get(id.toLowerCase());
	}

	public static void sendMessage(CommandSender sender, String id) {
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', prefix + getMessage(id))));
	}

	public static void sendSpecialMessage(CommandSender sender, String id, String special) {
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', prefix + getMessage(id).replaceAll("%SPECIAL%", special))));
	}

	public static void sendSpecialMessageWithoutPrefix(CommandSender sender, String id, String special) {
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', getMessage(id).replaceAll("%SPECIAL%", special))));
	}

	public void startup() {

		/* Empty */

	}

}
