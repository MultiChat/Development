package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.proxy.Player;
import xyz.olivermartin.multichat.velocity.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Reply Command
 * <p>Used to quickly reply to your last private message</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class ReplyCommand extends Command {

    public ReplyCommand() {
        super("r", ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("rcommand").getList(String::valueOf).toArray(new String[0]));
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.chat.msg");
    }

    public void execute(Invocation invocation) {
        var args = invocation.arguments();
        var sender = invocation.source();

        if (args.length < 1) {

            MessageManager.sendMessage(sender, "command_reply_usage");
            MessageManager.sendMessage(sender, "command_reply_desc");

        } else if ((sender instanceof Player)) {

            String message = MultiChatUtil.getMessageFromArgs(args);

            Optional<String> crm;

            if (ChatControl.isMuted(((Player) sender).getUniqueId(), "private_messages")) {
                MessageManager.sendMessage(sender, "mute_cannot_send_message");
                return;
            }

            if (ChatControl.handleSpam(((Player) sender), message, "private_messages")) {
                return;
            }

            crm = ChatControl.applyChatRules(message, "private_messages", ((Player) sender).getUsername());

            if (crm.isPresent()) {
                message = crm.get();
            } else {
                return;
            }

            if (MultiChat.lastmsg.containsKey(((Player) sender).getUniqueId())) {

                if (MultiChat.getInstance().getServer().getPlayer(MultiChat.lastmsg.get(((Player) sender).getUniqueId())).isPresent()) {

                    Player target = MultiChat.getInstance().getServer().getPlayer(MultiChat.lastmsg.get(((Player) sender).getUniqueId())).orElse(null);

                    if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("no_pm").getList(String::valueOf).contains(((Player) sender).getCurrentServer().get().getServerInfo().getName())) {

                        if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("no_pm").getList(String::valueOf).contains(target.getCurrentServer().get().getServerInfo().getName())) {

                            if (ChatControl.ignores(((Player) sender).getUniqueId(), target.getUniqueId(), "private_messages")) {
                                ChatControl.sendIgnoreNotifications(target, sender, "private_messages");
                                return;
                            }

                            PrivateMessageManager.getInstance().sendMessage(message, (Player) sender, target);

                        } else {
                            MessageManager.sendMessage(sender, "command_msg_disabled_target");
                        }

                    } else {
                        MessageManager.sendMessage(sender, "command_msg_disabled_sender");
                    }

                } else if (MultiChat.lastmsg.get(((Player) sender).getUniqueId()).equals(new UUID(0L, 0L))) {

                    // Console target stuff

                    if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("no_pm").getList(String::valueOf).contains(((Player) sender).getCurrentServer().get().getServerInfo().getName())) {

                        PrivateMessageManager.getInstance().sendMessageConsoleTarget(message, (Player) sender);

                    } else {
                        MessageManager.sendMessage(sender, "command_msg_disabled_sender");
                    }

                    // End console target stuff

                } else {
                    MessageManager.sendMessage(sender, "command_reply_no_one_to_reply_to");
                }

            } else {
                MessageManager.sendMessage(sender, "command_reply_no_one_to_reply_to");
            }

        } else {

            // New console reply

            String message = MultiChatUtil.getMessageFromArgs(args);

            if (MultiChat.lastmsg.containsKey(new UUID(0L, 0L))) {

                if (MultiChat.getInstance().getServer().getPlayer(MultiChat.lastmsg.get((new UUID(0L, 0L)))).isPresent()) {

                    Player target = MultiChat.getInstance().getServer().getPlayer(MultiChat.lastmsg.get((new UUID(0L, 0L)))).orElse(null);

                    if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("no_pm").getList(String::valueOf).contains(target.getCurrentServer().get().getServerInfo().getName())) {

                        PrivateMessageManager.getInstance().sendMessageConsoleSender(message, target);

                    } else {
                        MessageManager.sendMessage(sender, "command_msg_disabled_target");
                    }

                } else {
                    MessageManager.sendMessage(sender, "command_reply_no_one_to_reply_to");
                }

            } else {
                MessageManager.sendMessage(sender, "command_reply_no_one_to_reply_to");
            }

            // End new console stuff

        }
    }
}
