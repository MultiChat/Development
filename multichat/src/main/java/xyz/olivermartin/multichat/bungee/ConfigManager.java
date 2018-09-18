package xyz.olivermartin.multichat.bungee;


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
public class ConfigManager {

	private static ConfigManager instance;

	static {

		instance = new ConfigManager();

	}

	public static ConfigManager getInstance() {
		return instance;
	}

	// END OF STATIC

	private Map<String,ConfigHandler> handlerMap;

	// NEW AREA //

	private ConfigManager() {

		// OLD V
		// config = null;
		// OLD ^

		handlerMap = new HashMap<String,ConfigHandler>();

	}

	/**
	 * Create a new configHandler for a given filename and path
	 * @param fileName filename i.e. config.yml
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

	// OLD AREA //

	//
	//
	//	private Configuration config;
	//
	//	public Configuration getConfig() {
	//		return config;
	//	}
	//
	//	public String getString(String path) {
	//		return config.getString(path);
	//	}
	//
	//	public Boolean getBoolean(String path) {
	//		return config.getBoolean(path);
	//	}
	//
	//	public Integer getInt(String path) {
	//		return config.getInt(path);
	//	}
	//
	//	public List<String> getStringList(String path) {
	//		return config.getStringList(path);
	//	}
	//
	//	public void startupConfig() {
	//
	//		try {
	//
	//			File file = new File(MultiChat.ConfigDir, "config.yml");
	//
	//			if (!file.exists()) {
	//
	//				System.out.println("[MultiChat] Config.yml not found, creating!");
	//
	//				saveDefaultConfig();
	//				loadConfig();
	//
	//			} else {
	//				System.out.println("[MultiChat] Config.yml already exists, loading!");
	//				loadConfig();
	//			}
	//
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//	}
	//
	//	private void saveDefaultConfig() {
	//
	//		try {
	//
	//			InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml");Throwable localThrowable3 = null;
	//
	//			try {
	//
	//				Files.copy(in, new File(MultiChat.ConfigDir, "config.yml").toPath(), new CopyOption[0]);
	//
	//			} catch (Throwable localThrowable1) {
	//
	//				localThrowable3 = localThrowable1;throw localThrowable1;
	//
	//			} finally {
	//
	//				if (in != null) {
	//
	//					if (localThrowable3 != null) {
	//
	//						try	{
	//							in.close();
	//						} catch (Throwable localThrowable2) {
	//							localThrowable3.addSuppressed(localThrowable2);
	//						}
	//
	//					} else {
	//						in.close();
	//					}
	//
	//				}
	//			}
	//
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//	}
	//
	//	private void loadConfig() {
	//
	//		try {
	//			this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(MultiChat.ConfigDir, "config.yml"));
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//	}
}
