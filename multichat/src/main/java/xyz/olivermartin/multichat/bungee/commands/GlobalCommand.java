package xyz.olivermartin.multichat.bungee.commands;

import java.util.Optional;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatModeManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyChatManager;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

/**
 * Global Command
 * <p>Causes players to see messages sent from all servers in the global chat</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class GlobalCommand extends Command {

    public GlobalCommand() {
        super("mcglobal", "multichat.chat.mode", ProxyConfigs.ALIASES.getAliases("mcglobal"));
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_global_only_players");
            return;
        }
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

        if (args.length == 0) {
            ChatModeManager.getInstance().setGlobal(proxiedPlayer.getUniqueId());

            ProxyConfigs.MESSAGES.sendMessage(sender, "command_global_enabled_1");
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_global_enabled_2");
            return;
        }

        if (!ProxyConfigs.CONFIG.isGlobal()) {
            // TODO: Maybe add a message here?
            //  Don't think anyone will disable global chat and expect /global to work but you never know...
            return;
        }

        ServerInfo serverInfo = proxiedPlayer.getServer().getInfo();
        if (!ProxyConfigs.CONFIG.isGlobalServer(serverInfo.getName())) {
            // TODO: Same as above
            return;
        }

        if (ProxyConfigs.CONFIG.isFetchSpigotDisplayNames())
            ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(proxiedPlayer.getName(), serverInfo);

        ProxyChatManager chatManager = MultiChatProxy.getInstance().getChatManager();
        Optional<String> optionalMessage = chatManager.handleChatMessage(proxiedPlayer, String.join(" ", args));
        if (!optionalMessage.isPresent())
            return;

        String message = optionalMessage.get();
        ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();

        // If they had this channel hidden, then unhide it...
        if (channelManager.isHidden(proxiedPlayer.getUniqueId(), "global")) {
            channelManager.show(proxiedPlayer.getUniqueId(), "global");
            ProxyConfigs.MESSAGES.sendMessage(proxiedPlayer, "command_channel_show", "GLOBAL");
        }

        // Let server know players channel preference
        String currentChannel = channelManager.getChannel(proxiedPlayer);
        String channelFormat = currentChannel.equals("local")
                ? channelManager.getLocalChannel().getFormat()
                : channelManager.getProxyChannel(currentChannel)
                .orElse(channelManager.getGlobalChannel()).getInfo().getFormat();

        ProxyLocalCommunicationManager.sendPlayerDataMessage(proxiedPlayer.getName(),
                currentChannel,
                channelFormat,
                serverInfo,
                // TODO: Move this permissions check somewhere else or make it simpler
                (proxiedPlayer.hasPermission("multichat.chat.color") || proxiedPlayer.hasPermission("multichat.chat.colour.simple") || proxiedPlayer.hasPermission("multichat.chat.color.simple")),
                (proxiedPlayer.hasPermission("multichat.chat.color") || proxiedPlayer.hasPermission("multichat.chat.colour.rgb") || proxiedPlayer.hasPermission("multichat.chat.color.rgb"))
        );

        // Send message directly to global chat...
        ProxyLocalCommunicationManager.sendPlayerDirectChatMessage("global",
                proxiedPlayer.getName(),
                message,
                serverInfo
        );

        // TODO:  Move this to actual message distribution
        MultiChatProxy.getInstance().getDataStore().getHiddenStaff().remove(proxiedPlayer.getUniqueId());
    }
}
