package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;

import java.util.UUID;

    public IgnoreCommand() {
        super("mcignore", "multichat.ignore", ConfigManager.getInstance().getHandler(ConfigFile.ALIASES).getConfig().getStringList("ignore").toArray(new String[0]));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            MessageManager.sendMessage(sender, "ignore_only_players");
            return;
        }

        if (args.length == 0) {
            MessageManager.sendMessage(sender, "ignore_usage");
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {
            MessageManager.sendMessage(sender, "ignore_player_not_found");
            return;
        }

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
        if (proxiedPlayer.equals(target)) {
            MessageManager.sendMessage(sender, "ignore_cannot_ignore_yourself");
            return;
        }

        if (target.hasPermission("multichat.ignore.bypass")) {
            MessageManager.sendMessage(sender, "ignore_bypass");
            return;
        }

        UUID playerUID = proxiedPlayer.getUniqueId();
        UUID targetUID = target.getUniqueId();

        // TODO: ChatControl.toggleIgnore
        if (!ChatControl.ignoresAnywhere(targetUID, playerUID)) {
            ChatControl.ignore(playerUID, targetUID);
            MessageManager.sendSpecialMessage(sender, "ignore_ignored", target.getName());
        } else {
            ChatControl.unignore(playerUID, targetUID);
            MessageManager.sendSpecialMessage(sender, "ignore_unignored", target.getName());
        }
    }
}
