package xyz.olivermartin.multichat.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.olivermartin.multichat.proxy.common.ServerGroups;
import xyz.olivermartin.multichat.velocity.events.PostBroadcastEvent;
import xyz.olivermartin.multichat.velocity.events.PostGlobalChatEvent;

import java.util.*;

/**
 * Channel
 * <p>A class to represent a chat channel and control the messages sent etc.</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class Channel {

    private static final GlobalChannel global;
    private static final LocalChannel local;

    static {
        global = new GlobalChannel("&f%DISPLAYNAME%&f: ");
        local = new LocalChannel();
    }

    public static GlobalChannel getGlobalChannel() {
        return global;
    }

    public static LocalChannel getLocalChannel() {
        return local;
    }

    public static Map<UUID, Channel> playerChannels = new HashMap<>();

    public static void setChannel(UUID uuid, Channel channel) {
        Channel.playerChannels.put(uuid, channel);
    }

    public static Channel getChannel(UUID uuid) {
        return Channel.playerChannels.get(uuid);
    }

    public static void removePlayer(UUID uuid) {
        Channel.playerChannels.remove(uuid);
    }

    /* END STATIC */

    boolean whitelistMembers;
    protected List<UUID> members;

    boolean whitelistServers;
    protected List<String> servers;

    protected String name;
    protected String format;

    public Channel(String name, String format, boolean whitelistServers, boolean whitelistMembers) {

        this.name = name;
        this.whitelistServers = whitelistServers;
        this.format = format;
        this.servers = new ArrayList<>();
        this.members = new ArrayList<>();
        this.whitelistMembers = whitelistMembers;

    }

    public boolean isMember(UUID player) {
        if (this.whitelistMembers) {
            return this.members.contains(player);
        } else {
            return !this.members.contains(player);
        }
    }

    public void removeMember(UUID player) {
        this.members.remove(player);
    }

    public List<UUID> getMembers() {
        return this.members;
    }

    public boolean isWhitelistMembers() {
        return this.whitelistMembers;
    }

    public void addServer(String server) {
        if (!servers.contains(server)) servers.add(server);
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    public void clearServers() {
        this.servers = new ArrayList<>();
    }

    public void addMember(UUID member) {
        if (!members.contains(member)) members.add(member);
    }

    public void setMembers(List<UUID> members) {
        this.members = members;
    }

    public String getName() {
        return this.name;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void sendMessage(Player sender, String message, String format) {

        DebugManager.log("CHANNEL #" + getName() + ": Got a message for the channel");
        DebugManager.log("CHANNEL #" + getName() + ": SENDER = " + sender.getUsername());
        DebugManager.log("CHANNEL #" + getName() + ": MESSAGE = " + message);
        DebugManager.log("CHANNEL #" + getName() + ": FORMAT = " + format);

        Boolean serverGroupsEnabled = ServerGroups.getServerGroupsEnabled();
        ArrayList<String> serverGroupList = ServerGroups.getServerGroupList(sender);

        for (Player receiver : MultiChat.getInstance().getServer().getAllPlayers()) {

            if (receiver != null) {

                synchronized (receiver) {

                    if (sender.getCurrentServer().isPresent() && receiver.getCurrentServer().isPresent()) {

                        if ((whitelistMembers && members.contains(receiver.getUniqueId())) || (!whitelistMembers && !members.contains(receiver.getUniqueId()))) {

                            if (!serverGroupsEnabled || serverGroupsEnabled == null) {
                                if ((whitelistServers && servers.contains(receiver.getCurrentServer().get().getServerInfo().getName())) ||
                                        (!whitelistServers && !servers.contains(receiver.getCurrentServer().get().getServerInfo().getName()))) {

                                    if (!ChatControl.ignores(sender.getUniqueId(), receiver.getUniqueId(), "global_chat")) {
                                        if (!receiver.getCurrentServer().get().getServerInfo().getName().equals(sender.getCurrentServer().get().getServerInfo().getName())) {
                                            receiver.sendMessage(Component.join(Component.text(), buildFormat(sender, receiver, format, message)));
                                        }
                                    } else {
                                        ChatControl.sendIgnoreNotifications(receiver, sender, "global_chat");
                                    }
                                }
                            } else {
                                if (serverGroupList != null && serverGroupList.contains(receiver.getCurrentServer().get().getServerInfo().getName())) {

                                    if (!ChatControl.ignores(sender.getUniqueId(), receiver.getUniqueId(), "global_chat")) {
                                        if (!receiver.getCurrentServer().get().getServerInfo().getName().equals(sender.getCurrentServer().get().getServerInfo().getName())) {
                                            receiver.sendMessage(Component.join(Component.text(), buildFormat(sender, receiver, format, message)));
                                        }
                                    } else {
                                        ChatControl.sendIgnoreNotifications(receiver, sender, "global_chat");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Trigger PostGlobalChatEvent
        MultiChat.getInstance().getServer().getEventManager().fire(new PostGlobalChatEvent(sender, format, message));

        sendToConsole(sender, format, message);

    }

    public void sendMessage(String message, CommandSource sender) {
        LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().character('&').hexCharacter('#').hexColors().build();

        for (Player receiver : MultiChat.getInstance().getServer().getAllPlayers()) {
            if (receiver != null && sender != null) {
                if (receiver.getCurrentServer().isPresent()) {
                    if ((whitelistMembers && members.contains(receiver.getUniqueId())) || (!whitelistMembers && !members.contains(receiver.getUniqueId()))) {
                        if ((whitelistServers && servers.contains(receiver.getCurrentServer().get().getServerInfo().getName())) ||
                                (!whitelistServers && !servers.contains(receiver.getCurrentServer().get().getServerInfo().getName()))) {
                            //TODO hiding & showing streams

                            if (MultiChat.legacyServers.contains(receiver.getCurrentServer().get().getServerInfo().getName())) {
                                receiver.sendMessage(serializer.deserialize(MultiChatUtil.approximateHexCodes(message)));
                            } else {
                                receiver.sendMessage(serializer.deserialize(message));
                            }

                        }
                    }
                }
            }
        }

        // Trigger PostBroadcastEvent
        MultiChat.getInstance().getServer().getEventManager().fire(new PostBroadcastEvent("cast", message));

        ConsoleManager.logDisplayMessage(message);

    }

    public Component buildFormat(Player sender, Player receiver, String format, String message) {

        String newFormat = format;
        newFormat = newFormat + "%MESSAGE%";

        Component toSend;

        LegacyComponentSerializer serializer = LegacyComponentSerializer.builder().character('&').hexCharacter('#').hexColors().build();

        if (sender.hasPermission("multichat.chat.colour") || sender.hasPermission("multichat.chat.color")) {
            newFormat = newFormat.replace("%MESSAGE%", message);

            if (MultiChat.legacyServers.contains(receiver.getCurrentServer().get().getServerInfo().getName())) {
                newFormat = MultiChatUtil.approximateHexCodes(newFormat);
            }

            toSend = serializer.deserialize(MultiChatUtil.approximateHexCodes(MultiChatUtil.reformatRGB(newFormat)));
        } else {
            newFormat = newFormat.replace("%MESSAGE%", "");

            if (MultiChat.legacyServers.contains(receiver.getCurrentServer().get().getServerInfo().getName())) {
                newFormat = MultiChatUtil.approximateHexCodes(newFormat);
            }

            toSend = serializer.deserialize(MultiChatUtil.approximateHexCodes(MultiChatUtil.reformatRGB(newFormat))).append(Component.text(message));
        }

        return toSend;

    }

    public Component buildFormat(String name, String displayName, String server, String world, Player receiver, String format, String message) {

        String newFormat = format;

        newFormat = newFormat.replace("%DISPLAYNAME%", displayName);
        newFormat = newFormat.replace("%NAME%", name);
        newFormat = newFormat.replace("%DISPLAYNAMET%", receiver.getUsername());
        newFormat = newFormat.replace("%NAMET%", receiver.getUsername());

        Optional<PlayerMeta> opmt = PlayerMetaManager.getInstance().getPlayer(receiver.getUniqueId());
        if (opmt.isPresent()) {
            newFormat = newFormat.replace("%PREFIXT%", opmt.get().prefix);
            newFormat = newFormat.replace("%SUFFIXT%", opmt.get().suffix);
            newFormat = newFormat.replace("%NICKT%", opmt.get().nick);
            newFormat = newFormat.replace("%WORLDT%", opmt.get().world);
        }

        newFormat = newFormat.replace("%SERVER%", server);
        newFormat = newFormat.replace("%SERVERT%", receiver.getCurrentServer().get().getServerInfo().getName());

        newFormat = newFormat.replace("%WORLD%", world);

        newFormat = newFormat + "%MESSAGE%";

        Component toSend;

        newFormat = newFormat.replace("%MESSAGE%", message);
        if (MultiChat.legacyServers.contains(receiver.getCurrentServer().get().getServerInfo().getName())) {
            newFormat = MultiChatUtil.approximateHexCodes(newFormat);
        }
        toSend = LegacyComponentSerializer.legacyAmpersand().deserialize(newFormat);

        return toSend;

    }

    public void sendToConsole(Player sender, String format, String message) {

        String newFormat = format;

        newFormat = newFormat.replace("%DISPLAYNAME%", sender.getUsername());
        newFormat = newFormat.replace("%NAME%", sender.getUsername());

        Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(sender.getUniqueId());
        if (opm.isPresent()) {
            newFormat = newFormat.replace("%PREFIX%", opm.get().prefix);
            newFormat = newFormat.replace("%SUFFIX%", opm.get().suffix);
            newFormat = newFormat.replace("%NICK%", opm.get().nick);
            newFormat = newFormat.replace("%WORLD%", opm.get().world);
        }

        newFormat = newFormat.replace("%DISPLAYNAMET%", "CONSOLE");
        newFormat = newFormat.replace("%NAMET%", "CONSOLE");
        newFormat = newFormat.replace("%SERVER%", sender.getCurrentServer().get().getServerInfo().getName());
        newFormat = newFormat.replace("%SERVERT%", "CONSOLE");
        newFormat = newFormat.replace("%WORLDT%", "CONSOLE");

        newFormat = newFormat + "%MESSAGE%";

        if (sender.hasPermission("multichat.chat.colour") || sender.hasPermission("multichat.chat.color")) {

            newFormat = newFormat.replace("%MESSAGE%", message);
            ConsoleManager.logChat(newFormat);

        } else {

            newFormat = newFormat.replace("%MESSAGE%", "");
            ConsoleManager.logBasicChat(newFormat, message);

        }

    }

    public void sendToConsole(String name, String displayName, String server, String world, String format, String message) {

        String newFormat = format;

        newFormat = newFormat.replace("%DISPLAYNAME%", displayName);
        newFormat = newFormat.replace("%NAME%", name);
        newFormat = newFormat.replace("%DISPLAYNAMET%", "CONSOLE");
        newFormat = newFormat.replace("%NAMET%", "CONSOLE");
        newFormat = newFormat.replace("%SERVER%", server);
        newFormat = newFormat.replace("%SERVERT%", "CONSOLE");
        newFormat = newFormat.replace("%WORLD%", world);
        newFormat = newFormat.replace("%WORLDT%", "CONSOLE");

        newFormat = newFormat + "%MESSAGE%";

        newFormat = newFormat.replace("%MESSAGE%", message);

        ConsoleManager.logChat(newFormat);

    }
}
