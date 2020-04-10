package xyz.olivermartin.multichat.local.sponge;

import java.io.File;
import java.io.IOException;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import xyz.olivermartin.multichat.local.LocalConfig;
import xyz.olivermartin.multichat.local.LocalConfigStatus;
import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;

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
	protected void setMemberAttributes() {
		showNicknamePrefix = config.getNode("show_nickname_prefix").getBoolean(false);
		nicknamePrefix = config.getNode("nickname_prefix").getString("~");
		serverName = config.getNode("server_name").getString("SPONGE_SERVER");
	}

}
