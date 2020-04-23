package xyz.olivermartin.multichat.proxy.bungee.config;

import java.util.List;

import net.md_5.bungee.config.Configuration;
import xyz.olivermartin.multichat.proxy.common.config.MultiChatConfig;

public class MultiChatBungeeConfig implements MultiChatConfig {

	private Configuration config;

	public MultiChatBungeeConfig(Configuration config) {
		this.config = config;
	}

	@Override
	public String getString(String node) {
		return config.getString(node);
	}

	@Override
	public boolean getBoolean(String node) {
		return config.getBoolean(node);
	}

	@Override
	public int getInteger(String node) {
		return config.getInt(node);
	}

	@Override
	public List<String> getStringList(String node) {
		return config.getStringList(node);
	}

	@Override
	public boolean containsNode(String node) {
		return config.contains(node);
	}

	@Override
	public String getString(String node, String defaultValue) {
		return config.getString(node, defaultValue);
	}

	@Override
	public boolean getBoolean(String node, boolean defaultValue) {
		return config.getBoolean(node, defaultValue);
	}

	@Override
	public int getInteger(String node, int defaultValue) {
		return config.getInt(node, defaultValue);
	}

}
