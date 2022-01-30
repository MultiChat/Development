package xyz.olivermartin.multichat.velocity.commands;

import com.olivermartin410.plugins.TGroupChatInfo;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.olivermartin.multichat.velocity.*;

import java.util.Optional;

/**
 * Group Chat Messaging Command
 * <p>Allows players to send a message direct to a group chat or toggle group chats</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class GCCommand extends Command {

    private static final String[] aliases = new String[]{};

    public GCCommand() {
        super("gc", aliases);
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.group");
    }

    public void execute(Invocation invocation) {
        var args = invocation.arguments();
        var sender = invocation.source();

        if (args.length < 1) {

            if ((sender instanceof Player)) {
                Player player = (Player) sender;

                boolean toggleresult = Events.toggleGC(player.getUniqueId());

                if (toggleresult) {
                    MessageManager.sendMessage(sender, "command_gc_toggle_on");
                } else {
                    MessageManager.sendMessage(sender, "command_gc_toggle_off");
                }

            } else {

                MessageManager.sendMessage(sender, "command_gc_only_players_toggle");
            }

        } else if ((sender instanceof Player)) {
            Player player = (Player) sender;

            if (MultiChat.viewedchats.get(player.getUniqueId()) != null) {

                String groupName = MultiChat.viewedchats.get(player.getUniqueId());

                if (MultiChat.groupchats.containsKey(groupName)) {

                    TGroupChatInfo groupInfo = MultiChat.groupchats.get(groupName);

                    String message = MultiChatUtil.getMessageFromArgs(args);

                    String playerName = ((Player) sender).getUsername();

                    if ((groupInfo.getFormal())
                            && (groupInfo.getAdmins().contains(player.getUniqueId()))) {
                        playerName = "&o" + playerName;
                    }

                    sendMessage(message, playerName, groupInfo);

                } else {

                    MessageManager.sendMessage(sender, "command_gc_no_longer_exists");
                }

            } else {
                MessageManager.sendMessage(sender, "command_gc_no_chat_selected");
            }

        } else {
            MessageManager.sendMessage(sender, "command_gc_only_players_speak");
        }
    }

    public static void sendMessage(String message, String playerName, TGroupChatInfo groupInfo) {

        ChatManipulation chatfix = new ChatManipulation();

        message = MultiChatUtil.reformatRGB(message);

        Player potentialPlayer = MultiChat.getInstance().getServer().getPlayer(playerName).orElse(null);
        if (potentialPlayer != null) {
            if (ChatControl.isMuted(potentialPlayer.getUniqueId(), "group_chats")) {
                MessageManager.sendMessage(potentialPlayer, "mute_cannot_send_message");
                return;
            }

            if (ChatControl.handleSpam(potentialPlayer, message, "group_chats")) {
                return;
            }
        }

        Optional<String> crm;

        crm = ChatControl.applyChatRules(message, "group_chats", playerName);

        if (crm.isPresent()) {
            message = crm.get();
        } else {
            return;
        }

        String messageFormat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("groupchat").getNode("format").getString();
        message = chatfix.replaceGroupChatVars(messageFormat, playerName, message, groupInfo.getName());

        for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {

            if (((groupInfo.existsViewer(onlineplayer.getUniqueId())) && (onlineplayer.hasPermission("multichat.group"))) || ((MultiChat.allspy.contains(onlineplayer.getUniqueId())) && (onlineplayer.hasPermission("multichat.staff.spy")))) {

                if (potentialPlayer != null) {
                    if (!ChatControl.ignores(potentialPlayer.getUniqueId(), onlineplayer.getUniqueId(), "group_chats")) {
                        if (MultiChat.legacyServers.contains(onlineplayer.getCurrentServer().get().getServerInfo().getName())) {
                            onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(MultiChatUtil.approximateHexCodes(message)));
                        } else {
                            onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
                        }
                    } else {
                        ChatControl.sendIgnoreNotifications(onlineplayer, potentialPlayer, "group_chats");
                    }
                } else {
                    if (MultiChat.legacyServers.contains(onlineplayer.getCurrentServer().get().getServerInfo().getName())) {
                        onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(MultiChatUtil.approximateHexCodes(message)));
                    } else {
                        onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
                    }
                }

            }

        }

        ConsoleManager.logGroupChat(message);
    }
}
