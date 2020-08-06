package xyz.olivermartin.multichat.bungee.commands;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.PrivateMessageManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

/**
 * Reply Command
 * <p>Used to quickly reply to your last private message</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class ReplyCommand extends Command {

    public ReplyCommand() {
        super("mcr", "multichat.chat.msg", ConfigManager.getInstance().getHandler(ConfigFile.ALIASES).getConfig().getStringList("r").toArray(new String[0]));
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            MessageManager.sendMessage(sender, "command_reply_usage");
            MessageManager.sendMessage(sender, "command_reply_desc");
            return;
        }

        UUID consoleUID = new UUID(0L, 0L);
        UUID senderUID = sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : consoleUID;
        ProxyDataStore proxyDataStore = MultiChatProxy.getInstance().getDataStore();

        if (!proxyDataStore.getLastMsg().containsKey(senderUID)) {
            MessageManager.sendMessage(sender, "command_reply_no_one_to_reply_to");
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
            MessageManager.sendMessage(sender, "command_reply_no_one_to_reply_to");
            return;
        }

        List<String> noPmServers = ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig()
                .getStringList(ConfigValues.Config.NO_PM);

        if (noPmServers.contains(target.getServer().getInfo().getName())) {
            MessageManager.sendMessage(sender, "command_msg_disabled_target");
            return;
        }

        if (!(sender instanceof ProxiedPlayer)) {
            PrivateMessageManager.getInstance().sendMessageConsoleSender(message, target);
            return;
        }
        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;

        if (noPmServers.contains(proxiedPlayer.getServer().getInfo().getName())) {
            MessageManager.sendMessage(sender, "command_msg_disabled_sender");
            return;
        }

        if (ChatControl.isMuted(proxiedPlayer.getUniqueId(), "private_messages")) {
            MessageManager.sendMessage(sender, "mute_cannot_send_message");
            return;
        }

        if (ChatControl.ignores(proxiedPlayer.getUniqueId(), target.getUniqueId(), "private_messages")) {
            ChatControl.sendIgnoreNotifications(target, sender, "private_messages");
            return;
        }

        if (ChatControl.handleSpam(proxiedPlayer, message, "private_messages"))
            return;

        Optional<String> crm = ChatControl.applyChatRules(message, "private_messages", sender.getName());
        if (!crm.isPresent())
            return;

        PrivateMessageManager.getInstance().sendMessage(crm.get(), proxiedPlayer, target);
    }
}
