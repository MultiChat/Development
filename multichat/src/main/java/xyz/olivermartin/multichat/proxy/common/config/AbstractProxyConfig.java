package xyz.olivermartin.multichat.proxy.common.config;


import java.io.File;
import java.io.IOException;

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

    private Plugin plugin;
    private File folder;
    private Configuration config;

    public final void reloadConfig() {
        if (plugin == null)
            throw new IllegalArgumentException("You have not created this config with the plugin constructor yet.");
        reloadConfig(plugin, folder == null ? plugin.getDataFolder() : folder);
    }

    public final void reloadConfig(Plugin plugin) {
        reloadConfig(plugin, plugin.getDataFolder());
    }

    public final void reloadConfig(Plugin plugin, File folder) {
        this.plugin = plugin;
        this.folder = folder;

        ProxyConfigUpdater configUpdater = new ProxyConfigUpdater(plugin, folder, fileName);
        plugin.getLogger().info("Loading " + fileName + " ...");
        configUpdater.update();

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configUpdater.getConfigFile());
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
