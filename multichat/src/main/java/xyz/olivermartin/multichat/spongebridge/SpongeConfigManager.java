package xyz.olivermartin.multichat.spongebridge;


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
public class SpongeConfigManager {

	private static SpongeConfigManager instance;

	static {

		instance = new SpongeConfigManager();

	}

	public static SpongeConfigManager getInstance() {
		return instance;
	}

	// END OF STATIC

	private Map<String,SpongeConfigHandler> handlerMap;

	private SpongeConfigManager() {

		handlerMap = new HashMap<String,SpongeConfigHandler>();

	}

	/**
	 * Create a new configHandler for a given filename and path
	 * @param fileName filename i.e. config.yml
	 * @param configPath THE PATH WITHOUT THE FILE NAME
	 */
	public void registerHandler(String fileName, File configPath) {

		handlerMap.put(fileName, new SpongeConfigHandler(configPath, fileName));

	}

	public Optional<SpongeConfigHandler> getSafeHandler(String fileName) {

		if (handlerMap.containsKey(fileName)) {
			return Optional.of(handlerMap.get(fileName));
		}

		return Optional.empty();

	}

	public SpongeConfigHandler getHandler(String fileName) {

		if (handlerMap.containsKey(fileName)) {
			return handlerMap.get(fileName);
		}

		return null;

	}

}
