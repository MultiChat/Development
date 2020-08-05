package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.Events;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.bungee.StaffChatManager;

import java.util.UUID;

/**
 * Admin-Chat command
 * <p>Allows the user to toggle / send a message to admin-chat</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class ACCommand extends Command {

    public ACCommand() {
        super("mcac", "multichat.staff.admin", ConfigManager.getInstance().getHandler(ConfigFile.ALIASES).getConfig().getStringList("ac").toArray(new String[0]));
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof ProxiedPlayer)) {
                MessageManager.sendMessage(sender, "command_ac_only_players");
                return;
            }

            UUID playerUID = ((ProxiedPlayer) sender).getUniqueId();
            boolean toggleResult = Events.toggleAC(playerUID);
            MessageManager.sendMessage(sender, "command_ac_toggle_" + (toggleResult ? "on" : "off"));
            return;
        }

        String name = "CONSOLE";
        String displayName = "CONSOLE";
        String serverName = "#";

        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            name = player.getName();
            displayName = player.getDisplayName();
            serverName = player.getServer().getInfo().getName();
        }

        new StaffChatManager().sendAdminMessage(name, displayName, serverName, String.join(" ", args));
    }
}
