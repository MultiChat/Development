package xyz.olivermartin.multichat.proxy.common.config;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ProxyConfigUpdater {

    private final Plugin plugin;
    private final AbstractProxyConfig config;
    private final File backupDir;
    private BufferedWriter writer;
    private final Yaml yaml = new Yaml();

    /**
     * Create an updater for a proxy config. Will not do anything until you call {@link #update()}
     *
     * @param config the config that this updater will update
     */
    public ProxyConfigUpdater(AbstractProxyConfig config) {
        this.config = config;
        this.plugin = config.getPlugin();
        this.backupDir = new File(config.getDataFolder(), "backups");
    }

    /**
     * Update a yaml file from a resource inside your plugin jar, if the new version is higher than the old version.
     * <p>
     * Creates the yaml file if it does not exist.
     */
    public void update() {
        if (!config.getConfigFile().exists()) {
            plugin.getLogger().info("Creating " + config.getFileName() + " ...");
            try {
                Files.copy(plugin.getResourceAsStream(config.getFileName()), config.getConfigFile().toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        Configuration oldConfig;
        try {
            oldConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config.getConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Configuration newConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(
                new InputStreamReader(plugin.getResourceAsStream(config.getFileName()))
        );

        String oldVersionString = oldConfig.getString("version");
        String newVersionString = newConfig.getString("version");
        if (oldVersionString == null) {
            plugin.getLogger().info("Your saved version of " + config.getFileName()
                    + " does not have a version. The auto updater will cancel.");
            return;
        }
        if (newVersionString == null) {
            plugin.getLogger().info("The plugin-stored version of " + config.getFileName()
                    + " does not have a version. Please contact the plugin developer: "
                    + plugin.getDescription().getAuthor());
            return;
        }

        boolean shouldUpdate = false;
        String[] oldVersionStringSplit = oldVersionString.split("\\.");
        String[] newVersionStringSplit = newVersionString.split("\\.");
        for (int i = 0; i < oldVersionStringSplit.length; i++) {
            if (i > newVersionStringSplit.length - 1) break;
            try {
                int oldVersionInt = Integer.parseInt(oldVersionStringSplit[i]);
                int newVersionInt = Integer.parseInt(newVersionStringSplit[i]);

                if (newVersionInt > oldVersionInt) {
                    shouldUpdate = true;
                    break;
                }
            } catch (NumberFormatException ignored) {
                break;
            }
        }

        if (!shouldUpdate) return;

        plugin.getLogger().info("New config version found for " + config.getFileName() + "! Attempting auto update...");

        if (!backupDir.exists() && !backupDir.mkdirs()) {
            plugin.getLogger().severe("The backups directory could not be created.");
            return;
        }

        // Back old config up
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(config.getConfigFile()));
            OutputStream out = new BufferedOutputStream(new FileOutputStream(
                    new File(backupDir, config.getFileName().replace(".yml", oldVersionString + ".yml")))
            );

            byte[] buffer = new byte[1024];
            int lengthRead;
            while ((lengthRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                out.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        oldConfig.set("version", newVersionString);

        try {
            write(oldConfig, newConfig);
            plugin.getLogger().info("Auto update for " + config.getFileName() + " successful! Please check it yourself to verify.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all keys and child keys of a configuration
     * <p>
     * Thanks BungeeCord for not offering this.
     *
     * @param configuration the configuration to get the keys of
     * @param previousKey   the previous key (used for recursion, should be called with "")
     * @return an insertion-ordered set of key names
     */
    private Set<String> getDeepKeys(Configuration configuration, String previousKey) {
        Set<String> output = new LinkedHashSet<>();
        configuration.getKeys().forEach(key -> {
            Object deepKeyObject = configuration.get(key);
            output.add(previousKey + key);
            if (deepKeyObject instanceof Configuration) {
                output.add(previousKey + key);
                Configuration deepKey = (Configuration) deepKeyObject;
                output.addAll(getDeepKeys(deepKey, key + "."));
            }
        });
        return output;
    }

    private void write(Configuration oldConfig, Configuration newConfig) throws IOException {
        BufferedReader resourceBufferedReader = new BufferedReader(
                new InputStreamReader(plugin.getResourceAsStream(config.getFileName()), StandardCharsets.UTF_8)
        );
        Map<String, String> comments = getComments(resourceBufferedReader.lines().collect(Collectors.toList()));
        resourceBufferedReader.close();

        writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(config.getConfigFile()), StandardCharsets.UTF_8)
        );

        for (String key : getDeepKeys(newConfig, "")) {
            String[] currentKeys = key.split("\\.");
            String actualKey = currentKeys[currentKeys.length - 1];
            String comment = comments.remove(key);

            StringBuilder indentBuilder = new StringBuilder();
            for (int i = 0; i < currentKeys.length - 1; i++)
                indentBuilder.append("  ");
            String indent = indentBuilder.toString();

            if (comment != null)
                writer.write(comment);

            Object newObject = newConfig.get(key);
            Object oldObject = oldConfig.get(key);

            if (newObject instanceof Configuration && oldObject instanceof Configuration) {
                // Write the old section
                writeSection(indent, actualKey, (Configuration) oldObject);
            } else if (newObject instanceof Configuration) {
                // Write the new section
                writeSection(indent, actualKey, (Configuration) newObject);
            } else if (oldObject != null) {
                // Write the old object
                write(indent, actualKey, oldObject);
            } else {
                // Write the new object
                write(indent, actualKey, newObject);
            }
        }

        // All keys written, write leftover comments
        String danglingComments = comments.get(null);

        if (danglingComments != null)
            writer.write(danglingComments);

        writer.close();
    }

    private void write(String indent, String key, Object toWrite) throws IOException {
        if (toWrite instanceof String || toWrite instanceof Character) {
            String string = String.valueOf(toWrite);
            writer.write(indent + key + ": " + yaml.dump(string.replace("\n", "\\n")));
        } else if (toWrite instanceof List) {
            writer.write(getListAsString(indent, key, (List<?>) toWrite));
        } else {
            writer.write(indent + key + ": " + yaml.dump(toWrite));
        }
    }

    private void writeSection(String indent, String key, Configuration section) throws IOException {
        if (section.getKeys().isEmpty()) {
            writer.write(indent + key + ": {}");
        } else {
            writer.write(indent + key + ":");
        }

        writer.write("\n");
    }

    private String getListAsString(String indent, String key, List<?> list) {
        StringBuilder builder = new StringBuilder(indent).append(key).append(":");

        if (list.isEmpty()) {
            builder.append(" []\n");
            return builder.toString();
        }

        builder.append("\n");

        for (Object toWrite : list) {
            builder.append(indent);

            if (toWrite instanceof String || toWrite instanceof Character) {
                builder.append("- '").append(toWrite).append("'");
            } else if (toWrite instanceof List) {
                builder.append("- ").append(yaml.dump(toWrite));
            } else if (toWrite instanceof Map) {
                AtomicBoolean first = new AtomicBoolean(true);
                ((Map<?, ?>) toWrite).forEach(((mapKey, mapValue) -> {
                    if (first.get()) {
                        builder.append("- ");
                        first.set(false);
                    } else
                        builder.append("  ");
                    builder.append(mapKey).append(": ").append(yaml.dump(mapValue));
                }));
            } else {
                builder.append("- ").append(toWrite);
            }
        }

        return builder.toString();
    }

    private Map<String, String> getComments(List<String> lines) {
        Map<String, String> comments = new HashMap<>();
        StringBuilder keysBuilder = new StringBuilder();
        StringBuilder commentBuilder = new StringBuilder();
        long indents = 0;

        for (String line : lines) {
            String trimmedLine = line == null ? "" : line.trim();
            if (trimmedLine.startsWith("-")) continue;

            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#")) {
                commentBuilder.append(line).append("\n");
            } else {
                indents = setFullKey(keysBuilder, line, indents);

                if (keysBuilder.length() > 0) {
                    comments.put(keysBuilder.toString(), commentBuilder.toString());
                    commentBuilder.setLength(0);
                }
            }
        }

        if (commentBuilder.length() > 0) {
            comments.put(null, commentBuilder.toString());
        }

        return comments;
    }

    private void removeLastKey(StringBuilder keyBuilder) {
        String temp = keyBuilder.toString();
        String[] keys = temp.split("\\.");

        if (keys.length == 1) {
            keyBuilder.setLength(0);
            return;
        }

        temp = temp.substring(0, temp.length() - keys[keys.length - 1].length() - 1);
        keyBuilder.setLength(temp.length());
    }

    private long setFullKey(StringBuilder keysBuilder, String configLine, long indents) {
        // One indent is 2 spaces
        long currentIndents = 0;
        for (char c : configLine.toCharArray()) {
            if (c == ' ') currentIndents++;
                // Ignore further spaces
            else break;
        }
        currentIndents = currentIndents / 2;

        String key = configLine.trim().split(":")[0];

        if (keysBuilder.length() == 0) {
            keysBuilder.append(key);
        } else if (currentIndents == indents) {
            removeLastKey(keysBuilder);

            if (keysBuilder.length() > 0) {
                keysBuilder.append(".");
            }

            keysBuilder.append(key);
        } else if (currentIndents > indents) {
            keysBuilder.append(".").append(key);
        } else {
            long difference = indents - currentIndents;

            for (int i = 0; i < difference + 1; i++) {
                removeLastKey(keysBuilder);
            }

            if (keysBuilder.length() > 0) {
                keysBuilder.append(".");
            }

            keysBuilder.append(key);
        }

        return currentIndents;
    }
}