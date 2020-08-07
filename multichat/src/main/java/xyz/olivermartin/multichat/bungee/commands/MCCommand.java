package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.Events;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.StaffChatManager;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

import java.util.UUID;

/**
 * Mod-Chat Commands
 * <p>Allows staff members to send mod-chat messages or toggle the chat</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class MCCommand extends Command {

    public MCCommand() {
        super("mcmc", "multichat.staff.mod", ProxyConfigs.ALIASES.getAliases("mcmc"));
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof ProxiedPlayer)) {
                MessageManager.sendMessage(sender, "command_mc_only_players");
                return;
            }

            UUID playerUID = ((ProxiedPlayer) sender).getUniqueId();
            boolean toggleResult = Events.toggleMC(playerUID);
            MessageManager.sendMessage(sender, "command_mc_toggle_" + (toggleResult ? "on" : "off"));
            return;
        }

        String name = "CONSOLE";
        String displayName = "CONSOLE";
        String serverName = "#";

        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer) sender;
            name = proxiedPlayer.getName();
            displayName = proxiedPlayer.getDisplayName();
            serverName = proxiedPlayer.getServer().getInfo().getName();
        }

        new StaffChatManager().sendModMessage(name, displayName, serverName, String.join(" ", args));
    }
}
