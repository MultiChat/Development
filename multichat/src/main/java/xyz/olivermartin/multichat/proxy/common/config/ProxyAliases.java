package xyz.olivermartin.multichat.proxy.common.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to represent the proxy's aliases.yml.
 */
public class ProxyAliases extends AbstractProxyConfig {

    private final Map<String, String[]> commandAliases = new HashMap<>();

    ProxyAliases() {
        super("aliases.yml");
    }

    @Override
    void reloadValues() {
        commandAliases.clear();

        getConfig().getKeys().forEach(key ->
                commandAliases.put(key, getConfig().getStringList(key).toArray(new String[0]))
        );
    }

    /**
     * Get all aliases to a certain command.
     *
     * @param command The base name of the command
     * @return the array of defined aliases, empty if command does not exist
     */
    public String[] getAliases(String command) {
        return commandAliases.getOrDefault(command, new String[0]);
    }
}
