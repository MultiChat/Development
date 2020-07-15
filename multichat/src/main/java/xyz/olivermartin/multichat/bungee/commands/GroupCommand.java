package xyz.olivermartin.multichat.bungee.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.olivermartin410.plugins.TGroupChatInfo;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import xyz.olivermartin.multichat.bungee.GroupManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.UUIDNameManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyDataStore;

/**
 * The Group Command
 * <p>From here the player can manipulate group chats in every possible way</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class GroupCommand extends Command implements TabExecutor {

	private static String[] aliases = new String[] {};

	public GroupCommand() {
		super("group", "multichat.group", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		if ((args.length < 1) || ((args.length == 1) && (args[0].toLowerCase().equals("help")))) {

			GroupManager groupman = new GroupManager();
			groupman.displayHelp(1, sender);
			groupman = null;

		} else if ((sender instanceof ProxiedPlayer)) {

			switch (args.length) {

			case 1: 

				if ((sender instanceof ProxiedPlayer)) {

					if (ds.getGroupChats().containsKey(args[0].toLowerCase())) {

						TGroupChatInfo groupInfo = (TGroupChatInfo) ds.getGroupChats().get(args[0].toLowerCase());
						ProxiedPlayer player = (ProxiedPlayer) sender;

						if (groupInfo.existsMember(player.getUniqueId())) {

							String viewedchat = (String)ds.getViewedChats().get(player.getUniqueId());
							viewedchat = args[0].toLowerCase();
							ds.getViewedChats().remove(player.getUniqueId());
							ds.getViewedChats().put(player.getUniqueId(), viewedchat);

							MessageManager.sendSpecialMessage(sender, "command_group_selected", args[0].toUpperCase());

						} else {
							MessageManager.sendSpecialMessage(sender, "command_group_not_a_member", args[0].toUpperCase());
						}

						groupInfo = null;
					} else {
						MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[0].toUpperCase());
					}

				} else {
					MessageManager.sendMessage(sender, "command_group_only_players_select");
				}

				break;

			case 2: 

				if ((!args[0].toLowerCase().equals("members")) && (!args[0].toLowerCase().equals("list"))
						&& (!args[0].toLowerCase().equals("spyall")) && (!args[0].toLowerCase().equals("spy"))
						&& (!args[0].toLowerCase().equals("help")) && (!args[0].toLowerCase().equals("create"))
						&& (!args[0].toLowerCase().equals("make")) && (!args[0].toLowerCase().equals("join"))
						&& (!args[0].toLowerCase().equals("quit")) && (!args[0].toLowerCase().equals("leave"))
						&& (!args[0].toLowerCase().equals("formal")) && (!args[0].toLowerCase().equals("delete"))) {

					MessageManager.sendMessage(sender, "command_group_incorrect_usage");
				}

				if ((args[0].toLowerCase().equals("list")) || (args[0].toLowerCase().equals("members"))) {

					if (ds.getGroupChats().containsKey(args[1].toLowerCase())) {

						TGroupChatInfo groupChatInfo = new TGroupChatInfo();
						ProxiedPlayer player = (ProxiedPlayer) sender;

						groupChatInfo = (TGroupChatInfo)ds.getGroupChats().get(args[1].toLowerCase());

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

						groupChatInfo = null;

					} else {
						MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
					}

				}

				if (args[0].toLowerCase().equals("spy")) {

					ProxiedPlayer player = (ProxiedPlayer) sender;

					if (args[1].toLowerCase().equals("all")) {

						if (player.hasPermission("multichat.staff.spy")) {

							if (ds.getAllSpy().contains(player.getUniqueId())) {

								ds.getAllSpy().remove(player.getUniqueId());
								MessageManager.sendMessage(sender, "command_group_spy_all_disabled_1");
								MessageManager.sendMessage(sender, "command_group_spy_all_disabled_2");
								MessageManager.sendMessage(sender, "command_group_spy_all_disabled_3");

							} else {

								ds.getAllSpy().add(player.getUniqueId());
								MessageManager.sendMessage(sender, "command_group_spy_all_enabled");
							}

						} else {
							MessageManager.sendMessage(sender, "command_group_spy_no_permission");
						}

					} else if (player.hasPermission("multichat.staff.spy"))	{

						if (ds.getGroupChats().containsKey(args[1].toLowerCase())) {

							TGroupChatInfo groupChatInfo = (TGroupChatInfo)ds.getGroupChats().get(args[1].toLowerCase());

							if (!groupChatInfo.existsMember(player.getUniqueId())) {

								if (groupChatInfo.existsViewer(player.getUniqueId())) {

									groupChatInfo.delViewer(player.getUniqueId());
									ds.getGroupChats().remove(groupChatInfo.getName().toLowerCase());
									ds.getGroupChats().put(groupChatInfo.getName().toLowerCase(), groupChatInfo);
									MessageManager.sendSpecialMessage(sender, "command_group_spy_off", groupChatInfo.getName().toUpperCase());

								} else {

									groupChatInfo.addViewer(player.getUniqueId());
									ds.getGroupChats().remove(groupChatInfo.getName().toLowerCase());
									ds.getGroupChats().put(groupChatInfo.getName().toLowerCase(), groupChatInfo);
									MessageManager.sendSpecialMessage(sender, "command_group_spy_on", groupChatInfo.getName().toUpperCase());

								}

							} else {
								MessageManager.sendMessage(sender, "command_group_spy_already_a_member");
							}

							groupChatInfo = null;

						} else {
							MessageManager.sendMessage(sender, "command_group_spy_does_not_exist");
						}

					} else {
						MessageManager.sendMessage(sender, "command_group_spy_no_permission");
					}

				}

				if (args[0].toLowerCase().equals("help")) {

					if (args[1].equals("1")) {
						GroupManager groupman = new GroupManager();
						groupman.displayHelp(1,sender);
						groupman = null;
					} else {
						GroupManager groupman = new GroupManager();
						groupman.displayHelp(2,sender);
						groupman = null;
					}

				}

				if ((args[0].toLowerCase().equals("create")) || (args[0].toLowerCase().equals("make"))) {

					ProxiedPlayer player = (ProxiedPlayer) sender;

					if (player.hasPermission("multichat.group.create")) {

						if (args[1].length() <= 20) {

							if (!ds.getGroupChats().containsKey(args[1].toLowerCase())) {

								GroupManager groupman = new GroupManager();

								// Make the new group
								groupman.createGroup(args[1], player.getUniqueId(), false, "");
								// Select the new group for the player
								groupman.setViewedChat(player.getUniqueId(), args[1]);
								// Announce join to group members
								MessageManager.sendSpecialMessage(sender, "command_group_created", args[1].toUpperCase());

								groupman.announceJoinGroup(sender.getName(), args[1]);
								groupman = null;

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

				if (args[0].toLowerCase().equals("join")) {

					if (ds.getGroupChats().containsKey(args[1].toLowerCase())) {

						GroupManager groupman = new GroupManager();
						ProxiedPlayer player = (ProxiedPlayer)sender;

						//Run the join group routine
						if (groupman.joinGroup(args[1], player, "") == true ){

							//If the join is successful, set their viewed chat
							groupman.setViewedChat(player.getUniqueId(), args[1]);
							MessageManager.sendSpecialMessage(sender, "command_group_joined", args[1].toUpperCase());
							//Announce their join
							groupman.announceJoinGroup(player.getName(), args[1]);
						}   

					} else {
						MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
					}
				}

				if ((args[0].toLowerCase().equals("quit")) || (args[0].toLowerCase().equals("leave"))) {

					if (ds.getGroupChats().containsKey(args[1].toLowerCase())) {

						GroupManager groupman = new GroupManager();

						ProxiedPlayer player = (ProxiedPlayer)sender;

						groupman.quitGroup(args[1].toLowerCase(), player.getUniqueId(), player);

					} else {
						MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
					}
				}

				if (args[0].toLowerCase().equals("formal")) {

					if (ds.getGroupChats().containsKey(args[1].toLowerCase())) {

						TGroupChatInfo groupChatInfo = (TGroupChatInfo)ds.getGroupChats().get(args[1].toLowerCase());

						if (!groupChatInfo.getFormal()) {

							ProxiedPlayer player = (ProxiedPlayer) sender;

							if (groupChatInfo.getAdmins().contains(player.getUniqueId())) {

								groupChatInfo.setFormal(true);
								ds.getGroupChats().remove(groupChatInfo.getName());
								ds.getGroupChats().put(groupChatInfo.getName(), groupChatInfo);
								GCCommand.sendMessage(sender.getName() + MessageManager.getMessage("groups_info_formal"), "&lINFO", groupChatInfo);

							} else {
								MessageManager.sendMessage(sender, "command_group_formal_not_owner");
							}

						} else {
							MessageManager.sendSpecialMessage(sender, "command_group_formal_already_formal", args[1].toUpperCase());
						}

						groupChatInfo = null;

					} else {
						MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
					}
				}

				if (args[0].toLowerCase().equals("delete")) {

					if (ds.getGroupChats().containsKey(args[1].toLowerCase())) {

						TGroupChatInfo groupChatInfo = (TGroupChatInfo) ds.getGroupChats().get(args[1].toLowerCase());
						ProxiedPlayer player = (ProxiedPlayer) sender;

						if (groupChatInfo.getAdmins().contains(player.getUniqueId())) {

							for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

								if ((ds.getViewedChats().get(onlineplayer.getUniqueId()) != null) && 
										(((String)ds.getViewedChats().get(onlineplayer.getUniqueId())).toLowerCase().equals(groupChatInfo.getName().toLowerCase()))) {

									ds.getViewedChats().remove(onlineplayer.getUniqueId());
									ds.getViewedChats().put(onlineplayer.getUniqueId(), null);

								}
							}

							GCCommand.sendMessage(sender.getName() + MessageManager.getMessage("groups_info_deleted"), "&lINFO", groupChatInfo);
							GCCommand.sendMessage(MessageManager.getMessage("groups_info_goodbye"), "&lINFO", groupChatInfo);

							ds.getGroupChats().remove(groupChatInfo.getName().toLowerCase());

							groupChatInfo = null;

						} else {
							MessageManager.sendMessage(sender, "command_group_formal_not_admin");
						}

					} else {
						MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
					}
				}

				break;

			case 3: 

				if ((!args[0].toLowerCase().equals("create")) && (!args[0].toLowerCase().equals("make"))
						&& (!args[0].toLowerCase().equals("join")) && (!args[0].toLowerCase().equals("transfer"))
						&& (!args[0].toLowerCase().equals("admin")) && (!args[0].toLowerCase().equals("addadmin"))
						&& (!args[0].toLowerCase().equals("removeadmin")) && (!args[0].toLowerCase().equals("ban"))) {

					MessageManager.sendMessage(sender, "command_group_incorrect_usage");

				}

				if ((args[0].toLowerCase().equals("create")) || (args[0].toLowerCase().equals("make"))) {

					ProxiedPlayer player = (ProxiedPlayer) sender;

					if (player.hasPermission("multichat.group.create")) {

						if ((args[1].length() <= 20) && (args[2].length() <= 20)) {

							if (!ds.getGroupChats().containsKey(args[1].toLowerCase())) {

								GroupManager groupman = new GroupManager();

								//Make the new group
								groupman.createGroup(args[1], player.getUniqueId(), true, args[2]);
								//Select the new group for the player
								groupman.setViewedChat(player.getUniqueId(), args[1]);
								//Announce join to group members
								groupman.announceJoinGroup(sender.getName(), args[1]);

								MessageManager.sendSpecialMessage(sender, "command_group_created", args[1].toUpperCase());
								groupman = null;

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

				if (args[0].toLowerCase().equals("join")) {
					if (ds.getGroupChats().containsKey(args[1].toLowerCase())) {

						GroupManager groupman = new GroupManager();
						ProxiedPlayer player = (ProxiedPlayer)sender;

						//Run the join group routine
						if (groupman.joinGroup(args[1], player, args[2]) == true ){

							//If the join is successful, set their viewed chat
							groupman.setViewedChat(player.getUniqueId(), args[1]);
							MessageManager.sendSpecialMessage(sender, "command_group_joined", args[1].toUpperCase());
							//Announce their join
							groupman.announceJoinGroup(player.getName(), args[1]);
						}   

					} else {
						MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
					}
				}

				if (args[0].toLowerCase().equals("transfer")) {

					ProxiedPlayer player = (ProxiedPlayer) sender;

					if (ds.getGroupChats().containsKey(args[1].toLowerCase())) {

						if (ProxyServer.getInstance().getPlayer(args[2].toLowerCase()) != null) {

							ProxiedPlayer newplayer = ProxyServer.getInstance().getPlayer(args[2].toLowerCase());
							TGroupChatInfo groupChatInfo = (TGroupChatInfo)ds.getGroupChats().get(args[1].toLowerCase());

							if (!groupChatInfo.getFormal()) {

								if (groupChatInfo.existsAdmin(player.getUniqueId())) {

									if (groupChatInfo.existsMember(newplayer.getUniqueId())) {

										groupChatInfo.addAdmin(newplayer.getUniqueId());
										groupChatInfo.delAdmin(player.getUniqueId());

										ds.getGroupChats().remove(groupChatInfo.getName());
										ds.getGroupChats().put(groupChatInfo.getName(), groupChatInfo);

										GCCommand.sendMessage(sender.getName() + MessageManager.getMessage("groups_info_transfer") + newplayer.getName(), "&lINFO", groupChatInfo);

									} else {
										MessageManager.sendMessage(sender, "command_group_transfer_not_member");
									}

								} else {
									MessageManager.sendMessage(sender, "command_group_transfer_not_owner");
								}

							} else {
								MessageManager.sendMessage(sender, "command_group_transfer_not_informal");
							}

							groupChatInfo = null;

						} else {
							MessageManager.sendMessage(sender, "command_group_player_not_online");
						}
					} else {
						MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
					}
				}

				if ((args[0].toLowerCase().equals("admin")) || (args[0].toLowerCase().equals("addadmin")) || (args[0].toLowerCase().equals("removeadmin"))) {

					ProxiedPlayer player = (ProxiedPlayer)sender;

					if (ds.getGroupChats().containsKey(args[1].toLowerCase())) {

						if (ProxyServer.getInstance().getPlayer(args[2].toLowerCase()) != null) {

							ProxiedPlayer newplayer = ProxyServer.getInstance().getPlayer(args[2].toLowerCase());

							TGroupChatInfo groupChatInfo = (TGroupChatInfo)ds.getGroupChats().get(args[1].toLowerCase());

							if (groupChatInfo.getFormal() == true) {

								if (groupChatInfo.existsAdmin(player.getUniqueId())) {

									if (groupChatInfo.existsMember(newplayer.getUniqueId())) {

										if (!groupChatInfo.existsAdmin(newplayer.getUniqueId())) {

											groupChatInfo.addAdmin(newplayer.getUniqueId());

											ds.getGroupChats().remove(groupChatInfo.getName());
											ds.getGroupChats().put(groupChatInfo.getName(), groupChatInfo);

											GCCommand.sendMessage(sender.getName() + MessageManager.getMessage("groups_info_promoted") + newplayer.getName(), "&lINFO", groupChatInfo);

										} else if (newplayer.getUniqueId().equals(player.getUniqueId())) {

											if (groupChatInfo.getAdmins().size() > 1) {

												groupChatInfo.delAdmin(player.getUniqueId());

												ds.getGroupChats().remove(groupChatInfo.getName());
												ds.getGroupChats().put(groupChatInfo.getName(), groupChatInfo);

												GCCommand.sendMessage(sender.getName() + MessageManager.getMessage("groups_info_step_down"), "&lINFO", groupChatInfo);

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

							groupChatInfo = null;

						} else {
							MessageManager.sendMessage(sender, "command_group_player_not_online");
						}

					} else {
						MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
					}

				}

				if (args[0].toLowerCase().equals("ban")) {

					ProxiedPlayer player = (ProxiedPlayer) sender;

					if (ds.getGroupChats().containsKey(args[1].toLowerCase())) {

						if (ProxyServer.getInstance().getPlayer(args[2].toLowerCase()) != null) {

							ProxiedPlayer newPlayer = ProxyServer.getInstance().getPlayer(args[2].toLowerCase());
							TGroupChatInfo groupChatInfo = (TGroupChatInfo)ds.getGroupChats().get(args[1].toLowerCase());

							if (groupChatInfo.getFormal() == true) {

								if (groupChatInfo.existsAdmin(player.getUniqueId())) {

									if (!groupChatInfo.existsAdmin(newPlayer.getUniqueId())) {

										if (!groupChatInfo.existsBanned(newPlayer.getUniqueId())) {

											groupChatInfo.addBanned(newPlayer.getUniqueId());

											if (groupChatInfo.existsMember(newPlayer.getUniqueId())) {

												groupChatInfo.delMember(newPlayer.getUniqueId());
												groupChatInfo.delViewer(newPlayer.getUniqueId());

												ds.getViewedChats().remove(newPlayer.getUniqueId());
												ds.getViewedChats().put(newPlayer.getUniqueId(), null);

												GCCommand.sendMessage(sender.getName() + MessageManager.getMessage("groups_info_kick") + newPlayer.getName(), "&lINFO", groupChatInfo);
											}

											ds.getGroupChats().remove(groupChatInfo.getName());
											ds.getGroupChats().put(groupChatInfo.getName(), groupChatInfo);

											GCCommand.sendMessage(sender.getName() + MessageManager.getMessage("groups_info_ban") + newPlayer.getName(), "&lINFO", groupChatInfo);

											MessageManager.sendSpecialMessage(newPlayer, "command_group_banned", groupChatInfo.getName());


										} else {

											groupChatInfo.delBanned(newPlayer.getUniqueId());

											ds.getGroupChats().remove(groupChatInfo.getName());
											ds.getGroupChats().put(groupChatInfo.getName(), groupChatInfo);

											GCCommand.sendMessage(sender.getName() + MessageManager.getMessage("groups_info_unban") + newPlayer.getName(), "&lINFO", groupChatInfo);

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

							groupChatInfo = null;

						} else {
							MessageManager.sendMessage(sender, "command_group_player_not_online");
						}

					} else {
						MessageManager.sendSpecialMessage(sender, "command_group_does_not_exist", args[1].toUpperCase());
					}
				}

				break;

			case 4: 

				if ((args[0].toLowerCase().equals("color")) || (args[0].toLowerCase().equals("colour"))) {

					if (ds.getGroupChats().containsKey(args[1].toLowerCase())) {

						TGroupChatInfo groupChatInfo = new TGroupChatInfo();
						ProxiedPlayer player = (ProxiedPlayer) sender;

						groupChatInfo = (TGroupChatInfo)ds.getGroupChats().get(args[1].toLowerCase());

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

									ds.getGroupChats().remove(groupChatInfo.getName());

									groupChatInfo.setChatColor(args[2].charAt(0));
									groupChatInfo.setNameColor(args[3].charAt(0));

									ds.getGroupChats().put(groupChatInfo.getName(), groupChatInfo);

									GCCommand.sendMessage(MessageManager.getMessage("groups_info_colors") + sender.getName(), "&lINFO", groupChatInfo);

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

						groupChatInfo = null;

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

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {

		Set<String> matches = new HashSet<String>();

		if ( args.length == 1 ) {

			String search = args[0].toLowerCase();

			List<String> subCommands = new ArrayList<String>();

			subCommands.add("create");
			subCommands.add("join");
			subCommands.add("leave");
			subCommands.add("quit");
			subCommands.add("color");
			subCommands.add("colour");
			subCommands.add("transfer");
			subCommands.add("delete");
			subCommands.add("list");
			subCommands.add("members");
			subCommands.add("formal");
			subCommands.add("admin");
			subCommands.add("ban");

			for ( String sub : subCommands ) {
				if ( sub.toLowerCase().startsWith( search ) ) {
					matches.add( sub );
				}
			}
		}

		return matches;

	}
}
