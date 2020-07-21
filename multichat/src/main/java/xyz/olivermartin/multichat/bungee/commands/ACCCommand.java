package xyz.olivermartin.multichat.bungee.commands;

import com.olivermartin410.plugins.TChatInfo;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.DebugManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyDataStore;

/**
 * Admin-Chat colour command
 * <p> This command allows individual staff members to set their colour of the admin-chat messages they receive</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ACCCommand extends Command {

	public ACCCommand() {
		super("mcacc", "multichat.staff.admin", (String[]) ConfigManager.getInstance().getHandler("aliases.yml").getConfig().getStringList("acc").toArray(new String[0]));
	}

	public void execute(CommandSender sender, String[] args) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		// Check correct arguments
		if (args.length != 2) {

			if ((sender instanceof ProxiedPlayer)) {
				MessageManager.sendMessage(sender, "command_acc_usage");
			} else {
				MessageManager.sendMessage(sender, "command_acc_only_players");
			}

		} else if ((sender instanceof ProxiedPlayer)) {

			DebugManager.log("[ACCCommand] Command sender is a player");

			TChatInfo chatinfo = new TChatInfo();
			ProxiedPlayer player = (ProxiedPlayer)sender;

			// Convert args to lowercase
			args[0] = args[0].toLowerCase();
			args[1] = args[1].toLowerCase();

			if ((args[0].equals("a")) || (args[0].equals("b")) || (args[0].equals("c")) || (args[0].equals("d"))
					|| (args[0].equals("e")) || (args[0].equals("f")) || (args[0].equals("0")) || (args[0].equals("1"))
					|| (args[0].equals("2")) || (args[0].equals("3")) || (args[0].equals("4")) || (args[0].equals("5"))
					|| (args[0].equals("6")) || (args[0].equals("7")) || (args[0].equals("8")) || (args[0].equals("9"))) {

				if ((args[1].equals("a")) || (args[1].equals("b")) || (args[1].equals("c")) || (args[1].equals("d"))
						|| (args[1].equals("e")) || (args[1].equals("f")) || (args[1].equals("0")) || (args[1].equals("1"))
						|| (args[1].equals("2")) || (args[1].equals("3")) || (args[1].equals("4")) || (args[1].equals("5"))
						|| (args[1].equals("6")) || (args[1].equals("7")) || (args[1].equals("8")) || (args[1].equals("9"))) {

					DebugManager.log("[ACCCommand] Colour codes are valid");

					chatinfo.setChatColor(args[0].charAt(0));
					chatinfo.setNameColor(args[1].charAt(0));

					ds.getAdminChatPreferences().remove(player.getUniqueId());
					ds.getAdminChatPreferences().put(player.getUniqueId(), chatinfo);

					DebugManager.log("[ACCCommand] Preferences updated");

					MessageManager.sendMessage(sender, "command_acc_updated");

				} else {

					MessageManager.sendMessage(sender, "command_acc_invalid");
					MessageManager.sendMessage(sender, "command_acc_invalid_usage");

				}

			} else {

				MessageManager.sendMessage(sender, "command_acc_invalid");
				MessageManager.sendMessage(sender, "command_acc_invalid_usage");

			}

		} else {

			MessageManager.sendMessage(sender, "command_acc_only_players");

		}
	}
}
