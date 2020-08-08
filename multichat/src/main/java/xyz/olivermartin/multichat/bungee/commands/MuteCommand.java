package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

public class MuteCommand extends Command {

    public MuteCommand() {
        super("mcmute", "multichat.mute", ProxyConfigs.ALIASES.getAliases("mcmute"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "mute_usage");
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target == null) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "mute_player_not_found");
            return;
        }

        if (target.hasPermission("multichat.mute.bypass")) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "mute_bypass");
            return;
        }

        if (!ChatControl.isMutedAnywhere(target.getUniqueId())) {
            ChatControl.mute(target.getUniqueId());
            ProxyConfigs.MESSAGES.sendMessage(sender, "mute_muted_staff");
            ProxyConfigs.MESSAGES.sendMessage(target, "mute_muted");
        } else {
            ChatControl.unmute(target.getUniqueId());
            ProxyConfigs.MESSAGES.sendMessage(sender, "mute_unmuted_staff");
            ProxyConfigs.MESSAGES.sendMessage(target, "mute_unmuted");
        }
    }
}
