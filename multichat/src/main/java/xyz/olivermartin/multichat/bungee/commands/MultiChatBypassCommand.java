package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.Events;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

import java.util.UUID;

public class MultiChatBypassCommand extends Command {

    public MultiChatBypassCommand() {
        super("mcbypass", "multichat.bypass", ProxyConfigs.ALIASES.getAliases("mcbypass"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            // TODO: Add a message here like in all other commands
            return;
        }

        if (args.length != 0) {
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_multichatbypass_usage");
            return;
        }

        UUID playerUID = ((ProxiedPlayer) sender).getUniqueId();

        // TODO: This should definitely be changed later
        if (Events.mcbPlayers.contains(playerUID)) {
            Events.mcbPlayers.remove(playerUID);
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_multichatbypass_disabled");
        } else {
            Events.mcbPlayers.add(playerUID);
            ProxyConfigs.MESSAGES.sendMessage(sender, "command_multichatbypass_enabled");
        }
    }
}
