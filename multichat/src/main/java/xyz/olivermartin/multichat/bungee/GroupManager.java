package xyz.olivermartin.multichat.bungee;

import java.util.UUID;

import com.olivermartin410.plugins.TGroupChatInfo;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.bungee.commands.GCCommand;

/**
 * Group Chat Management Class
 * <p>Handles Group Chat Operations</p>
 * 
 * @author Oliver Martin (Revilo410)
 * 
 */
public class GroupManager {

	/**
	 * Creates a new informal group chat based on the specified parameters
	 * Also adds the creator to the group as the owner
	 */
	public void createGroup(String groupname, UUID owneruuid, boolean secret, String password) {

		TGroupChatInfo newgroup = new TGroupChatInfo();

		newgroup.addMember(owneruuid);
		newgroup.addViewer(owneruuid);
		newgroup.addAdmin(owneruuid);
		newgroup.setName(groupname.toLowerCase());
		newgroup.setChatColor(MultiChat.configman.config.getString("groupchat.ccdefault").toCharArray()[0]);
		newgroup.setNameColor(MultiChat.configman.config.getString("groupchat.ncdefault").toCharArray()[0]);
		newgroup.setSecret(secret);
		newgroup.setPassword(password);
		newgroup.setFormal(false);

		MultiChat.groupchats.put(groupname.toLowerCase(), newgroup);

	}

	/**
	 * Adds a player to a group chat while removing them from the spy list if they were spying on it before
	 * This will also check if they are banned and stop them being added
	 * It will also check if they are already a member
	 * Passwords for the group are also checked
	 */
	public boolean joinGroup(String groupname, ProxiedPlayer player, String password) {

		boolean success = false;

		TGroupChatInfo groupchat = new TGroupChatInfo();
		groupchat = (TGroupChatInfo)MultiChat.groupchats.get(groupname.toLowerCase());

		if (!groupchat.existsBanned(player.getUniqueId())) {

			if (!groupchat.existsMember(player.getUniqueId())) {

				if (!groupchat.getSecret()) {

					if (groupchat.existsViewer(player.getUniqueId())) {

						if (player.hasPermission("multichat.staff.spy")) {

							player.sendMessage(new ComponentBuilder("You are no longer spying on the group: " + groupname.toUpperCase()).color(ChatColor.RED).create());
							groupchat.delViewer(player.getUniqueId());

						} else {

							groupchat.delViewer(player.getUniqueId());

						}

					}

					groupchat.addMember(player.getUniqueId());
					groupchat.addViewer(player.getUniqueId());

					MultiChat.groupchats.remove(groupname.toLowerCase());
					MultiChat.groupchats.put(groupname.toLowerCase(), groupchat);

					success = true;

				} else {

					if (password.equals("")) {

						player.sendMessage(new ComponentBuilder("Sorry this group chat is password protected: " + groupname.toUpperCase()).color(ChatColor.RED).create());

					} else {

						if (password.equals(groupchat.getPassword())) {

							if (groupchat.existsViewer(player.getUniqueId())) {

								if (player.hasPermission("multichat.staff.spy")) {

									player.sendMessage(new ComponentBuilder("You are no longer spying on the group: " + groupname.toUpperCase()).color(ChatColor.RED).create());
									groupchat.delViewer(player.getUniqueId());

								} else {
									groupchat.delViewer(player.getUniqueId());
								}

							}

							groupchat.addMember(player.getUniqueId());
							groupchat.addViewer(player.getUniqueId());

							MultiChat.groupchats.remove(groupname.toLowerCase());
							MultiChat.groupchats.put(groupname.toLowerCase(), groupchat);

							success = true;

						} else {

							player.sendMessage(new ComponentBuilder("Sorry incorrect password for: " + groupname.toUpperCase()).color(ChatColor.RED).create());

						}

					}
				}

			} else {
				player.sendMessage(new ComponentBuilder("Sorry you are already a member of the group: " + groupname.toUpperCase()).color(ChatColor.RED).create());
			}

		} else {
			player.sendMessage(new ComponentBuilder("Sorry you are banned from the group: " + groupname.toUpperCase()).color(ChatColor.RED).create());
		}

		groupchat = null;
		return success;

	}

