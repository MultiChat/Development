package xyz.olivermartin.multichat.bungee.commands;

import java.util.Iterator;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.BungeeComm;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;

/**
 * Staff List Command
 * <p>Allows the user to view a list of all online staff, sorted by their server</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class StaffListCommand extends Command {

	private static String[] aliases = new String[] {};

	public StaffListCommand() {
		super("staff", "multichat.staff.list", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		String server;
		boolean staff = false;

		MessageManager.sendMessage(sender, "command_stafflist_list");

		for (Iterator<String> localIterator1 = ProxyServer.getInstance().getServers().keySet().iterator(); localIterator1.hasNext();) {

			server = (String)localIterator1.next();

			if (!ProxyServer.getInstance().getServerInfo(server).getPlayers().isEmpty()) {

				MessageManager.sendSpecialMessage(sender, "command_stafflist_list_server", server);

				for (ProxiedPlayer onlineplayer2 : ProxyServer.getInstance().getPlayers()) {

					if ((onlineplayer2.hasPermission("multichat.staff"))) {

						if (onlineplayer2.getServer().getInfo().getName().equals(server)) {

							if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean("fetch_spigot_display_names") == true) {
								BungeeComm.sendMessage(onlineplayer2.getName(), onlineplayer2.getServer().getInfo());
							}

							staff = true;
							MessageManager.sendSpecialMessage(sender, "command_stafflist_list_item", onlineplayer2.getDisplayName());

						}
					}
				}

			}
		}
		
		if (!staff) MessageManager.sendMessage(sender, "command_stafflist_no_staff");
		
	}
}
