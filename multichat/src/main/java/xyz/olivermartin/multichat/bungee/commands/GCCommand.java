package xyz.olivermartin.multichat.bungee.commands;

import com.olivermartin410.plugins.TGroupChatInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.*;
import xyz.olivermartin.multichat.common.MessageType;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyJsonUtils;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

import java.util.Optional;

/**
 * Group Chat Messaging Command
 * <p>Allows players to send a message direct to a group chat or toggle group chats</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class GCCommand extends Command {

    public GCCommand() {
        super("mcgc", "multichat.group", ProxyConfigs.ALIASES.getAliases("mcgc"));
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            ProxyConfigs.MESSAGES.sendMessage(sender, args.length == 0
                    ? "command_gc_only_players_toggle"
                    : "command_gc_only_players_speak"
            );
            return;
        }

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
        if (args.length == 0) {
            boolean toggleResult = Events.toggleGC(proxiedPlayer.getUniqueId());
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_gc_toggle_" + (toggleResult ? "on" : "off"));
            return;
        }

        ProxyDataStore proxyDataStore = MultiChatProxy.getInstance().getDataStore();
        String viewedChat = proxyDataStore.getViewedChats().get(proxiedPlayer.getUniqueId());
        if (viewedChat == null) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_gc_no_chat_selected");
            return;
        }

        TGroupChatInfo groupChatInfo = proxyDataStore.getGroupChats().get(viewedChat);
        if (groupChatInfo == null) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_gc_no_longer_exists");
            return;
        }

        String playerName = sender.getName();

        if (groupChatInfo.getFormal() && groupChatInfo.getAdmins().contains(proxiedPlayer.getUniqueId())) {
            playerName = "&o" + playerName;
        }

        sendMessage(String.join(" ", args), playerName, groupChatInfo);
    }

    public static void sendMessage(String originalMessage, String playerName, TGroupChatInfo groupInfo) {
        ProxyDataStore proxyDataStore = MultiChatProxy.getInstance().getDataStore();
        ChatManipulation manipulation = new ChatManipulation();

        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playerName);
        if (proxiedPlayer != null) {
            if (ChatControl.isMuted(proxiedPlayer.getUniqueId(), MessageType.GROUP_CHATS)) {
                ProxyConfigs.MESSAGES.sendMessage(proxiedPlayer, "mute_cannot_send_message");
                return;
            }

            if (ChatControl.handleSpam(proxiedPlayer, originalMessage, MessageType.GROUP_CHATS))
                return;
        }

        Optional<String> optionalChatRules;

        optionalChatRules = ChatControl.applyChatRules(proxiedPlayer, originalMessage, MessageType.GROUP_CHATS);

        if (!optionalChatRules.isPresent())
            return;
        originalMessage = optionalChatRules.get();

        String messageFormat = ProxyConfigs.CONFIG.getGroupChatFormat();
        String translatedMessage = MultiChatUtil.translateColorCodes(
                manipulation.replaceGroupChatVars(messageFormat, playerName, originalMessage, groupInfo.getName())
        );
        String translatedOriginalMessage = MultiChatUtil.translateColorCodes(originalMessage);

        BaseComponent[] modernMessage = ProxyJsonUtils.parseMessage(translatedMessage,
                "%MESSAGE%",
                translatedOriginalMessage
        );

        BaseComponent[] legacyMessage = ProxyJsonUtils.parseMessage(
                MultiChatUtil.approximateRGBColorCodes(translatedMessage),
                "%MESSAGE%",
                MultiChatUtil.approximateRGBColorCodes(translatedOriginalMessage)
        );

        ProxyServer.getInstance().getPlayers().stream()
                .filter(target -> target.getServer() != null
                        && (groupInfo.isViewer(target.getUniqueId()) && target.hasPermission("multichat.group"))
                        || proxyDataStore.getAllSpy().contains(target.getUniqueId())
                )
                .forEach(target -> {
                    if (proxiedPlayer != null
                            && ChatControl.ignores(proxiedPlayer.getUniqueId(), target.getUniqueId(), MessageType.GROUP_CHATS)) {
                        ChatControl.sendIgnoreNotifications(target, proxiedPlayer, "group_chats");
                        return;
                    }

                    // TODO: Move legacy check inside parsing at some point
                    if (ProxyConfigs.CONFIG.isLegacyServer(target.getServer().getInfo().getName())) {
                        target.sendMessage(legacyMessage);
                        return;
                    }
                    target.sendMessage(modernMessage);
                });

        StringBuilder consoleMessage = new StringBuilder();
        for (BaseComponent bc : legacyMessage)
            consoleMessage.append(bc.toLegacyText());
        ConsoleManager.logGroupChat(consoleMessage.toString());
    }
}
