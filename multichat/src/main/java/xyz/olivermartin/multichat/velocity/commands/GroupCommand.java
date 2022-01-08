package xyz.olivermartin.multichat.velocity.commands;

import com.olivermartin410.plugins.TGroupChatInfo;
import com.velocitypowered.api.proxy.Player;
import xyz.olivermartin.multichat.velocity.GroupManager;
import xyz.olivermartin.multichat.velocity.MessageManager;
import xyz.olivermartin.multichat.velocity.MultiChat;
import xyz.olivermartin.multichat.velocity.UUIDNameManager;

import java.util.List;
import java.util.UUID;

/**
 * The Group Command
 * <p>From here the player can manipulate group chats in every possible way</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class GroupCommand extends Command {

    private static final String[] aliases = new String[]{};

    public GroupCommand() {
        super("group", aliases);
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.group");
    }

    public void execute(Invocation invocation) {
        var args = invocation.arguments();
        var sender = invocation.source();

        if ((args.length < 1) || ((args.length == 1) && (args[0].equalsIgnoreCase("help")))) {

            GroupManager groupman = new GroupManager();
            groupman.displayHelp(1, sender);

        } else if (sender instanceof Player) {
            Player player = (Player) sender;

            switch (args.length) {
                case 1:
                    if (MultiChat.groupchats.containsKey(args[0].toLowerCase())) {

                        TGroupChatInfo groupInfo = MultiChat.groupchats.get(args[0].toLowerCase());

                        if (groupInfo.existsMember(player.getUniqueId())) {

                            String viewedchat = args[0].toLowerCase();
                            MultiChat.viewedchats.remove(player.getUniqueId());
                            MultiChat.viewedchats.put(player.getUniqueId(), viewedchat);

                            MessageManager.sendSpecialMessage(sender, "command_group_selected", args[0].toUpperCase());

                        } else {
                            MessageManager.sendSpecialMessage(sender, "command_group_not_a_member", args[0].toUpperCase());
                        }

                    } else {
                        MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[0].toUpperCase());
                    }

                    break;

                case 2:

                    if ((!args[0].equalsIgnoreCase("members")) && (!args[0].equalsIgnoreCase("list"))
                            && (!args[0].equalsIgnoreCase("spyall")) && (!args[0].equalsIgnoreCase("spy"))
                            && (!args[0].equalsIgnoreCase("help")) && (!args[0].equalsIgnoreCase("create"))
                            && (!args[0].equalsIgnoreCase("make")) && (!args[0].equalsIgnoreCase("join"))
                            && (!args[0].equalsIgnoreCase("quit")) && (!args[0].equalsIgnoreCase("leave"))
                            && (!args[0].equalsIgnoreCase("formal")) && (!args[0].equalsIgnoreCase("delete"))) {

                        MessageManager.sendMessage(sender, "command_group_incorrect_usage");
                    }

                    if ((args[0].equalsIgnoreCase("list")) || (args[0].equalsIgnoreCase("members"))) {

                        if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

                            TGroupChatInfo groupChatInfo = MultiChat.groupchats.get(args[1].toLowerCase());

                            if ((groupChatInfo.existsMember(player.getUniqueId())) || (sender.hasPermission("multichat.staff.spy"))) {

                                List<UUID> memberlist = groupChatInfo.getMembers();

                                MessageManager.sendSpecialMessage(sender, "command_group_member_list", groupChatInfo.getName().toUpperCase());

                                for (UUID member : memberlist) {

                                    if (!groupChatInfo.existsAdmin(member)) {
                                        MessageManager.sendSpecialMessage(sender, "command_group_member_list_item", UUIDNameManager.getName(member));
                                    } else {
                                        MessageManager.sendSpecialMessage(sender, "command_group_member_list_item_admin", UUIDNameManager.getName(member));
                                    }
                                }

                            } else {
                                MessageManager.sendSpecialMessage(sender, "command_group_not_a_member", args[1].toUpperCase());
                            }

                        } else {
                            MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
                        }

                    }

                    if (args[0].equalsIgnoreCase("spy")) {
                        if (args[1].equalsIgnoreCase("all")) {
                            if (player.hasPermission("multichat.staff.spy")) {

                                if (MultiChat.allspy.contains(player.getUniqueId())) {

                                    MultiChat.allspy.remove(player.getUniqueId());
                                    MessageManager.sendMessage(sender, "command_group_spy_all_disabled_1");
                                    MessageManager.sendMessage(sender, "command_group_spy_all_disabled_2");
                                    MessageManager.sendMessage(sender, "command_group_spy_all_disabled_3");

                                } else {

                                    MultiChat.allspy.add(player.getUniqueId());
                                    MessageManager.sendMessage(sender, "command_group_spy_all_enabled");
                                }

                            } else {
                                MessageManager.sendMessage(sender, "command_group_spy_no_permission");
                            }

                        } else if (player.hasPermission("multichat.staff.spy")) {

                            if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

                                TGroupChatInfo groupChatInfo = MultiChat.groupchats.get(args[1].toLowerCase());

                                if (!groupChatInfo.existsMember(player.getUniqueId())) {

                                    if (groupChatInfo.existsViewer(player.getUniqueId())) {

                                        groupChatInfo.delViewer(player.getUniqueId());
                                        MultiChat.groupchats.remove(groupChatInfo.getName().toLowerCase());
                                        MultiChat.groupchats.put(groupChatInfo.getName().toLowerCase(), groupChatInfo);
                                        MessageManager.sendSpecialMessage(sender, "command_group_spy_off", groupChatInfo.getName().toUpperCase());

                                    } else {

                                        groupChatInfo.addViewer(player.getUniqueId());
                                        MultiChat.groupchats.remove(groupChatInfo.getName().toLowerCase());
                                        MultiChat.groupchats.put(groupChatInfo.getName().toLowerCase(), groupChatInfo);
                                        MessageManager.sendSpecialMessage(sender, "command_group_spy_on", groupChatInfo.getName().toUpperCase());

                                    }

                                } else {
                                    MessageManager.sendMessage(sender, "command_group_spy_already_a_member");
                                }

                            } else {
                                MessageManager.sendMessage(sender, "command_group_spy_does_not_exist");
                            }

                        } else {
                            MessageManager.sendMessage(sender, "command_group_spy_no_permission");
                        }

                    }

                    if (args[0].equalsIgnoreCase("help")) {

                        GroupManager groupman = new GroupManager();
                        if (args[1].equals("1")) {
                            groupman.displayHelp(1, sender);
                        } else {
                            groupman.displayHelp(2, sender);
                        }

                    }

                    if ((args[0].equalsIgnoreCase("create")) || (args[0].equalsIgnoreCase("make"))) {
                        if (player.hasPermission("multichat.group.create")) {
                            if (args[1].length() <= 20) {
                                if (!MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

                                    GroupManager groupman = new GroupManager();

                                    // Make the new group
                                    groupman.createGroup(args[1], player.getUniqueId(), false, "");
                                    // Select the new group for the player
                                    groupman.setViewedChat(player.getUniqueId(), args[1]);
                                    // Announce join to group members
                                    MessageManager.sendSpecialMessage(sender, "command_group_created", args[1].toUpperCase());

                                    groupman.announceJoinGroup(player.getUsername(), args[1]);

                                } else {
                                    MessageManager.sendSpecialMessage(sender, "command_group_already_exists", args[1].toUpperCase());
                                }

                            } else {
                                MessageManager.sendMessage(sender, "command_group_max_length");
                            }

                        } else {
                            MessageManager.sendMessage(sender, "command_group_create_no_permission");
                        }
                    }

                    if (args[0].equalsIgnoreCase("join")) {

                        if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

                            GroupManager groupman = new GroupManager();

                            //Run the join group routine
                            if (groupman.joinGroup(args[1], player, "")) {

                                //If the join is successful, set their viewed chat
                                groupman.setViewedChat(player.getUniqueId(), args[1]);
                                MessageManager.sendSpecialMessage(sender, "command_group_joined", args[1].toUpperCase());
                                //Announce their join
                                groupman.announceJoinGroup(player.getUsername(), args[1]);
                            }

                        } else {
                            MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
                        }
                    }

                    if ((args[0].equalsIgnoreCase("quit")) || (args[0].equalsIgnoreCase("leave"))) {
                        if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {
                            GroupManager groupman = new GroupManager();
                            groupman.quitGroup(args[1].toLowerCase(), player.getUniqueId(), player);
                        } else {
                            MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
                        }
                    }

                    if (args[0].equalsIgnoreCase("formal")) {
                        if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {
                            TGroupChatInfo groupChatInfo = MultiChat.groupchats.get(args[1].toLowerCase());
                            if (!groupChatInfo.getFormal()) {
                                if (groupChatInfo.getAdmins().contains(player.getUniqueId())) {
                                    groupChatInfo.setFormal(true);
                                    MultiChat.groupchats.remove(groupChatInfo.getName());
                                    MultiChat.groupchats.put(groupChatInfo.getName(), groupChatInfo);
                                    GCCommand.sendMessage(player.getUsername() + MessageManager.getMessage("groups_info_formal"), "&lINFO", groupChatInfo);
                                } else {
                                    MessageManager.sendMessage(sender, "command_group_formal_not_owner");
                                }
                            } else {
                                MessageManager.sendSpecialMessage(sender, "command_group_formal_already_formal", args[1].toUpperCase());
                            }
                        } else {
                            MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
                        }
                    }

                    if (args[0].equalsIgnoreCase("delete")) {
                        if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {
                            TGroupChatInfo groupChatInfo = MultiChat.groupchats.get(args[1].toLowerCase());
                            if (groupChatInfo.getAdmins().contains(player.getUniqueId())) {
                                for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {

                                    if ((MultiChat.viewedchats.get(onlineplayer.getUniqueId()) != null) &&
                                            (MultiChat.viewedchats.get(onlineplayer.getUniqueId()).equalsIgnoreCase(groupChatInfo.getName()))) {

                                        MultiChat.viewedchats.remove(onlineplayer.getUniqueId());
                                        MultiChat.viewedchats.put(onlineplayer.getUniqueId(), null);

                                    }
                                }

                                GCCommand.sendMessage(player.getUsername() + MessageManager.getMessage("groups_info_deleted"), "&lINFO", groupChatInfo);
                                GCCommand.sendMessage(MessageManager.getMessage("groups_info_goodbye"), "&lINFO", groupChatInfo);

                                MultiChat.groupchats.remove(groupChatInfo.getName().toLowerCase());
                            } else {
                                MessageManager.sendMessage(sender, "command_group_formal_not_admin");
                            }

                        } else {
                            MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
                        }
                    }

                    break;

                case 3:

                    if ((!args[0].equalsIgnoreCase("create")) && (!args[0].equalsIgnoreCase("make"))
                            && (!args[0].equalsIgnoreCase("join")) && (!args[0].equalsIgnoreCase("transfer"))
                            && (!args[0].equalsIgnoreCase("admin")) && (!args[0].equalsIgnoreCase("addadmin"))
                            && (!args[0].equalsIgnoreCase("removeadmin")) && (!args[0].equalsIgnoreCase("ban"))) {

                        MessageManager.sendMessage(sender, "command_group_incorrect_usage");

                    }

                    if ((args[0].equalsIgnoreCase("create")) || (args[0].equalsIgnoreCase("make"))) {
                        if (player.hasPermission("multichat.group.create")) {
                            if ((args[1].length() <= 20) && (args[2].length() <= 20)) {
                                if (!MultiChat.groupchats.containsKey(args[1].toLowerCase())) {
                                    GroupManager groupman = new GroupManager();

                                    //Make the new group
                                    groupman.createGroup(args[1], player.getUniqueId(), true, args[2]);
                                    //Select the new group for the player
                                    groupman.setViewedChat(player.getUniqueId(), args[1]);
                                    //Announce join to group members
                                    groupman.announceJoinGroup(player.getUsername(), args[1]);

                                    MessageManager.sendSpecialMessage(sender, "command_group_created", args[1].toUpperCase());
                                } else {
                                    MessageManager.sendSpecialMessage(sender, "command_group_already_exists", args[1].toUpperCase());
                                }

                            } else {
                                MessageManager.sendMessage(sender, "command_group_max_length_password");
                            }

                        } else {
                            MessageManager.sendMessage(sender, "command_group_create_no_permission");
                        }
                    }

                    if (args[0].equalsIgnoreCase("join")) {
                        if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {
                            GroupManager groupman = new GroupManager();
                            //Run the join group routine
                            if (groupman.joinGroup(args[1], player, args[2])) {
                                //If the join is successful, set their viewed chat
                                groupman.setViewedChat(player.getUniqueId(), args[1]);
                                MessageManager.sendSpecialMessage(sender, "command_group_joined", args[1].toUpperCase());
                                //Announce their join
                                groupman.announceJoinGroup(player.getUsername(), args[1]);
                            }

                        } else {
                            MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
                        }
                    }

                    if (args[0].equalsIgnoreCase("transfer")) {
                        if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {
                            if (MultiChat.getInstance().getServer().getPlayer(args[2].toLowerCase()).isPresent()) {

                                Player newplayer = MultiChat.getInstance().getServer().getPlayer(args[2].toLowerCase()).get();
                                TGroupChatInfo groupChatInfo = MultiChat.groupchats.get(args[1].toLowerCase());

                                if (!groupChatInfo.getFormal()) {

                                    if (groupChatInfo.existsAdmin(player.getUniqueId())) {

                                        if (groupChatInfo.existsMember(newplayer.getUniqueId())) {

                                            groupChatInfo.addAdmin(newplayer.getUniqueId());
                                            groupChatInfo.delAdmin(player.getUniqueId());

                                            MultiChat.groupchats.remove(groupChatInfo.getName());
                                            MultiChat.groupchats.put(groupChatInfo.getName(), groupChatInfo);

                                            GCCommand.sendMessage(player.getUsername() + MessageManager.getMessage("groups_info_transfer") + newplayer.getUsername(), "&lINFO", groupChatInfo);

                                        } else {
                                            MessageManager.sendMessage(sender, "command_group_transfer_not_member");
                                        }

                                    } else {
                                        MessageManager.sendMessage(sender, "command_group_transfer_not_owner");
                                    }

                                } else {
                                    MessageManager.sendMessage(sender, "command_group_transfer_not_informal");
                                }

                            } else {
                                MessageManager.sendMessage(sender, "command_group_player_not_online");
                            }
                        } else {
                            MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
                        }
                    }

                    if ((args[0].equalsIgnoreCase("admin")) || (args[0].equalsIgnoreCase("addadmin")) || (args[0].equalsIgnoreCase("removeadmin"))) {

                        if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

                            if (MultiChat.getInstance().getServer().getPlayer(args[2].toLowerCase()).isPresent()) {

                                Player newplayer = MultiChat.getInstance().getServer().getPlayer(args[2].toLowerCase()).orElse(null);

                                TGroupChatInfo groupChatInfo = MultiChat.groupchats.get(args[1].toLowerCase());

                                if (groupChatInfo.getFormal()) {

                                    if (groupChatInfo.existsAdmin(player.getUniqueId())) {

                                        if (groupChatInfo.existsMember(newplayer.getUniqueId())) {

                                            if (!groupChatInfo.existsAdmin(newplayer.getUniqueId())) {

                                                groupChatInfo.addAdmin(newplayer.getUniqueId());

                                                MultiChat.groupchats.remove(groupChatInfo.getName());
                                                MultiChat.groupchats.put(groupChatInfo.getName(), groupChatInfo);

                                                GCCommand.sendMessage(player.getUsername() + MessageManager.getMessage("groups_info_promoted") + newplayer.getUsername(), "&lINFO", groupChatInfo);

                                            } else if (newplayer.getUniqueId().equals(player.getUniqueId())) {

                                                if (groupChatInfo.getAdmins().size() > 1) {

                                                    groupChatInfo.delAdmin(player.getUniqueId());

                                                    MultiChat.groupchats.remove(groupChatInfo.getName());
                                                    MultiChat.groupchats.put(groupChatInfo.getName(), groupChatInfo);

                                                    GCCommand.sendMessage(player.getUsername() + MessageManager.getMessage("groups_info_step_down"), "&lINFO", groupChatInfo);

                                                } else {
                                                    MessageManager.sendMessage(sender, "command_group_formal_only_admin");
                                                }

                                            } else {
                                                MessageManager.sendMessage(sender, "command_group_formal_cannot_demote");
                                            }

                                        } else {
                                            MessageManager.sendMessage(sender, "command_group_transfer_not_member");
                                        }

                                    } else {
                                        MessageManager.sendMessage(sender, "command_group_formal_not_admin");
                                    }

                                } else {
                                    MessageManager.sendMessage(sender, "command_group_not_formal");
                                }

                            } else {
                                MessageManager.sendMessage(sender, "command_group_player_not_online");
                            }

                        } else {
                            MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
                        }

                    }

                    if (args[0].equalsIgnoreCase("ban")) {

                        if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

                            if (MultiChat.getInstance().getServer().getPlayer(args[2].toLowerCase()).isPresent()) {

                                Player newPlayer = MultiChat.getInstance().getServer().getPlayer(args[2].toLowerCase()).orElse(null);
                                TGroupChatInfo groupChatInfo = MultiChat.groupchats.get(args[1].toLowerCase());

                                if (groupChatInfo.getFormal()) {

                                    if (groupChatInfo.existsAdmin(player.getUniqueId())) {

                                        if (!groupChatInfo.existsAdmin(newPlayer.getUniqueId())) {

                                            if (!groupChatInfo.existsBanned(newPlayer.getUniqueId())) {

                                                groupChatInfo.addBanned(newPlayer.getUniqueId());

                                                if (groupChatInfo.existsMember(newPlayer.getUniqueId())) {

                                                    groupChatInfo.delMember(newPlayer.getUniqueId());
                                                    groupChatInfo.delViewer(newPlayer.getUniqueId());

                                                    MultiChat.viewedchats.remove(newPlayer.getUniqueId());
                                                    MultiChat.viewedchats.put(newPlayer.getUniqueId(), null);

                                                    GCCommand.sendMessage(player.getUsername() + MessageManager.getMessage("groups_info_kick") + newPlayer.getUsername(), "&lINFO", groupChatInfo);
                                                }

                                                MultiChat.groupchats.remove(groupChatInfo.getName());
                                                MultiChat.groupchats.put(groupChatInfo.getName(), groupChatInfo);

                                                GCCommand.sendMessage(player.getUsername() + MessageManager.getMessage("groups_info_ban") + newPlayer.getUsername(), "&lINFO", groupChatInfo);

                                                MessageManager.sendSpecialMessage(newPlayer, "command_group_banned", groupChatInfo.getName());


                                            } else {

                                                groupChatInfo.delBanned(newPlayer.getUniqueId());

                                                MultiChat.groupchats.remove(groupChatInfo.getName());
                                                MultiChat.groupchats.put(groupChatInfo.getName(), groupChatInfo);

                                                GCCommand.sendMessage(player.getUsername() + MessageManager.getMessage("groups_info_unban") + newPlayer.getUsername(), "&lINFO", groupChatInfo);

                                                MessageManager.sendSpecialMessage(newPlayer, "command_group_unbanned", groupChatInfo.getName());
                                            }

                                        } else {
                                            MessageManager.sendMessage(sender, "command_group_cannot_ban_admin");
                                        }

                                    } else {
                                        MessageManager.sendMessage(sender, "command_group_ban_not_admin");
                                    }

                                } else {
                                    MessageManager.sendMessage(sender, "command_group_not_formal");
                                }

                            } else {
                                MessageManager.sendMessage(sender, "command_group_player_not_online");
                            }

                        } else {
                            MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
                        }
                    }

                    break;

                case 4:

                    if ((args[0].equalsIgnoreCase("color")) || (args[0].equalsIgnoreCase("colour"))) {

                        if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

                            TGroupChatInfo groupChatInfo = MultiChat.groupchats.get(args[1].toLowerCase());

                            if (((groupChatInfo.existsMember(player.getUniqueId()))
                                    && (!groupChatInfo.getFormal())) || (groupChatInfo.existsAdmin(player.getUniqueId()))) {

                                args[2] = args[2].toLowerCase();
                                args[3] = args[3].toLowerCase();

                                if ((args[2].equals("a")) || (args[2].equals("b")) || (args[2].equals("c")) || (args[2].equals("d"))
                                        || (args[2].equals("e")) || (args[2].equals("f")) || (args[2].equals("0")) || (args[2].equals("1"))
                                        || (args[2].equals("2")) || (args[2].equals("3")) || (args[2].equals("4")) || (args[2].equals("5"))
                                        || (args[2].equals("6")) || (args[2].equals("7")) || (args[2].equals("8")) || (args[2].equals("9"))) {

                                    if ((args[3].equals("a")) || (args[3].equals("b")) || (args[3].equals("c")) || (args[3].equals("d"))
                                            || (args[3].equals("e")) || (args[3].equals("f")) || (args[3].equals("0")) || (args[3].equals("1"))
                                            || (args[3].equals("2")) || (args[3].equals("3")) || (args[3].equals("4")) || (args[3].equals("5"))
                                            || (args[3].equals("6")) || (args[3].equals("7")) || (args[3].equals("8")) || (args[3].equals("9"))) {

                                        MultiChat.groupchats.remove(groupChatInfo.getName());

                                        groupChatInfo.setChatColor(args[2].charAt(0));
                                        groupChatInfo.setNameColor(args[3].charAt(0));

                                        MultiChat.groupchats.put(groupChatInfo.getName(), groupChatInfo);

                                        GCCommand.sendMessage(MessageManager.getMessage("groups_info_colors") + player.getUsername(), "&lINFO", groupChatInfo);

                                    } else {
                                        MessageManager.sendMessage(sender, "command_group_color_invalid");
                                        MessageManager.sendMessage(sender, "command_group_color_usage");
                                    }

                                } else {
                                    MessageManager.sendMessage(sender, "command_group_color_invalid");
                                    MessageManager.sendMessage(sender, "command_group_color_usage");
                                }

                            } else {
                                MessageManager.sendMessage(sender, "command_group_formal_not_admin");
                            }

                        } else {
                            MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
                        }

                    } else {
                        MessageManager.sendMessage(sender, "command_group_incorrect_usage");
                    }

                    break;

            }

        } else {
            MessageManager.sendMessage(sender, "command_group_only_players");
        }
    }
}
