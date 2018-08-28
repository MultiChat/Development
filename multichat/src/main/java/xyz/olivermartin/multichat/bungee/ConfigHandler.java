package xyz.olivermartin.multichat.bungee;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.Optional;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * Configuration Handler Class
 * <p>Manages loading / creation of an individual configuration file</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ConfigHandler {

	// The config file
	private Configuration config;
	// Path of config file
	private String configPath;

	/**
	 * 
	 * @param configPath Must have the path to the folder, i.e. MultiChat.ConfigDir AND the file name like "config.yml"
	 */
	public ConfigHandler(String configPath) {

		this.configPath = configPath;
		this.config = null;
		this.startupConfig();

	}

	public Optional<Configuration> getConfig() {
		if (config == null) return Optional.empty();
		return Optional.of(config);
	}

	public void startupConfig() {

		try {

			File file = new File(configPath);

			if (!file.exists()) {

				System.out.println("[MultiChat] File config.yml not found... Creating new one.");
				saveDefaultConfig();

				loadConfig();

			} else {

				System.out.println("[MultiChat] Loading config.yml...");
				loadConfig();

			}

		} catch (Exception e) {
			System.out.println("[MultiChat] [ERROR] Could not load config.yml");
			e.printStackTrace();
		}
	}

	private void saveDefaultConfig() {

		// Load default file into input stream
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.yml");

		// Copy to desired location
		try {
			Files.copy(inputStream, new File(configPath).toPath(), new CopyOption[0]);
		} catch (IOException e) {
			System.err.println("[MultiChat] [ERROR] Could not create new config.yml file...");
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// OLD WAY BELOW:

		//		try {
		//
		//			InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml");
		//			Throwable localThrowable3 = null;
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
	}

	private void loadConfig() {

		try {

			this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(configPath));

		} catch (IOException e) {

			System.err.println("[MultiChat] [ERROR] Could not load config.yml file...");
			e.printStackTrace();

		}
	}
}
