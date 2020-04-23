package xyz.olivermartin.multichat.proxy.common.config;

import java.util.List;

public interface MultiChatConfig {
	
	public boolean containsNode(String node);
	
	public String getString(String node);
	
	public String getString(String node, String defaultValue);
	
	public boolean getBoolean(String node);
	
	public boolean getBoolean(String node, boolean defaultValue);
	
	public int getInteger(String node);
	
	public int getInteger(String node, int defaultValue);
	
	public List<String> getStringList(String node);
	
}
