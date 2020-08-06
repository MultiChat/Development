package xyz.olivermartin.multichat.bungee.commands;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import xyz.olivermartin.multichat.bungee.*;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;
import xyz.olivermartin.multichat.proxy.common.channels.local.LocalChannel;
import xyz.olivermartin.multichat.proxy.common.channels.proxy.GlobalStaticProxyChannel;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.contexts.GlobalContext;

/**
 * MultiChat (Admin) Command
 * <p>Used to view details about the plugin and display help information</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class MultiChatCommand extends Command {

    public MultiChatCommand() {
        super("multichat", "multichat.admin", ConfigManager.getInstance().getHandler(ConfigFile.ALIASES).getConfig().getStringList("multichat").toArray(new String[0]));
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&2Multi&aChat &bVersion " + MultiChat.LATEST_VERSION)).create());
            sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bBy Revilo410")).create());
            sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bOriginally created for &3Oasis-MC.US")).create());
            sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "&bUse &3/multichat help &bfor all commands")).create());
            return;
        }

        String subCommand = args[0].toLowerCase();
        String subArgument = args.length > 1 ? args[1].toLowerCase() : "";

        switch (subCommand) {
            case "help": {
                int page = 1;
                try {
                    page = Integer.parseInt(subArgument);
                } catch (NumberFormatException ignored) {
                }

                MessageManager.sendMessage(sender, "command_multichat_help_" + Math.max(1, Math.min(page, 3)));
                break;
            }
            case "debug": {
                DebugManager.toggle();
                DebugManager.log("Debug mode toggled");
                break;
            }
            case "save": {
                MessageManager.sendMessage(sender, "command_multichat_save_prepare");
                MultiChatProxy.getInstance().getFileStoreManager().save();
                MessageManager.sendMessage(sender, "command_multichat_save_completed");
                break;
            }
            case "reload": {
                MessageManager.sendMessage(sender, "command_multichat_reload_prepare");

                // TODO: This REALLY needs to change
                MultiChat multiChat = (MultiChat) MultiChatProxy.getInstance().getPlugin();

                multiChat.unregisterCommands();

                ConfigManager configManager = ConfigManager.getInstance();
                for (ConfigFile configFile : ConfigFile.values())
                    configManager.getHandler(configFile).startupConfig();

                Configuration config = ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig();
                MultiChat.configversion = config.getString("version");

                Configuration chatControl = ConfigManager.getInstance().getHandler(ConfigFile.CHAT_CONTROL).getConfig();

                // TODO: Should change this when we update config handling too
                ConfigManager.getInstance().getRawHandler("messages_fr.yml").startupConfig();
                ConfigManager.getInstance().getRawHandler("joinmessages_fr.yml").startupConfig();
                ConfigManager.getInstance().getRawHandler("config_fr.yml").startupConfig();
                ConfigManager.getInstance().getRawHandler("chatcontrol_fr.yml").startupConfig();
                ConfigManager.getInstance().getRawHandler("aliases_fr.yml").startupConfig();

                // Reload, and re-register commands
                CommandManager.reload();
                multiChat.registerCommands(config, chatControl);

                ChatControl.reload();

                // TODO: Change to appropriate logger
                System.out.println("VERSION LOADED: " + MultiChat.configversion);

                // Set up chat control stuff
                ChatControl.controlLinks = chatControl.getBoolean("link_control", false);
                ChatControl.linkMessage = chatControl.getString("link_removal_message", "[LINK REMOVED]");
                String linkRegex = chatControl.getString("link_regex");
                if (linkRegex != null && !linkRegex.isEmpty())
                    ChatControl.linkRegex = linkRegex;

                Configuration privacySettings = config.getSection("privacy_settings");
                if (privacySettings != null) {
                    MultiChat.logPMs = privacySettings.getBoolean("log_pms");
                    MultiChat.logStaffChat = privacySettings.getBoolean("log_staffchat");
                    MultiChat.logGroupChat = privacySettings.getBoolean("log_groupchat");
                }

                // Legacy servers for RGB approximation
                MultiChat.legacyServers = config.getStringList("legacy_servers");

                // Set default channel
                String defaultChannel = config.getString("default_channel");
                boolean forceChannelOnJoin = config.getBoolean("force_channel_on_join");
                List<String> noGlobalServers = new ArrayList<>(config.getStringList("no_global"));

                // New context manager and channels
                GlobalContext globalContext = new GlobalContext(defaultChannel, forceChannelOnJoin, true, noGlobalServers);
                MultiChatProxy.getInstance().getContextManager().setGlobalContext(globalContext);

                Configuration aliases = configManager.getHandler(ConfigFile.ALIASES).getConfig();

                ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();
                channelManager.setGlobalChannel(
                        new GlobalStaticProxyChannel("Global Channel",
                                config.getString("globalformat"),
                                aliases.getStringList("global"),
                                channelManager
                        )
                );
                channelManager.setLocalChannel(
                        new LocalChannel("Local Channel",
                                config.getString("globalformat"),
                                aliases.getStringList("local"),
                                channelManager
                        )
                );

                // No need to check if the plugin exists again, you can't live load vanish anyways
                if (MultiChat.premiumVanish) {
                    Configuration premiumVanish = config.getSection("premium_vanish");
                    if (premiumVanish != null) {
                        MultiChat.hideVanishedStaffInMsg = premiumVanish.getBoolean("prevent_message");
                        MultiChat.hideVanishedStaffInStaffList = premiumVanish.getBoolean("prevent_staff_list");
                        MultiChat.hideVanishedStaffInJoin = premiumVanish.getBoolean("silence_join");
                    }
                }

                MessageManager.sendMessage(sender, "command_multichat_reload_completed");
                break;
            }
        }
    }
}
