package xyz.olivermartin.multichat.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.ServerInfo;
import ninja.leaping.configurate.ConfigurationNode;

import java.io.*;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.PatternSyntaxException;

/**
 * Bungee Communication Manager
 * <p>Manages all plug-in messaging channels on the BungeeCord side</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class BungeeComm {

    public static void sendMessage(String message, ServerInfo server) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        try {
            // Players name
            out.writeUTF(message);

            // Should display name be set?
            ConfigurationNode configYML = ConfigManager.getInstance().getHandler("config.yml").getConfig();
            if (configYML.getChildrenMap().containsKey("set_display_name")) {
                if (configYML.getNode("set_display_name").getBoolean()) {
                    out.writeUTF("T");
                } else {
                    out.writeUTF("F");
                }
            } else {
                out.writeUTF("T");
            }

            // Display name format
            if (configYML.getChildrenMap().containsKey("display_name_format")) {
                out.writeUTF(Objects.requireNonNull(configYML.getNode("display_name_format").getString()));
            } else {
                out.writeUTF("%PREFIX%%NICK%%SUFFIX%");
            }

            // Is this server a global chat server?
            if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("global").getBoolean()
                    && !ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("no_global").getList(String::valueOf).contains(server.getName())) {
                out.writeUTF("T");
            } else {
                out.writeUTF("F");
            }

            // Send the global format
            out.writeUTF(Channel.getGlobalChannel().getFormat());

        } catch (IOException e) {
            e.printStackTrace();
        }
        MultiChat.getInstance().getServer().getServer(server.getName()).ifPresent(
                registeredServer -> registeredServer.sendPluginMessage(MinecraftChannelIdentifier.from("multichat:comm"), stream.toByteArray()));
    }

    public static void sendCommandMessage(String command, ServerInfo server) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        try {

            // Command
            out.writeUTF(command);

        } catch (IOException e) {
            e.printStackTrace();
        }

        MultiChat.getInstance().getServer().getServer(server.getName()).ifPresent(
                registeredServer -> registeredServer.sendPluginMessage(MinecraftChannelIdentifier.from("multichat:act"), stream.toByteArray()));
    }

    public static void sendPlayerCommandMessage(String command, String playerRegex, ServerInfo server) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        try {

            // Command
            out.writeUTF(playerRegex);
            out.writeUTF(command);

        } catch (IOException e) {
            e.printStackTrace();
        }

        MultiChat.getInstance().getServer().getServer(server.getName()).ifPresent(
                registeredServer -> registeredServer.sendPluginMessage(MinecraftChannelIdentifier.from("multichat:pact"), stream.toByteArray()));
    }

    public static void sendChatMessage(String message, ServerInfo server) {

        // This has been repurposed to send casts to local chat streams!

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);


        try {
            // message part
            out.writeUTF(message);


        } catch (IOException e) {
            e.printStackTrace();
        }

        MultiChat.getInstance().getServer().getServer(server.getName()).ifPresent(
                registeredServer -> registeredServer.sendPluginMessage(MinecraftChannelIdentifier.from("multichat:chat"), stream.toByteArray()));
    }

    public static void sendIgnoreMap(ServerInfo server) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oout = new ObjectOutputStream(stream);

            oout.writeObject(ChatControl.getIgnoreMap());

        } catch (IOException e) {
            e.printStackTrace();
        }

        MultiChat.getInstance().getServer().getServer(server.getName()).ifPresent(
                registeredServer -> registeredServer.sendPluginMessage(MinecraftChannelIdentifier.from("multichat:ignore"), stream.toByteArray()));
    }

    public static void sendPlayerChannelMessage(String playerName, String channel, Channel channelObject, ServerInfo server, boolean colour, boolean rgb) {

        sendIgnoreMap(server);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //DataOutputStream out = new DataOutputStream(stream);
        try {
            ObjectOutputStream oout = new ObjectOutputStream(stream);

            // Players name
            oout.writeUTF(playerName);
            // Channel part
            oout.writeUTF(channel);
            oout.writeBoolean(colour);
            oout.writeBoolean(rgb);
            oout.writeBoolean(channelObject.isWhitelistMembers());
            oout.writeObject(channelObject.getMembers());

        } catch (IOException e) {
            e.printStackTrace();
        }

        MultiChat.getInstance().getServer().getServer(server.getName()).ifPresent(
                registeredServer -> registeredServer.sendPluginMessage(MinecraftChannelIdentifier.from("multichat:ch"), stream.toByteArray()));

        DebugManager.log("Sent message on multichat:ch channel!");

    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent ev) {
        if (!ev.getIdentifier().getId().startsWith("multichat:"))
            return;

        if (!(ev.getSource() instanceof ServerConnection)) {
            ev.setResult(PluginMessageEvent.ForwardResult.handled());
            return;
        }

        if (ev.getIdentifier().getId().equals("multichat:chat")) {

            ev.setResult(PluginMessageEvent.ForwardResult.handled());

            DebugManager.log("{multichat:chat} Got a plugin message");

            ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
            DataInputStream in = new DataInputStream(stream);

            try {

                UUID uuid = UUID.fromString(in.readUTF());
                DebugManager.log("{multichat:chat} UUID = " + uuid);
                String message = in.readUTF();
                DebugManager.log("{multichat:chat} Message = " + message);
                String format = in.readUTF();

                DebugManager.log("{multichat:chat} Format (before removal of double chars) = " + format);

                format = format.replace("%%", "%");

                DebugManager.log("{multichat:chat} Format = " + format);

                Player player = MultiChat.getInstance().getServer().getPlayer(uuid).orElse(null);

                if (player == null) {
                    DebugManager.log("{multichat:chat} Could not get player! Abandoning chat message... (Is IP-Forwarding on?)");
                    return;
                }

                DebugManager.log("{multichat:chat} Got player successfully! Name = " + player.getUsername());

                //synchronized (player) {

                DebugManager.log("{multichat:chat} Global Channel Available? = " + (Channel.getGlobalChannel() != null));
                Channel.getGlobalChannel().sendMessage(player, message, format);

                //}

            } catch (IOException e) {
                DebugManager.log("{multichat:chat} ERROR READING PLUGIN MESSAGE");
                e.printStackTrace();
            }


            return;

        }

        if (ev.getIdentifier().getId().equals("multichat:nick")) {

            ev.setResult(PluginMessageEvent.ForwardResult.handled());

            ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
            DataInputStream in = new DataInputStream(stream);

            try {

                UUID uuid = UUID.fromString(in.readUTF());
                String nick = in.readUTF();
                Player player = MultiChat.getInstance().getServer().getPlayer(uuid).orElse(null);

                if (player == null) return;

                synchronized (player) {

                    /*
                     * Update the nickname stored somewhere and call for an update of the player
                     * display name in that location. (Pending the "true" value of fetch display names)
                     * and a new config option to decide if the display name should be set.
                     */

                    Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(uuid);

                    if (opm.isPresent()) {

                        opm.get().nick = nick;
                        PlayerMetaManager.getInstance().updateDisplayName(uuid);

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (ev.getIdentifier().getId().equals("multichat:prefix")) {

            ev.setResult(PluginMessageEvent.ForwardResult.handled());

            ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
            DataInputStream in = new DataInputStream(stream);

            try {

                UUID uuid = UUID.fromString(in.readUTF());
                String prefix = in.readUTF();
                Player player = MultiChat.getInstance().getServer().getPlayer(uuid).orElse(null);

                if (player == null) return;

                synchronized (player) {

                    /*
                     * Update the prefix stored somewhere and call for an update of the player
                     * display name in that location. (Pending the "true" value of fetch display names)
                     * and a new config option to decide if the display name should be set.
                     */

                    Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(uuid);

                    if (opm.isPresent()) {

                        opm.get().prefix = prefix;
                        PlayerMetaManager.getInstance().updateDisplayName(uuid);

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (ev.getIdentifier().getId().equals("multichat:suffix")) {

            ev.setResult(PluginMessageEvent.ForwardResult.handled());

            ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
            DataInputStream in = new DataInputStream(stream);

            try {

                UUID uuid = UUID.fromString(in.readUTF());
                String suffix = in.readUTF();
                Player player = MultiChat.getInstance().getServer().getPlayer(uuid).orElse(null);

                if (player == null) return;

                synchronized (player) {

                    /*
                     * Update the suffix stored somewhere and call for an update of the player
                     * display name in that location. (Pending the "true" value of fetch display names)
                     * and a new config option to decide if the display name should be set.
                     */

                    Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(uuid);

                    if (opm.isPresent()) {

                        opm.get().suffix = suffix;
                        PlayerMetaManager.getInstance().updateDisplayName(uuid);

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (ev.getIdentifier().getId().equals("multichat:dn")) {

            ev.setResult(PluginMessageEvent.ForwardResult.handled());

            DebugManager.log("[multichat:dn] Got an incoming channel message!");

            ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
            DataInputStream in = new DataInputStream(stream);

            try {

                UUID uuid = UUID.fromString(in.readUTF());
                String spigotDisplayName = in.readUTF();
                Player player = MultiChat.getInstance().getServer().getPlayer(uuid).orElse(null);

                if (player == null) return;

                synchronized (player) {

                    DebugManager.log("[multichat:dn] Player exists!");

                    Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(uuid);

                    if (opm.isPresent()) {

                        DebugManager.log("[multichat:dn] Player meta exists!");

                        DebugManager.log("[multichat:dn] The displayname received is: " + spigotDisplayName);

                        opm.get().spigotDisplayName = spigotDisplayName;
                        PlayerMetaManager.getInstance().updateDisplayName(uuid);

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (ev.getIdentifier().getId().equals("multichat:world")) {

            ev.setResult(PluginMessageEvent.ForwardResult.handled());

            ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
            DataInputStream in = new DataInputStream(stream);

            DebugManager.log("[multichat:world] Got an incoming channel message!");

            try {

                UUID uuid = UUID.fromString(in.readUTF());
                String world = in.readUTF();
                Player player = MultiChat.getInstance().getServer().getPlayer(uuid).orElse(null);

                if (player == null) return;

                DebugManager.log("[multichat:world] Player is online!");

                synchronized (player) {

                    /*
                     * Update the world stored somewhere
                     */

                    Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(uuid);

                    if (opm.isPresent()) {

                        DebugManager.log("[multichat:world] Got their meta data correctly");

                        opm.get().world = world;

                        DebugManager.log("[multichat:world] Set their world to: " + world);

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        if (ev.getIdentifier().getId().equals("multichat:pxe")) {

            ev.setResult(PluginMessageEvent.ForwardResult.handled());

            DebugManager.log("[multichat:pxe] Got an incoming pexecute message!");

            ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
            DataInputStream in = new DataInputStream(stream);

            try {

                String command = in.readUTF();
                DebugManager.log("[multichat:pxe] Command is: " + command);
                MultiChat.getInstance().getServer().getCommandManager().executeAsync(MultiChat.getInstance().getServer().getConsoleCommandSource(), command);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (ev.getIdentifier().getId().equals("multichat:ppxe")) {

            ev.setResult(PluginMessageEvent.ForwardResult.handled());

            DebugManager.log("[multichat:ppxe] Got an incoming pexecute message (for a player)!");

            ByteArrayInputStream stream = new ByteArrayInputStream(ev.getData());
            DataInputStream in = new DataInputStream(stream);

            try {

                String command = in.readUTF();
                String playerRegex = in.readUTF();

                DebugManager.log("[multichat:ppxe] Command is: " + command);
                DebugManager.log("[multichat:ppxe] Player regex is: " + playerRegex);

                for (Player p : MultiChat.getInstance().getServer().getAllPlayers()) {

                    if (p.getUsername().matches(playerRegex)) {

                        MultiChat.getInstance().getServer().getCommandManager().executeAsync(p, command);

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (PatternSyntaxException e2) {
                MessageManager.sendMessage(MultiChat.getInstance().getServer().getConsoleCommandSource(), "command_execute_regex");
            }

        }

    }
}
