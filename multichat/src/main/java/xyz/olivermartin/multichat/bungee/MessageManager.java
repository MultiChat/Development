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

	private static Map<String,String> defaultMessages;

	private static String prefix;

	static {

		defaultMessages = new HashMap<String,String>();

		prefix = "&8&l[&2&lM&a&lC&8&l]&f ";

		// *** COMMANDS *** //

		defaultMessages.put("command_acc_usage", "&aUsage: /acc <chatcolorcode> <namecolorcode>");
		defaultMessages.put("command_acc_only_players", "&cOnly players can change chat colours!");
		defaultMessages.put("command_acc_updated", "&aAdmin-Chat colours updated!");
		defaultMessages.put("command_acc_invalid", "&cInvalid color codes specified: Must be letters a-f or numbers 0-9");
		defaultMessages.put("command_acc_invalid_usage", "&cUsage: /acc <chatcolorcode> <namecolorcode>");

		defaultMessages.put("command_ac_toggle_on", "&dAdmin chat toggled on!");
		defaultMessages.put("command_ac_toggle_off", "&cAdmin chat toggled off!");
		defaultMessages.put("command_ac_only_players", "&cOnly players can toggle the chat!");

		defaultMessages.put("command_announcement_list", "&aList of avaliable announcements:");
		defaultMessages.put("command_announcement_list_item", "&b -> %SPECIAL%");
		defaultMessages.put("command_announcement_does_not_exist", "&cSorry, no such announcement found: %SPECIAL%");
		defaultMessages.put("command_announcement_removed", "&aRemoved announcement: %SPECIAL%");
		defaultMessages.put("command_announcement_stopped", "&aStopped announcement: %SPECIAL%");
		defaultMessages.put("command_announcement_stopped_error", "&cSorry, unable to stop announcement: %SPECIAL%");
		defaultMessages.put("command_announcement_started", "&aStarted announcement: %SPECIAL%");
		defaultMessages.put("command_announcement_started_error", "&cSorry, unable to start announcement: %SPECIAL%");
		defaultMessages.put("command_announcement_added", "&aAdded announcement: %SPECIAL%");
		defaultMessages.put("command_announcement_added_error", "&cSorry, announcement already exists: %SPECIAL%");
		defaultMessages.put("command_announcement_usage", "&aUsage:");

		defaultMessages.put("command_bulletin_stopped", "&bBulletins stopped");
		defaultMessages.put("command_bulletin_list", "&aList of bulletin messages with index:");
		defaultMessages.put("command_bulletin_list_item", "&b -> %SPECIAL%");
		defaultMessages.put("command_bulletin_removed", "&bRemoved bulletin");
		defaultMessages.put("command_bulletin_started", "&bStarted bulletin");
		defaultMessages.put("command_bulletin_added", "&bAdded to bulletin");
		defaultMessages.put("command_bulletin_invalid_usage", "&cInvalid command usage!");
		defaultMessages.put("command_bulletin_usage", "&aUsage:");

		defaultMessages.put("command_cast_usage", "&aUsage:");
		defaultMessages.put("command_cast_list", "&aList of avaliable casts:");
		defaultMessages.put("command_cast_list_item", "&b -> %SPECIAL%");
		defaultMessages.put("command_cast_removed", "&aRemoved cast: %SPECIAL%");
		defaultMessages.put("command_cast_does_not_exist", "&cSorry, no such cast found: %SPECIAL%");
		defaultMessages.put("command_cast_added", "&aAdded cast: %SPECIAL%");
		defaultMessages.put("command_cast_added_error", "&cSorry, cast already exists: %SPECIAL%");

		defaultMessages.put("command_clearchat_self", "&bYour chat has been cleared");
		defaultMessages.put("command_clearchat_server", "&bServer chat has been cleared");
		defaultMessages.put("command_clearchat_global", "&bGlobal chat has been cleared");
		defaultMessages.put("command_clearchat_all", "&bAll chat has been cleared");
		defaultMessages.put("command_clearchat_no_permission", "&cYou do not have permission to clear %SPECIAL% chat");
		defaultMessages.put("command_clearchat_usage", "&cUsage: /clearchat [self/server/global/all]");

		defaultMessages.put("command_display_desc", "&3Display a message to the entire network");
		defaultMessages.put("command_display_usage", "&bUsage /display <message>");

		defaultMessages.put("command_freezechat_thawed", "&b&lChat was &3&lTHAWED &b&lby &a&l%SPECIAL%");
		defaultMessages.put("command_freezechat_frozen", "&b&lChat was &3&lFROZEN &b&lby &a&l%SPECIAL%");

		defaultMessages.put("command_gc_toggle_on", "&aGroup chat toggled on!");
		defaultMessages.put("command_gc_toggle_off", "&cGroup chat toggled off!");
		defaultMessages.put("command_gc_only_players_toggle", "&cOnly players can toggle the chat!");
		defaultMessages.put("command_gc_no_longer_exists", "&cSorry your selected chat no longer exists, please select a chat with /group <group name>");
		defaultMessages.put("command_gc_no_chat_selected", "&cPlease select the chat you wish to message using /group <group name>");
		defaultMessages.put("command_gc_only_players_speak", "&cOnly players can speak in group chats");

		defaultMessages.put("command_global_enabled_1", "&3GLOBAL CHAT ENABLED");
		defaultMessages.put("command_global_enabled_2", "&bYou will see messages from players on all servers!");
		defaultMessages.put("command_global_only_players", "&cOnly players can change their chat state");

		defaultMessages.put("command_group_selected", "&aYour /gc messages will now go to group: %SPECIAL%");
		defaultMessages.put("command_group_not_a_member", "&cSorry you aren't a member of group: %SPECIAL%");
		defaultMessages.put("command_group_does_not_exist", "&cSorry the following group chat does not exist: %SPECIAL%");
		defaultMessages.put("command_group_only_players_select", "&cOnly players can select a group chat");
		defaultMessages.put("command_group_incorrect_usage", "&cIncorrect command usage, use /group to see a list of commands!");
		defaultMessages.put("command_group_member_list", "&a&lShowing members of group: %SPECIAL%");
		defaultMessages.put("command_group_member_list_item", "&b- %SPECIAL%");
		defaultMessages.put("command_group_member_list_item_admin", "&b- &b&o%SPECIAL%");
		defaultMessages.put("command_group_spy_all_disabled_1", "&cGlobal group spy disabled");
		defaultMessages.put("command_group_spy_all_disabled_2", "&cAny groups you previously activated spy for will still be spied on!");
		defaultMessages.put("command_group_spy_all_disabled_3", "&cDisable spy for individual groups with /group spy <groupname>");
		defaultMessages.put("command_group_spy_all_enabled", "&aGlobal group spy enabled for all group chats!");
		defaultMessages.put("command_group_spy_no_permission", "&cSorry this command does not exist, use /group");
		defaultMessages.put("command_group_spy_off", "&cYou are no longer spying on: %SPECIAL%");
		defaultMessages.put("command_group_spy_on", "&aYou are now spying on: %SPECIAL%");
		defaultMessages.put("command_group_spy_already_a_member", "&cYou are already a member of this chat so can't spy on it!");
		defaultMessages.put("command_group_spy_does_not_exist", "&cSorry this group chat does not exist!");
		defaultMessages.put("command_group_created", "&aYou successfully created, joined, and selected the group: %SPECIAL%");
		defaultMessages.put("command_group_already_exists", "&cSorry the following group chat already exists: %SPECIAL%");
		defaultMessages.put("command_group_max_length", "&cSorry group name cannot exceed 20 characters!");
		defaultMessages.put("command_group_create_no_permission", "&cSorry you do not have permission to create new group chats");
		defaultMessages.put("command_group_joined", "&aYou successfully joined and selected the group: %SPECIAL%");
		defaultMessages.put("command_group_formal_not_owner", "&cSorry this command can only be used by the group chat owner");
		defaultMessages.put("command_group_formal_already_formal", "&cSorry this chat is already a formal group chat: %SPECIAL%");
		defaultMessages.put("command_group_formal_not_admin", "&cSorry this command can only be used by group admins/owners");
		defaultMessages.put("command_group_max_length_password", "&cSorry neither group name or password must exceed 20 characters");
		defaultMessages.put("command_group_transfer_not_member", "&cThis player is not already a member of the group!");
		defaultMessages.put("command_group_transfer_not_owner", "&cSorry you are not the owner of this chat!");
		defaultMessages.put("command_group_transfer_not_informal", "&cThis command can only be used on informal chats!");
		defaultMessages.put("command_group_player_not_online", "&cThis player is not online!");
		defaultMessages.put("command_group_formal_only_admin", "&cYou can't step down as a group admin because you are the only one!");
		defaultMessages.put("command_group_formal_cannot_demote", "&cYou can't demote another group admin!");
		defaultMessages.put("command_group_not_formal", "&cThis command can only be used on formal chats!");
		defaultMessages.put("command_group_banned", "&cYou were banned from group: %SPECIAL%");
		defaultMessages.put("command_group_unbanned", "&aYou were unbanned from group: %SPECIAL%");
		defaultMessages.put("command_group_cannot_ban_admin", "&cYou can't ban a group admin!");
		defaultMessages.put("command_group_color_invalid", "&cInvalid color codes specified: Must be letters a-f or numbers 0-9");
		defaultMessages.put("command_group_color_usage", "&cUsage: /group color/colour <group name> <chatcolorcode> <namecolorcode>");
		defaultMessages.put("command_group_only_players", "&cOnly players can use group chats");

		defaultMessages.put("command_grouplist_list", "&a&lGroup List:");
		defaultMessages.put("command_grouplist_list_item", "&b- %SPECIAL%");

		defaultMessages.put("command_helpme_desc", "&4Request help from a staff member!");
		defaultMessages.put("command_helpme_usage", "&cUsage: /HelpMe <Message>");
		defaultMessages.put("command_helpme_sent", "&cYour request for help has been sent to all online staff :)");
		defaultMessages.put("command_helpme_only_players", "&4Only players can request help!");
		defaultMessages.put("command_helpme_format", "&c&l<< &4HELPME &c&l>> &f&o%SPECIAL%");

		defaultMessages.put("command_local_enabled_1", "&3LOCAL CHAT ENABLED");
		defaultMessages.put("command_local_enabled_2", "&bYou will only see messages from players on the same server!");
		defaultMessages.put("command_local_only_players", "&cOnly players can change their chat state");

		defaultMessages.put("command_mcc_usage", "&aUsage: /mcc <chatcolorcode> <namecolorcode>");
		defaultMessages.put("command_mcc_only_players", "&cOnly players can change chat colours!");
		defaultMessages.put("command_mcc_updated", "&aMod-Chat colours updated!");
		defaultMessages.put("command_mcc_invalid", "&cInvalid color codes specified: Must be letters a-f or numbers 0-9");
		defaultMessages.put("command_mcc_invalid_usage", "&cUsage: /mcc <chatcolorcode> <namecolorcode>");

		defaultMessages.put("command_mc_toggle_on", "&bMod chat toggled on!");
		defaultMessages.put("command_mc_toggle_off", "&cMod chat toggled off!");
		defaultMessages.put("command_mc_only_players", "&cOnly players can toggle the chat!");

		defaultMessages.put("command_msg_usage", "&bUsage: /msg <player> [message]");
		defaultMessages.put("command_msg_usage_toggle", "&bUsing /msg <player> with no message will toggle chat to go to that player");
		defaultMessages.put("command_msg_toggle_on", "&ePrivate chat toggled on! [You -> %SPECIAL%] (Type the same command to disable the toggle)");
		defaultMessages.put("command_msg_toggle_off", "&cPrivate chat toggled off!");
		defaultMessages.put("command_msg_only_players", "&cOnly players can toggle the chat!");
		defaultMessages.put("command_msg_not_online", "&cSorry this person is not online!");
		defaultMessages.put("command_msg_disabled_target", "&cSorry private messages are disabled on the target player's server!");
		defaultMessages.put("command_msg_disabled_sender", "&cSorry private messages are disabled on this server!");

		defaultMessages.put("command_reply_usage", "&bUsage: /r <message>");
		defaultMessages.put("command_reply_desc", "&bReply to the person who you private messaged most recently");
		defaultMessages.put("command_reply_no_one_to_reply_to", "&cYou have no one to reply to!");
		defaultMessages.put("command_reply_only_players", "&cOnly players can reply to private messages");

		defaultMessages.put("command_socialspy_disabled", "&cSocial Spy Disabled");
		defaultMessages.put("command_socialspy_enabled", "&bSocial Spy Enabled");
		defaultMessages.put("command_socialspy_usage", "&bUsage: /socialspy");
		defaultMessages.put("command_socialspy_desc", "&bToggles if the user has social spy enabled or disabled");
		defaultMessages.put("command_socialspy_only_players", "&cOnly players can toggle socialspy");

		defaultMessages.put("command_stafflist_list", "&a&lOnline Staff");
		defaultMessages.put("command_stafflist_list_item", "&b- %SPECIAL%");
		defaultMessages.put("command_stafflist_list_server", "&a%SPECIAL%");

		defaultMessages.put("command_usecast_usage", "&aUsage:");
		defaultMessages.put("command_usecast_does_not_exist", "&cSorry, no such cast found: %SPECIAL%");

		// *** GROUPS *** //

		defaultMessages.put("groups_toggled_but_no_longer_exists_1", "&cYou have toggled group chat but selected group doesn't exist!");
		defaultMessages.put("groups_toggled_but_no_longer_exists_2", "&cPlease select the chat you wish to message using /group <group name> or disable the toggle with /gc");
		defaultMessages.put("groups_password_protected", "&cSorry this group chat is password protected: %SPECIAL%");
		defaultMessages.put("groups_password_incorrect", "&cSorry incorrect password for: %SPECIAL%");
		defaultMessages.put("groups_already_joined", "&cSorry you are already a member of the group: %SPECIAL%");
		defaultMessages.put("groups_banned", "&cSorry you are banned from the group: %SPECIAL%");
		defaultMessages.put("groups_quit", "&aYou successfully left the group: %SPECIAL%");
		defaultMessages.put("groups_cannot_quit_owner_1", "&cSorry you cannot leave as you created the group!: %SPECIAL%");
		defaultMessages.put("groups_cannot_quit_owner_2", "&cPlease transfer group ownership first! Use /group transfer %SPECIAL% <playername>");
		defaultMessages.put("groups_cannot_quit_admin_1", "&cSorry you cannot leave as you are the only group admin!: %SPECIAL%");
		defaultMessages.put("groups_cannot_quit_admin_2", "&cPlease appoint a new admin using /group admin %SPECIAL% <playername>");

		// *** FREEZECHAT *** //

		defaultMessages.put("freezechat_frozen", "&bSorry chat has been &3&lFROZEN");

	}

	public static String getMessage(String id) {
		if (!defaultMessages.containsKey(id)) return "&cERROR - Please report to plugin developer - No message defined for: " + id;
		return defaultMessages.get(id.toLowerCase());
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

}
