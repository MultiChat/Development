package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Message Manager
 * <p>Used to display all plugin messages to players</p>
 * 
 * @author Oliver Martin (Revilo410)
 */
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
		
		messages.put("command_display_desc", "&3Display a message to the entire network");
		messages.put("command_display_usage", "&bUsage /display <message>");
		
		messages.put("command_freezechat_thawed", "&b&lChat was &3&lTHAWED &b&lby &a&l%SPECIAL%");
		messages.put("command_freezechat_frozen", "&b&lChat was &3&lFROZEN &b&lby &a&l%SPECIAL%");
		
		messages.put("command_gc_toggle_on", "&aGroup chat toggled on!");
		messages.put("command_gc_toggle_off", "&cGroup chat toggled off!");
		messages.put("command_gc_only_players_toggle", "&cOnly players can toggle the chat!");
		messages.put("command_gc_no_longer_exists", "&cSorry your selected chat no longer exists, please select a chat with /group <group name>");
		messages.put("command_gc_no_chat_selected", "&cPlease select the chat you wish to message using /group <group name>");
		messages.put("command_gc_only_players_speak", "&cOnly players can speak in group chats");
		
		messages.put("command_global_enabled_1", "&3GLOBAL CHAT ENABLED");
		messages.put("command_global_enabled_2", "&bYou will see messages from players on all servers!");
		messages.put("command_global_only_players", "&cOnly players can change their chat state");
		
		messages.put("command_group_selected", "&aYour /gc messages will now go to group: %SPECIAL%");
		messages.put("command_group_not_a_member", "&cSorry you aren't a member of group: %SPECIAL%");
		messages.put("command_group_does_not_exist", "&cSorry the following group chat does not exist: %SPECIAL%");
		messages.put("command_group_only_players_select", "&cOnly players can select a group chat");
		messages.put("command_group_incorrect_usage", "&cIncorrect command usage, use /group to see a list of commands!");
		messages.put("command_group_member_list", "&a&lShowing members of group: %SPECIAL%");
		messages.put("command_group_member_list_item", "&b- %SPECIAL%");
		messages.put("command_group_member_list_item_admin", "&b- &b&o%SPECIAL%");
		messages.put("command_group_spy_all_disabled_1", "&cGlobal group spy disabled");
		messages.put("command_group_spy_all_disabled_2", "&cAny groups you previously activated spy for will still be spied on!");
		messages.put("command_group_spy_all_disabled_3", "&cDisable spy for individual groups with /group spy <groupname>");
		messages.put("command_group_spy_all_enabled", "&aGlobal group spy enabled for all group chats!");
		messages.put("command_group_spy_no_permission", "&cSorry this command does not exist, use /group");
		messages.put("command_group_spy_off", "&cYou are no longer spying on: %SPECIAL%");
		messages.put("command_group_spy_on", "&aYou are now spying on: %SPECIAL%");
		messages.put("command_group_spy_already_a_member", "&cYou are already a member of this chat so can't spy on it!");
		messages.put("command_group_spy_does_not_exist", "&cSorry this group chat does not exist!");
		messages.put("command_group_", "&");
		messages.put("command_group_", "&");
		messages.put("command_group_", "&");
		messages.put("command_group_", "&");
		
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
