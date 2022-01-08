package xyz.olivermartin.multichat.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.olivermartin.multichat.bungee.MultiChatUtil;

import java.util.UUID;

public class PrivateMessageManager {

    private static PrivateMessageManager instance;

    public static PrivateMessageManager getInstance() {
        return instance;
    }

    static {
        instance = new PrivateMessageManager();
    }

    /* END STATIC */

    private ChatManipulation chatfix;

    private PrivateMessageManager() {
        chatfix = new ChatManipulation();
    }

    public void sendMessage(String message, Player sender, Player target) {

        message = MultiChatUtil.reformatRGB(message);

        String messageoutformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("pmout").getString();
        String messageinformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("pmin").getString();
        String messagespyformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("pmspy").getString();

        String finalmessage = chatfix.replaceMsgVars(messageoutformat, message, sender, target);
        if (MultiChat.legacyServers.contains(sender.getCurrentServer().get().getServerInfo().getName())) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(MultiChatUtil.approximateHexCodes(finalmessage)));
        } else {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(finalmessage));
        }

        finalmessage = chatfix.replaceMsgVars(messageinformat, message, sender, target);
        if (MultiChat.legacyServers.contains(target.getCurrentServer().get().getServerInfo().getName())) {
            target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(MultiChatUtil.approximateHexCodes(finalmessage)));
        } else {
            target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(finalmessage));
        }

        finalmessage = chatfix.replaceMsgVars(messagespyformat, message, sender, target);
        for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {

            if (onlineplayer.hasPermission("multichat.staff.spy")
                    && MultiChat.socialspy.contains(onlineplayer.getUniqueId())
                    && onlineplayer.getUniqueId() != sender.getUniqueId()
                    && onlineplayer.getUniqueId() != target.getUniqueId()
                    && !(sender.hasPermission("multichat.staff.spy.bypass")
                    || target.hasPermission("multichat.staff.spy.bypass"))) {

                if (onlineplayer.getCurrentServer() != null
                        && onlineplayer.getCurrentServer().get().getServerInfo() != null
                        && MultiChat.legacyServers.contains(onlineplayer.getCurrentServer().get().getServerInfo().getName())) {
                    onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(MultiChatUtil.approximateHexCodes(finalmessage)));
                } else {
                    onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(finalmessage));
                }

            }

        }

        MultiChat.lastmsg.remove(sender.getUniqueId());

        MultiChat.lastmsg.put(sender.getUniqueId(), target.getUniqueId());

        MultiChat.lastmsg.remove(target.getUniqueId());

        MultiChat.lastmsg.put(target.getUniqueId(), sender.getUniqueId());

        ConsoleManager.logSocialSpy(sender.getUsername(), target.getUsername(), message);

    }

    public void sendMessageConsoleTarget(String message, Player sender) {

        message = MultiChatUtil.reformatRGB(message);

        String messageoutformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("pmout").getString();
        String messageinformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("pmin").getString();
        String messagespyformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("pmspy").getString();

        String finalmessage = chatfix.replaceMsgConsoleTargetVars(messageoutformat, message, sender);
        if (MultiChat.legacyServers.contains(sender.getCurrentServer().get().getServerInfo().getName())) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(MultiChatUtil.approximateHexCodes(finalmessage)));
        } else {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(finalmessage));
        }

        finalmessage = chatfix.replaceMsgConsoleTargetVars(messageinformat, message, sender);
        MultiChat.getInstance().getServer().getConsoleCommandSource().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(finalmessage));

        finalmessage = chatfix.replaceMsgConsoleTargetVars(messagespyformat, message, sender);
        for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {

            if (onlineplayer.hasPermission("multichat.staff.spy")
                    && MultiChat.socialspy.contains(onlineplayer.getUniqueId())
                    && onlineplayer.getUniqueId() != sender.getUniqueId()
                    && !sender.hasPermission("multichat.staff.spy.bypass")) {

                if (MultiChat.legacyServers.contains(onlineplayer.getCurrentServer().get().getServerInfo().getName())) {
                    onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(MultiChatUtil.approximateHexCodes(finalmessage)));
                } else {
                    onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(finalmessage));
                }
            }

        }

        MultiChat.lastmsg.remove(sender.getUniqueId());

        MultiChat.lastmsg.put(sender.getUniqueId(), new UUID(0L, 0L));

        MultiChat.lastmsg.remove(new UUID(0L, 0L));

        MultiChat.lastmsg.put(new UUID(0L, 0L), sender.getUniqueId());

    }

    public void sendMessageConsoleSender(String message, Player target) {

        message = MultiChatUtil.reformatRGB(message);

        CommandSource sender = MultiChat.getInstance().getServer().getConsoleCommandSource();

        String messageoutformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("pmout").getString();
        String messageinformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("pmin").getString();
        String messagespyformat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("pmspy").getString();

        String finalmessage = chatfix.replaceMsgConsoleSenderVars(messageoutformat, message, target);
        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(finalmessage));

        finalmessage = chatfix.replaceMsgConsoleSenderVars(messageinformat, message, target);
        if (MultiChat.legacyServers.contains(target.getCurrentServer().get().getServerInfo().getName())) {
            target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(MultiChatUtil.approximateHexCodes(finalmessage)));
        } else {
            target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(finalmessage));
        }

        finalmessage = chatfix.replaceMsgConsoleSenderVars(messagespyformat, message, target);
        for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {

            if (onlineplayer.hasPermission("multichat.staff.spy")
                    && MultiChat.socialspy.contains(onlineplayer.getUniqueId())
                    && onlineplayer.getUniqueId() != target.getUniqueId()
                    && !target.hasPermission("multichat.staff.spy.bypass")) {

                if (MultiChat.legacyServers.contains(onlineplayer.getCurrentServer().get().getServerInfo().getName())) {
                    onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(MultiChatUtil.approximateHexCodes(finalmessage)));
                } else {
                    onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(finalmessage));
                }
            }

        }

        MultiChat.lastmsg.remove(new UUID(0L, 0L));

        MultiChat.lastmsg.put(new UUID(0L, 0L), target.getUniqueId());

        MultiChat.lastmsg.remove(target.getUniqueId());

        MultiChat.lastmsg.put(target.getUniqueId(), new UUID(0L, 0L));

    }

}
