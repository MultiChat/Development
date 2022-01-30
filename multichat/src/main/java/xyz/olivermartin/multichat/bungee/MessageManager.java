package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;
import org.bukkit.entity.Player;

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

		// *** CONSOLE LOGS *** //

		defaultMessages.put("console_main_prefix", "&8[&2M&aC&8]&f ");
		defaultMessages.put("console_chat_prefix", "&fCHAT &f> ");
		defaultMessages.put("console_modchat_prefix", "&3STAFFCHAT &f> &3");
		defaultMessages.put("console_adminchat_prefix", "&5STAFFCHAT &f> &5");
		defaultMessages.put("console_groupchat_prefix", "&2GROUPCHAT &f> &2");
		defaultMessages.put("console_display_prefix", "&fDISPLAY &f> ");
		defaultMessages.put("console_socialspy_prefix", "&cSOCIALSPY &f> &c");
		defaultMessages.put("console_helpme_prefix", "&4HELPME &f> &4");


		// *** PREFIX *** //

		defaultMessages.put("prefix", "&8&l[&2&lM&a&lC&8&l]&f ");

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

		defaultMessages.put("command_channel_help",
				"&3&lChannel Command Help\n"
						+ "&bSwitch channel\n"
						+ "&f&o/channel switch <channel>\n"
						+ "&bShow/Hide channel\n"
						+ "&f&o/channel show/hide <channel>");
		defaultMessages.put("command_channel_switch", "&bSwitched to channel: &f&o%SPECIAL%");
		defaultMessages.put("command_channel_hide", "&bYou have hidden channel: &f&o%SPECIAL%");
		defaultMessages.put("command_channel_show", "&bYou have un-hidden channel: &f&o%SPECIAL%");
		defaultMessages.put("command_channel_already_hide", "&bYou have already hidden channel: &f&o%SPECIAL%");
		defaultMessages.put("command_channel_already_show", "&bYou have already un-hidden channel: &f&o%SPECIAL%");
		defaultMessages.put("command_channel_does_not_exist", "&cSorry, that channel does not exist");
		defaultMessages.put("command_channel_only_players", "&cSorry, only players can use chat channel commands");
		defaultMessages.put("command_channel_switch_no_permission", "&cYou are unable to switch channels");
		defaultMessages.put("command_channel_hide_no_permission", "&cYou are unable to hide channels");
		defaultMessages.put("command_channel_show_no_permission", "&cYou are unable to show channels");
		defaultMessages.put("command_channel_cannot_hide", "&cYou cannot hide your currently selected channel");

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
		defaultMessages.put("command_global_enabled_2", "&bYour messages will go to all servers!");
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
		defaultMessages.put("command_local_enabled_2", "&bYour messages will only go to this server!");
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
		defaultMessages.put("command_msg_no_toggle", "&cSorry, message toggles are not allowed on this server!");

		// TODO Somehow combine all these into one message but provide a special method like "displayMessagePage()" in this
		// message manager which automatically decides how many lines to show for the page specified to the message manager.
		defaultMessages.put("command_multichat_help_1",
				"&2&lMulti&a&lChat &b&lHelp\n"
						+ "&3Display plugin version info\n"
						+ "&b/multichat\n"
						+ "&3Reload the plugin config\n"
						+ "&b/multichat reload\n"
						+ "&3Save ALL plugin data\n"
						+ "&b/multichat save\n"
						+ "&3Display a message to all players\n"
						+ "&b/display <message>\n"
						+ "&3View group chat help\n"
						+ "&b/group\n"
						+ "&3Send mod chat message &o(Send admin chat message)\n"
						+ "&b/mc <message> &o(/ac <message>)\n"
						+ "&3Change mod/&oadmin &3chat colours\n"
						+ "&b/mcc <chat colour code> <name colour code>\n"
						+ "&b&o/acc <chat colour code> <name colour code>\n"
						+ "&3Toggle mod chat &o(Toggle admin chat)\n"
						+ "&b/mc &o(/ac)\n"
						+ "&3&lType &b&l/multichat help <page number> &3&lto view more commands");

		defaultMessages.put("command_multichat_help_2",
				"&2&lMulti&a&lChat &b&lHelp [Page 2]\n"
						+ "&3View all global chat (Enabled by default)\n"
						+ "&b/global\n"
						+ "&3Only view chat from your current server\n"
						+ "&b/local\n"
						+ "&3See a list of online staff members\n"
						+ "&b/staff\n"
						+ "&3See a list of all group chats\n"
						+ "&b/groups\n"
						+ "&3Send a player a private message\n"
						+ "&b/msg <player> [message]\n"
						+ "&3Reply to your last message\n"
						+ "&b/r <message>\n"
						+ "&3Toggle socialspy to view private messages\n"
						+ "&b/socialspy\n"
						+ "&3Freeze the chat to stop messages being sent\n"
						+ "&b/freezechat\n"
						+ "&3Clear the chat stream for yourself or a group of people\n"
						+ "&b/clearchat [self,server,global,all]");

		defaultMessages.put("command_multichat_help_3",
				"&2&lMulti&a&lChat &b&lHelp [Page 3]\n"
						+ "&3View announcement commands\n"
						+ "&b/announcement\n"
						+ "&3View bulletin commands\n"
						+ "&b/bulletin\n"
						+ "&3View cast commands\n"
						+ "&b/cast\n"
						+ "&3Use a specified cast from the console\n"
						+ "&b/usecast <cast> <message>\n"
						+ "&3Alert staff members of a problem\n"
						+ "&b/helpme <message>\n"
						+ "&3Nickname a player (Only works if MultiChat installed on Spigot / Sponge)\n"
						+ "&b/nick [player] <nickname/off>\n"
						+ "&3Get players real name from their nickname (Only works on Spigot)\n"
						+ "&b/realname <nickname>\n"
						+ "&3Mute players to prevent them sending messages\n"
						+ "&b/mute <player>\n"
						+ "&3Ignore players to stop seeing their messages\n"
						+ "&b/ignore <player>");

		defaultMessages.put("command_multichat_save_prepare", "&3Preparing to save multichat files!");
		defaultMessages.put("command_multichat_save_completed", "&bSave completed!");
		defaultMessages.put("command_multichat_reload_prepare", "&3Preparing to reload multichat files!");
		defaultMessages.put("command_multichat_reload_completed", "&bReload completed!");

		defaultMessages.put("command_multichatbypass_usage", "&4Usage: /mcb\n"
				+ "&c&oThis command causes your chat messages to bypass MultiChat and be handled directly by spigot.");
		defaultMessages.put("command_multichatbypass_enabled", "&aMultiChat BYPASS Enabled");
		defaultMessages.put("command_multichatbypass_disabled", "&bMultiChat BYPASS Disabled");

		defaultMessages.put("command_execute_usage", "&2Usage: /mce [-s <server-regex>] [-p <player-regex>] <command>\n"
				+ "&a&oThis command allows you to execute a command over all your spigot servers (&lwhich have at least 1 player online!&a&o)\n"
				+ "By default the command will be executed by console, you can instead make players execute the command using the -p flag\n"
				+ "By default the command will be executed on all servers, you can limit which servers using the -s flag.");
		defaultMessages.put("command_execute_sent", "&2The command has been sent");
		defaultMessages.put("command_execute_regex", "&cThe regex specified was invalid");

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
		defaultMessages.put("command_stafflist_no_staff", "&b&oThere are currently no staff online");

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

		defaultMessages.put("groups_info_joined", " has joined the group chat!");
		defaultMessages.put("groups_info_quit", " has left the group chat!");
		defaultMessages.put("groups_info_formal", " has converted this group to a FORMAL group chat!");
		defaultMessages.put("groups_info_deleted", " has deleted this group chat!");
		defaultMessages.put("groups_info_goodbye", "Goodbye! If you want to see group chat commands do /group");
		defaultMessages.put("groups_info_transfer", " has transferred ownership to ");
		defaultMessages.put("groups_info_promoted", " has promoted the following member to group admin: ");
		defaultMessages.put("groups_info_step_down", " has stepped down as a group admin");
		defaultMessages.put("groups_info_kick", " kicked the following player from the group chat: ");
		defaultMessages.put("groups_info_ban", " has banned the following player from the group chat: ");
		defaultMessages.put("groups_info_unban", " has unbanned the following player from the group chat: ");
		defaultMessages.put("groups_info_colors", "Group chat colours changed by ");

		defaultMessages.put("groups_help_1",
				"&cGroup Chat Command Usage [Page 1] - INFORMAL GROUPS\n"
						+ "&2MAKE A NEW GROUP CHAT\n"
						+ "&a/group create/make <group name> [password]\n"
						+ "&2JOIN AN EXISTING GROUP CHAT\n"
						+ "&a/group join <group name> [password]\n"
						+ "&2LEAVE A GROUP CHAT\n"
						+ "&a/group leave/quit <group name>\n"
						+ "&2SELECT THE GROUP CHAT YOU WISH FOR YOUR MESSAGES TO GO TO\n"
						+ "&a/group <group name>\n"
						+ "&2SET THE COLOURS OF YOUR GROUP CHAT\n"
						+ "&a/group color/colour <group name> <chatcolorcode> <namecolorcode>\n"
						+ "&2TRANSFER OWNERSHIP OF YOUR INFORMAL GROUP CHAT\n"
						+ "&a/group transfer <group name> <player name>\n"
						+ "&2DELETE A GROUP CHAT\n"
						+ "&a/group delete <group name>\n"
						+ "&2LIST GROUP CHAT MEMBERS\n"
						+ "&a/group list/members <group name>\n"
						+ "&2SEND A MESSAGE TO THE SELECTED GROUP CHAT\n"
						+ "&a/gc <message>\n"
						+ "&cTo see FORMAL group chat commands do /group help 2");

		defaultMessages.put("groups_help_2",
				"&cGroup Chat Command Usage [Page 2] - FORMAL GROUPS\n"
						+ "&3All group chats default to informal group chats\n"
						+ "&3If you are a group owner you can convert your group to a formal group chat\n"
						+ "&3Formal group chats restrict changing colours to appointed group admins only\n"
						+ "&3Appointed group admins will also be able to ban people from the chat\n"
						+ "&3CONVERSION TO A FORMAL GROUP CHAT IS IRREVERSIBLE\n"
						+ "&2CONVERT YOUR GROUP CHAT TO A FORMAL GROUP CHAT (IRREVERSIBLE)\n"
						+ "&a/group formal <group name>\n"
						+ "&2ADD OR REMOVE AN ADMIN FROM A FORMAL GROUP CHAT\n"
						+ "&a/group admin <group name> <player name>\n"
						+ "&2BAN/UNBAN A PLAYER FROM YOUR FORMAL GROUP CHAT\n"
						+ "&a/group ban <group name> <player name>\n"
						+ "&cTo see INFORMAL group chat commands do /group help 1");

		// *** FREEZECHAT *** //

		defaultMessages.put("freezechat_frozen", "&bSorry chat has been &3&lFROZEN");

		// *** MUTE ***//

		defaultMessages.put("mute_muted", "&cYou have been muted by staff! You can no longer send chat messages.");
		defaultMessages.put("mute_unmuted", "&aYou have been unmuted by staff, you can now send messages.");
		defaultMessages.put("mute_muted_staff", "&cPlayer has been muted!");
		defaultMessages.put("mute_unmuted_staff", "&aPlayer has been unmuted!");
		defaultMessages.put("mute_cannot_send_message", "&cYou are currently muted so cannot send messages!");
		defaultMessages.put("mute_usage", "&cUsage: /mute <player> (Also used to unmute players)");
		defaultMessages.put("mute_player_not_found", "&cPlayer cannot be muted as they are not online");
		defaultMessages.put("mute_bypass", "&cYou cannot mute this player");

		// *** IGNORE *** //

		defaultMessages.put("ignore_sender", "&cYou cannot message this person");
		defaultMessages.put("ignore_target", "&c[%SPECIAL% sent a message, but you ignore them]");
		defaultMessages.put("ignore_ignored", "&bYou will no longer see chat messages from %SPECIAL%");
		defaultMessages.put("ignore_unignored", "&bYou have un-ignored %SPECIAL%");
		defaultMessages.put("ignore_player_not_found", "&cPlayer cannot be ignored as they are not online");
		defaultMessages.put("ignore_usage", "&cUsage: /ignore <player> (Also used to un-ignore players)");
		defaultMessages.put("ignore_bypass", "&cYou cannot ignore this player");
		defaultMessages.put("ignore_only_players", "&cOnly players can use this command");
		defaultMessages.put("ignore_cannot_ignore_yourself", "&cYou cannot ignore yourself!");

		// *** ANTI-SPAM *** //

		defaultMessages.put("anti_spam_cooldown", "&cANTI-SPAM: Your messages have been blocked. You cannot chat for another %SPECIAL% seconds.");

	}

	public static String getPrefix() {
		return prefix;
	}

	public static String getMessage(String id) {

		Configuration config = ConfigManager.getInstance().getHandler("messages.yml").getConfig();

		if (config.contains(id)) return config.getString(id);
		if (!defaultMessages.containsKey(id)) return "&cERROR - Please report to plugin developer - No message defined for: " + id;
		return defaultMessages.get(id.toLowerCase());

	}

	public static void sendMessage(CommandSender sender, String id) {
		updatePrefix();

		Player player;
		String server = "";
		if (sender instanceof Player) {
			player = (Player) sender;
			server = player.getServer().getName();
		}

		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', prefix + MultiChatUtil.reformatRGB(getMessage(id).replaceAll("%SERVER%", server)))));
	}

	public static void sendSpecialMessage(CommandSender sender, String id, String special) {
		updatePrefix();

		Player player;
		String server = "";
		if (sender instanceof Player) {
			player = (Player) sender;
			server = player.getServer().getName();
		}

		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', prefix + MultiChatUtil.reformatRGB(getMessage(id)).replaceAll("%SPECIAL%", special).replaceAll("%SERVER%", server))));
	}

	public static void sendSpecialMessageWithoutPrefix(CommandSender sender, String id, String special) {
		updatePrefix();
		sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', MultiChatUtil.reformatRGB(getMessage(id)).replaceAll("%SPECIAL%", special))));
	}

	private static void updatePrefix() {

		Configuration config = ConfigManager.getInstance().getHandler("messages.yml").getConfig();

		if (config.contains("prefix")) {
			prefix = config.getString("prefix");
		} else {
			prefix = defaultMessages.get("prefix");
		}

	}

}
