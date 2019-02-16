package xyz.olivermartin.multichat.spigotbridge;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Configuration Manager Class
 * <p>Manages all access and creation of the config.yml file</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class SpigotConfigManager {

	private static SpigotConfigManager instance;

	static {

		instance = new SpigotConfigManager();

	}

	public static SpigotConfigManager getInstance() {
		return instance;
	}

	// END OF STATIC

	private Map<String,SpigotConfigHandler> handlerMap;

	private SpigotConfigManager() {

		handlerMap = new HashMap<String,SpigotConfigHandler>();

	}

	/**
	 * Create a new configHandler for a given filename and path
	 * @param fileName filename i.e. config.yml
	 * @param configPath THE PATH WITHOUT THE FILE NAME
	 */
	public void registerHandler(String fileName, File configPath) {

		handlerMap.put(fileName, new SpigotConfigHandler(configPath, fileName));

	}

	public Optional<SpigotConfigHandler> getSafeHandler(String fileName) {

		if (handlerMap.containsKey(fileName)) {
			return Optional.of(handlerMap.get(fileName));
		}

		return Optional.empty();

	}

	public SpigotConfigHandler getHandler(String fileName) {

		if (handlerMap.containsKey(fileName)) {
			return handlerMap.get(fileName);
		}

		return null;

	}

}
