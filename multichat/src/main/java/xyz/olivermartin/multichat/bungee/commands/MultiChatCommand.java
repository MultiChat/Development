package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
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
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;

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

					MultiChatProxy.getInstance().getFileStoreManager().save();

					//MultiChat.saveChatInfo();
					MultiChat.saveGroupChatInfo();
					MultiChat.saveGroupSpyInfo();
					MultiChat.saveGlobalChatInfo();
					MultiChat.saveSocialSpyInfo();
					//TODO Legacy MultiChat.saveAnnouncements();
					//MultiChat.saveBulletins();
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
					ConfigManager.getInstance().getHandler("config_fr.yml").startupConfig();
					ConfigManager.getInstance().getHandler("chatcontrol_fr.yml").startupConfig();

					// Reload, and re-register commands
					CommandManager.reload();
					MultiChat.getInstance().registerCommands(ConfigManager.getInstance().getHandler("config.yml").getConfig(), ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig());

					ChatControl.reload();

					System.out.println("VERSION LOADED: " + MultiChat.configversion);

					// Set up chat control stuff
					if (ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().contains("link_control")) {
						ChatControl.controlLinks = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().getBoolean("link_control");
						ChatControl.linkMessage = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().getString("link_removal_message");
						if (ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().contains("link_regex")) {
							ChatControl.linkRegex = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().getString("link_regex");
						}
					}

					if (ConfigManager.getInstance().getHandler("config.yml").getConfig().contains("privacy_settings")) {
						MultiChat.logPMs = ConfigManager.getInstance().getHandler("config.yml").getConfig().getSection("privacy_settings").getBoolean("log_pms");
						MultiChat.logStaffChat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getSection("privacy_settings").getBoolean("log_staffchat");
						MultiChat.logGroupChat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getSection("privacy_settings").getBoolean("log_groupchat");
					}

					// Legacy servers for RGB approximation
					if (ConfigManager.getInstance().getHandler("config.yml").getConfig().contains("legacy_servers")) {
						MultiChat.legacyServers = ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("legacy_servers");
					}

					// Set default channel
					MultiChat.defaultChannel = ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("default_channel");
					MultiChat.forceChannelOnJoin = ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean("force_channel_on_join");

					Channel.getGlobalChannel().setFormat(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("globalformat"));
					Channel.getGlobalChannel().clearServers();

					for (String server : ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_global")) {
						Channel.getGlobalChannel().addServer(server);
					}

					if (ProxyServer.getInstance().getPluginManager().getPlugin("PremiumVanish") != null) {
						MultiChat.premiumVanish = true;

						if (ConfigManager.getInstance().getHandler("config.yml").getConfig().contains("premium_vanish")) {
							MultiChat.hideVanishedStaffInMsg = ConfigManager.getInstance().getHandler("config.yml").getConfig().getSection("premium_vanish").getBoolean("prevent_message");
							MultiChat.hideVanishedStaffInStaffList = ConfigManager.getInstance().getHandler("config.yml").getConfig().getSection("premium_vanish").getBoolean("prevent_staff_list");
							MultiChat.hideVanishedStaffInJoin = ConfigManager.getInstance().getHandler("config.yml").getConfig().getSection("premium_vanish").getBoolean("silence_join");
						}

					} else {
						MultiChat.premiumVanish = false;
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
