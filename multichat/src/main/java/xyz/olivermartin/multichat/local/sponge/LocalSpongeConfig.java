package xyz.olivermartin.multichat.local.sponge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;
import xyz.olivermartin.multichat.local.common.config.LocalConfig;
import xyz.olivermartin.multichat.local.common.config.LocalConfigStatus;
import xyz.olivermartin.multichat.local.common.config.RegexChannelForcer;

public class LocalSpongeConfig extends LocalConfig {

	private ConfigurationNode config;

	public LocalSpongeConfig(File configPath, String fileName) {
		super(configPath, fileName, MultiChatLocalPlatform.SPONGE);
	}

	@Override
	protected LocalConfigStatus loadConfig() {

		ConfigurationLoader<ConfigurationNode> loader =
				YAMLConfigurationLoader.builder().setFile(getFile()).build();

		try {
			this.config = loader.load();
			return LocalConfigStatus.LOADED;
		} catch (IOException e) {
			return LocalConfigStatus.FAILED;
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

	@SuppressWarnings({ "serial", "rawtypes" })
	@Override
	protected List<RegexChannelForcer> getRegexChannelForcers(String rootConfigNode) {

		List<RegexChannelForcer> regexChannelForcers = new ArrayList<RegexChannelForcer>();

		if (!config.getNode(rootConfigNode).isVirtual()) {

			List configData;

			try {

				configData = config.getNode(rootConfigNode).getList(new TypeToken<Map>() { /* EMPTY */ });

				for (Object configItem : configData) {

					Map dictionary = (Map) configItem;

					String regex = String.valueOf(dictionary.get("regex"));
					boolean ignoreFormatCodes = (Boolean)(dictionary.get("ignore_format_codes"));
					String channel = String.valueOf(dictionary.get("channel"));

					RegexChannelForcer regexChannelForcer = new RegexChannelForcer(regex, ignoreFormatCodes, channel);
					regexChannelForcers.add(regexChannelForcer);

				}

			} catch (ObjectMappingException e) {

				/* EMPTY */
				MultiChatLocal.getInstance().getConsoleLogger().debug("Could not load RegexChannelForcers...");

			}

		}

		return regexChannelForcers;

	}

}
