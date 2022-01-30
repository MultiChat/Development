package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.olivermartin.multichat.velocity.*;

/**
 * MultiChat (Admin) Command
 * <p>Used to view details about the plugin and display help information</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class MultiChatCommand extends Command {

    private static final String[] aliases = new String[]{};

    public MultiChatCommand() {
        super("multichat", aliases);
    }

    private void displayHelp(CommandSource sender, int page) {

        switch (page) {
            case 1: MessageManager.sendMessage(sender, "command_multichat_help_1");
            case 2: MessageManager.sendMessage(sender, "command_multichat_help_2");
            default: MessageManager.sendMessage(sender, "command_multichat_help_3");
        }

    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.admin");
    }

    public void execute(Invocation invocation) {
        var args = invocation.arguments();
        var sender = invocation.source();

        if (args.length < 1) {

            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&2Multi&aChat &bVersion " + MultiChat.LATEST_VERSION));
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&bBy Revilo410"));
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&bOriginally created for &3Oasis-MC.US"));
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&bUse &3/multichat help &bfor all commands"));

        } else {

            if (args.length == 1) {

                if (args[0].equalsIgnoreCase("help")) {

                    displayHelp(sender, 1);

                } else if (args[0].equalsIgnoreCase("debug")) {

                    DebugManager.toggle();
                    DebugManager.log("Debug mode toggled");

                } else if (args[0].equalsIgnoreCase("save")) {

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

                } else if (args[0].equalsIgnoreCase("reload")) {

                    MessageManager.sendMessage(sender, "command_multichat_reload_prepare");

                    // Unregister commands
                    MultiChat.getInstance().unregisterCommands(ConfigManager.getInstance().getHandler("config.yml").getConfig(), ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig());

                    ConfigManager.getInstance().getHandler("config.yml").startupConfig();
                    MultiChat.configversion = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("version").getString();

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
                    if (ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().getChildrenMap().containsKey("link_control")) {
                        ChatControl.controlLinks = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().getNode("link_control").getBoolean();
                        ChatControl.linkMessage = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().getNode("link_removal_message").getString();
                        if (ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().getChildrenMap().containsKey("link_regex")) {
                            ChatControl.linkRegex = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig().getNode("link_regex").getString();
                        }
                    }

                    if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getChildrenMap().containsKey("privacy_settings")) {
                        MultiChat.logPMs = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("privacy_settings").getNode("log_pms").getBoolean();
                        MultiChat.logStaffChat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("privacy_settings").getNode("log_staffchat").getBoolean();
                        MultiChat.logGroupChat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("privacy_settings").getNode("log_groupchat").getBoolean();
                    }

                    // Legacy servers for RGB approximation
                    if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getChildrenMap().containsKey("legacy_servers")) {
                        MultiChat.legacyServers = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("legacy_servers").getList(String::valueOf);
                    }

                    // Set default channel
                    MultiChat.defaultChannel = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("default_channel").getString();
                    MultiChat.forceChannelOnJoin = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("force_channel_on_join").getBoolean();

                    Channel.getGlobalChannel().setFormat(ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("globalformat").getString());
                    Channel.getGlobalChannel().clearServers();

                    for (String server : ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("no_global").getList(String::valueOf)) {
                        Channel.getGlobalChannel().addServer(server);
                    }

                    MessageManager.sendMessage(sender, "command_multichat_reload_completed");
                }
            }

            if (args.length == 2) {

                if (args[0].equalsIgnoreCase("help")) {

                    if (args[1].equalsIgnoreCase("1")) {
                        displayHelp(sender, 1);
                    } else if (args[1].equalsIgnoreCase("2")) {
                        displayHelp(sender, 2);
                    } else {
                        displayHelp(sender, 3);
                    }

                }
            }
        }
    }
}
