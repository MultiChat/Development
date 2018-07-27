package com.olivermartin410.plugins.commands;

import com.olivermartin410.plugins.ChatStream;
import com.olivermartin410.plugins.MultiChat;
import com.olivermartin410.plugins.UUIDNameManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

/**
 * MultiChat (Admin) Command
 * <p>Used to view details about the plugin and display help information</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class MultiChatCommand extends Command {

	private static String[] aliases = new String[] {};

	public MultiChatCommand() {
		super("multichat", "multichat.admin", aliases);
	}

	private void displayHelp(CommandSender sender, int page) {

		switch (page) {

		case 1:
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
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3&lType &b&l/multichat help <page number> &3&lto &3&lview &3&lmore &3&lcommands")).create());

		case 2:

			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&2&lMulti&a&lChat &b&lHelp [Page 2]")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3View all global chat (Enabled by default)")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/global")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Only view chat from your current server")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/local")).create());
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
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Clear &3the &3chat &3tream &3for &3yourself &3or &3a &3group &3of &3people")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/clearchat [self,server,global,all]")).create());

		default:

			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&2&lMulti&a&lChat &b&lHelp [Page 3]")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3View announcement commands")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/announcement")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3View bulletin commands")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/bulletin")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3View cast commands")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/cast")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Use a specified cast from the console")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/usecast <cast> <message>")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Alert staff members of a problem")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/helpme <message>")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Nickname a player &3(&3Only &3works &3if &3MultiChat &3installed &3on &3Spigot &3/ &3Sponge)")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/nick [player] <nickname/off>")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&3Get players real name from their nickname &3(&3Only &3works &3on &3Spigot)")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&b/realname <nickname>")).create());
		}

	}

	public void execute(CommandSender sender, String[] args) {

		if (args.length < 1) {

			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&2Multi&aChat &bVersion " + MultiChat.latestversion)).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bBy Revilo410")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bOriginally created for &3Oasis-MC.US")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bUse &3/multichat help &bfor all commands")).create());

		} else {

			if (args.length == 1) {

				if (args[0].toLowerCase().equals("help")) {

					displayHelp(sender, 1);

				} else if (args[0].toLowerCase().equals("save")) {

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

				} else if (args[0].toLowerCase().equals("reload")) {

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
					
					if (args[1].toLowerCase().equals("1")) {
						displayHelp(sender,1);
					} else if (args[1].toLowerCase().equals("2")) {
						displayHelp(sender,2);
					} else {
						displayHelp(sender,3);
					}
					
				}
			}
		}
	}
}
