package xyz.olivermartin.multichat.bungee.commands;

import java.util.Iterator;

import de.myzelyam.api.vanish.BungeeVanishAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.DebugManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;

/**
 * Staff List Command
 * <p>Allows the user to view a list of all online staff, sorted by their server</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class StaffListCommand extends Command {

	public StaffListCommand() {
		super("mcstaff", "multichat.staff.list", (String[])ConfigManager.getInstance().getHandler("aliases.yml").getConfig().getStringList("staff").toArray(new String[0]));
	}

	public void execute(CommandSender sender, String[] args) {

		String server;
		boolean staff = false;
		boolean onServer = false;

		MessageManager.sendMessage(sender, "command_stafflist_list");

		DebugManager.log("[StaffList] Player: " + sender.getName() + " is the command sender!");

		for (Iterator<String> localIterator1 = ProxyServer.getInstance().getServers().keySet().iterator(); localIterator1.hasNext();) {

			server = (String)localIterator1.next();

			DebugManager.log("[StaffList] First Server: " + server);

			if (!ProxyServer.getInstance().getServerInfo(server).getPlayers().isEmpty()) {

				onServer = false;

				for (ProxiedPlayer onlineplayer2 : ProxyServer.getInstance().getPlayers()) {

					if ((onlineplayer2.hasPermission("multichat.staff"))) {

						DebugManager.log("[StaffList] Found a staff member: " + onlineplayer2.getName());

						boolean showInList = true;

						DebugManager.log("[StaffList] Are we hooked to PremiumVanish: " + MultiChat.premiumVanish);
						DebugManager.log("[StaffList] Are we hiding vanished players as set in config?: " + MultiChat.hideVanishedStaffInStaffList);

						if (MultiChat.premiumVanish && MultiChat.hideVanishedStaffInStaffList) {

							DebugManager.log("[StaffList] Is staff invisible: " + BungeeVanishAPI.isInvisible(onlineplayer2));
							DebugManager.log("[StaffList] Can player see vanished staff?: " + sender.hasPermission("multichat.staff.list.vanished"));

							if (BungeeVanishAPI.isInvisible(onlineplayer2) && !sender.hasPermission("multichat.staff.list.vanished")) {
								DebugManager.log("[StaffList] This staff member will be hidden from list!");
								showInList = false;
							}
						}

						if (showInList) {

							if (onlineplayer2.getServer().getInfo().getName().equals(server)) {

								if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean(ConfigValues.Config.FETCH_SPIGOT_DISPLAY_NAMES) == true) {
									ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(onlineplayer2.getName(), onlineplayer2.getServer().getInfo());
								}

								staff = true;

								if (!onServer) {
									MessageManager.sendSpecialMessage(sender, "command_stafflist_list_server", server);
									onServer = true;
								}

								MessageManager.sendSpecialMessage(sender, "command_stafflist_list_item", onlineplayer2.getDisplayName());

							}

						}
					}
				}

			}
		}

		if (!staff) MessageManager.sendMessage(sender, "command_stafflist_no_staff");

	}
}
