package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

import java.util.UUID;

public class IgnoreCommand extends Command {

    public IgnoreCommand() {
        super("mcignore", "multichat.ignore", ProxyConfigs.ALIASES.getAliases("mcignore"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "ignore_only_players");
            return;
        }

        if (args.length == 0) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "ignore_usage");
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "ignore_player_not_found");
            return;
        }

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
        if (proxiedPlayer.equals(target)) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "ignore_cannot_ignore_yourself");
            return;
        }

        if (target.hasPermission("multichat.ignore.bypass")) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "ignore_bypass");
            return;
        }

        UUID playerUID = proxiedPlayer.getUniqueId();
        UUID targetUID = target.getUniqueId();

        // TODO: ChatControl.toggleIgnore
        if (!ChatControl.ignoresAnywhere(targetUID, playerUID)) {
            ChatControl.ignore(playerUID, targetUID);
            ProxyConfigs.MESSAGES.sendMessage(sender, "ignore_ignored", target.getName());
        } else {
            ChatControl.unignore(playerUID, targetUID);
            ProxyConfigs.MESSAGES.sendMessage(sender, "ignore_unignored", target.getName());
        }
    }
}
