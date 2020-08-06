package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.Events;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;

import java.util.UUID;

public class MultiChatBypassCommand extends Command {

    public MultiChatBypassCommand() {
        super("mcbypass", "multichat.bypass", ConfigManager.getInstance().getHandler(ConfigFile.ALIASES).getConfig().getStringList("bypass").toArray(new String[0]));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            // TODO: Add a message here like in all other commands
            return;
        }

        if (args.length != 0) {
            MessageManager.sendMessage(sender, "command_multichatbypass_usage");
            return;
        }

        UUID playerUID = ((ProxiedPlayer) sender).getUniqueId();

        // TODO: This should definitely be changed later
        if (Events.mcbPlayers.contains(playerUID)) {
            Events.mcbPlayers.remove(playerUID);
            MessageManager.sendMessage(sender, "command_multichatbypass_disabled");
        } else {
            Events.mcbPlayers.add(playerUID);
            MessageManager.sendMessage(sender, "command_multichatbypass_enabled");
        }
    }
}
