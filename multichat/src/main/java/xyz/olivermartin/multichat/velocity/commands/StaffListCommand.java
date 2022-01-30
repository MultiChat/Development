package xyz.olivermartin.multichat.velocity.commands;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import xyz.olivermartin.multichat.velocity.*;

/**
 * Staff List Command
 * <p>Allows the user to view a list of all online staff, sorted by their server</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class StaffListCommand extends Command {

    private static final String[] aliases = new String[]{};

    public StaffListCommand() {
        super("staff", aliases);
    }

    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("multichat.staff.list");
    }

    public void execute(Invocation invocation) {
        var sender = invocation.source();

        String server;
        boolean staff = false;
        boolean onServer;

        MessageManager.sendMessage(sender, "command_stafflist_list");

        DebugManager.log("[StaffList] Player: " + ((sender instanceof Player) ? ((Player) sender).getUsername() : "CONSOLE") + " is the command sender!");

        for (RegisteredServer registeredServer : MultiChat.getInstance().getServer().getAllServers()) {

            server = registeredServer.getServerInfo().getName();

            DebugManager.log("[StaffList] First Server: " + server);

            if (!registeredServer.getPlayersConnected().isEmpty()) {

                onServer = false;

                for (Player onlineplayer2 : MultiChat.getInstance().getServer().getAllPlayers()) {

                    if ((onlineplayer2.hasPermission("multichat.staff"))) {

                        DebugManager.log("[StaffList] Found a staff member: " + onlineplayer2.getUsername());


                        if (onlineplayer2.getCurrentServer().get().getServerInfo().getName().equals(server)) {

                            if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getNode("fetch_spigot_display_names").getBoolean()) {
                                BungeeComm.sendMessage(onlineplayer2.getUsername(), onlineplayer2.getCurrentServer().get().getServerInfo());
                            }

                            staff = true;

                            if (!onServer) {
                                MessageManager.sendSpecialMessage(sender, "command_stafflist_list_server", server);
                                onServer = true;
                            }

                            MessageManager.sendSpecialMessage(sender, "command_stafflist_list_item", onlineplayer2.getUsername());

                        }

                    }
                }

            }
        }

        if (!staff) MessageManager.sendMessage(sender, "command_stafflist_no_staff");

    }
}
