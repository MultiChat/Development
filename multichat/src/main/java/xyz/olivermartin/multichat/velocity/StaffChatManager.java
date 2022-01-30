package xyz.olivermartin.multichat.velocity;

import com.olivermartin410.plugins.TChatInfo;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.olivermartin.multichat.bungee.MultiChatUtil;
import xyz.olivermartin.multichat.velocity.events.PostStaffChatEvent;

import java.util.Optional;

/**
 * Staff Chat Manager
 * <p>Manages chat input to the staff chats, both mod and admin</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class StaffChatManager {

    public void sendModMessage(String username, String displayname, String server, String message) {

        message = MultiChatUtil.reformatRGB(message);

        ChatManipulation chatfix = new ChatManipulation();
        String messageFormat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("modchat").getNode("format").getString();
        String original = message;

        Optional<String> crm;

        crm = ChatControl.applyChatRules(original, "staff_chats", username);

        if (crm.isPresent()) {
            original = crm.get();
        } else {
            return;
        }

        for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {
            if (onlineplayer.hasPermission("multichat.staff.mod")) {
                if (!MultiChat.modchatpreferences.containsKey(onlineplayer.getUniqueId())) {
                    TChatInfo chatinfo = new TChatInfo();
                    chatinfo.setChatColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("modchat").getNode("ccdefault").getString().toCharArray()[0]);
                    chatinfo.setNameColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("modchat").getNode("ncdefault").getString().toCharArray()[0]);
                    MultiChat.modchatpreferences.put(onlineplayer.getUniqueId(), chatinfo);
                }

                message = chatfix.replaceModChatVars(messageFormat, username, displayname, server, original, onlineplayer);
                LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
                onlineplayer.sendMessage(serializer.deserialize(message));
            }
        }

        // Trigger PostStaffChatEvent
        if (username.equalsIgnoreCase("console")) {
            MultiChat.getInstance().getServer().getEventManager().fire(new PostStaffChatEvent("mod", MultiChat.getInstance().getServer().getConsoleCommandSource(), original));
        } else {
            if (MultiChat.getInstance().getServer().getPlayer(username).isPresent()) {
                MultiChat.getInstance().getServer().getEventManager().fire(new PostStaffChatEvent("mod", MultiChat.getInstance().getServer().getPlayer(username).get(), original));
            }
        }

        ConsoleManager.logModChat("(" + username + ") " + original);

    }

    public void sendAdminMessage(String username, String displayname, String server, String message) {

        message = MultiChatUtil.reformatRGB(message);

        String original = message;
        ChatManipulation chatfix = new ChatManipulation();
        String messageFormat = ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("adminchat").getNode("format").getString();

        Optional<String> crm;

        crm = ChatControl.applyChatRules(original, "staff_chats", username);

        if (crm.isPresent()) {
            original = crm.get();
        } else {
            return;
        }

        for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {

            if (onlineplayer.hasPermission("multichat.staff.admin")) {

                if (!MultiChat.adminchatpreferences.containsKey(onlineplayer.getUniqueId())) {

                    TChatInfo chatinfo = new TChatInfo();
                    chatinfo.setChatColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("adminchat").getNode("ccdefault").getString().toCharArray()[0]);
                    chatinfo.setNameColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("adminchat").getNode("ncdefault").getString().toCharArray()[0]);

                    MultiChat.adminchatpreferences.put(onlineplayer.getUniqueId(), chatinfo);

                }

                message = chatfix.replaceAdminChatVars(messageFormat, username, displayname, server, original, onlineplayer);
                if (MultiChat.legacyServers.contains(onlineplayer.getCurrentServer().get().getServerInfo().getName())) {
                    onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(MultiChatUtil.approximateHexCodes(message)));
                } else {
                    onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
                }

            }
        }

        // Trigger PostStaffChatEvent
        if (username.equalsIgnoreCase("console")) {
            MultiChat.getInstance().getServer().getEventManager().fire(new PostStaffChatEvent("admin", MultiChat.getInstance().getServer().getConsoleCommandSource(), original));
        } else {
            if (MultiChat.getInstance().getServer().getPlayer(username).isPresent()) {
                MultiChat.getInstance().getServer().getEventManager().fire(new PostStaffChatEvent("admin", MultiChat.getInstance().getServer().getPlayer(username).orElse(null), original));
            }
        }

        ConsoleManager.logAdminChat("(" + username + ") " + original);

    }
}
