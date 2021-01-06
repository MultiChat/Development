package xyz.olivermartin.multichat.bungee.commands;

import java.util.Optional;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.PrivateMessageManager;
import xyz.olivermartin.multichat.common.MessageType;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

/**
 * Reply Command
 * <p>Used to quickly reply to your last private message</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class ReplyCommand extends Command {

    public ReplyCommand() {
        super("mcr", "multichat.chat.msg", ProxyConfigs.ALIASES.getAliases("mcr"));
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_reply_usage");
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_reply_desc");
            return;
        }

        UUID consoleUID = new UUID(0L, 0L);
        UUID senderUID = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : consoleUID;
        ProxyDataStore proxyDataStore = MultiChatProxy.getInstance().getDataStore();

        if (!proxyDataStore.getLastMsg().containsKey(senderUID)) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_reply_no_one_to_reply_to");
            return;
        }

        UUID targetUID = proxyDataStore.getLastMsg().get(senderUID);
        String message = String.join(" ", args);

        if (targetUID.equals(consoleUID) && sender instanceof ProxiedPlayer) {
            PrivateMessageManager.getInstance().sendMessageConsoleTarget(message, (ProxiedPlayer) sender);
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(targetUID);
        if (target == null) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_reply_no_one_to_reply_to");
            return;
        }

        if (ProxyConfigs.CONFIG.isNoPmServer(target.getServer().getInfo().getName())) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_msg_disabled_target");
            return;
        }

        if (!(sender instanceof ProxiedPlayer)) {
            PrivateMessageManager.getInstance().sendMessageConsoleSender(message, target);
            return;
        }
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

        if (ProxyConfigs.CONFIG.isNoPmServer(proxiedPlayer.getServer().getInfo().getName())) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_msg_disabled_sender");
            return;
        }

        if (ChatControl.isMuted(proxiedPlayer.getUniqueId(), MessageType.PRIVATE_MESSAGES)) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "mute_cannot_send_message");
            return;
        }

        if (ChatControl.ignores(proxiedPlayer.getUniqueId(), target.getUniqueId(), MessageType.PRIVATE_MESSAGES)) {
            ChatControl.sendIgnoreNotifications(target, sender, "private_messages");
            return;
        }

        if (ChatControl.handleSpam(proxiedPlayer, message, MessageType.PRIVATE_MESSAGES))
            return;

        Optional<String> crm = ChatControl.applyChatRules(proxiedPlayer, message, MessageType.PRIVATE_MESSAGES);
        if (!crm.isPresent())
            return;

        PrivateMessageManager.getInstance().sendMessage(crm.get(), proxiedPlayer, target);
    }
}
