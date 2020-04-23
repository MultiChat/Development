package xyz.olivermartin.multichat.proxy.common.config;

import java.io.File;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;

public abstract class ProxyMessagesConfig extends ProxyConfig {

	private MultiChatConfig mcConfig;
	private String version;

	public ProxyMessagesConfig(File configPath, String fileName, MultiChatProxyPlatform platform) {
		super(configPath, fileName, platform);
	}

	@Override
	public String getVersion() {
		if (version == null) throw new IllegalStateException("No version number found in MultiChatProxy's Messages Config!");
		return version;
	}

	public MultiChatConfig getMultiChatConfig() {
		return mcConfig;
	}

	/**
	 * This config file does not use member attributes.
	 */
	@Override
	protected void setMemberAttributes() {
		mcConfig = getMultiChatConfigFromConfig();
		version = getString("version", null);
	}

	protected abstract MultiChatConfig getMultiChatConfigFromConfig();

}
