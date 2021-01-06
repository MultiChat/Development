package xyz.olivermartin.multichat.proxy.common.config;

import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.*;

/**
 * Utility class to access proxy configs.
 */
public class ProxyConfigs {

    private ProxyConfigs() {
    }

    /**
     * Used to access the proxy's config.yml.
     */
    public static final ProxyConfig CONFIG = new ProxyConfig();

    /**
     * Used to access the proxy's aliases.yml.
     */
    public static final ProxyAliases ALIASES = new ProxyAliases();

    /**
     * Used to access the proxy's chatcontrol.yml.
     */
    public static final ProxyChatControl CHAT_CONTROL = new ProxyChatControl();

    /**
     * Used to access the proxy's messages.yml.
     */
    public static final ProxyMessages MESSAGES = new ProxyMessages();

    /**
     * Used to access the proxy's joinmessages.yml.
     */
    public static final ProxyJoinMessages JOIN_MESSAGES = new ProxyJoinMessages();

    /**
     * List of all configurations that are managed by the plugin and have pre-defined values.
     */
    public static final List<AbstractProxyConfig> ALL = Arrays.asList(CONFIG, ALIASES, CHAT_CONTROL, MESSAGES, JOIN_MESSAGES);

    /**
     * List of all configurations that are saved in the plugin jar and don't have pre-defined values.
     */
    public static final Set<AbstractProxyConfig> RAW_CONFIGS = new HashSet<>();

    /**
     * Loads or reloads and gets the {@link AbstractProxyConfig} of a .yml file inside a plugin's resources.
     * Calling reloadValues on the config will do nothing, you will have to make your own implementation.
     * <p>
     * Currently only used by french translations in MultiChat.
     *
     * @param plugin   the plugin of which the resources should be loaded
     * @param fileName the name of the file that ends with .yml
     * @return the {@link AbstractProxyConfig} of that file
     */
    public static AbstractProxyConfig loadRawConfig(Plugin plugin, String fileName) {
        return loadRawConfig(plugin, fileName, plugin.getDataFolder());
    }

    /**
     * Loads or reloads and gets the {@link AbstractProxyConfig} of a .yml file inside a plugin's resources.
     * Calling reloadValues on the config will do nothing, you will have to make your own implementation.
     * <p>
     * Currently only used by french translations in MultiChat.
     *
     * @param plugin   the plugin of which the resources should be loaded
     * @param fileName the name of the file that ends with .yml
     * @param dataFolder the path where the file should end up
     * @return the {@link AbstractProxyConfig} of that file
     */
    public static AbstractProxyConfig loadRawConfig(Plugin plugin, String fileName, File dataFolder) {
        if (!fileName.endsWith(".yml"))
            throw new IllegalArgumentException("File name did not end with .yml");

        AbstractProxyConfig rawConfig = RAW_CONFIGS.stream()
                .filter(abstractProxyConfig -> abstractProxyConfig.getFileName().equals(fileName))
                .findFirst()
                .orElse(null);
        if (rawConfig == null) {
            rawConfig = new AbstractProxyConfig(fileName) {};
            rawConfig.setPlugin(plugin);
            rawConfig.setDataFolder(dataFolder);
        } else
            RAW_CONFIGS.remove(rawConfig);
        rawConfig.reloadConfig();
        RAW_CONFIGS.add(rawConfig);

        return rawConfig;
    }
}
