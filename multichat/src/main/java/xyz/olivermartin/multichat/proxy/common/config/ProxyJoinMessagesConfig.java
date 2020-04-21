package xyz.olivermartin.multichat.proxy.common.config;

import java.io.File;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;

public abstract class ProxyJoinMessagesConfig extends ProxyConfig {

	// TODO ADD ATTRIBUTES

	public ProxyJoinMessagesConfig(File configPath, String fileName, MultiChatProxyPlatform platform) {
		super(configPath, fileName, platform);
	}

	@Override
	protected void setMemberAttributes() {

		// TODO E.g

		// GENERAL
		//fetchSpigotDisplayNames = getBoolean("fetch_spigot_display_names", true);
		//setDisplayName = getBoolean("set_display_name", true);
		//displayNameFormat = getString("display_name_format", "%PREFIX%%NICK%%SUFFIX%");

		// PMs
		//usePrivateMessaging = getBoolean("pm", true);
		//noPrivateMessaging = getStringList("no_pm");

	}

	// TODO Add Getters and Setters

}
