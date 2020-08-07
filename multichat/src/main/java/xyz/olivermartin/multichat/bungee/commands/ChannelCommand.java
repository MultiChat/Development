package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatModeManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;
import xyz.olivermartin.multichat.proxy.common.channels.proxy.ProxyChannel;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

import java.util.Optional;
import java.util.UUID;

/**
 * Chat Channel Command
 * <p>Players can use this command to switch channels, as well as show and hide specific channels</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class ChannelCommand extends Command {

    public ChannelCommand() {
        super("mcchannel", "multichat.chat.channel", ProxyConfigs.ALIASES.getAliases("mcchannel"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            MessageManager.sendMessage(sender, "command_channel_only_players");
            return;
        }

        if (args.length < 2) {
            showCommandUsage(sender);
            return;
        }

        ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();
        String operand = args[1].toLowerCase();

        // TODO: This check is horrible...
        //  Future implementation of channelManager.exists?
        //  Or implement a Channel interface that both Proxy and Local extend
        if (!channelManager.existsProxyChannel(operand) && !operand.equals("local")) {
            MessageManager.sendMessage(sender, "command_channel_does_not_exist");
            return;
        }

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
        UUID proxiedPlayerUID = proxiedPlayer.getUniqueId();
        Optional<ProxyChannel> optionalProxyChannel = channelManager.getProxyChannel(operand);

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "switch": {
                if (!sender.hasPermission("multichat.chat.channel.switch")) {
                    MessageManager.sendMessage(sender, "command_channel_switch_no_permission");
                    return;
                }

                if (optionalProxyChannel.isPresent()) {
                    ProxyChannel proxyChannel = optionalProxyChannel.get();

                    if (!proxyChannel.getInfo().hasSpeakPermission(sender)) {
                        MessageManager.sendMessage(sender, "command_channel_switch_no_permission");
                        return;
                    }
                }

                ChatModeManager.getInstance().setGlobal(proxiedPlayerUID);
                channelManager.select(proxiedPlayerUID, operand);
                MessageManager.sendSpecialMessage(sender, "command_channel_switch", operand.toUpperCase());
                break;
            }
            case "hide": {
                if (!sender.hasPermission("multichat.chat.channel.hide")) {
                    MessageManager.sendMessage(sender, "command_channel_hide_no_permission");
                    return;
                }

                if (channelManager.getChannel(proxiedPlayer).equalsIgnoreCase(operand)) {
                    MessageManager.sendMessage(sender, "command_channel_cannot_hide");
                    return;
                }

                if (channelManager.isHidden(proxiedPlayerUID, operand)) {
                    MessageManager.sendSpecialMessage(sender, "command_channel_already_hide", operand.toUpperCase());
                    return;
                }

                channelManager.hide(proxiedPlayerUID, operand);
                MessageManager.sendSpecialMessage(sender, "command_channel_hide", operand.toUpperCase());
                break;
            }
            case "show": {
                if (!sender.hasPermission("multichat.chat.channel.show")) {
                    MessageManager.sendMessage(sender, "command_channel_show_no_permission");
                    return;
                }

                if (!channelManager.isHidden(proxiedPlayerUID, operand)) {
                    MessageManager.sendSpecialMessage(sender, "command_channel_already_show", operand.toUpperCase());
                    return;
                }

                channelManager.show(proxiedPlayerUID, operand);
                MessageManager.sendSpecialMessage(sender, "command_channel_show", operand.toUpperCase());
                break;
            }
            default: {
                showCommandUsage(sender);
                return;
            }
        }

        String channelFormat = optionalProxyChannel.isPresent()
                ? optionalProxyChannel.get().getInfo().getFormat()
                : channelManager.getLocalChannel().getFormat();

        // Update channel info
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            ProxyLocalCommunicationManager.sendPlayerDataMessage(p.getName(),
                    channelManager.getChannel(p),
                    channelFormat,
                    p.getServer().getInfo(),
                    // TODO: Change this permission check mess please for the love of god (Move inside sendPlayerDataMessage)
                    (p.hasPermission("multichat.chat.colour") || p.hasPermission("multichat.chat.color") || p.hasPermission("multichat.chat.colour.simple") || p.hasPermission("multichat.chat.color.simple")),
                    (p.hasPermission("multichat.chat.colour") || p.hasPermission("multichat.chat.color") || p.hasPermission("multichat.chat.colour.rgb") || p.hasPermission("multichat.chat.color.rgb"))
            );
        }

    }

    private void showCommandUsage(CommandSender sender) {
        MessageManager.sendMessage(sender, "command_channel_help");
    }
}
