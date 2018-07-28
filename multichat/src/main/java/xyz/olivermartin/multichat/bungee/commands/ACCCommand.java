package xyz.olivermartin.multichat.bungee.commands;

import com.olivermartin410.plugins.TChatInfo;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.MultiChat;

/**
 * Admin-Chat colour command
 * <p> This command allows individual staff members to set their colour of the admin-chat messages they receive</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ACCCommand extends Command {
	
	// Command aliases
	private static String[] aliases = new String[] {};

	public ACCCommand() {
		super("acc", "multichat.staff.mod", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		// Check correct arguments
		if (args.length != 2) {

			if ((sender instanceof ProxiedPlayer)) {
				sender.sendMessage(new ComponentBuilder("Usage: /acc <chatcolorcode> <namecolorcode>").color(ChatColor.GREEN).create());
			} else {
				sender.sendMessage(new ComponentBuilder("Only players can change chat colours!").color(ChatColor.RED).create());
			}

		} else if ((sender instanceof ProxiedPlayer)) {

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

					chatinfo.setChatColor(args[0].charAt(0));
					chatinfo.setNameColor(args[1].charAt(0));

					MultiChat.adminchatpreferences.remove(player.getUniqueId());
					MultiChat.adminchatpreferences.put(player.getUniqueId(), chatinfo);

					sender.sendMessage(new ComponentBuilder("Admin-Chat colours updated!").color(ChatColor.GREEN).create());

				} else {

					sender.sendMessage(new ComponentBuilder("Invalid color codes specified: Must be letters a-f or numbers 0-9").color(ChatColor.RED).create());
					sender.sendMessage(new ComponentBuilder("Usage: /acc <chatcolorcode> <namecolorcode>").color(ChatColor.RED).create());

				}

			} else {

				sender.sendMessage(new ComponentBuilder("Invalid color codes specified: Must be letters a-f or numbers 0-9").color(ChatColor.RED).create());
				sender.sendMessage(new ComponentBuilder("Usage: /acc <chatcolorcode> <namecolorcode>").color(ChatColor.RED).create());

			}

		} else {

			sender.sendMessage(new ComponentBuilder("Only players can change chat colours!").color(ChatColor.RED).create());

		}
	}
}
