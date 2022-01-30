package xyz.olivermartin.multichat.velocity;


import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

/**
 * Configuration Handler Class
 * <p>Manages loading / creation of an individual configuration file</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class ConfigHandler {

    // The config file
    private ConfigurationNode config;
    // Path of config file
    private final File configPath;
    // Name of config file
    private final String fileName;

    public ConfigHandler(File configPath, String fileName) {

        this.configPath = configPath;
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

            File file = new File(configPath, fileName);

            if (!file.exists()) {

                MultiChat.getInstance().getLogger().info("Config file " + fileName + " not found... Creating new one.");
                saveDefaultConfig();

                loadConfig();

            } else {

                MultiChat.getInstance().getLogger().info("Loading " + fileName + "...");
                loadConfig();

            }

        } catch (Exception e) {
            MultiChat.getInstance().getLogger().info("[ERROR] Could not load  " + fileName);
            e.printStackTrace();
        }
    }

    private void saveDefaultConfig() {

        // Load default file into input stream
        // Copy to desired location
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            Files.copy(Objects.requireNonNull(inputStream), new File(configPath, fileName).toPath());
        } catch (IOException | NullPointerException e) {
            MultiChat.getInstance().getLogger().info("[ERROR] Could not create new " + fileName + " file...");
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        try {
            this.config = YAMLConfigurationLoader.builder().setFile(new File(configPath, fileName)).build().load();
        } catch (IOException e) {
            MultiChat.getInstance().getLogger().info("[ERROR] Could not load " + fileName + " file...");
            e.printStackTrace();
        }
    }
}
