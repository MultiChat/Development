package xyz.olivermartin.multichat.bungee.commands;

import java.util.Optional;
import java.util.UUID;

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
 * Local Chat Command
 * <p>Players can use this command to only see the chat sent from players on their current server</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class LocalCommand extends Command {

    public LocalCommand() {
        super("mclocal", "multichat.chat.mode", ProxyConfigs.ALIASES.getAliases("mclocal"));
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_local_only_players");
            return;
        }
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
        UUID playerUID = proxiedPlayer.getUniqueId();

        if (args.length == 0) {
            ChatModeManager.getInstance().setLocal(playerUID);

            ProxyConfigs.MESSAGES.sendMessage(sender, "command_local_enabled_1");
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_local_enabled_2");
            return;
        }

        ServerInfo serverInfo = proxiedPlayer.getServer().getInfo();
        if (ProxyConfigs.CONFIG.isFetchSpigotDisplayNames())
            ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(proxiedPlayer.getName(), serverInfo);

        ProxyChatManager chatManager = MultiChatProxy.getInstance().getChatManager();

        String message = String.join(" ", args);

        Optional<String> optionalMessage = chatManager.handleChatMessage(proxiedPlayer, message); // Processed message

        if (!optionalMessage.isPresent())
            return;

        message = optionalMessage.get();

        ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();

        // If they had this channel hidden, then unhide it...
        if (channelManager.isHidden(playerUID, "local")) {
            channelManager.show(playerUID, "local");
            ProxyConfigs.MESSAGES.sendMessage(proxiedPlayer, "command_channel_show", "LOCAL");
        }

        // Let server know players channel preference

        String currentChannel = channelManager.getChannel(proxiedPlayer);
        String channelFormat = currentChannel.equals("local")
                ? channelManager.getLocalChannel().getFormat()
                : channelManager.getProxyChannel(currentChannel).orElse(channelManager.getGlobalChannel()).getInfo()
                .getFormat();

        ProxyLocalCommunicationManager.sendPlayerDataMessage(proxiedPlayer.getName(),
                currentChannel,
                channelFormat,
                serverInfo,
                (proxiedPlayer.hasPermission("multichat.chat.color") || proxiedPlayer.hasPermission("multichat.chat.colour.simple") || proxiedPlayer.hasPermission("multichat.chat.color.simple")),
                (proxiedPlayer.hasPermission("multichat.chat.color") || proxiedPlayer.hasPermission("multichat.chat.colour.rgb") || proxiedPlayer.hasPermission("multichat.chat.color.rgb"))
        );

        // Message passes through to spigot here
        // Send message directly to local chat...
        ProxyLocalCommunicationManager.sendPlayerDirectChatMessage("local", sender.getName(), message, serverInfo);
        // TODO: Move this somewhere else in the future
        MultiChatProxy.getInstance().getDataStore().getHiddenStaff().remove(playerUID);
    }
}
