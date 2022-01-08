package xyz.olivermartin.multichat.velocity;

import com.velocitypowered.api.proxy.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerMetaManager {

    static {
        instance = new PlayerMetaManager();
    }

    private static final PlayerMetaManager instance;

    public static PlayerMetaManager getInstance() {
        return instance;
    }

    // END OF STATIC

    private final Map<UUID, PlayerMeta> metaMap;

    public PlayerMetaManager() {
        this.metaMap = new HashMap<>();
    }

    public void registerPlayer(UUID uuid, String name) {
        this.metaMap.put(uuid, new PlayerMeta(uuid, name));
    }

    public void unregisterPlayer(UUID uuid) {
        metaMap.remove(uuid);
    }

    public Optional<PlayerMeta> getPlayer(UUID uuid) {
        if (!metaMap.containsKey(uuid)) return Optional.empty();
        return Optional.of(metaMap.get(uuid));
    }

    public void updateDisplayName(UUID uuid) {

        DebugManager.log("[PlayerMetaManager] Updating display name...");

        Optional<PlayerMeta> opm = getPlayer(uuid);

        if (opm.isEmpty()) return;

        DebugManager.log("[PlayerMetaManager] Player is present!");

        Player player = MultiChat.getInstance().getServer().getPlayer(uuid).orElse(null);

        if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("fetch_spigot_display_names").getBoolean() && player != null) {

            DebugManager.log("[PlayerMetaManager] Fetch Spigot Display Names is true");

            DebugManager.log("[PlayerMetaManager] display names [UNSUPPORTED IN VELOCITY]");

        }

    }

}
