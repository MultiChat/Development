package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatStream;
import xyz.olivermartin.multichat.bungee.CommandManager;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.bungee.UUIDNameManager;

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

			MessageManager.sendMessage(sender, "command_multichat_help_1");
			break;

		case 2:

			MessageManager.sendMessage(sender, "command_multichat_help_2");
			break;

		default:

			MessageManager.sendMessage(sender, "command_multichat_help_3");
			break;

		}

	}

	public void execute(CommandSender sender, String[] args) {

		if (args.length < 1) {

			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&2Multi&aChat &bVersion " + MultiChat.LATEST_VERSION)).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bBy Revilo410")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bOriginally created for &3Oasis-MC.US")).create());
			sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bUse &3/multichat help &bfor all commands")).create());

		} else {

			if (args.length == 1) {

				if (args[0].toLowerCase().equals("help")) {

					displayHelp(sender, 1);

				} else if (args[0].toLowerCase().equals("save")) {

					MessageManager.sendMessage(sender, "command_multichat_save_prepare");

					MultiChat.saveChatInfo();
					MultiChat.saveGroupChatInfo();
					MultiChat.saveGroupSpyInfo();
					MultiChat.saveGlobalChatInfo();
					MultiChat.saveSocialSpyInfo();
					MultiChat.saveAnnouncements();
					MultiChat.saveBulletins();
					MultiChat.saveCasts();

					UUIDNameManager.saveUUIDS();

					MessageManager.sendMessage(sender, "command_multichat_save_completed");

				} else if (args[0].toLowerCase().equals("reload")) {

					MessageManager.sendMessage(sender, "command_multichat_reload_prepare");

					ConfigManager.getInstance().getHandler("config.yml").startupConfig();
					MultiChat.configversion = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("version");
					//MultiChat.jmconfigman.startupConfig();
					ConfigManager.getInstance().getHandler("joinmessages.yml").startupConfig();
					ConfigManager.getInstance().getHandler("messages.yml").startupConfig();
					
					// Unregister, reload, and register commands
					// THIS IS NEW AND EXPERIMENTAL!
					MultiChat.getInstance().unregisterCommands(ConfigManager.getInstance().getHandler("config.yml").getConfig());
					CommandManager.reload();
					MultiChat.getInstance().registerCommands(ConfigManager.getInstance().getHandler("config.yml").getConfig());

					System.out.println("VERSION LOADED: " + MultiChat.configversion);

					MultiChat.globalChat = new ChatStream("GLOBAL", ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("globalformat"), false, false);
					for (String server : ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_global")) {
						MultiChat.globalChat.addServer(server);
					}

					MessageManager.sendMessage(sender, "command_multichat_reload_completed");
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