	/**
	 * Sets the selected group of a player to the specified group
	 */
	public void setViewedChat(UUID playeruuid, String groupname) {

		String viewedchat = (String)MultiChat.viewedchats.get(playeruuid);

		viewedchat = groupname.toLowerCase();
		MultiChat.viewedchats.remove(playeruuid);
		MultiChat.viewedchats.put(playeruuid, viewedchat);

	}

	/**
	 * The INFO announce in a group that a player has joined
	 */
	public void announceJoinGroup(String playername, String groupname) {

		GCCommand.sendMessage(playername + " has joined the group chat!", "&lINFO", MultiChat.groupchats.get(groupname.toLowerCase()));

	}

	/**
	 * The INFO announce in a group that a player has left
	 */
	public void announceQuitGroup(String playername, String groupname) {

		GCCommand.sendMessage(playername + " has left the group chat!", "&lINFO", MultiChat.groupchats.get(groupname.toLowerCase()));

	}

	/**
	 * Quits a group, announces in the group chat and notifies the player quitting
	 */
	public void quitGroup(String groupname, UUID player, ProxiedPlayer pinstance) {

		TGroupChatInfo groupchatinfo = new TGroupChatInfo();
		String viewedchat = (String)MultiChat.viewedchats.get(player);

		groupchatinfo = (TGroupChatInfo)MultiChat.groupchats.get(groupname.toLowerCase());

		if (groupchatinfo.existsMember(player)) {

			if ((!groupchatinfo.existsAdmin(player)) || (groupchatinfo.getAdmins().size() > 1)) {

				groupchatinfo.delMember(player);
				groupchatinfo.delViewer(player);

				if (groupchatinfo.existsAdmin(player)) {
					groupchatinfo.delAdmin(player);
				}

				viewedchat = null;

				MultiChat.viewedchats.remove(player);
				MultiChat.viewedchats.put(player, viewedchat);
				MultiChat.groupchats.remove(groupname.toLowerCase());
				MultiChat.groupchats.put(groupname.toLowerCase(), groupchatinfo);

				pinstance.sendMessage(new ComponentBuilder("You successfully left the group: " + groupname.toUpperCase()).color(ChatColor.GREEN).create());
				announceQuitGroup(pinstance.getName(), groupname);

			} else if (!groupchatinfo.getFormal()) {

				pinstance.sendMessage(new ComponentBuilder("Sorry you cannot leave as you created the group!: " + groupname.toUpperCase()).color(ChatColor.RED).create());
				pinstance.sendMessage(new ComponentBuilder("Please transfer group ownership first! Use /group transfer " + groupname.toUpperCase() + " <playername>").color(ChatColor.RED).create());

			} else {

				pinstance.sendMessage(new ComponentBuilder("Sorry you cannot leave as you are the only group admin!: " + groupname.toUpperCase()).color(ChatColor.RED).create());
				pinstance.sendMessage(new ComponentBuilder("Please appoint a new admin using /group admin " + groupname.toUpperCase() + " <playername>").color(ChatColor.RED).create());

			}

		} else {

			pinstance.sendMessage(new ComponentBuilder("Sorry you aren't a member of the group: " + groupname.toUpperCase()).color(ChatColor.RED).create());

		}

		groupchatinfo = null;

	}

