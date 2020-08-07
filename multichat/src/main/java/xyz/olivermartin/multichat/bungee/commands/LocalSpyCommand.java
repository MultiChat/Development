package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.MessageManager;
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
public class LocalSpyCommand extends Command {

    public LocalSpyCommand() {
        super("mclocalspy", "multichat.staff.spy", ProxyConfigs.ALIASES.getAliases("mclocalspy"));
    }

    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            MessageManager.sendMessage(sender, "command_localspy_only_players");
            return;
        }

        // TODO: Do we really need this? I don't think so... just toggle it, no matter how many arguments
        if (args.length != 0) {
            MessageManager.sendMessage(sender, "command_localspy_usage");
            MessageManager.sendMessage(sender, "command_localspy_desc");
            return;
        }

        UUID playerUID = ((ProxiedPlayer) sender).getUniqueId();

        ProxyDataStore proxyDataStore = MultiChatProxy.getInstance().getDataStore();

        // TODO: Potentially proxyDataStore.toggleSpy(playerUID)
        if (proxyDataStore.getLocalSpy().contains(playerUID)) {
            proxyDataStore.getLocalSpy().remove(playerUID);
            MessageManager.sendMessage(sender, "command_localspy_disabled");
            return;
        }

        proxyDataStore.getLocalSpy().add(playerUID);
        MessageManager.sendMessage(sender, "command_localspy_enabled");
    }
}
