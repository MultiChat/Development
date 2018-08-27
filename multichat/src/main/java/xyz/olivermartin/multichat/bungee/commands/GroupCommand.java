package xyz.olivermartin.multichat.bungee.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.olivermartin410.plugins.TGroupChatInfo;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import xyz.olivermartin.multichat.bungee.GroupManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.bungee.UUIDNameManager;

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

		if ((args.length < 1) || ((args.length == 1) && (args[0].toLowerCase().equals("help")))) {

			GroupManager groupman = new GroupManager();
			groupman.displayHelp(1,sender);
			groupman = null;

		} else if ((sender instanceof ProxiedPlayer)) {

			switch (args.length) {

			case 1: 

				if ((sender instanceof ProxiedPlayer)) {

					if (MultiChat.groupchats.containsKey(args[0].toLowerCase())) {

						TGroupChatInfo groupInfo = (TGroupChatInfo) MultiChat.groupchats.get(args[0].toLowerCase());
						ProxiedPlayer player = (ProxiedPlayer) sender;

						if (groupInfo.existsMember(player.getUniqueId())) {

							String viewedchat = (String)MultiChat.viewedchats.get(player.getUniqueId());
							viewedchat = args[0].toLowerCase();
							MultiChat.viewedchats.remove(player.getUniqueId());
							MultiChat.viewedchats.put(player.getUniqueId(), viewedchat);

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

					if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

						TGroupChatInfo groupChatInfo = new TGroupChatInfo();
						ProxiedPlayer player = (ProxiedPlayer) sender;

						groupChatInfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());

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

					} else if (player.hasPermission("multichat.staff.spy"))	{

						if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

							TGroupChatInfo groupChatInfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());

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

							if (!MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

								GroupManager groupman = new GroupManager();

								//Make the new group
								groupman.createGroup(args[1], player.getUniqueId(), false, "");
								//Select the new group for the player
								groupman.setViewedChat(player.getUniqueId(), args[1]);
								//Announce join to group members
								sender.sendMessage(new ComponentBuilder("You successfully created, joined, and selected the group: " + args[1].toUpperCase()).color(ChatColor.GREEN).create());

								groupman.announceJoinGroup(sender.getName(), args[1]);
								groupman = null;

							} else {
								sender.sendMessage(new ComponentBuilder("Sorry the following group chat already exists: " + args[1].toUpperCase()).color(ChatColor.RED).create());
							}

						} else {
							sender.sendMessage(new ComponentBuilder("Sorry group name cannot exceed 20 characters!").color(ChatColor.RED).create());
						}

					} else {
						sender.sendMessage(new ComponentBuilder("Sorry you do not have permission to create new group chats").color(ChatColor.RED).create());
					}
				} 

				if (args[0].toLowerCase().equals("join")) {

					if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

						GroupManager groupman = new GroupManager();
						ProxiedPlayer player = (ProxiedPlayer)sender;

						//Run the join group routine
						if (groupman.joinGroup(args[1], player, "") == true ){

							//If the join is successful, set their viewed chat
							groupman.setViewedChat(player.getUniqueId(), args[1]);
							sender.sendMessage(new ComponentBuilder("You successfully joined and selected the group: " + args[1].toUpperCase()).color(ChatColor.GREEN).create());
							//Announce their join
							groupman.announceJoinGroup(player.getName(), args[1]);
						}   

					} else {
						sender.sendMessage(new ComponentBuilder("Sorry the following group chat does not exist: " + args[1].toUpperCase()).color(ChatColor.RED).create());
					}
				}

				if ((args[0].toLowerCase().equals("quit")) || (args[0].toLowerCase().equals("leave"))) {

					if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

						GroupManager groupman = new GroupManager();

						ProxiedPlayer player = (ProxiedPlayer)sender;

						groupman.quitGroup(args[1].toLowerCase(), player.getUniqueId(), player);

					} else {
						sender.sendMessage(new ComponentBuilder("Sorry the following group chat does not exist: " + args[1].toUpperCase()).color(ChatColor.RED).create());
					}
				}

				if (args[0].toLowerCase().equals("formal")) {

					if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

						TGroupChatInfo groupChatInfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());

						if (!groupChatInfo.getFormal()) {

							ProxiedPlayer player = (ProxiedPlayer) sender;

							if (groupChatInfo.getAdmins().contains(player.getUniqueId())) {

								groupChatInfo.setFormal(true);
								MultiChat.groupchats.remove(groupChatInfo.getName());
								MultiChat.groupchats.put(groupChatInfo.getName(), groupChatInfo);
								GCCommand.sendMessage(sender.getName() + " has converted this group to a FORMAL group chat!", "&lINFO", groupChatInfo);

							} else {
								sender.sendMessage(new ComponentBuilder("Sorry this command can only be used by the group chat owner").color(ChatColor.RED).create());
							}

						} else {
							sender.sendMessage(new ComponentBuilder("Sorry this chat is already a formal group chat: " + args[1].toUpperCase()).color(ChatColor.RED).create());
						}

						groupChatInfo = null;

					} else {
						sender.sendMessage(new ComponentBuilder("Sorry the following group chat does not exist: " + args[1].toUpperCase()).color(ChatColor.RED).create());
					}
				}

				if (args[0].toLowerCase().equals("delete")) {

					if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

						TGroupChatInfo groupChatInfo = (TGroupChatInfo) MultiChat.groupchats.get(args[1].toLowerCase());
						ProxiedPlayer player = (ProxiedPlayer) sender;

						if (groupChatInfo.getAdmins().contains(player.getUniqueId())) {

							for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

								if ((MultiChat.viewedchats.get(onlineplayer.getUniqueId()) != null) && 
										(((String)MultiChat.viewedchats.get(onlineplayer.getUniqueId())).toLowerCase().equals(groupChatInfo.getName().toLowerCase()))) {

									MultiChat.viewedchats.remove(onlineplayer.getUniqueId());
									MultiChat.viewedchats.put(onlineplayer.getUniqueId(), null);

								}
							}

							GCCommand.sendMessage(sender.getName() + " has deleted this group chat!", "&lINFO", groupChatInfo);
							GCCommand.sendMessage("Goodbye! If you want to see group chat commands do /group", "&lINFO", groupChatInfo);

							MultiChat.groupchats.remove(groupChatInfo.getName().toLowerCase());

							groupChatInfo = null;

						} else {
							sender.sendMessage(new ComponentBuilder("Sorry this command can only be used by group admins/owners").color(ChatColor.RED).create());
						}

					} else {
						sender.sendMessage(new ComponentBuilder("Sorry the following group chat does not exist: " + args[1].toUpperCase()).color(ChatColor.RED).create());
					}
				}

				break;

			case 3: 

				if ((!args[0].toLowerCase().equals("create")) && (!args[0].toLowerCase().equals("make"))
						&& (!args[0].toLowerCase().equals("join")) && (!args[0].toLowerCase().equals("transfer"))
						&& (!args[0].toLowerCase().equals("admin")) && (!args[0].toLowerCase().equals("addadmin"))
						&& (!args[0].toLowerCase().equals("removeadmin")) && (!args[0].toLowerCase().equals("ban"))) {

					sender.sendMessage(new ComponentBuilder("Incorrect command usage, use /group to see a list of commands!").color(ChatColor.RED).create());

				}

				if ((args[0].toLowerCase().equals("create")) || (args[0].toLowerCase().equals("make"))) {

					ProxiedPlayer player = (ProxiedPlayer) sender;

					if (player.hasPermission("multichat.group.create")) {

						if ((args[1].length() <= 20) && (args[2].length() <= 20)) {

							if (!MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

								GroupManager groupman = new GroupManager();

								//Make the new group
								groupman.createGroup(args[1], player.getUniqueId(), true, args[2]);
								//Select the new group for the player
								groupman.setViewedChat(player.getUniqueId(), args[1]);
								//Announce join to group members
								groupman.announceJoinGroup(sender.getName(), args[1]);

								sender.sendMessage(new ComponentBuilder("You successfully created, joined, and selected the group: " + args[1].toUpperCase()).color(ChatColor.GREEN).create());
								groupman = null;

							} else {
								sender.sendMessage(new ComponentBuilder("Sorry the following group chat already exists: " + args[1].toUpperCase()).color(ChatColor.RED).create());
							}

						} else {
							sender.sendMessage(new ComponentBuilder("Sorry neither group name or password must exceed 20 characters").color(ChatColor.RED).create());
						}

					} else {
						sender.sendMessage(new ComponentBuilder("Sorry you do not have permission to create new group chats").color(ChatColor.RED).create());
					}
				}

				if (args[0].toLowerCase().equals("join")) {
					if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

						GroupManager groupman = new GroupManager();
						ProxiedPlayer player = (ProxiedPlayer)sender;

						//Run the join group routine
						if (groupman.joinGroup(args[1], player, args[2]) == true ){

							//If the join is successful, set their viewed chat
							groupman.setViewedChat(player.getUniqueId(), args[1]);
							sender.sendMessage(new ComponentBuilder("You successfully joined and selected the group: " + args[1].toUpperCase()).color(ChatColor.GREEN).create());
							//Announce their join
							groupman.announceJoinGroup(player.getName(), args[1]);
						}   

					} else {
						sender.sendMessage(new ComponentBuilder("Sorry the following group chat does not exist: " + args[1].toUpperCase()).color(ChatColor.RED).create());
					}
				}

				if (args[0].toLowerCase().equals("transfer")) {

					ProxiedPlayer player = (ProxiedPlayer) sender;

					if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

						if (ProxyServer.getInstance().getPlayer(args[2].toLowerCase()) != null) {

							ProxiedPlayer newplayer = ProxyServer.getInstance().getPlayer(args[2].toLowerCase());
							TGroupChatInfo groupChatInfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());

							if (!groupChatInfo.getFormal()) {

								if (groupChatInfo.existsAdmin(player.getUniqueId())) {

									if (groupChatInfo.existsMember(newplayer.getUniqueId())) {

										groupChatInfo.addAdmin(newplayer.getUniqueId());
										groupChatInfo.delAdmin(player.getUniqueId());

										MultiChat.groupchats.remove(groupChatInfo.getName());
										MultiChat.groupchats.put(groupChatInfo.getName(), groupChatInfo);

										GCCommand.sendMessage(sender.getName() + " has transferred ownership to " + newplayer.getName(), "&lINFO", groupChatInfo);

									} else {
										sender.sendMessage(new ComponentBuilder("This player is not already a member of the group!").color(ChatColor.RED).create());
									}

								} else {
									sender.sendMessage(new ComponentBuilder("Sorry you are not the owner of this chat!").color(ChatColor.RED).create());
								}

							} else {
								sender.sendMessage(new ComponentBuilder("This command can only be used on informal chats!").color(ChatColor.RED).create());
							}

							groupChatInfo = null;

						} else {
							sender.sendMessage(new ComponentBuilder("This player is not online!").color(ChatColor.RED).create());
						}
					} else {
						sender.sendMessage(new ComponentBuilder("Specified Group Does Not Exist!").color(ChatColor.RED).create());
					}
				}

				if ((args[0].toLowerCase().equals("admin")) || (args[0].toLowerCase().equals("addadmin")) || (args[0].toLowerCase().equals("removeadmin"))) {

					ProxiedPlayer player = (ProxiedPlayer)sender;

					if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

						if (ProxyServer.getInstance().getPlayer(args[2].toLowerCase()) != null) {

							ProxiedPlayer newplayer = ProxyServer.getInstance().getPlayer(args[2].toLowerCase());

							TGroupChatInfo groupChatInfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());

							if (groupChatInfo.getFormal() == true) {

								if (groupChatInfo.existsAdmin(player.getUniqueId())) {

									if (groupChatInfo.existsMember(newplayer.getUniqueId())) {

										if (!groupChatInfo.existsAdmin(newplayer.getUniqueId())) {

											groupChatInfo.addAdmin(newplayer.getUniqueId());

											MultiChat.groupchats.remove(groupChatInfo.getName());
											MultiChat.groupchats.put(groupChatInfo.getName(), groupChatInfo);

											GCCommand.sendMessage(sender.getName() + " has promoted the following member to group admin: " + newplayer.getName(), "&lINFO", groupChatInfo);

										} else if (newplayer.getUniqueId().equals(player.getUniqueId())) {

											if (groupChatInfo.getAdmins().size() > 1) {

												groupChatInfo.delAdmin(player.getUniqueId());

												MultiChat.groupchats.remove(groupChatInfo.getName());
												MultiChat.groupchats.put(groupChatInfo.getName(), groupChatInfo);

												GCCommand.sendMessage(sender.getName() + " has stepped down as a group admin", "&lINFO", groupChatInfo);

											} else {
												sender.sendMessage(new ComponentBuilder("You can't step down as a group admin because you are the only one!").color(ChatColor.RED).create());
											}

										} else {
											sender.sendMessage(new ComponentBuilder("You can't demote another group admin!").color(ChatColor.RED).create());
										}

									} else {
										sender.sendMessage(new ComponentBuilder("This player is not already a member of the group!").color(ChatColor.RED).create());
									}

								} else {
									sender.sendMessage(new ComponentBuilder("Sorry you are not an admin of this chat!").color(ChatColor.RED).create());
								}

							} else {
								sender.sendMessage(new ComponentBuilder("This command can only be used on formal chats!").color(ChatColor.RED).create());
							}

							groupChatInfo = null;

						} else {
							sender.sendMessage(new ComponentBuilder("This player is not online!").color(ChatColor.RED).create());
						}

					} else {
						sender.sendMessage(new ComponentBuilder("Specified Group Does Not Exist!").color(ChatColor.RED).create());
					}

				}

				if (args[0].toLowerCase().equals("ban")) {

					ProxiedPlayer player = (ProxiedPlayer) sender;

					if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

						if (ProxyServer.getInstance().getPlayer(args[2].toLowerCase()) != null) {

							ProxiedPlayer newPlayer = ProxyServer.getInstance().getPlayer(args[2].toLowerCase());
							TGroupChatInfo groupChatInfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());

							if (groupChatInfo.getFormal() == true) {

								if (groupChatInfo.existsAdmin(player.getUniqueId())) {

									if (!groupChatInfo.existsAdmin(newPlayer.getUniqueId())) {

										if (!groupChatInfo.existsBanned(newPlayer.getUniqueId())) {

											groupChatInfo.addBanned(newPlayer.getUniqueId());

											if (groupChatInfo.existsMember(newPlayer.getUniqueId())) {

												groupChatInfo.delMember(newPlayer.getUniqueId());
												groupChatInfo.delViewer(newPlayer.getUniqueId());

												MultiChat.viewedchats.remove(newPlayer.getUniqueId());
												MultiChat.viewedchats.put(newPlayer.getUniqueId(), null);

												GCCommand.sendMessage(sender.getName() + " kicked the following player from the group chat: " + newPlayer.getName(), "&lINFO", groupChatInfo);
											}

											MultiChat.groupchats.remove(groupChatInfo.getName());
											MultiChat.groupchats.put(groupChatInfo.getName(), groupChatInfo);

											GCCommand.sendMessage(sender.getName() + " has banned the following player from the group chat: " + newPlayer.getName(), "&lINFO", groupChatInfo);

											newPlayer.sendMessage(new ComponentBuilder("You were banned from group: " + groupChatInfo.getName()).color(ChatColor.RED).create());

										} else {

											groupChatInfo.delBanned(newPlayer.getUniqueId());

											MultiChat.groupchats.remove(groupChatInfo.getName());
											MultiChat.groupchats.put(groupChatInfo.getName(), groupChatInfo);

											GCCommand.sendMessage(sender.getName() + " has unbanned the following player from the group chat: " + newPlayer.getName(), "&lINFO", groupChatInfo);

											newPlayer.sendMessage(new ComponentBuilder("You were unbanned from group: " + groupChatInfo.getName()).color(ChatColor.RED).create());
										}

									} else {
										sender.sendMessage(new ComponentBuilder("You can't ban a group admin!").color(ChatColor.RED).create());
									}

								} else {
									sender.sendMessage(new ComponentBuilder("Sorry you are not an admin of this chat!").color(ChatColor.RED).create());
								}

							} else {
								sender.sendMessage(new ComponentBuilder("This command can only be used on formal chats!").color(ChatColor.RED).create());
							}

							groupChatInfo = null;

						} else {
							sender.sendMessage(new ComponentBuilder("This player is not online!").color(ChatColor.RED).create());
						}

					} else {
						sender.sendMessage(new ComponentBuilder("Specified Group Does Not Exist!").color(ChatColor.RED).create());
					}
				}

				break;

			case 4: 

				if ((args[0].toLowerCase().equals("color")) || (args[0].toLowerCase().equals("colour"))) {

					if (MultiChat.groupchats.containsKey(args[1].toLowerCase())) {

						TGroupChatInfo groupChatInfo = new TGroupChatInfo();
						ProxiedPlayer player = (ProxiedPlayer) sender;

						groupChatInfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());

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

									GCCommand.sendMessage("Group Chat Colours Changed by " + sender.getName(), "&lINFO", groupChatInfo);

								} else {
									sender.sendMessage(new ComponentBuilder("Invalid color codes specified: Must be letters a-f or numbers 0-9").color(ChatColor.RED).create());
									sender.sendMessage(new ComponentBuilder("Usage: /group color/colour <group name> <chatcolorcode> <namecolorcode>").color(ChatColor.RED).create());
								}

							} else {
								sender.sendMessage(new ComponentBuilder("Invalid color codes specified: Must be letters a-f or numbers 0-9").color(ChatColor.RED).create());
								sender.sendMessage(new ComponentBuilder("Usage: /group color/colour <group name> <chatcolorcode> <namecolorcode>").color(ChatColor.RED).create());
							}

						} else {
							sender.sendMessage(new ComponentBuilder("Sorry you don't have permission to do this!").color(ChatColor.RED).create());
						}

						groupChatInfo = null;

					} else {
						sender.sendMessage(new ComponentBuilder("Sorry the specified group chat does not exist!").color(ChatColor.RED).create());
					}

				} else {
					sender.sendMessage(new ComponentBuilder("Incorrect command usage, use /group to see a list of commands!").color(ChatColor.RED).create());
				}

				break;

			}

		} else {
			sender.sendMessage(new ComponentBuilder("Only players can use group chats").color(ChatColor.RED).create());
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args)
	{

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
