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

    /**
     * Prepare an abstract config.
     * You have to call {@link #setPlugin(Plugin)} to make {@link #reloadConfig()} work.
     * You can call {@link #setDataFolder(File)} to change the folder under which the plugin will be saved.
     *
     * @param fileName The name of the file, needs
     */
    AbstractProxyConfig(String fileName) {
        if (!fileName.endsWith(".yml"))
            throw new IllegalArgumentException("Filename did not end with .yml");

        this.fileName = fileName;
    }

    private Plugin plugin;
    private File dataFolder, configFile;
    private Configuration config;

    /**
     * Set the plugin that owns the configuration file
     *
     * @param plugin the BungeeCord plugin
     * @throws IllegalArgumentException if the plugin has already been set
     */
    public final void setPlugin(Plugin plugin) throws IllegalArgumentException {
        if (this.plugin != null)
            throw new IllegalArgumentException("You can not change the plugin after setting it once.");

        this.plugin = plugin;
    }

    /**
     * Set the folder under which the file should be
     *
     * @param dataFolder the folder
     * @throws IllegalArgumentException if the data folder has already been set
     */
    public final void setDataFolder(File dataFolder) throws IllegalArgumentException {
        if (this.dataFolder != null)
            throw new IllegalArgumentException("You can not change the data folder after setting it once.");

        this.dataFolder = dataFolder;
    }

    /**
     * Reload the config.
     * <p>
     * This method can only be called if  {@link #setPlugin(Plugin)} has been called.
     * Data folder will be the plugin folder unless first set with with {@link #setDataFolder(File)}.
     *
     * @throws IllegalArgumentException if the plugin has not been set yet.
     */
    public final void reloadConfig() throws IllegalArgumentException {
        if (plugin == null)
            throw new IllegalArgumentException("You have not set the plugin yet.");

        if (this.configFile == null)
            this.configFile = new File(getDataFolder(), fileName);

        plugin.getLogger().info("Loading " + fileName + " ...");
        ProxyConfigUpdater configUpdater = new ProxyConfigUpdater(this);
        configUpdater.update();

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            reloadValues();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that can be overridden to define what should happen after configuration has been reloaded.
     */
    void reloadValues() {
    }

    /**
     * Convenience method to get the plugin. Same-Package only.
     *
     * @return the data folder.
     * @throws IllegalStateException if the plugin has not been set
     */
    Plugin getPlugin() {
        if (plugin == null)
            throw new IllegalStateException("Method has been called without defining the plugin first.");

        return plugin;
    }

    /**
     * Convenience method to get the file name.
     *
     * @return the file name.
     */
    public final String getFileName() {
        return fileName;
    }

    /**
     * Convenience method to get the data folder.
     *
     * @return the data folder.
     * @throws IllegalStateException if the plugin has not been set
     */
    public final File getDataFolder() throws IllegalStateException {
        if (plugin == null)
            throw new IllegalStateException("Method has been called without defining the plugin first.");

        if (dataFolder == null)
            dataFolder = plugin.getDataFolder();

        return dataFolder;
    }

    /**
     * Convenience method to get the config's file.
     *
     * @return the config's file.
     * @throws IllegalStateException if the plugin has not been set
     */
    public final File getConfigFile() throws IllegalStateException {
        if (plugin == null)
            throw new IllegalStateException("Method has been called without defining the plugin first.");

        return configFile;
    }

    /**
     * Convenience method to get the bungee configuration.
     *
     * @return the config.
     */
    public final Configuration getConfig() {
        if (config == null)
            throw new IllegalStateException("Configuration has not been loaded yet. This should NOT happen! " +
                    "Check the server startup for errors.");
        return config;
    }
}
