package xyz.olivermartin.multichat.local.spigot;

import java.io.File;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import xyz.olivermartin.multichat.local.LocalConfig;
import xyz.olivermartin.multichat.local.LocalConfigStatus;
import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;

public class LocalSpigotConfig extends LocalConfig {

	private Configuration config;

	public LocalSpigotConfig(File configPath, String fileName) {
		super(configPath, fileName, MultiChatLocalPlatform.SPIGOT);
	}

	public Configuration getConfig() {
		return this.config;
	}

	@Override
	protected LocalConfigStatus loadConfig() {
		this.config = YamlConfiguration.loadConfiguration(getFile());
		return LocalConfigStatus.LOADED;
	}

	@Override
	protected void setMemberAttributes() {
		showNicknamePrefix = config.getBoolean("show_nickname_prefix",false);
		nicknamePrefix = config.getString("nickname_prefix","~");
	}

}
