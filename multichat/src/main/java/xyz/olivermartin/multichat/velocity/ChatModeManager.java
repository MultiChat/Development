package xyz.olivermartin.multichat.velocity;

import com.velocitypowered.api.proxy.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatModeManager {

    private static final ChatModeManager instance;

    public static ChatModeManager getInstance() {
        return instance;
    }

    static {
        instance = new ChatModeManager();
    }

    /* END STATIC */

    private Map<UUID, Boolean> globalPlayers;

    private ChatModeManager() {
        globalPlayers = new HashMap<>();
    }

    public void setLocal(UUID uuid) {

        globalPlayers.put(uuid, false);

        // TODO
        Channel.setChannel(uuid, Channel.getLocalChannel());

        // TODO
        Player player = MultiChat.getInstance().getServer().getPlayer(uuid).orElse(null);
        if (player == null) return;

        Channel local = Channel.getLocalChannel();
        if (!local.isMember(uuid)) {
            local.removeMember(uuid);
            MessageManager.sendSpecialMessage(player, "command_channel_show", "LOCAL");
        }

        BungeeComm.sendPlayerChannelMessage(player.getUsername(), Channel.getChannel(uuid).getName(), Channel.getChannel(uuid), player.getCurrentServer().get().getServerInfo(), (player.hasPermission("multichat.chat.colour") || player.hasPermission("multichat.chat.color") || player.hasPermission("multichat.chat.colour.simple") || player.hasPermission("multichat.chat.color.simple")), (player.hasPermission("multichat.chat.colour") || player.hasPermission("multichat.chat.color") || player.hasPermission("multichat.chat.colour.rgb") || player.hasPermission("multichat.chat.color.rgb")));

    }

    public void setGlobal(UUID uuid) {

        globalPlayers.put(uuid, true);

        // TODO
        Channel.setChannel(uuid, Channel.getGlobalChannel());

        // TODO
        Player player = MultiChat.getInstance().getServer().getPlayer(uuid).orElse(null);
        if (player == null) return;

        Channel global = Channel.getGlobalChannel();
        if (!global.isMember(uuid)) {
            global.removeMember(uuid);
            MessageManager.sendSpecialMessage(player, "command_channel_show", "GLOBAL");
        }

        BungeeComm.sendPlayerChannelMessage(player.getUsername(), Channel.getChannel(uuid).getName(), Channel.getChannel(uuid), player.getCurrentServer().get().getServerInfo(), (player.hasPermission("multichat.chat.colour") || player.hasPermission("multichat.chat.color") || player.hasPermission("multichat.chat.colour.simple") || player.hasPermission("multichat.chat.color.simple")), (player.hasPermission("multichat.chat.colour") || player.hasPermission("multichat.chat.color") || player.hasPermission("multichat.chat.colour.rgb") || player.hasPermission("multichat.chat.color.rgb")));

    }

    public void registerPlayer(UUID uuid, boolean global) {

        globalPlayers.put(uuid, global);

    }

    public boolean existsPlayer(UUID uuid) {

        return globalPlayers.containsKey(uuid);

    }

    public Map<UUID, Boolean> getData() {
        return globalPlayers;
    }

    public void loadData(Map<UUID, Boolean> data) {
        this.globalPlayers = data;
        if (this.globalPlayers == null) {
            DebugManager.log("The global players data loaded was null... So made a new map!");
            globalPlayers = new HashMap<>();
        }
    }

    public boolean isGlobal(UUID uuid) {
        return globalPlayers.getOrDefault(uuid, true);
    }

}
