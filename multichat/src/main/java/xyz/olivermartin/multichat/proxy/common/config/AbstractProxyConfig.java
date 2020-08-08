package xyz.olivermartin.multichat.proxy.common.config;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * Class that represents the base of a config that can be handled by MultiChat
 * <p>
 * Manages creation and reloads
 *
 * @author Oliver Martin (Revilo410)
 */
public abstract class AbstractProxyConfig {

    private final String fileName;

    AbstractProxyConfig(String fileName) {
        this.fileName = fileName;
    }

    private Configuration config;

    public final void reloadConfig(Plugin plugin) {
        reloadConfig(plugin, plugin.getDataFolder());
    }

    public final void reloadConfig(Plugin plugin, File folder) {
        File configFile = new File(folder, fileName);
        if (!configFile.exists()) {
            plugin.getLogger().info("Creating " + fileName + " ...");
            InputStream in = plugin.getResourceAsStream(fileName);
            try {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        plugin.getLogger().info("Loading " + fileName + " ...");
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            // TODO: We can probably add some config update code here
            reloadValues();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void reloadValues() {
    }

    public final String getFileName() {
        return fileName;
    }

    public final Configuration getConfig() {
        if (config == null)
            throw new IllegalStateException("Configuration has not been loaded yet. This should NOT happen! " +
                    "Check the server startup for errors.");
        return config;
    }
}
