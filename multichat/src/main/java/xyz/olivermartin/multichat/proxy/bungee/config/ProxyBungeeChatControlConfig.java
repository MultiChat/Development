package xyz.olivermartin.multichat.proxy.bungee.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import xyz.olivermartin.multichat.common.config.ConfigStatus;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;
import xyz.olivermartin.multichat.proxy.common.RegexAction;
import xyz.olivermartin.multichat.proxy.common.RegexRule;
import xyz.olivermartin.multichat.proxy.common.config.ProxyChatControlConfig;

public class ProxyBungeeChatControlConfig extends ProxyChatControlConfig {

	private Configuration config;

	public ProxyBungeeChatControlConfig(File configPath, String fileName) {
		super(configPath, fileName, MultiChatProxyPlatform.BUNGEE);
	}

	public Configuration getConfig() {
		return this.config;
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

	@SuppressWarnings("rawtypes")
	@Override
	protected List<RegexRule> getRegexRulesFromConfig(String node) {

		List<RegexRule> resultingRules = new ArrayList<RegexRule>();
		List configRules = config.getList(node);

		if (configRules != null) {
			for (Object rule : configRules) {
				Map dictionary = (Map) rule;
				RegexRule r = new RegexRule(String.valueOf( dictionary.get("look_for")), String.valueOf(dictionary.get("replace_with") ));
				resultingRules.add(r);
			}
		}

		return resultingRules;

	}

	@SuppressWarnings("rawtypes")
	@Override
	protected List<RegexAction> getRegexActionsFromConfig(String node) {

		List<RegexAction> resultingActions = new ArrayList<RegexAction>();
		List configActions = config.getList(node);

		if (configActions != null) {
			for (Object action : configActions) {
				Map dictionary = (Map) action;
				RegexAction a = new RegexAction(String.valueOf( dictionary.get("look_for")), String.valueOf(dictionary.get("command")), (Boolean)( dictionary.get("cancel")),(Boolean)( dictionary.get("local")));
				resultingActions.add(a);
			}
		}

		return resultingActions;

	}

}
