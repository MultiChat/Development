package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.Channel;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.CommandManager;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.DebugManager;
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

				} else if (args[0].toLowerCase().equals("debug")) {

					DebugManager.toggle();
					DebugManager.log("Debug mode toggled");

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
					MultiChat.saveMute();
					MultiChat.saveIgnore();
					UUIDNameManager.saveUUIDS();

					MessageManager.sendMessage(sender, "command_multichat_save_completed");

				} else if (args[0].toLowerCase().equals("reload")) {

					MessageManager.sendMessage(sender, "command_multichat_reload_prepare");

					// Unregister commands
					MultiChat.getInstance().unregisterCommands(ConfigManager.getInstance().getHandler("config.yml").getConfig(), ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig());

					ConfigManager.getInstance().getHandler("config.yml").startupConfig();
					MultiChat.configversion = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("version");

					ConfigManager.getInstance().getHandler("joinmessages.yml").startupConfig();
					ConfigManager.getInstance().getHandler("messages.yml").startupConfig();
					ConfigManager.getInstance().getHandler("chatcontrol.yml").startupConfig();

					ConfigManager.getInstance().getHandler("messages_fr.yml").startupConfig();
					ConfigManager.getInstance().getHandler("joinmessages_fr.yml").startupConfig();

					// Reload, and re-register commands
					CommandManager.reload();
					MultiChat.getInstance().registerCommands(ConfigManager.getInstance().getHandler("config.yml").getConfig(), ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig());

					ChatControl.reload();

					System.out.println("VERSION LOADED: " + MultiChat.configversion);
					
					// Set up chat control stuff
					if (ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().contains("link_control")) {
						ChatControl.controlLinks = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().getBoolean("link_control");
						ChatControl.linkMessage = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().getString("link_removal_message");
					}

					// Set default channel
					MultiChat.defaultChannel = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("default_channel");
					MultiChat.forceChannelOnJoin = ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean("force_channel_on_join");

					Channel.getGlobalChannel().setFormat(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("globalformat"));
					Channel.getGlobalChannel().clearServers();

					for (String server : ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_global")) {
						Channel.getGlobalChannel().addServer(server);
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
