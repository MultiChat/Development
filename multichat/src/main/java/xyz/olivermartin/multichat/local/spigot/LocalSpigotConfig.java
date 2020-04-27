package xyz.olivermartin.multichat.local.spigot;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.common.config.LocalConfig;
import xyz.olivermartin.multichat.local.common.config.LocalConfigStatus;
import xyz.olivermartin.multichat.local.common.config.RegexChannelForcer;

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

	@SuppressWarnings("rawtypes")
	@Override
	protected List<RegexChannelForcer> getRegexChannelForcers(String rootConfigNode) {

		List<RegexChannelForcer> regexChannelForcers = new ArrayList<RegexChannelForcer>();
		List configData = config.getList(rootConfigNode);

		if (configData != null) {
			for (Object configItem : configData) {

				Map dictionary = (Map) configItem;

				String regex = String.valueOf(dictionary.get("regex"));
				boolean ignoreFormatCodes = (Boolean)(dictionary.get("ignore_format_codes"));
				String channel = String.valueOf(dictionary.get("channel"));

				RegexChannelForcer regexChannelForcer = new RegexChannelForcer(regex, ignoreFormatCodes, channel);
				regexChannelForcers.add(regexChannelForcer);

			}
		}

		return regexChannelForcers;

	}

}
