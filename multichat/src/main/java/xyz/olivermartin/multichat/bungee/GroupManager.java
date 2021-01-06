package xyz.olivermartin.multichat.bungee;

import java.util.UUID;

import com.olivermartin410.plugins.TGroupChatInfo;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.bungee.commands.GCCommand;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

/**
 * Group Chat Management Class
 * <p>Handles Group Chat Operations</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class GroupManager {

    /**
     * Creates a new informal group chat based on the specified parameters
     * Also adds the creator to the group as the owner
     */
    public void createGroup(String groupname, UUID owneruuid, boolean secret, String password) {

        TGroupChatInfo newgroup = new TGroupChatInfo();
        ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

        newgroup.addMember(owneruuid);
        newgroup.addViewer(owneruuid);
        newgroup.addAdmin(owneruuid);
        newgroup.setName(groupname.toLowerCase());
        newgroup.setChatColor(ProxyConfigs.CONFIG.getGroupChatColor());
        newgroup.setNameColor(ProxyConfigs.CONFIG.getGroupNameColor());
        newgroup.setSecret(secret);
        newgroup.setPassword(password);
        newgroup.setFormal(false);

        ds.getGroupChats().put(groupname.toLowerCase(), newgroup);

    }

    /**
     * Adds a player to a group chat while removing them from the spy list if they were spying on it before
     * This will also check if they are banned and stop them being added
     * It will also check if they are already a member
     * Passwords for the group are also checked
     */
    public boolean joinGroup(String groupname, ProxiedPlayer player, String password) {
        ProxyDataStore dataStore = MultiChatProxy.getInstance().getDataStore();
        TGroupChatInfo groupChatInfo = dataStore.getGroupChats().get(groupname.toLowerCase());

        UUID playerUID = player.getUniqueId();
        if (groupChatInfo.isBanned(playerUID)) {
            ProxyConfigs.MESSAGES.sendMessage(player, "groups_banned", groupname.toUpperCase());
            return false;
        }

        if (groupChatInfo.isMember(playerUID)) {
            ProxyConfigs.MESSAGES.sendMessage(player, "groups_already_joined", groupname.toUpperCase());
            return false;
        }

        if (groupChatInfo.getSecret()) {
            if (password.isEmpty()) {
                ProxyConfigs.MESSAGES.sendMessage(player, "groups_password_protected", groupname.toUpperCase());
                return false;
            }

            if (!password.equals(groupChatInfo.getPassword())) {
                ProxyConfigs.MESSAGES.sendMessage(player, "groups_password_incorrect", groupname.toUpperCase());
                return false;
            }
        }

        if (groupChatInfo.isViewer(player.getUniqueId())) {
            if (player.hasPermission("multichat.staff.spy"))
                ProxyConfigs.MESSAGES.sendMessage(player, "command_group_spy_off", groupname.toUpperCase());

            groupChatInfo.delViewer(player.getUniqueId());
        }

        groupChatInfo.addMember(player.getUniqueId());
        groupChatInfo.addViewer(player.getUniqueId());

        dataStore.getGroupChats().remove(groupname.toLowerCase());
        dataStore.getGroupChats().put(groupname.toLowerCase(), groupChatInfo);

        return true;
    }

    /**
     * Sets the selected group of a player to the specified group
     */
    public void setViewedChat(UUID playeruuid, String groupname) {
        ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();
        ds.getViewedChats().get(playeruuid);
        String viewedchat = groupname.toLowerCase();
        ds.getViewedChats().remove(playeruuid);
        ds.getViewedChats().put(playeruuid, viewedchat);
    }

    /**
     * The INFO announce in a group that a player has joined
     */
    public void announceJoinGroup(String playername, String groupname) {
        ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

        GCCommand.sendMessage(playername + ProxyConfigs.MESSAGES.getMessage("groups_info_joined"), "&lINFO", ds.getGroupChats().get(groupname.toLowerCase()));
    }

    /**
     * The INFO announce in a group that a player has left
     */
    public void announceQuitGroup(String playername, String groupname) {
        ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

        GCCommand.sendMessage(playername + ProxyConfigs.MESSAGES.getMessage("groups_info_quit"), "&lINFO", ds.getGroupChats().get(groupname.toLowerCase()));
    }

    /**
     * Quits a group, announces in the group chat and notifies the player quitting
     */
    public void quitGroup(String groupname, UUID player, ProxiedPlayer pinstance) {
        ProxyDataStore dataStore = MultiChatProxy.getInstance().getDataStore();
        TGroupChatInfo groupChatInfo = dataStore.getGroupChats().get(groupname.toLowerCase());

        if (!groupChatInfo.isMember(player)) {
            ProxyConfigs.MESSAGES.sendMessage(pinstance, "command_group_not_a_member", groupname.toUpperCase());
            return;
        }

        if ((!groupChatInfo.isAdmin(player)) || (groupChatInfo.getAdmins().size() > 1)) {
            groupChatInfo.delMember(player);
            groupChatInfo.delViewer(player);

            if (groupChatInfo.isAdmin(player)) {
                groupChatInfo.delAdmin(player);
            }

            dataStore.getViewedChats().remove(player);
            dataStore.getViewedChats().put(player, null);
            dataStore.getGroupChats().remove(groupname.toLowerCase());
            dataStore.getGroupChats().put(groupname.toLowerCase(), groupChatInfo);

            ProxyConfigs.MESSAGES.sendMessage(pinstance, "groups_quit", groupname.toUpperCase());
            announceQuitGroup(pinstance.getName(), groupname);
        } else if (!groupChatInfo.getFormal()) {
            ProxyConfigs.MESSAGES.sendMessage(pinstance, "groups_cannot_quit_owner_1", groupname.toUpperCase());
            ProxyConfigs.MESSAGES.sendMessage(pinstance, "groups_cannot_quit_owner_2", groupname.toUpperCase());
        } else {
            ProxyConfigs.MESSAGES.sendMessage(pinstance, "groups_cannot_quit_admin_1", groupname.toUpperCase());
            ProxyConfigs.MESSAGES.sendMessage(pinstance, "groups_cannot_quit_admin_2", groupname.toUpperCase());
        }
    }

    public void displayHelp(int page, CommandSender sender) {
        ProxyConfigs.MESSAGES.sendMessage(sender, "groups_help_" + (page == 1 ? "1" : "2"));
    }
}
