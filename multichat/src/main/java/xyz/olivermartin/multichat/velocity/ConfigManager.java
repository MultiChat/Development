package xyz.olivermartin.multichat.velocity;


import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Configuration Manager Class
 * <p>Manages all access and creation of the config.yml file</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class ConfigManager {

    private static final ConfigManager instance;

    static {

        instance = new ConfigManager();

    }

    public static ConfigManager getInstance() {
        return instance;
    }

    // END OF STATIC

    private final Map<String, ConfigHandler> handlerMap;

    private ConfigManager() {

        handlerMap = new HashMap<String, ConfigHandler>();

    }

    /**
     * Create a new configHandler for a given filename and path
     *
     * @param fileName   filename i.e. config.yml
     * @param configPath THE PATH WITHOUT THE FILE NAME
     */
    public void registerHandler(String fileName, File configPath) {

        handlerMap.put(fileName, new ConfigHandler(configPath, fileName));

    }

    public Optional<ConfigHandler> getSafeHandler(String fileName) {

        if (handlerMap.containsKey(fileName)) {
            return Optional.of(handlerMap.get(fileName));
        }

        return Optional.empty();

    }

    public ConfigHandler getHandler(String fileName) {

        if (handlerMap.containsKey(fileName)) {
            return handlerMap.get(fileName);
        }

        return null;

    }

}
