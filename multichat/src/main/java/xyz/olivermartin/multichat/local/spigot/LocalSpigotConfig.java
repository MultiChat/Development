package xyz.olivermartin.multichat.local.spigot;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import xyz.olivermartin.multichat.common.config.ConfigStatus;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.common.config.LocalConfig;

public class LocalSpigotConfig extends LocalConfig {

	private Configuration config;

	public LocalSpigotConfig(File configPath, String fileName) {
		super(configPath, fileName, MultiChatLocalPlatform.SPIGOT);
	}

	public Configuration getConfig() {
		return this.config;
	}

	@Override
	protected ConfigStatus loadConfig() {
		this.config = YamlConfiguration.loadConfiguration(getFile());
		return ConfigStatus.LOADED;
	}

	@Override
	protected String getString(String configNode, String defaultValue) {
		return config.getString(configNode, defaultValue);
	}

	@Override
	protected boolean getBoolean(String configNode, boolean defaultValue) {
		return config.getBoolean(configNode, defaultValue);
	}

	@Override
	protected int getInt(String configNode, int defaultValue) {
		return config.getInt(configNode, defaultValue);
	}

	@Override
	protected List<String> getStringList(String configNode) {
		return config.getStringList(configNode);
	}

	@Override
	protected Map<String, String> getPlaceholdersMap(String rootConfigNode) {
		Map<String,String> map = new HashMap<String,String>();
		ConfigurationSection placeholders = config.getConfigurationSection(rootConfigNode);
		if (placeholders != null) {
			for (String placeholder : placeholders.getKeys(false)) {
				map.put("{multichat_" + placeholder + "}", placeholders.getString(placeholder));
			}

		}
		return map;
	}

}
