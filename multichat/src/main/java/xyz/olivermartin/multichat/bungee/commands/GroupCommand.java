package xyz.olivermartin.multichat.bungee.commands;

import java.util.*;

import com.olivermartin410.plugins.TGroupChatInfo;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import xyz.olivermartin.multichat.bungee.GroupManager;
import xyz.olivermartin.multichat.bungee.UUIDNameManager;
import xyz.olivermartin.multichat.common.RegexUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

/**
 * The Group Command
 * <p>From here the player can manipulate group chats in every possible way</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class GroupCommand extends Command implements TabExecutor {

    private final Set<String> args_zero = new HashSet<>(Arrays.asList("help", "members", "list", "spy",
            "create", "make", "join", "quit", "leave", "formal", "delete", "transfer", "admin", "addadmin",
            "removeadmin", "ban", "color")
    );

    public GroupCommand() {
        super("mcgroup", "multichat.group", ProxyConfigs.ALIASES.getAliases("mcgroup"));
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_only_players");
            return;
        }
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
        UUID playerUID = proxiedPlayer.getUniqueId();

        // TODO: This class needs some work in the future, then we can clean up this command more
        GroupManager groupManager = new GroupManager();

        String subCommand = args.length > 0 ? args[0].toLowerCase() : "help";
        String subArgument = args.length > 1 ? args[1].toLowerCase() : subCommand;

        ProxyDataStore proxyDataStore = MultiChatProxy.getInstance().getDataStore();

        switch (subCommand) {
            case "help": {
                int page = 1;
                try {
                    page = Integer.parseInt(subArgument);
                } catch (NumberFormatException ignored) {
                }

                groupManager.displayHelp(page, sender);
                return;
            }
            case "members":
            case "list": {
                TGroupChatInfo groupChatInfo = getGroupChatFromName(proxyDataStore, proxiedPlayer, subArgument, false);
                if (groupChatInfo == null) break;

                ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_member_list", subArgument);
                groupChatInfo.getMembers().forEach(member ->
                        ProxyConfigs.MESSAGES.sendMessage(sender,
                                "command_group_member_list_item" + (groupChatInfo.existsAdmin(member) ? "_admin" : ""),
                                UUIDNameManager.getName(member)
                        )
                );
                return;
            }
            case "spy": {
                if (!proxiedPlayer.hasPermission("multichat.staff.spy")) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_spy_no_permission");
                    return;
                }

                if (subArgument.equals("all")) {
                    if (!proxyDataStore.getAllSpy().contains(playerUID)) {
                        proxyDataStore.getAllSpy().add(playerUID);
                        ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_spy_all_enabled");
                        return;
                    }

                    proxyDataStore.getAllSpy().remove(playerUID);
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_spy_all_disabled_1");
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_spy_all_disabled_2");
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_spy_all_disabled_3");
                    return;
                }

                TGroupChatInfo groupChatInfo = getGroupChatFromName(proxyDataStore, proxiedPlayer, subArgument, false);
                if (groupChatInfo == null) break;

                if (groupChatInfo.isMember(playerUID)) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_spy_already_a_member");
                    return;
                }

                if (groupChatInfo.isViewer(playerUID)) {
                    groupChatInfo.delViewer(playerUID);
                    proxyDataStore.getGroupChats().put(subArgument, groupChatInfo);
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_spy_off", subArgument);
                    return;
                }

                groupChatInfo.addViewer(playerUID);
                proxyDataStore.getGroupChats().put(subArgument, groupChatInfo);
                ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_spy_on", subArgument);
                return;
            }
            case "create":
            case "make": {
                if (!proxiedPlayer.hasPermission("multichat.group.create")) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_create_no_permission");
                    return;
                }

                // TODO: Should probably make this configurable
                if (subArgument.length() > 20) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_max_length");
                    return;
                }

                String password = args.length > 2 ? args[2] : "";
                if (password.length() > 20) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_max_length_password");
                    return;
                }

                if (proxyDataStore.getGroupChats().containsKey(subArgument)) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_already_exists", subArgument);
                    return;
                }

                // Make the new group, select it and announce the join
                groupManager.createGroup(subArgument, playerUID, false, "");
                // TODO: Should probably move this inside the createGroup (joinGroup for join below)
                groupManager.setViewedChat(playerUID, subArgument);
                ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_created", subArgument);

                groupManager.announceJoinGroup(sender.getName(), subArgument);
                return;
            }
            case "join": {
                TGroupChatInfo groupChatInfo = getGroupChatFromName(proxyDataStore, proxiedPlayer, subArgument, false);
                if (groupChatInfo == null) break;

                if (!groupManager.joinGroup(subArgument, proxiedPlayer, args.length > 2 ? args[2] : ""))
                    return;

                groupManager.setViewedChat(playerUID, subArgument);
                ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_joined", subArgument);
                groupManager.announceJoinGroup(sender.getName(), subArgument);
                return;
            }
            case "quit":
            case "leave": {
                TGroupChatInfo groupChatInfo = getGroupChatFromName(proxyDataStore, proxiedPlayer, subArgument, true);
                if (groupChatInfo == null) break;

                groupManager.quitGroup(subArgument, playerUID, proxiedPlayer);
                return;
            }
            case "formal": {
                TGroupChatInfo groupChatInfo = getGroupChatFromName(proxyDataStore, proxiedPlayer, subArgument, true);
                if (groupChatInfo == null) break;

                if (groupChatInfo.getFormal()) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_formal_already_formal", subArgument);
                    return;
                }

                if (!groupChatInfo.getAdmins().contains(playerUID)) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_formal_not_owner");
                    return;
                }

                groupChatInfo.setFormal(true);
                proxyDataStore.getGroupChats().put(subArgument, groupChatInfo);
                // TODO: Excuse me what even is this
                //  We need to generalize sending messages at some point (and how placeholders are handled)
                GCCommand.sendMessage(sender.getName() + ProxyConfigs.MESSAGES.getMessage("groups_info_formal"), "&lINFO", groupChatInfo);
                return;
            }
            case "delete": {
                TGroupChatInfo groupChatInfo = getGroupChatFromName(proxyDataStore, proxiedPlayer, subArgument, true);
                if (groupChatInfo == null) break;

                if (!groupChatInfo.getAdmins().contains(playerUID)) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_formal_not_admin");
                    return;
                }

                proxyDataStore.getViewedChats().entrySet().forEach(entry -> {
                    if (entry.getValue() != null && entry.getValue().equals(subArgument))
                        entry.setValue(null);
                });

                GCCommand.sendMessage(sender.getName() + ProxyConfigs.MESSAGES.getMessage("groups_info_deleted"),
                        "&lINFO",
                        groupChatInfo
                );
                GCCommand.sendMessage(ProxyConfigs.MESSAGES.getMessage("groups_info_goodbye"), "&lINFO", groupChatInfo);
                proxyDataStore.getGroupChats().remove(subArgument);
                return;
            }
            case "transfer": {
                if (args.length < 3)
                    break;

                TGroupChatInfo groupChatInfo = getGroupChatFromName(proxyDataStore, proxiedPlayer, subArgument, true);
                if (groupChatInfo == null) break;

                if (groupChatInfo.getFormal()) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_transfer_not_informal");
                    return;
                }

                if (!groupChatInfo.existsAdmin(playerUID)) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_transfer_not_owner");
                    return;
                }

                ProxiedPlayer target = getProxiedTargetFromName(groupChatInfo, proxiedPlayer, args[2], true);
                if (target == null)
                    return;
                UUID targetUID = target.getUniqueId();

                groupChatInfo.addAdmin(targetUID);
                groupChatInfo.delAdmin(playerUID);
                GCCommand.sendMessage(sender.getName() + ProxyConfigs.MESSAGES.getMessage("groups_info_transfer") + target.getName(), "&lINFO", groupChatInfo);

                proxyDataStore.getGroupChats().put(subArgument, groupChatInfo);
                return;
            }
            case "admin":
            case "addadmin":
            case "removeadmin": {
                if (args.length < 3)
                    break;

                TGroupChatInfo groupChatInfo = getGroupChatFromName(proxyDataStore, proxiedPlayer, subArgument, true);
                if (groupChatInfo == null) break;

                if (!groupChatInfo.getFormal()) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_not_formal");
                    return;
                }

                if (!groupChatInfo.existsAdmin(playerUID)) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_formal_not_admin");
                    return;
                }

                ProxiedPlayer target = getProxiedTargetFromName(groupChatInfo, proxiedPlayer, args[2], true);
                if (target == null)
                    return;
                UUID targetUID = target.getUniqueId();

                if (playerUID.equals(targetUID)) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_formal_cannot_demote");
                    return;
                }

                if (groupChatInfo.existsAdmin(targetUID)) {
                    groupChatInfo.delAdmin(targetUID);
                    GCCommand.sendMessage(target.getName() + ProxyConfigs.MESSAGES.getMessage("groups_info_step_down"), "&lINFO", groupChatInfo);
                    return;
                }

                groupChatInfo.addAdmin(targetUID);
                GCCommand.sendMessage(sender.getName() + ProxyConfigs.MESSAGES.getMessage("groups_info_promoted") + target.getName(), "&lINFO", groupChatInfo);
                return;
            }
            case "ban": {
                if (args.length < 3)
                    break;

                TGroupChatInfo groupChatInfo = getGroupChatFromName(proxyDataStore, proxiedPlayer, subArgument, true);
                if (groupChatInfo == null) break;

                if (!groupChatInfo.getFormal()) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_not_formal");
                    return;
                }

                if (!groupChatInfo.existsAdmin(playerUID)) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_ban_not_admin");
                    return;
                }

                ProxiedPlayer target = getProxiedTargetFromName(groupChatInfo, proxiedPlayer, args[2], false);
                if (target == null)
                    return;
                UUID targetUID = target.getUniqueId();

                if (groupChatInfo.existsAdmin(targetUID)) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_cannot_ban_admin");
                    return;
                }

                if (groupChatInfo.isBanned(targetUID)) {
                    groupChatInfo.delBanned(targetUID);
                    proxyDataStore.getGroupChats().put(subArgument, groupChatInfo);
                    GCCommand.sendMessage(sender.getName() + ProxyConfigs.MESSAGES.getMessage("groups_info_unban") + target.getName(), "&lINFO", groupChatInfo);
                    ProxyConfigs.MESSAGES.sendMessage(target, "command_group_unbanned", subArgument);
                    return;
                }

                groupChatInfo.addBanned(targetUID);
                if (groupChatInfo.isMember(targetUID)) {
                    groupChatInfo.delMember(targetUID);
                    groupChatInfo.delViewer(targetUID);
                    proxyDataStore.getViewedChats().put(targetUID, null);

                    // TODO: I don't think we need to notify the user of being kicked AND banned further down below
                    GCCommand.sendMessage(sender.getName() + ProxyConfigs.MESSAGES.getMessage("groups_info_kick") + target.getName(), "&lINFO", groupChatInfo);
                }

                proxyDataStore.getGroupChats().put(subArgument, groupChatInfo);
                GCCommand.sendMessage(sender.getName() + ProxyConfigs.MESSAGES.getMessage("groups_info_ban") + target.getName(), "&lINFO", groupChatInfo);
                ProxyConfigs.MESSAGES.sendMessage(target, "command_group_banned", subArgument);
                return;
            }
            // Saving Private Byte
            case "color": {
                if (args.length < 4)
                    break;

                TGroupChatInfo groupChatInfo = getGroupChatFromName(proxyDataStore, proxiedPlayer, subArgument, true);
                if (groupChatInfo == null) break;

                if (!groupChatInfo.isMember(playerUID)
                        || (groupChatInfo.getFormal() && !groupChatInfo.existsAdmin(playerUID))) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_formal_not_admin");
                    return;
                }

                String chatColor = args[2].toLowerCase();
                String nameColor = args[3].toLowerCase();

                if (!RegexUtil.LEGACY_COLOR.matches(chatColor) || !RegexUtil.LEGACY_COLOR.matches(nameColor)) {
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_color_invalid");
                    ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_color_usage");
                    return;
                }

                groupChatInfo.setChatColor(chatColor.charAt(0));
                groupChatInfo.setNameColor(nameColor.charAt(0));

                proxyDataStore.getGroupChats().put(subArgument, groupChatInfo);
                GCCommand.sendMessage(ProxyConfigs.MESSAGES.getMessage("groups_info_colors") + sender.getName(), "&lINFO", groupChatInfo);
                return;
            }
            default: {
                TGroupChatInfo groupChatInfo = getGroupChatFromName(proxyDataStore, proxiedPlayer, subArgument, true);
                if (groupChatInfo == null) break;

                proxyDataStore.getViewedChats().put(playerUID, subArgument);
                ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_selected", subArgument);
                return;
            }
        }

        ProxyConfigs.MESSAGES.sendMessage(sender, "command_group_incorrect_usage");
    }

    private TGroupChatInfo getGroupChatFromName(ProxyDataStore proxyDataStore, ProxiedPlayer proxiedPlayer, String groupName, boolean checkMember) {
        TGroupChatInfo groupChatInfo = proxyDataStore.getGroupChats().get(groupName);
        if (groupChatInfo == null) {
            ProxyConfigs.MESSAGES.sendMessage(proxiedPlayer, "command_group_does_not_exist", groupName);
            return null;
        }

        if (checkMember && !groupChatInfo.isMember(proxiedPlayer.getUniqueId())) {
            ProxyConfigs.MESSAGES.sendMessage(proxiedPlayer, "command_group_not_a_member", groupName);
            return null;
        }

        return groupChatInfo;
    }

    private ProxiedPlayer getProxiedTargetFromName(TGroupChatInfo groupChatInfo, ProxiedPlayer proxiedPlayer, String name, boolean checkMember) {
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(name);
        if (target == null) {
            ProxyConfigs.MESSAGES.sendMessage(proxiedPlayer, "command_group_player_not_online");
            return null;
        }
        UUID targetUID = target.getUniqueId();

        if (checkMember && !groupChatInfo.isMember(targetUID)) {
            ProxyConfigs.MESSAGES.sendMessage(proxiedPlayer, "command_group_transfer_not_member");
            return null;
        }

        return target;
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        Set<String> matches = new HashSet<>();

        if (args.length == 1) {
            String search = args[0].toLowerCase();
            args_zero.forEach(subCommand -> {
                if (subCommand.length() > search.length()
                        && subCommand.regionMatches(true, 0, search, 0, search.length()))
                    matches.add(subCommand);
            });
        }

        return matches;
    }
}
