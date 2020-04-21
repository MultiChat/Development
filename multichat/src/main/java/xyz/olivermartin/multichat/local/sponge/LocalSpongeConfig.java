package xyz.olivermartin.multichat.local.sponge;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import xyz.olivermartin.multichat.common.config.ConfigStatus;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.common.config.LocalConfig;

public class LocalSpongeConfig extends LocalConfig {

	private ConfigurationNode config;

	public LocalSpongeConfig(File configPath, String fileName) {
		super(configPath, fileName, MultiChatLocalPlatform.SPONGE);
	}

	@Override
	protected ConfigStatus loadConfig() {

		ConfigurationLoader<ConfigurationNode> loader =
				YAMLConfigurationLoader.builder().setFile(getFile()).build();

		try {
			this.config = loader.load();
			return ConfigStatus.LOADED;
		} catch (IOException e) {
			return ConfigStatus.FAILED;
		}

	}

	@Override
	protected String getString(String configNode, String defaultValue) {
		return config.getNode(configNode).getString(defaultValue);
	}

	@Override
	protected boolean getBoolean(String configNode, boolean defaultValue) {
		return config.getNode(configNode).getBoolean(defaultValue);
	}

	@Override
	protected int getInt(String configNode, int defaultValue) {
		return config.getNode(configNode).getInt(defaultValue);
	}

	@Override
	protected List<String> getStringList(String configNode) {
		return config.getNode(configNode).getList(value -> value.toString());
	}

	@Override
	protected Map<String, String> getPlaceholdersMap(String rootConfigNode) {
		Map<String,String> map = new HashMap<String,String>();
		return map; // TODO This feature is currently unused in Sponge.
	}

}
