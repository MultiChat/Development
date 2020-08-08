package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.*;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;
import xyz.olivermartin.multichat.proxy.common.channels.local.LocalChannel;
import xyz.olivermartin.multichat.proxy.common.channels.proxy.GlobalStaticProxyChannel;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;
import xyz.olivermartin.multichat.proxy.common.contexts.GlobalContext;

import java.util.Arrays;

/**
 * MultiChat (Admin) Command
 * <p>Used to view details about the plugin and display help information</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class MultiChatCommand extends Command {

    public MultiChatCommand() {
        super("multichat", "multichat.admin", ProxyConfigs.ALIASES.getAliases("multichat"));
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

                ProxyConfigs.ALL.forEach(abstractProxyConfig -> abstractProxyConfig.reloadConfig(multiChat));
                ProxyConfigs.RAW_CONFIGS.forEach(abstractProxyConfig -> abstractProxyConfig.reloadConfig(multiChat));

                // Reload, and re-register commands
                CommandManager.reload();
                multiChat.registerCommands();

                ChatControl.reload();

                multiChat.getLogger().info("VERSION LOADED: " + ProxyConfigs.CONFIG.getVersion());

                // Set default channel
                String defaultChannel = ProxyConfigs.CONFIG.getDefaultChannel();
                boolean forceChannelOnJoin = ProxyConfigs.CONFIG.isForceChannelOnJoin();

                // New context manager and channels
                GlobalContext globalContext = new GlobalContext(defaultChannel, forceChannelOnJoin, true);
                MultiChatProxy.getInstance().getContextManager().setGlobalContext(globalContext);

                // TODO: [ConfigRefactor] Potential change
                ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();
                channelManager.setGlobalChannel(
                        new GlobalStaticProxyChannel("Global Channel",
                                ProxyConfigs.CONFIG.getGlobalFormat(),
                                Arrays.asList(ProxyConfigs.ALIASES.getAliases("global")),
                                channelManager
                        )
                );
                channelManager.setLocalChannel(
                        new LocalChannel("Local Channel",
                                ProxyConfigs.CONFIG.getGlobalFormat(),
                                Arrays.asList(ProxyConfigs.ALIASES.getAliases("local")),
                                channelManager
                        )
                );

                MessageManager.sendMessage(sender, "command_multichat_reload_completed");
                break;
            }
        }
    }
}
