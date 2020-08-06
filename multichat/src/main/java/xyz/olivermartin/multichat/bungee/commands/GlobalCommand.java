package xyz.olivermartin.multichat.bungee.commands;

import java.util.Optional;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatModeManager;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyChatManager;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;

/**
 * Global Command
 * <p>Causes players to see messages sent from all servers in the global chat</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class GlobalCommand extends Command {

    public GlobalCommand() {
        super("mcglobal", "multichat.chat.mode", ConfigManager.getInstance().getHandler(ConfigFile.ALIASES).getConfig().getStringList("global").toArray(new String[0]));
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            MessageManager.sendMessage(sender, "command_global_only_players");
            return;
        }
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

        if (args.length == 0) {
            ChatModeManager.getInstance().setGlobal(proxiedPlayer.getUniqueId());

            MessageManager.sendMessage(sender, "command_global_enabled_1");
            MessageManager.sendMessage(sender, "command_global_enabled_2");
            return;
        }

        if (!ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getBoolean(ConfigValues.Config.GLOBAL)) {
            // TODO: Maybe add a message here?
            //  Don't think anyone will disable global chat and expect /global to work but you never know...
            return;
        }

        if (proxiedPlayer.getServer() != null
                && ConfigManager.getInstance().getHandler(ConfigFile.CONFIG)
                .getConfig().getStringList(ConfigValues.Config.NO_GLOBAL).contains(proxiedPlayer.getServer().getInfo().getName())) {
            // TODO: Same as above
            return;
        }

        if (ConfigManager.getInstance().getHandler(ConfigFile.CONFIG)
                .getConfig().getBoolean(ConfigValues.Config.FETCH_SPIGOT_DISPLAY_NAMES)) {
            ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(proxiedPlayer.getName(),
                    proxiedPlayer.getServer().getInfo()
            );
        }

        ProxyChatManager chatManager = MultiChatProxy.getInstance().getChatManager();
        Optional<String> optionalMessage = chatManager.handleChatMessage(proxiedPlayer, String.join(" ", args));
        if (!optionalMessage.isPresent())
            return;

        String message = optionalMessage.get();
        ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();

        // If they had this channel hidden, then unhide it...
        if (channelManager.isHidden(proxiedPlayer.getUniqueId(), "global")) {
            channelManager.show(proxiedPlayer.getUniqueId(), "global");
            MessageManager.sendSpecialMessage(proxiedPlayer, "command_channel_show", "GLOBAL");
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
                proxiedPlayer.getServer().getInfo(),
                // TODO: Move this permissions check somewhere else or make it simpler
                (proxiedPlayer.hasPermission("multichat.chat.color") || proxiedPlayer.hasPermission("multichat.chat.colour.simple") || proxiedPlayer.hasPermission("multichat.chat.color.simple")),
                (proxiedPlayer.hasPermission("multichat.chat.color") || proxiedPlayer.hasPermission("multichat.chat.colour.rgb") || proxiedPlayer.hasPermission("multichat.chat.color.rgb"))
        );

        // Send message directly to global chat...
        ProxyLocalCommunicationManager.sendPlayerDirectChatMessage("global",
                proxiedPlayer.getName(),
                message,
                proxiedPlayer.getServer().getInfo()
        );

        // TODO:  Move this to actual message distribution
        MultiChatProxy.getInstance().getDataStore().getHiddenStaff().remove(proxiedPlayer.getUniqueId());
    }
}
