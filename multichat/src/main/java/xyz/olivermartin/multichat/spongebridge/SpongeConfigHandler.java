package xyz.olivermartin.multichat.spongebridge;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

/**
 * Configuration Handler Class
 * <p>Manages loading / creation of an individual configuration file</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class SpongeConfigHandler {

	// The config file
	private ConfigurationNode config;
	// Path of config file
	//private File configPath;
	// Name of config file
	private String fileName;

	public SpongeConfigHandler(String fileName) {

		//this.configPath = configPath;
		this.config = null;
		this.fileName = fileName;
		this.startupConfig();

	}

	public ConfigurationNode getConfig() {
		if (config == null) startupConfig();
		return config;
	}

	public void startupConfig() {

		try {

			File file = new File(fileName);

			if (!file.exists()) {

				System.out.println("Config file " + fileName + " not found... Creating new one.");
				saveDefaultConfig();

				loadConfig();

			} else {

				System.out.println("Loading " + fileName + "...");
				loadConfig();

			}

		} catch (Exception e) {
			System.out.println("[ERROR] Could not load  " + fileName);
			e.printStackTrace();
		}
	}

	private void saveDefaultConfig() {

		// Load default file into input stream
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

		// Copy to desired location
		try {
			Files.copy(inputStream, new File(fileName).toPath(), new CopyOption[0]);
		} catch (IOException e) {
			System.out.println("[ERROR] Could not create new " + fileName + " file...");
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void loadConfig() {

		ConfigurationLoader<ConfigurationNode> loader =
				YAMLConfigurationLoader.builder().setFile(new File(fileName)).build();

		try {
			this.config = loader.load();
		} catch (IOException e) {
			System.out.println("[ERROR] Issue loading config file!");
			e.printStackTrace();
		}

	}
}
