package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.DebugManager;
import xyz.olivermartin.multichat.bungee.Events;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.bungee.StaffChatManager;

/**
 * Admin-Chat command
 * <p>Allows the user to toggle / send a message to admin-chat</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ACCommand extends Command {

	public ACCommand() {
		super("mcac", "multichat.staff.admin", (String[]) ConfigManager.getInstance().getHandler("aliases.yml").getConfig().getStringList("ac").toArray(new String[0]));
	}

	public void execute(CommandSender sender, String[] args) {

		boolean toggleresult;

		if (args.length < 1) {

			if ((sender instanceof ProxiedPlayer)) {

				DebugManager.log("[ACCommand] Command sender is a player");

				ProxiedPlayer player = (ProxiedPlayer)sender;
				toggleresult = Events.toggleAC(player.getUniqueId());

				DebugManager.log("[ACCommand] AC new toggle state: " + toggleresult);

				if (toggleresult == true) {
					MessageManager.sendMessage(sender, "command_ac_toggle_on");
				} else {
					MessageManager.sendMessage(sender, "command_ac_toggle_off");
				}

			} else {

				MessageManager.sendMessage(sender, "command_ac_only_players");

			}

		} else if ((sender instanceof ProxiedPlayer)) {

			DebugManager.log("[ACCommand] Command sender is a player");

			String message = MultiChatUtil.getMessageFromArgs(args);

			ProxiedPlayer player = (ProxiedPlayer)sender;
			StaffChatManager chatman = new StaffChatManager();

			DebugManager.log("[ACCommand] Next line of code will send the message, if no errors, then it worked!");

			chatman.sendAdminMessage(player.getName(), player.getDisplayName(), player.getServer().getInfo().getName(), message);
			chatman = null;

		} else {

			DebugManager.log("[ACCommand] Command sender is the console");

			String message = MultiChatUtil.getMessageFromArgs(args);

			StaffChatManager chatman = new StaffChatManager();

			DebugManager.log("[ACCommand] Next line of code will send the message, if no errors, then it worked!");

			chatman.sendAdminMessage("CONSOLE", "CONSOLE", "#", message);
			chatman = null;

		}
	}
}
