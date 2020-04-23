package xyz.olivermartin.multichat.proxy.common.config;

import java.io.File;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;

public abstract class ProxyMessagesConfig extends ProxyConfig {

	private MultiChatConfig mcConfig;

	public ProxyMessagesConfig(File configPath, String fileName, MultiChatProxyPlatform platform) {
		super(configPath, fileName, platform);
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
	}

	protected abstract MultiChatConfig getMultiChatConfigFromConfig();

}
