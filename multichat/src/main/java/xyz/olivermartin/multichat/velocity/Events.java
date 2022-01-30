package xyz.olivermartin.multichat.velocity;

import com.olivermartin410.plugins.TChatInfo;
import com.olivermartin410.plugins.TGroupChatInfo;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ninja.leaping.configurate.ConfigurationNode;
import xyz.olivermartin.multichat.velocity.commands.GCCommand;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Events Manager
 * <p>Manages the majority of the event listeners, chat message, login and logout</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class Events {

    public static List<UUID> mcbPlayers = new ArrayList<>();

    private static final List<UUID> MCToggle = new ArrayList<>();
    private static final List<UUID> ACToggle = new ArrayList<>();
    private static final List<UUID> GCToggle = new ArrayList<>();
    public static Map<UUID, UUID> PMToggle = new HashMap<>();

    public static Set<UUID> hiddenStaff = new HashSet<>();

    public static boolean toggleMC(UUID uuid) {

        if (MCToggle.contains(uuid)) {
            MCToggle.remove(uuid);
            return false;
        }

        ACToggle.remove(uuid);
        GCToggle.remove(uuid);
        PMToggle.remove(uuid);

        MCToggle.add(uuid);
        return true;

    }

    public static boolean toggleAC(UUID uuid) {

        if (ACToggle.contains(uuid)) {
            ACToggle.remove(uuid);
            return false;
        }

        MCToggle.remove(uuid);
        GCToggle.remove(uuid);
        PMToggle.remove(uuid);

        ACToggle.add(uuid);
        return true;

    }

    public static boolean toggleGC(UUID uuid) {

        if (GCToggle.contains(uuid)) {
            GCToggle.remove(uuid);
            return false;
        }

        MCToggle.remove(uuid);
        ACToggle.remove(uuid);
        PMToggle.remove(uuid);

        GCToggle.add(uuid);
        return true;

    }

    public static boolean togglePM(UUID uuid, UUID uuidt) {

        if (PMToggle.containsKey(uuid)) {
            PMToggle.remove(uuid);
            return false;
        }

        MCToggle.remove(uuid);
        ACToggle.remove(uuid);
        GCToggle.remove(uuid);

        PMToggle.put(uuid, uuidt);
        return true;

    }

    @Subscribe(async = false)
    public void onChat(PlayerChatEvent event) {

        Player player = event.getPlayer();

        // New null pointer checks
        if (player.getCurrentServer().isEmpty()) {
            DebugManager.log("Player sending chat message has null server! Abandoning...");
            return;
        } else {
            if (player.getCurrentServer().get().getServerInfo() == null) {
                DebugManager.log("Player sending chat message has null server info! Abandoning...");
                return;
            }
        }

        // If player is bypassing MultiChat
        if (mcbPlayers.contains(player.getUniqueId())) {
            return;
        }

        ///
        if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("fetch_spigot_display_names").getBoolean()) {
            if (player.getCurrentServer().isPresent()) {
                BungeeComm.sendMessage(player.getUsername(), player.getCurrentServer().get().getServerInfo());
            }
        }
        ///

        if (MCToggle.contains(player.getUniqueId())) {

            String message = event.getMessage();

            if (!event.getMessage().trim().startsWith("/")) {
                StaffChatManager chatman = new StaffChatManager();
                event.setResult(PlayerChatEvent.ChatResult.denied());
                chatman.sendModMessage(player.getUsername(), player.getUsername(), player.getCurrentServer().get().getServerInfo().getName(), message);
            }
        }

        if (ACToggle.contains(player.getUniqueId())) {

            String message = event.getMessage();

            if (!event.getMessage().trim().startsWith("/")) {
                StaffChatManager chatman = new StaffChatManager();
                event.setResult(PlayerChatEvent.ChatResult.denied());
                chatman.sendAdminMessage(player.getUsername(), player.getUsername(), player.getCurrentServer().get().getServerInfo().getName(), message);
            }
        }

        if (GCToggle.contains(player.getUniqueId())) {

            String message = event.getMessage();

            if (!event.getMessage().trim().startsWith("/")) {


                event.setResult(PlayerChatEvent.ChatResult.denied());

                if (MultiChat.viewedchats.get(player.getUniqueId()) != null) {

                    String chatName = MultiChat.viewedchats.get(player.getUniqueId()).toLowerCase();

                    if (MultiChat.groupchats.containsKey(chatName)) {

                        TGroupChatInfo chatInfo = MultiChat.groupchats.get(chatName);
                        String playerName = player.getUsername();

                        if (chatInfo.getFormal()
                                && chatInfo.getAdmins().contains(player.getUniqueId())) {

                            playerName = "&o" + playerName;

                        }

                        GCCommand.sendMessage(message, playerName, chatInfo);

                    } else {
                        MessageManager.sendMessage(player, "groups_toggled_but_no_longer_exists_1");
                        MessageManager.sendMessage(player, "groups_toggled_but_no_longer_exists_2");
                    }

                } else {
                    MessageManager.sendMessage(player, "groups_toggled_but_no_longer_exists_1");
                    MessageManager.sendMessage(player, "groups_toggled_but_no_longer_exists_2");
                }
            }
        }

        if (PMToggle.containsKey(player.getUniqueId())) {

            String message = event.getMessage();

            if (!event.getMessage().trim().startsWith("/")) {

                Optional<String> crm;


                event.setResult(PlayerChatEvent.ChatResult.denied());

                if (ChatControl.isMuted(player.getUniqueId(), "private_messages")) {
                    MessageManager.sendMessage(player, "mute_cannot_send_message");
                    return;
                }

                if (ChatControl.handleSpam(player, message, "private_messages")) {
                    return;
                }

                crm = ChatControl.applyChatRules(message, "private_messages", player.getUsername());

                if (crm.isPresent()) {
                    message = crm.get();
                } else {
                    return;
                }

                if (MultiChat.getInstance().getServer().getPlayer(PMToggle.get(player.getUniqueId())).isPresent()) {

                    Player target = MultiChat.getInstance().getServer().getPlayer(PMToggle.get(player.getUniqueId())).orElse(null);

                    BungeeComm.sendMessage(player.getUsername(), player.getCurrentServer().get().getServerInfo());
                    BungeeComm.sendMessage(target.getUsername(), target.getCurrentServer().get().getServerInfo());

                    if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("no_pm").getList(String::valueOf).contains(player.getCurrentServer().get().getServerInfo().getName())) {

                        if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("no_pm").getList(String::valueOf).contains(target.getCurrentServer().get().getServerInfo().getName())) {

                            if (ChatControl.ignores(player.getUniqueId(), target.getUniqueId(), "private_messages")) {
                                ChatControl.sendIgnoreNotifications(target, player, "private_messages");
                                return;
                            }

                            PrivateMessageManager.getInstance().sendMessage(message, player, target);

                        } else {
                            MessageManager.sendMessage(player, "command_msg_disabled_target");
                        }

                    } else {
                        MessageManager.sendMessage(player, "command_msg_disabled_sender");
                    }

                } else {
                    MessageManager.sendMessage(player, "command_msg_not_online");
                }

            }
        }

        if (event.getMessage().trim().startsWith("/")) {

            String[] parts = event.getMessage().split(" ");

            if (CastControl.castList.containsKey(parts[0].substring(1).toLowerCase())) {

                if (player.hasPermission("multichat.cast." + parts[0].substring(1).toLowerCase())
                        || player.hasPermission("multichat.cast.admin")) {

                    String message = MultiChatUtil.getMessageFromArgs(parts, 1);

                    CastControl.sendCast(parts[0].substring(1), message, Channel.getChannel(player.getUniqueId()), player);


                    event.setResult(PlayerChatEvent.ChatResult.denied());

                }

            }
        }

        if (event.getResult() == PlayerChatEvent.ChatResult.allowed() && !event.getMessage().trim().startsWith("/")) {
            if (!MultiChat.frozen || player.hasPermission("multichat.chat.always")) {

                String message = event.getMessage();

                if (ChatControl.isMuted(player.getUniqueId(), "global_chat")) {
                    MessageManager.sendMessage(player, "mute_cannot_send_message");

                    event.setResult(PlayerChatEvent.ChatResult.denied());
                    return;
                }

                DebugManager.log(player.getUsername() + "- about to check for spam");

                if (ChatControl.handleSpam(player, message, "global_chat")) {
                    DebugManager.log(player.getUsername() + " - chat message being cancelled due to spam");

                    event.setResult(PlayerChatEvent.ChatResult.denied());
                    return;
                }

                Optional<String> crm;

                crm = ChatControl.applyChatRules(message, "global_chat", player.getUsername());

                if (crm.isPresent()) {
                    message = crm.get();
                    event.setResult(PlayerChatEvent.ChatResult.message(message));
                } else {

                    event.setResult(PlayerChatEvent.ChatResult.denied());
                    return;
                }

                if (!player.hasPermission("multichat.chat.link")) {
                    message = ChatControl.replaceLinks(message);
                    event.setResult(PlayerChatEvent.ChatResult.message(message));
                }

                DebugManager.log("Does player have ALL colour permission? " + (player.hasPermission("multichat.chat.colour") || player.hasPermission("multichat.chat.color")));

                DebugManager.log("Does player have simple colour permission? " + (player.hasPermission("multichat.chat.colour.simple") || player.hasPermission("multichat.chat.color.simple")));

                DebugManager.log("Does player have rgb colour permission? " + (player.hasPermission("multichat.chat.colour.rgb") || player.hasPermission("multichat.chat.color.rgb")));

                if (Channel.getChannel(player.getUniqueId()) == null) {
                    return;
                }

                // Let server know players channel preference
                BungeeComm.sendPlayerChannelMessage(player.getUsername(),
                        Channel.getChannel(player.getUniqueId()).getName(),
                        Channel.getChannel(player.getUniqueId()),
                        player.getCurrentServer().get().getServerInfo(),
                        (player.hasPermission("multichat.chat.colour")
                                || player.hasPermission("multichat.chat.color")
                                || player.hasPermission("multichat.chat.colour.simple")
                                || player.hasPermission("multichat.chat.color.simple")),
                        (player.hasPermission("multichat.chat.colour")
                                || player.hasPermission("multichat.chat.color")
                                || player.hasPermission("multichat.chat.colour.rgb")
                                || player.hasPermission("multichat.chat.color.rgb")));

                // Message passes through to spigot here

                hiddenStaff.remove(player.getUniqueId());

            } else {
                MessageManager.sendMessage(player, "freezechat_frozen");

                event.setResult(PlayerChatEvent.ChatResult.denied());
            }

        }
    }

    @Subscribe
    public void onLogin(PostLoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        boolean firstJoin = false;

        if (player.hasPermission("multichat.staff.mod")) {
            if (!MultiChat.modchatpreferences.containsKey(uuid)) {
                TChatInfo chatinfo = new TChatInfo();
                chatinfo.setChatColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("modchat").getNode("ccdefault").getString().toCharArray()[0]);
                chatinfo.setNameColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("modchat").getNode("ncdefault").getString().toCharArray()[0]);
                MultiChat.modchatpreferences.put(uuid, chatinfo);
            }
        }

        if (player.hasPermission("multichat.staff.admin")) {
            if (!MultiChat.adminchatpreferences.containsKey(uuid)) {
                TChatInfo chatinfo = new TChatInfo();
                chatinfo.setChatColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("adminchat").getNode("ccdefault").getString().toCharArray()[0]);
                chatinfo.setNameColor(ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("adminchat").getNode("ncdefault").getString().toCharArray()[0]);
                MultiChat.adminchatpreferences.put(uuid, chatinfo);
            }
        }

        PlayerMetaManager.getInstance().registerPlayer(uuid, event.getPlayer().getUsername());

        if (!MultiChat.viewedchats.containsKey(uuid)) {
            MultiChat.viewedchats.put(uuid, null);
            ConsoleManager.log("Registered player " + player.getUsername());
        }

        if (!ChatModeManager.getInstance().existsPlayer(uuid)) {
            boolean globalMode;
            globalMode = !MultiChat.defaultChannel.equalsIgnoreCase("local");
            ChatModeManager.getInstance().registerPlayer(uuid, globalMode);
            firstJoin = true;
        }

        if (MultiChat.forceChannelOnJoin) {
            boolean globalMode;
            globalMode = !MultiChat.defaultChannel.equalsIgnoreCase("local");
            ChatModeManager.getInstance().registerPlayer(uuid, globalMode);
        }

        // Set player to appropriate channels
        if (ChatModeManager.getInstance().isGlobal(uuid)) {
            Channel.setChannel(player.getUniqueId(), Channel.getGlobalChannel());
        } else {
            Channel.setChannel(player.getUniqueId(), Channel.getLocalChannel());
        }

        if (UUIDNameManager.existsUUID(uuid)) {
            UUIDNameManager.removeUUID(uuid);
        }

        UUIDNameManager.addNew(uuid, player.getUsername());

        ConsoleManager.log("Refreshed UUID-Name lookup: " + uuid.toString());

        if (ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getNode("showjoin").getBoolean()) {

            String joinformat = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getNode("serverjoin").getString();
            String silentformat = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getNode("silentjoin").getString();
            String welcomeMessage = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getNode("welcome_message").getString();
            String privateWelcomeMessage = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getNode("private_welcome_message").getString();

            ChatManipulation chatman = new ChatManipulation();

            joinformat = chatman.replaceJoinMsgVars(joinformat, player.getUsername());
            silentformat = chatman.replaceJoinMsgVars(silentformat, player.getUsername());
            welcomeMessage = chatman.replaceJoinMsgVars(welcomeMessage, player.getUsername());
            privateWelcomeMessage = chatman.replaceJoinMsgVars(privateWelcomeMessage, player.getUsername());

            boolean broadcastWelcome = true;
            if (ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getChildrenMap().containsKey("welcome")) {
                broadcastWelcome = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getNode("welcome").getBoolean();
            }

            boolean privateWelcome = false;
            if (ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getChildrenMap().containsKey("private_welcome")) {
                privateWelcome = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getNode("private_welcome").getBoolean();
            }

            boolean broadcastJoin = !player.hasPermission("multichat.staff.silentjoin");
            for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {

                if (broadcastJoin) {

                    if (firstJoin && broadcastWelcome) {
                        onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(welcomeMessage));
                    }

                    if (firstJoin && privateWelcome && onlineplayer.getUsername().equals(player.getUsername())) {
                        onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(privateWelcomeMessage));
                    }

                    onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(joinformat));
                } else {

                    hiddenStaff.add(player.getUniqueId());

                    if (onlineplayer.hasPermission("multichat.staff.silentjoin")) {
                        onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(silentformat));
                    }

                }
            }
        }
    }

    @Subscribe
    public void onLogout(DisconnectEvent event) {

        Player player = event.getPlayer();
        UUID uuid = event.getPlayer().getUniqueId();

        hiddenStaff.remove(uuid);

        mcbPlayers.remove(uuid);

        MCToggle.remove(uuid);
        ACToggle.remove(uuid);
        GCToggle.remove(uuid);

        ConfigurationNode config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

        if (config.getNode("session_ignore").getBoolean()) {
            ChatControl.unignoreAll(uuid);
        }

        // Reset their spam data on logout (nothing is stored persistantly)
        ChatControl.spamPardonPlayer(uuid);

        ///
        Channel.removePlayer(player.getUniqueId());
        ///

        MultiChat.viewedchats.remove(uuid);

        PlayerMetaManager.getInstance().unregisterPlayer(uuid);

        ConsoleManager.log("Un-Registered player " + player.getUsername());

        if (!Channel.getGlobalChannel().isMember(player.getUniqueId())) {
            Channel.getGlobalChannel().removeMember(uuid);
        }

        if (!Channel.getLocalChannel().isMember(player.getUniqueId())) {
            Channel.getLocalChannel().removeMember(uuid);
        }

        if (ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getNode("showquit").getBoolean()) {

            String joinformat = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getNode("networkquit").getString();
            String silentformat = ConfigManager.getInstance().getHandler("joinmessages.yml").getConfig().getNode("silentquit").getString();

            ChatManipulation chatman = new ChatManipulation();

            joinformat = chatman.replaceJoinMsgVars(joinformat, player.getUsername());
            silentformat = chatman.replaceJoinMsgVars(silentformat, player.getUsername());

            for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {

                if (!player.hasPermission("multichat.staff.silentjoin")) {
                    onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(joinformat));
                } else {
                    if (onlineplayer.hasPermission("multichat.staff.silentjoin")) {
                        onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(silentformat));
                    }
                }
            }
        }
    }


    @Subscribe
    public void onServerSwitch(ServerConnectedEvent event) {
        // Tell the new server the player's channel preference
        MultiChat.getInstance().getServer().getScheduler().buildTask(MultiChat.getInstance(), () -> {
            try {
                BungeeComm.sendPlayerChannelMessage(event.getPlayer().getUsername(),
                        Channel.getChannel(event.getPlayer().getUniqueId()).getName(),
                        Channel.getChannel(event.getPlayer().getUniqueId()),
                        event.getPlayer().getCurrentServer().get().getServerInfo(),
                        event.getPlayer().hasPermission("multichat.chat.color") || event.getPlayer().hasPermission("multichat.chat.colour.simple"),
                        event.getPlayer().hasPermission("multichat.chat.color") || event.getPlayer().hasPermission("multichat.chat.colour.rgb"));

                // LEGACY SERVER HACK
                if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("legacy_servers").getList(String::valueOf).contains(event.getPlayer().getCurrentServer().get().getServerInfo().getName())) {
                    DebugManager.log("Player: " + event.getPlayer().getUsername() + ", switching to server: " + event.getPlayer().getCurrentServer().get().getServerInfo().getName() + ", is a LEGACY server!");
                    BungeeComm.sendCommandMessage("!!!LEGACYSERVER!!!", event.getPlayer().getCurrentServer().get().getServerInfo());
                } else {
                    BungeeComm.sendCommandMessage("!!!NOTLEGACYSERVER!!!", event.getPlayer().getCurrentServer().get().getServerInfo());
                }

            } catch (NullPointerException ex) { /* EMPTY */ }
        }).delay(500L, TimeUnit.MILLISECONDS).schedule();

    }

}
