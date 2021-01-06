package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

import java.util.UUID;

/**
 * SocialSpy Command
 * <p>Allows staff members to view private messages sent by players</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class SocialSpyCommand extends Command {

    public SocialSpyCommand() {
        super("mcsocialspy", "multichat.staff.spy", ProxyConfigs.ALIASES.getAliases("mcsocialspy"));
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_socialspy_only_players");
            return;
        }

        // TODO: We don't really need this check
        if (args.length != 0) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_socialspy_usage");
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_socialspy_desc");
            return;
        }

        UUID playerUID = ((ProxiedPlayer) sender).getUniqueId();
        ProxyDataStore proxyDataStore = MultiChatProxy.getInstance().getDataStore();

        if (proxyDataStore.getSocialSpy().contains(playerUID)) {
            proxyDataStore.getSocialSpy().remove(playerUID);
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_socialspy_disabled");
        } else {
            proxyDataStore.getSocialSpy().add(playerUID);
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_socialspy_enabled");
        }
    }
}
