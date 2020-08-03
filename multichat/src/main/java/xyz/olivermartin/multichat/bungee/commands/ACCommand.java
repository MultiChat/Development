package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.DebugManager;
import xyz.olivermartin.multichat.bungee.Events;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.bungee.StaffChatManager;

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
        if (args.length > 0) {
        	// Default sender values
            String name = "CONSOLE";
            String displayName = "CONSOLE";
            String serverName = "#";

            // Change values if sender is a player
            if (sender instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) sender;
                name = player.getName();
                displayName = player.getDisplayName();
                serverName = player.getServer().getInfo().getName();
            }

            // Send message
            DebugManager.log("[ACCommand] Attempting to send a staff chat message as " + name + ".");
            StaffChatManager staffChatManager = new StaffChatManager();
            staffChatManager.sendAdminMessage(name, displayName, serverName, String.join(" ", args));
            return;
        }

        // Console can't toggle AC
        if (!(sender instanceof ProxiedPlayer)) {
            MessageManager.sendMessage(sender, "command_ac_only_players");
            return;
        }

        // Toggle AC for player
        DebugManager.log("[ACCommand] Sender is a player, toggling AC...");

        ProxiedPlayer player = (ProxiedPlayer) sender;
        boolean toggleresult = Events.toggleAC(player.getUniqueId());

        DebugManager.log("[ACCommand] AC new toggle state: " + toggleresult);
        MessageManager.sendMessage(sender, "command_ac_toggle_" + (toggleresult ? "on" : "off"));
    }
}
