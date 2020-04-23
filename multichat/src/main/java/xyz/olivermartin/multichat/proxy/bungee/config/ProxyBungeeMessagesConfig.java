package xyz.olivermartin.multichat.proxy.bungee.config;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import xyz.olivermartin.multichat.common.config.ConfigStatus;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;
import xyz.olivermartin.multichat.proxy.common.config.MultiChatConfig;
import xyz.olivermartin.multichat.proxy.common.config.ProxyMessagesConfig;

public class ProxyBungeeMessagesConfig extends ProxyMessagesConfig {

	private Configuration config;

	public ProxyBungeeMessagesConfig(File configPath, String fileName) {
		super(configPath, fileName, MultiChatProxyPlatform.BUNGEE);
	}

	public Configuration getConfig() {
		return this.config;
	}

	@Override
	protected MultiChatConfig getMultiChatConfigFromConfig() {
		return new MultiChatBungeeConfig(config);
	}

	@Override
	protected ConfigStatus loadConfig() {
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(getFile());
			return ConfigStatus.LOADED;
		} catch (IOException e) {
			return ConfigStatus.FAILED;
		}
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

}
