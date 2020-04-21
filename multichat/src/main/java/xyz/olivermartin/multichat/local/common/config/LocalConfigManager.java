package xyz.olivermartin.multichat.local.common.config;

import java.io.File;

import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.spigot.LocalSpigotConfig;
import xyz.olivermartin.multichat.local.sponge.LocalSpongeConfig;

/**
 * MultiChatLocal's Config Manager
 * 
 * <p>Manages all access to configuration files etc.</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class LocalConfigManager {

	private LocalConfig localConfig;
	// Room to example by having: LocalJoinMessagesConfigHandler localJoinMessagesConfig;
	// etc.

	private MultiChatLocalPlatform platform;

	public LocalConfigManager(MultiChatLocalPlatform platform) { 
		this.platform = platform;
	}

	public MultiChatLocalPlatform getPlatform() {
		return this.platform;
	}

	/**
	 * Register the local config file with the Local Config Manager
	 * 
	 * <p>Should be registered in onEnable()</p>
	 * 
	 * @param platform The platform MultiChatLocal is using (Spigot, Sponge etc.)
	 * @param fileName filename i.e. config.yml
	 * @param configPath THE PATH WITHOUT THE FILE NAME
	 */
	public void registerLocalConfig(String fileName, File configPath) {

		switch (platform) {
		case SPIGOT:
			localConfig = new LocalSpigotConfig(configPath, fileName);
			break;
		case SPONGE:
			localConfig = new LocalSpongeConfig(configPath, fileName);
			break;
		default:
			throw new IllegalArgumentException("Could not register config because this type of platform (" + platform.toString() + ") is not allowed.");

		}

	}

	public LocalConfig getLocalConfig() {

		if (localConfig == null) throw new IllegalStateException("No local config has been registered");
		return localConfig;

	}

}
