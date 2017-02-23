package com.olivermartin410.plugins;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class MultiChatCommand
extends Command
{
	public MultiChatCommand()
	{
		super("multichat", "multichat.admin", new String[0]);
	}

	public void execute(CommandSender sender, String[] args)
	{
		if (args.length < 1)
		{ 
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&2Multi&aChat &bVersion " + MultiChat.latestversion)).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bBy Revilo410")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bOriginally created for &3Oasis-MC.US")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bUse &3/multichat help &bfor all commands")).create());
		}
		else
		{
			if (args.length == 1) {
				if (args[0].toLowerCase().equals("help"))
				{
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&2&lMulti&a&lChat &b&lHelp")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Display plugin version info")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/multichat")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Reload the plugin config")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/multichat reload")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Save ALL plugin data")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/multichat save")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Display a message to all players")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/display <message>")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3View group chat help")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/group")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Send mod chat message &o(Send admin chat message)")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/mc <message> &o(/ac <message>)")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Change mod/&oadmin &3chat colours")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/mcc <chat colour code> <name colour code>")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b&o/acc <chat colour code> <name colour code>")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Toggle mod chat &o(Toggle admin chat)")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/mc &o(/ac)")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3&lType &b&l/multichat help 2 &3&lto view more commands")).create());
				}
				else if (args[0].toLowerCase().equals("save"))
				{
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Preparing to save multichat files!")).create());
					MultiChat.saveChatInfo();
					MultiChat.saveGroupChatInfo();
					MultiChat.saveGroupSpyInfo();
					MultiChat.saveGlobalChatInfo();
					MultiChat.saveSocialSpyInfo();
					MultiChat.saveAnnouncements();
					MultiChat.saveBulletins();
					MultiChat.saveCasts();
					UUIDNameManager.saveUUIDS();
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bSave completed!")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3If any errors occured they can be viewed in the console log!")).create());
				}
				else if (args[0].toLowerCase().equals("reload"))
				{
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Preparing to reload config!")).create());
					MultiChat.configman.startupConfig();
					MultiChat.configversion = MultiChat.configman.config.getString("version");
					MultiChat.jmconfigman.startupConfig();
					System.out.println("VERSION LOADED: " + MultiChat.configversion);
					MultiChat.globalChat = new ChatStream("GLOBAL", MultiChat.configman.config.getString("globalformat"), false, false);
					for (String server : MultiChat.configman.config.getStringList("no_global")) {
						MultiChat.globalChat.addServer(server);
					}
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bReload completed!")).create());
					sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3If any errors occured they can be viewed in the console log!")).create());
				}
			}
			if (args.length == 2) {
				if (args[0].toLowerCase().equals("help")) {
					if (args[1].toLowerCase().equals("1"))
					{
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&2&lMulti&a&lChat &b&lHelp")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Display plugin version info")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/multichat")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Reload the plugin config")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/multichat reload")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Save ALL plugin data")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/multichat save")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Display a message to all players")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/display <message>")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3View group chat help")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/group")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Send mod chat message &o(Send admin chat message)")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/mc <message> &o(/ac <message>)")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Change mod/&oadmin &3chat colours")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/mcc <chat colour code> <name colour code>")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b&o/acc <chat colour code> <name colour code>")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Toggle mod chat &o(Toggle admin chat)")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/mc &o(/ac)")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3&lType &b&l/multichat help 2 &3&lto view more commands")).create());
					}
					else
					{
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&2&lMulti&a&lChat &b&lHelp [Page 2]")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3View all global chat (Enabled by default)")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/global")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Only view chat from your current server")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/local (Note, people on other servers still see your chat)")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3See a list of online staff members")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/staff")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3See a list of all group chats")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/groups")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Send a player a private message")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/msg <player> [message]")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Reply to your last message")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/r <message>")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Toggle socialspy to view private messages")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/socialspy")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Freeze the chat to stop messages being sent")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/freezechat")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Clear the chat stream for yourself or a group of people")).create());
						sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/clearchat [self,server,global,all]")).create());
					}
				}
			}
		}
	}
}

