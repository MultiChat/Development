package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

public class PlayerMetaManager {

    static {
        instance = new PlayerMetaManager();
    }

    private static PlayerMetaManager instance;

    public static PlayerMetaManager getInstance() {
        return instance;
    }

    // END OF STATIC

    private Map<UUID, PlayerMeta> metaMap;

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

        if (!opm.isPresent()) return;

        DebugManager.log("[PlayerMetaManager] Player is present!");

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

        // TODO: [ConfigRefactor] Decide whatever the hell this was supposed to be
        if (ProxyConfigs.CONFIG.isFetchSpigotDisplayNames() && player != null) {
            DebugManager.log("[PlayerMetaManager] Fetch Spigot Display Names is true");
            DebugManager.log("[PlayerMetaManager] Set as: " + opm.get().getSpigotDisplayname());
            player.setDisplayName(opm.get().getSpigotDisplayname());
        }
    }
}
