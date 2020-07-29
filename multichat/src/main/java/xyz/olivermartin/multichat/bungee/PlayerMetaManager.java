package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;

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
		this.metaMap = new HashMap<UUID, PlayerMeta>();
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

		if (ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getBoolean(ConfigValues.Config.FETCH_SPIGOT_DISPLAY_NAMES) == true && player != null) {

			DebugManager.log("[PlayerMetaManager] Fetch Spigot Display Names is true");

			if (ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().contains(ConfigValues.Config.SET_DISPLAY_NAME)) {

				if (ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getBoolean(ConfigValues.Config.SET_DISPLAY_NAME)) {

					DebugManager.log("[PlayerMetaManager] MultiChat is in charge of display names");

					if (ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().contains(ConfigValues.Config.DISPLAY_NAME_FORMAT)) {
						//player.setDisplayName(opm.get().getDisplayName(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("display_name_format")));

						DebugManager.log("[PlayerMetaManager] Set as: " + opm.get().getSpigotDisplayname());

						player.setDisplayName(opm.get().getSpigotDisplayname());
					} else {
						//player.setDisplayName(opm.get().getDisplayName("%PREFIX%%NICK%%SUFFIX%"));

						DebugManager.log("[PlayerMetaManager] Set as: " + opm.get().getSpigotDisplayname());

						player.setDisplayName(opm.get().getSpigotDisplayname());
					}
				} else {

					DebugManager.log("[PlayerMetaManager] MultiChat is NOT in charge of display names!");

					DebugManager.log("[PlayerMetaManager] Set as: " + opm.get().getSpigotDisplayname());

					player.setDisplayName(opm.get().getSpigotDisplayname());

				}

			} else {

				if (ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().contains(ConfigValues.Config.DISPLAY_NAME_FORMAT)) {
					//player.setDisplayName(opm.get().getDisplayName(ConfigManager.getInstance().getHandler("config.yml").getConfig().getString("display_name_format")));
					// TODO Maybe new option for "fetch_spigot_displayname"?

					DebugManager.log("[PlayerMetaManager] Set as: " + opm.get().getSpigotDisplayname());

					player.setDisplayName(opm.get().getSpigotDisplayname());
				} else {
					//player.setDisplayName(opm.get().getDisplayName("%PREFIX%%NICK%%SUFFIX%"));

					DebugManager.log("[PlayerMetaManager] Set as: " + opm.get().getSpigotDisplayname());

					player.setDisplayName(opm.get().getSpigotDisplayname());
				}
			}

		}

	}

}
