package com.olivermartin410.plugins;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer; 
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class GroupCommand
extends Command implements TabExecutor
{
	public GroupCommand()
	{
		super("group", "multichat.group", new String[0]);
	}

	public void execute(CommandSender sender, String[] args)
	{
		if ((args.length < 1) || ((args.length == 1) && (args[0].toLowerCase().equals("help"))))
		{
			GroupManager groupman = new GroupManager();
			groupman.displayHelp(1,sender);
			groupman = null;
		}
		else if ((sender instanceof ProxiedPlayer))
		{
			switch (args.length)
			{
			case 1: 
				if ((sender instanceof ProxiedPlayer))
				{
					if (MultiChat.groupchats.containsKey(args[0].toLowerCase()))
					{
						TGroupChatInfo chatinfo = (TGroupChatInfo)MultiChat.groupchats.get(args[0].toLowerCase());
						ProxiedPlayer player = (ProxiedPlayer)sender;
						if (chatinfo.existsMember(player.getUniqueId()))
						{
							String viewedchat = (String)MultiChat.viewedchats.get(player.getUniqueId());

							viewedchat = args[0].toLowerCase();

							MultiChat.viewedchats.remove(player.getUniqueId());

							MultiChat.viewedchats.put(player.getUniqueId(), viewedchat);

							sender.sendMessage(new ComponentBuilder("Your /gc messages will now go to group: " + args[0].toUpperCase()).color(ChatColor.GREEN).create());
						}
						else
						{
							sender.sendMessage(new ComponentBuilder("Sorry you aren't a member of group: " + args[0].toUpperCase()).color(ChatColor.RED).create());
						}
						chatinfo = null;
					}
					else
					{
						sender.sendMessage(new ComponentBuilder("Sorry the following group chat does not exist: " + args[0].toUpperCase()).color(ChatColor.RED).create());
					}
				}
				else {
					sender.sendMessage(new ComponentBuilder("Only players can select a group chat").color(ChatColor.RED).create());
				}
				break;
			case 2: 
				if ((!args[0].toLowerCase().equals("members")) && (!args[0].toLowerCase().equals("list")) && (!args[0].toLowerCase().equals("spyall")) && (!args[0].toLowerCase().equals("spy")) && (!args[0].toLowerCase().equals("help")) && (!args[0].toLowerCase().equals("create")) && (!args[0].toLowerCase().equals("make")) && (!args[0].toLowerCase().equals("join")) && (!args[0].toLowerCase().equals("quit")) && (!args[0].toLowerCase().equals("leave")) && (!args[0].toLowerCase().equals("formal")) && (!args[0].toLowerCase().equals("delete"))) {
					sender.sendMessage(new ComponentBuilder("Incorrect command usage, use /group to see a list of commands!").color(ChatColor.RED).create());
				}
				if ((args[0].toLowerCase().equals("list")) || (args[0].toLowerCase().equals("members"))) {
					if (MultiChat.groupchats.containsKey(args[1].toLowerCase()))
					{
						TGroupChatInfo partychatinfo = new TGroupChatInfo();
						ProxiedPlayer player = (ProxiedPlayer)sender;

						partychatinfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());
						if ((partychatinfo.existsMember(player.getUniqueId())) || (sender.hasPermission("multichat.staff.spy")))
						{
							List<UUID> memberlist = new ArrayList<UUID>();

							memberlist = partychatinfo.getMembers();

							sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&a&lShowing members of group: " + partychatinfo.getName().toUpperCase())).create());
							for (UUID member : memberlist) {
								if (!partychatinfo.existsAdmin(member)) {
									sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b- " + UUIDNameManager.getName(member))).create());
								} else {
									sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b- &b&o" + UUIDNameManager.getName(member))).create());
								}
							}
						}
						else
						{
							sender.sendMessage(new ComponentBuilder("Sorry you aren't a member of the group: " + args[1].toUpperCase()).color(ChatColor.RED).create());
						}
						partychatinfo = null;
					}
					else
					{
						sender.sendMessage(new ComponentBuilder("Sorry the following group chat does not exist: " + args[1].toUpperCase()).color(ChatColor.RED).create());
					}
				}
				if (args[0].toLowerCase().equals("spy"))
				{
					ProxiedPlayer player = (ProxiedPlayer)sender;
					if (args[1].toLowerCase().equals("all"))
					{
						if (player.hasPermission("multichat.staff.spy"))
						{
							if (MultiChat.allspy.contains(player.getUniqueId()))
							{
								MultiChat.allspy.remove(player.getUniqueId());
								sender.sendMessage(new ComponentBuilder("Global group spy disabled").color(ChatColor.RED).create());
								sender.sendMessage(new ComponentBuilder("Any groups you previously activated spy for will still be spied on!").color(ChatColor.RED).create());
								sender.sendMessage(new ComponentBuilder("Disable spy for individual groups with /group spy <groupname>").color(ChatColor.RED).create());
							}
							else
							{
								MultiChat.allspy.add(player.getUniqueId());
								sender.sendMessage(new ComponentBuilder("Global group spy enabled for all group chats!").color(ChatColor.GREEN).create());
							}
						}
						else {
							sender.sendMessage(new ComponentBuilder("Sorry this command does not exist, use /group").color(ChatColor.RED).create());
						}
					}
					else if (player.hasPermission("multichat.staff.spy"))
					{
						if (MultiChat.groupchats.containsKey(args[1].toLowerCase()))
						{
							TGroupChatInfo chatinfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());
							if (!chatinfo.existsMember(player.getUniqueId()))
							{
								if (chatinfo.existsViewer(player.getUniqueId()))
								{
									chatinfo.delViewer(player.getUniqueId());
									MultiChat.groupchats.remove(chatinfo.getName().toLowerCase());
									MultiChat.groupchats.put(chatinfo.getName().toLowerCase(), chatinfo);
									sender.sendMessage(new ComponentBuilder("You are no longer spying on: " + chatinfo.getName().toUpperCase()).color(ChatColor.RED).create());
								}
								else
								{
									chatinfo.addViewer(player.getUniqueId());
									MultiChat.groupchats.remove(chatinfo.getName().toLowerCase());
									MultiChat.groupchats.put(chatinfo.getName().toLowerCase(), chatinfo);
									sender.sendMessage(new ComponentBuilder("You are now spying on: " + chatinfo.getName().toUpperCase()).color(ChatColor.GREEN).create());
								}
							}
							else {
								sender.sendMessage(new ComponentBuilder("You are already a member of this chat so can't spy on it!").color(ChatColor.RED).create());
							}
							chatinfo = null;
						}
						else
						{
							sender.sendMessage(new ComponentBuilder("Sorry this group chat does not exist!").color(ChatColor.RED).create());
						}
					}
					else {
						sender.sendMessage(new ComponentBuilder("Sorry this command does not exist, use /group").color(ChatColor.RED).create());
					}
				}
				if (args[0].toLowerCase().equals("help")) {
					if (args[1].equals("1"))
					{
						GroupManager groupman = new GroupManager();
						groupman.displayHelp(1,sender);
						groupman = null;
					}
					else
					{
						GroupManager groupman = new GroupManager();
						groupman.displayHelp(2,sender);
						groupman = null;
					}
				}
				if ((args[0].toLowerCase().equals("create")) || (args[0].toLowerCase().equals("make"))) {
					ProxiedPlayer player = (ProxiedPlayer)sender;
					if (player.hasPermission("multichat.group.create")) {
						if (args[1].length() <= 20)
						{
							if (!MultiChat.groupchats.containsKey(args[1].toLowerCase()))
							{
								GroupManager groupman = new GroupManager();

								//Make the new group
								groupman.createGroup(args[1], player.getUniqueId(), false, "");
								//Select the new group for the player
								groupman.setViewedChat(player.getUniqueId(), args[1]);
								//Announce join to group members
								sender.sendMessage(new ComponentBuilder("You successfully created, joined, and selected the group: " + args[1].toUpperCase()).color(ChatColor.GREEN).create());

								groupman.announceJoinGroup(sender.getName(), args[1]);
								groupman = null;
							}
							else
							{
								sender.sendMessage(new ComponentBuilder("Sorry the following group chat already exists: " + args[1].toUpperCase()).color(ChatColor.RED).create());
							}
						}
						else {
							sender.sendMessage(new ComponentBuilder("Sorry group name cannot exceed 20 characters!").color(ChatColor.RED).create());
						}
					}
				} else {
					sender.sendMessage(new ComponentBuilder("Sorry you do not have permission to create new group chats").color(ChatColor.RED).create());
				}
				if (args[0].toLowerCase().equals("join")) {
					if (MultiChat.groupchats.containsKey(args[1].toLowerCase()))
					{
						GroupManager groupman = new GroupManager();
						ProxiedPlayer player = (ProxiedPlayer)sender;

						//Run the join group routine
						if (groupman.joinGroup(args[1], player, "") == true ){

							//If the join is successful
							//Set their viewed chat
							groupman.setViewedChat(player.getUniqueId(), args[1]);
							sender.sendMessage(new ComponentBuilder("You successfully joined and selected the group: " + args[1].toUpperCase()).color(ChatColor.GREEN).create());
							//Announce their join
							groupman.announceJoinGroup(player.getName(), args[1]);
						}   

					}
					else
					{
						sender.sendMessage(new ComponentBuilder("Sorry the following group chat does not exist: " + args[1].toUpperCase()).color(ChatColor.RED).create());
					}
				}
				if ((args[0].toLowerCase().equals("quit")) || (args[0].toLowerCase().equals("leave"))) {
					if (MultiChat.groupchats.containsKey(args[1].toLowerCase()))
					{
						/*TGroupChatInfo partychatinfo = new TGroupChatInfo();

            viewedchat = (String)MultiChat.viewedchats.get(player.getUniqueId());

            partychatinfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());
            if (partychatinfo.existsMember(player.getUniqueId()))
            {
              if ((!partychatinfo.existsAdmin(player.getUniqueId())) || (partychatinfo.getAdmins().size() > 1))
              {
                partychatinfo.delMember(player.getUniqueId());
                partychatinfo.delViewer(player.getUniqueId());
                if (partychatinfo.existsAdmin(player.getUniqueId())) {
                  partychatinfo.delAdmin(player.getUniqueId());
                }
                viewedchat = null;
                MultiChat.viewedchats.remove(player.getUniqueId());
                MultiChat.viewedchats.put(player.getUniqueId(), viewedchat);
                MultiChat.groupchats.remove(args[1].toLowerCase());
                MultiChat.groupchats.put(args[1].toLowerCase(), partychatinfo);

                sender.sendMessage(new ComponentBuilder("You successfully left the group: " + args[1].toUpperCase()).color(ChatColor.GREEN).create());
                GCCommand.chatMessage(sender.getName() + " has left the group chat!", "&lINFO", partychatinfo);
              }
              else if (!partychatinfo.getFormal())
              {
                sender.sendMessage(new ComponentBuilder("Sorry you cannot leave as you created the group!: " + args[1].toUpperCase()).color(ChatColor.RED).create());
                sender.sendMessage(new ComponentBuilder("Please transfer group ownership first! Use /group transfer " + args[1].toUpperCase() + " <playername>").color(ChatColor.RED).create());
              }
              else
              {
                sender.sendMessage(new ComponentBuilder("Sorry you cannot leave as you are the only group admin!: " + args[1].toUpperCase()).color(ChatColor.RED).create());
                sender.sendMessage(new ComponentBuilder("Please appoint a new admin using /group admin " + args[1].toUpperCase() + " <playername>").color(ChatColor.RED).create());
              }
            }
            else {
              sender.sendMessage(new ComponentBuilder("Sorry you aren't a member of the group: " + args[1].toUpperCase()).color(ChatColor.RED).create());
            }
            partychatinfo = null;*/

						GroupManager groupman = new GroupManager();

						ProxiedPlayer player = (ProxiedPlayer)sender;

						groupman.quitGroup(args[1].toLowerCase(), player.getUniqueId(), player);

					}
					else
					{
						sender.sendMessage(new ComponentBuilder("Sorry the following group chat does not exist: " + args[1].toUpperCase()).color(ChatColor.RED).create());
					}
				}
				if (args[0].toLowerCase().equals("formal")) {
					if (MultiChat.groupchats.containsKey(args[1].toLowerCase()))
					{
						TGroupChatInfo chatinfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());
						if (!chatinfo.getFormal())
						{
							ProxiedPlayer player = (ProxiedPlayer)sender;
							if (chatinfo.getAdmins().contains(player.getUniqueId()))
							{
								chatinfo.setFormal(true);
								MultiChat.groupchats.remove(chatinfo.getName());
								MultiChat.groupchats.put(chatinfo.getName(), chatinfo);
								GCCommand.chatMessage(sender.getName() + " has converted this group to a FORMAL group chat!", "&lINFO", chatinfo);
							}
							else
							{
								sender.sendMessage(new ComponentBuilder("Sorry this command can only be used by the group chat owner").color(ChatColor.RED).create());
							}
						}
						else
						{
							sender.sendMessage(new ComponentBuilder("Sorry this chat is already a formal group chat: " + args[1].toUpperCase()).color(ChatColor.RED).create());
						}
						chatinfo = null;
					}
					else
					{
						sender.sendMessage(new ComponentBuilder("Sorry the following group chat does not exist: " + args[1].toUpperCase()).color(ChatColor.RED).create());
					}
				}
				if (args[0].toLowerCase().equals("delete")) {
					if (MultiChat.groupchats.containsKey(args[1].toLowerCase()))
					{
						TGroupChatInfo chatinfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());

						ProxiedPlayer player = (ProxiedPlayer)sender;
						if (chatinfo.getAdmins().contains(player.getUniqueId()))
						{
							for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
								if ((MultiChat.viewedchats.get(onlineplayer.getUniqueId()) != null) && 
										(((String)MultiChat.viewedchats.get(onlineplayer.getUniqueId())).toLowerCase().equals(chatinfo.getName().toLowerCase())))
								{
									MultiChat.viewedchats.remove(onlineplayer.getUniqueId());
									MultiChat.viewedchats.put(onlineplayer.getUniqueId(), null);
								}
							}
							GCCommand.chatMessage(sender.getName() + " has deleted this group chat!", "&lINFO", chatinfo);
							GCCommand.chatMessage("Goodbye! If you want to see group chat commands do /group", "&lINFO", chatinfo);
							MultiChat.groupchats.remove(chatinfo.getName().toLowerCase());
							chatinfo = null;
						}
						else
						{
							sender.sendMessage(new ComponentBuilder("Sorry this command can only be used by group admins/owners").color(ChatColor.RED).create());
						}
					}
					else
					{
						sender.sendMessage(new ComponentBuilder("Sorry the following group chat does not exist: " + args[1].toUpperCase()).color(ChatColor.RED).create());
					}
				}
				break;
			case 3: 
				if ((!args[0].toLowerCase().equals("create")) && (!args[0].toLowerCase().equals("make")) && (!args[0].toLowerCase().equals("join")) && (!args[0].toLowerCase().equals("transfer")) && (!args[0].toLowerCase().equals("admin")) && (!args[0].toLowerCase().equals("addadmin")) && (!args[0].toLowerCase().equals("removeadmin")) && (!args[0].toLowerCase().equals("ban"))) {
					sender.sendMessage(new ComponentBuilder("Incorrect command usage, use /group to see a list of commands!").color(ChatColor.RED).create());
				}
				if ((args[0].toLowerCase().equals("create")) || (args[0].toLowerCase().equals("make"))) {
					ProxiedPlayer player = (ProxiedPlayer) sender;
					if (player.hasPermission("multichat.group.create")) {
						if ((args[1].length() <= 20) && (args[2].length() <= 20))
						{
							if (!MultiChat.groupchats.containsKey(args[1].toLowerCase()))
							{
								GroupManager groupman = new GroupManager();

								//Make the new group
								groupman.createGroup(args[1], player.getUniqueId(), true, args[2]);
								//Select the new group for the player
								groupman.setViewedChat(player.getUniqueId(), args[1]);
								//Announce join to group members
								groupman.announceJoinGroup(sender.getName(), args[1]);

								sender.sendMessage(new ComponentBuilder("You successfully created, joined, and selected the group: " + args[1].toUpperCase()).color(ChatColor.GREEN).create());
								groupman = null;
							}
							else
							{
								sender.sendMessage(new ComponentBuilder("Sorry the following group chat already exists: " + args[1].toUpperCase()).color(ChatColor.RED).create());
							}
						}
						else {
							sender.sendMessage(new ComponentBuilder("Sorry neither group name or password must exceed 20 characters").color(ChatColor.RED).create());
						}
					} else {
						sender.sendMessage(new ComponentBuilder("Sorry you do not have permission to create new group chats").color(ChatColor.RED).create());
					}
				}
				if (args[0].toLowerCase().equals("join")) {
					if (MultiChat.groupchats.containsKey(args[1].toLowerCase()))
					{
						GroupManager groupman = new GroupManager();
						ProxiedPlayer player = (ProxiedPlayer)sender;

						//Run the join group routine
						if (groupman.joinGroup(args[1], player, args[2]) == true ){

							//If the join is successful
							//Set their viewed chat
							groupman.setViewedChat(player.getUniqueId(), args[1]);
							sender.sendMessage(new ComponentBuilder("You successfully joined and selected the group: " + args[1].toUpperCase()).color(ChatColor.GREEN).create());
							//Announce their join
							groupman.announceJoinGroup(player.getName(), args[1]);
						}   
					}
					else
					{
						sender.sendMessage(new ComponentBuilder("Sorry the following group chat does not exist: " + args[1].toUpperCase()).color(ChatColor.RED).create());
					}
				}
				if (args[0].toLowerCase().equals("transfer"))
				{
					ProxiedPlayer player = (ProxiedPlayer)sender;
					if (MultiChat.groupchats.containsKey(args[1].toLowerCase()))
					{
						if (ProxyServer.getInstance().getPlayer(args[2].toLowerCase()) != null)
						{
							ProxiedPlayer newplayer = ProxyServer.getInstance().getPlayer(args[2].toLowerCase());

							TGroupChatInfo chatinfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());
							if (!chatinfo.getFormal())
							{
								if (chatinfo.existsAdmin(player.getUniqueId()))
								{
									if (chatinfo.existsMember(newplayer.getUniqueId()))
									{
										chatinfo.addAdmin(newplayer.getUniqueId());
										chatinfo.delAdmin(player.getUniqueId());
										MultiChat.groupchats.remove(chatinfo.getName());
										MultiChat.groupchats.put(chatinfo.getName(), chatinfo);
										GCCommand.chatMessage(sender.getName() + " has transferred ownership to " + newplayer.getName(), "&lINFO", chatinfo);
									}
									else
									{
										sender.sendMessage(new ComponentBuilder("This player is not already a member of the group!").color(ChatColor.RED).create());
									}
								}
								else {
									sender.sendMessage(new ComponentBuilder("Sorry you are not the owner of this chat!").color(ChatColor.RED).create());
								}
							}
							else {
								sender.sendMessage(new ComponentBuilder("This command can only be used on informal chats!").color(ChatColor.RED).create());
							}
							chatinfo = null;
						}
						else
						{
							sender.sendMessage(new ComponentBuilder("This player is not online!").color(ChatColor.RED).create());
						}
					}
					else {
						sender.sendMessage(new ComponentBuilder("Specified Group Does Not Exist!").color(ChatColor.RED).create());
					}
				}
				if ((args[0].toLowerCase().equals("admin")) || (args[0].toLowerCase().equals("addadmin")) || (args[0].toLowerCase().equals("removeadmin")))
				{
					ProxiedPlayer player = (ProxiedPlayer)sender;
					if (MultiChat.groupchats.containsKey(args[1].toLowerCase()))
					{
						if (ProxyServer.getInstance().getPlayer(args[2].toLowerCase()) != null)
						{
							ProxiedPlayer newplayer = ProxyServer.getInstance().getPlayer(args[2].toLowerCase());

							TGroupChatInfo chatinfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());
							if (chatinfo.getFormal() == true)
							{
								if (chatinfo.existsAdmin(player.getUniqueId()))
								{
									if (chatinfo.existsMember(newplayer.getUniqueId()))
									{
										if (!chatinfo.existsAdmin(newplayer.getUniqueId()))
										{
											chatinfo.addAdmin(newplayer.getUniqueId());
											MultiChat.groupchats.remove(chatinfo.getName());
											MultiChat.groupchats.put(chatinfo.getName(), chatinfo);
											GCCommand.chatMessage(sender.getName() + " has promoted the following member to group admin: " + newplayer.getName(), "&lINFO", chatinfo);
										}
										else if (newplayer.getUniqueId().equals(player.getUniqueId()))
										{
											if (chatinfo.getAdmins().size() > 1)
											{
												chatinfo.delAdmin(player.getUniqueId());
												MultiChat.groupchats.remove(chatinfo.getName());
												MultiChat.groupchats.put(chatinfo.getName(), chatinfo);
												GCCommand.chatMessage(sender.getName() + " has stepped down as a group admin", "&lINFO", chatinfo);
											}
											else
											{
												sender.sendMessage(new ComponentBuilder("You can't step down as a group admin because you are the only one!").color(ChatColor.RED).create());
											}
										}
										else
										{
											sender.sendMessage(new ComponentBuilder("You can't demote another group admin!").color(ChatColor.RED).create());
										}
									}
									else {
										sender.sendMessage(new ComponentBuilder("This player is not already a member of the group!").color(ChatColor.RED).create());
									}
								}
								else {
									sender.sendMessage(new ComponentBuilder("Sorry you are not an admin of this chat!").color(ChatColor.RED).create());
								}
							}
							else {
								sender.sendMessage(new ComponentBuilder("This command can only be used on formal chats!").color(ChatColor.RED).create());
							}
							chatinfo = null;
						}
						else
						{
							sender.sendMessage(new ComponentBuilder("This player is not online!").color(ChatColor.RED).create());
						}
					}
					else {
						sender.sendMessage(new ComponentBuilder("Specified Group Does Not Exist!").color(ChatColor.RED).create());
					}
				}
				if (args[0].toLowerCase().equals("ban"))
				{
					ProxiedPlayer player = (ProxiedPlayer)sender;
					if (MultiChat.groupchats.containsKey(args[1].toLowerCase()))
					{
						if (ProxyServer.getInstance().getPlayer(args[2].toLowerCase()) != null)
						{
							ProxiedPlayer newplayer = ProxyServer.getInstance().getPlayer(args[2].toLowerCase());

							TGroupChatInfo chatinfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());
							if (chatinfo.getFormal() == true)
							{
								if (chatinfo.existsAdmin(player.getUniqueId()))
								{
									if (!chatinfo.existsAdmin(newplayer.getUniqueId()))
									{
										if (!chatinfo.existsBanned(newplayer.getUniqueId()))
										{
											chatinfo.addBanned(newplayer.getUniqueId());
											if (chatinfo.existsMember(newplayer.getUniqueId()))
											{
												chatinfo.delMember(newplayer.getUniqueId());
												chatinfo.delViewer(newplayer.getUniqueId());

												MultiChat.viewedchats.remove(newplayer.getUniqueId());
												MultiChat.viewedchats.put(newplayer.getUniqueId(), null);

												GCCommand.chatMessage(sender.getName() + " kicked the following player from the group chat: " + newplayer.getName(), "&lINFO", chatinfo);
											}
											MultiChat.groupchats.remove(chatinfo.getName());
											MultiChat.groupchats.put(chatinfo.getName(), chatinfo);
											GCCommand.chatMessage(sender.getName() + " has banned the following player from the group chat: " + newplayer.getName(), "&lINFO", chatinfo);

											newplayer.sendMessage(new ComponentBuilder("You were banned from group: " + chatinfo.getName()).color(ChatColor.RED).create());
										}
										else
										{
											chatinfo.delBanned(newplayer.getUniqueId());
											MultiChat.groupchats.remove(chatinfo.getName());
											MultiChat.groupchats.put(chatinfo.getName(), chatinfo);
											GCCommand.chatMessage(sender.getName() + " has unbanned the following player from the group chat: " + newplayer.getName(), "&lINFO", chatinfo);

											newplayer.sendMessage(new ComponentBuilder("You were unbanned from group: " + chatinfo.getName()).color(ChatColor.RED).create());
										}
									}
									else {
										sender.sendMessage(new ComponentBuilder("You can't ban a group admin!").color(ChatColor.RED).create());
									}
								}
								else {
									sender.sendMessage(new ComponentBuilder("Sorry you are not an admin of this chat!").color(ChatColor.RED).create());
								}
							}
							else {
								sender.sendMessage(new ComponentBuilder("This command can only be used on formal chats!").color(ChatColor.RED).create());
							}
							chatinfo = null;
						}
						else
						{
							sender.sendMessage(new ComponentBuilder("This player is not online!").color(ChatColor.RED).create());
						}
					}
					else {
						sender.sendMessage(new ComponentBuilder("Specified Group Does Not Exist!").color(ChatColor.RED).create());
					}
				}
				break;
			case 4: 
				if ((args[0].toLowerCase().equals("color")) || (args[0].toLowerCase().equals("colour")))
				{
					if (MultiChat.groupchats.containsKey(args[1].toLowerCase()))
					{
						TGroupChatInfo chatinfo = new TGroupChatInfo();

						ProxiedPlayer player = (ProxiedPlayer)sender;

						chatinfo = (TGroupChatInfo)MultiChat.groupchats.get(args[1].toLowerCase());
						if (((chatinfo.existsMember(player.getUniqueId())) && (!chatinfo.getFormal())) || (chatinfo.existsAdmin(player.getUniqueId())))
						{
							args[2] = args[2].toLowerCase();
							args[3] = args[3].toLowerCase();
							if ((args[2].equals("a")) || (args[2].equals("b")) || (args[2].equals("c")) || (args[2].equals("d")) || (args[2].equals("e")) || (args[2].equals("f")) || (args[2].equals("0")) || (args[2].equals("1")) || (args[2].equals("2")) || (args[2].equals("3")) || (args[2].equals("4")) || (args[2].equals("5")) || (args[2].equals("6")) || (args[2].equals("7")) || (args[2].equals("8")) || (args[2].equals("9")))
							{
								if ((args[3].equals("a")) || (args[3].equals("b")) || (args[3].equals("c")) || (args[3].equals("d")) || (args[3].equals("e")) || (args[3].equals("f")) || (args[3].equals("0")) || (args[3].equals("1")) || (args[3].equals("2")) || (args[3].equals("3")) || (args[3].equals("4")) || (args[3].equals("5")) || (args[3].equals("6")) || (args[3].equals("7")) || (args[3].equals("8")) || (args[3].equals("9")))
								{
									MultiChat.groupchats.remove(chatinfo.getName());

									chatinfo.setChatColor(args[2].charAt(0));
									chatinfo.setNameColor(args[3].charAt(0));

									MultiChat.groupchats.put(chatinfo.getName(), chatinfo);

									GCCommand.chatMessage("Group Chat Colours Changed by " + sender.getName(), "&lINFO", chatinfo);
								}
								else
								{
									sender.sendMessage(new ComponentBuilder("Invalid color codes specified: Must be letters a-f or numbers 0-9").color(ChatColor.RED).create());
									sender.sendMessage(new ComponentBuilder("Usage: /group color/colour <group name> <chatcolorcode> <namecolorcode>").color(ChatColor.RED).create());
								}
							}
							else
							{
								sender.sendMessage(new ComponentBuilder("Invalid color codes specified: Must be letters a-f or numbers 0-9").color(ChatColor.RED).create());
								sender.sendMessage(new ComponentBuilder("Usage: /group color/colour <group name> <chatcolorcode> <namecolorcode>").color(ChatColor.RED).create());
							}
						}
						else
						{
							sender.sendMessage(new ComponentBuilder("Sorry you don't have permission to do this!").color(ChatColor.RED).create());
						}
						chatinfo = null;
					}
					else
					{
						sender.sendMessage(new ComponentBuilder("Sorry the specified group chat does not exist!").color(ChatColor.RED).create());
					}
				}
				else {
					sender.sendMessage(new ComponentBuilder("Incorrect command usage, use /group to see a list of commands!").color(ChatColor.RED).create());
				}
				break;
			}
		}
		else
		{
			sender.sendMessage(new ComponentBuilder("Only players can use group chats").color(ChatColor.RED).create());
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args)
	{

		Set<String> matches = new HashSet<>();
		if ( args.length == 1 )
		{
			String search = args[0].toLowerCase();
			List<String> SubCommands = new ArrayList<String>();
			SubCommands.add("create");
			SubCommands.add("join");
			SubCommands.add("leave");
			SubCommands.add("quit");
			SubCommands.add("color");
			SubCommands.add("colour");
			SubCommands.add("transfer");
			SubCommands.add("delete");
			SubCommands.add("list");
			SubCommands.add("members");
			SubCommands.add("formal");
			SubCommands.add("admin");
			SubCommands.add("ban");
			for ( String sub : SubCommands )
			{
				if ( sub.toLowerCase().startsWith( search ) )
				{
					matches.add( sub );
				}
			}
		}

		return matches;
	}

}

