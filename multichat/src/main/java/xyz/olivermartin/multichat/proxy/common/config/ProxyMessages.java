package xyz.olivermartin.multichat.proxy.common.config;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.ProxyJsonUtils;

import java.util.HashMap;
import java.util.Map;

public class ProxyMessages extends AbstractProxyConfig {

    private final Map<String, String> messagesMap = new HashMap<>();

    ProxyMessages() {
        super("messages.yml");
    }

    @Override
    void reloadValues() {
        messagesMap.clear();

        getConfig().getKeys().forEach(key -> messagesMap.put(key, getConfig().getString(key)));
    }

    public String getPrefix() {
        return messagesMap.getOrDefault("prefix", "&8&l[&2&lM&a&lC&8&l]&f ");
    }

    public String getMessage(String key) {
        return messagesMap.getOrDefault(key, "&cERROR - Please report to plugin dev - No message defined for: " + key);
    }

    public void sendMessage(CommandSender sender, String id) {
        sendMessage(sender, id, true);
    }

    public void sendMessage(CommandSender sender, String id, boolean usePrefix) {
        sendMessage(sender, id, usePrefix, null);
    }

    public void sendMessage(CommandSender sender, String id, String special) {
        sendMessage(sender, id, true, special, false);
    }

    public void sendMessage(CommandSender sender, String id, boolean usePrefix, String special) {
        sendMessage(sender, id, usePrefix, special, false);
    }

    public void sendMessage(CommandSender sender, String id, String special, boolean specialJson) {
        sendMessage(sender, id, true, special, specialJson);
    }

    public void sendMessage(CommandSender sender, String id, boolean usePrefix, String special, boolean specialJson) {

        boolean isSpecial = special != null;

        // Translate format codes
        String message = (usePrefix ? getPrefix() : "+++") + getMessage(id);
        message = MultiChatUtil.translateColorCodes(message);
        if (isSpecial) special = MultiChatUtil.translateColorCodes(special);

        // Handle legacy servers
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (ProxyConfigs.CONFIG.isLegacyServer(player.getServer().getInfo().getName())) {
                message = MultiChatUtil.approximateRGBColorCodes(message);
                if (isSpecial) special = MultiChatUtil.approximateRGBColorCodes(special);
            }
        } else {
            // Handle console
            message = MultiChatUtil.approximateRGBColorCodes(message);
            if (isSpecial) special = MultiChatUtil.approximateRGBColorCodes(special);
        }

        // If we want to treat the "Special" part as Json, then we will parse it here and treat it as a non special message
        if (isSpecial && specialJson) {
            message = message.replace("%SPECIAL%", special);
            isSpecial = false;
        }

        // Parse & send message
        if (isSpecial) {
            sender.sendMessage(ProxyJsonUtils.parseMessage(message, "%SPECIAL%", special));
        } else {
            sender.sendMessage(ProxyJsonUtils.parseMessage(message));
        }

    }
}
