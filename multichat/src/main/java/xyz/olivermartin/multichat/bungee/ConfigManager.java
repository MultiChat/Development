package xyz.olivermartin.multichat.bungee;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;

/**
 * Configuration Manager Class
 * <p>Manages all access and creation of the config.yml file</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ConfigManager {

	private static ConfigManager instance;

	static {

		instance = new ConfigManager();

	}

	public static ConfigManager getInstance() {
		return instance;
	}

	// END OF STATIC

	private Map<String, ConfigHandler> handlerMap;

	private ConfigManager() {

		handlerMap = new HashMap<String, ConfigHandler>();

	}

	/**
	 * Create a new configHandler for a given filename and path
	 * @param configFile the config file i.e. config.yml
	 * @param configPath THE PATH WITHOUT THE FILE NAME
	 */
	public void registerHandler(ConfigFile configFile, File configPath) {

		handlerMap.put(configFile.getFileName(), new ConfigHandler(configPath, configFile.getFileName()));

	}

	/**
	 * Create a new configHandler for a given RAW filename and path
	 * (Not recommended, should use registerHandler() instead)
	 * @param fileName filename i.e. config.yml
	 * @param configPath THE PATH WITHOUT THE FILE NAME
	 */
	public void registerRawHandler(String fileName, File configPath) {

		handlerMap.put(fileName, new ConfigHandler(configPath, fileName));

	}

	public Optional<ConfigHandler> getSafeHandler(ConfigFile configFile) {

		if (handlerMap.containsKey(configFile.getFileName())) {
			return Optional.of(handlerMap.get(configFile.getFileName()));
		}

		return Optional.empty();

	}

	public ConfigHandler getHandler(ConfigFile configFile) {

		if (handlerMap.containsKey(configFile.getFileName())) {
			return handlerMap.get(configFile.getFileName());
		}

		return null;

	}
	
	public ConfigHandler getRawHandler(String fileName) {

		if (handlerMap.containsKey(fileName)) {
			return handlerMap.get(fileName);
		}

		return null;

	}

}