	public void displayHelp(int page, CommandSender sender) {

		if (page == 1) {

			sender.sendMessage(new ComponentBuilder("Group Chats Command Usage [Page 1] - INFORMAL GROUP CHATS").color(ChatColor.RED).create());
			sender.sendMessage(new ComponentBuilder("MAKE A NEW GROUP CHAT").color(ChatColor.DARK_GREEN).create());
			sender.sendMessage(new ComponentBuilder("/group create/make <group name> [password]").color(ChatColor.GREEN).create());
			sender.sendMessage(new ComponentBuilder("JOIN AN EXISTING GROUP CHAT").color(ChatColor.DARK_GREEN).create());
			sender.sendMessage(new ComponentBuilder("/group join <group name> [password]").color(ChatColor.GREEN).create());
			sender.sendMessage(new ComponentBuilder("LEAVE A GROUP CHAT").color(ChatColor.DARK_GREEN).create());
			sender.sendMessage(new ComponentBuilder("/group leave/quit <group name>").color(ChatColor.GREEN).create());
			sender.sendMessage(new ComponentBuilder("SELECT THE GROUP CHAT YOU WISH FOR YOUR MESSAGES TO GO TO").color(ChatColor.DARK_GREEN).create());
			sender.sendMessage(new ComponentBuilder("/group <group name>").color(ChatColor.GREEN).create());
			sender.sendMessage(new ComponentBuilder("SET THE COLOURS OF YOUR GROUP CHAT").color(ChatColor.DARK_GREEN).create());
			sender.sendMessage(new ComponentBuilder("/group color/colour <group name> <chatcolorcode> <namecolorcode>").color(ChatColor.GREEN).create());
			sender.sendMessage(new ComponentBuilder("TRANSFER OWNERSHIP OF YOUR INFORMAL GROUP CHAT").color(ChatColor.DARK_GREEN).create());
			sender.sendMessage(new ComponentBuilder("/group transfer <group name> <player name>").color(ChatColor.GREEN).create());
			sender.sendMessage(new ComponentBuilder("DELETE A GROUP CHAT").color(ChatColor.DARK_GREEN).create());
			sender.sendMessage(new ComponentBuilder("/group delete <group name>").color(ChatColor.GREEN).create());
			sender.sendMessage(new ComponentBuilder("LIST GROUP CHAT MEMBERS").color(ChatColor.DARK_GREEN).create());
			sender.sendMessage(new ComponentBuilder("/group list/members <group name>").color(ChatColor.GREEN).create());
			sender.sendMessage(new ComponentBuilder("SEND A MESSAGE TO THE SELECTED GROUP CHAT").color(ChatColor.DARK_GREEN).create());
			sender.sendMessage(new ComponentBuilder("/gc <message>").color(ChatColor.GREEN).create());
			sender.sendMessage(new ComponentBuilder("To see FORMAL group chat commands do /group help 2").color(ChatColor.RED).create());

		} else {

			sender.sendMessage(new ComponentBuilder("Group Chats Command Usage [Page 2] - FORMAL GROUP CHATS").color(ChatColor.RED).create());
			sender.sendMessage(new ComponentBuilder("All group chats default to informal group chats").color(ChatColor.DARK_AQUA).create());
			sender.sendMessage(new ComponentBuilder("If you are a group owner you can convert your group to a formal group chat").color(ChatColor.DARK_AQUA).create());
			sender.sendMessage(new ComponentBuilder("Formal group chats restrict changing colours to appointed group admins only").color(ChatColor.DARK_AQUA).create());  
			sender.sendMessage(new ComponentBuilder("Appointed group admins will also be able to ban people from the chat").color(ChatColor.DARK_AQUA).create());
			sender.sendMessage(new ComponentBuilder("CONVERSION TO A FORMAL GROUP CHAT IS IRREVERSIBLE").color(ChatColor.DARK_AQUA).create());
			sender.sendMessage(new ComponentBuilder("CONVERT YOUR GROUP CHAT TO A FORMAL GROUP CHAT (IRREVERSIBLE)").color(ChatColor.DARK_GREEN).create());
			sender.sendMessage(new ComponentBuilder("/group formal <group name>").color(ChatColor.GREEN).create());
			sender.sendMessage(new ComponentBuilder("ADD OR REMOVE AN ADMIN FROM A FORMAL GROUP CHAT").color(ChatColor.DARK_GREEN).create());
			sender.sendMessage(new ComponentBuilder("/group admin <group name> <player name>").color(ChatColor.GREEN).create());
			sender.sendMessage(new ComponentBuilder("BAN/UNBAN A PLAYER FROM YOUR FORMAL GROUP CHAT").color(ChatColor.DARK_GREEN).create());
			sender.sendMessage(new ComponentBuilder("/group ban <group name> <player name>").color(ChatColor.GREEN).create());
			sender.sendMessage(new ComponentBuilder("To see INFORMAL group chat commands do /group help 1").color(ChatColor.RED).create());

		}
	}
}
