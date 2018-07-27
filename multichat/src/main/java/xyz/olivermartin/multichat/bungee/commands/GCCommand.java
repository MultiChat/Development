package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatManipulation;
import xyz.olivermartin.multichat.bungee.Events;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.bungee.TGroupChatInfo;

/**
 * Group Chat Messaging Command
 * <p>Allows players to send a message direct to a group chat or toggle group chats</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class GCCommand extends Command {

	private static String[] aliases = new String[] {};

	public GCCommand() {
		super("gc", "multichat.group", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		if (args.length < 1) {

			if ((sender instanceof ProxiedPlayer)) {

				ProxiedPlayer player = (ProxiedPlayer)sender;
				boolean toggleresult = Events.toggleGC(player.getUniqueId());

				if (toggleresult == true) {
					sender.sendMessage(new ComponentBuilder("Group chat toggled on!").color(ChatColor.GREEN).create());
				} else {
					sender.sendMessage(new ComponentBuilder("Group chat toggled off!").color(ChatColor.RED).create());
				}

			} else {

				sender.sendMessage(new ComponentBuilder("Only players can toggle the chat!").color(ChatColor.RED).create());
			}

		} else if ((sender instanceof ProxiedPlayer)) {

			ProxiedPlayer player = (ProxiedPlayer)sender;

			if (MultiChat.viewedchats.get(player.getUniqueId()) != null) {

				String groupName = (String)MultiChat.viewedchats.get(player.getUniqueId());

				if (MultiChat.groupchats.containsKey(groupName)) {

					TGroupChatInfo groupInfo = (TGroupChatInfo) MultiChat.groupchats.get(groupName);

					String message = "";
					for (String arg : args) {
						message = message + arg + " ";
					}

					String playerName = sender.getName();

					if ((groupInfo.getFormal() == true)
							&& (groupInfo.getAdmins().contains(player.getUniqueId()))) {
						playerName = "&o" + playerName;
					}

					sendMessage(message, playerName, groupInfo);

				} else {

					sender.sendMessage(new ComponentBuilder("Sorry your selected chat no longer exists, please select a chat with /group <group name>").color(ChatColor.RED).create());
				}

			} else {
				sender.sendMessage(new ComponentBuilder("Please select the chat you wish to message using /group <group name>").color(ChatColor.RED).create());
			}

		} else {
			sender.sendMessage(new ComponentBuilder("Only players can speak in group chats").color(ChatColor.RED).create());
		}
	}

	public static void sendMessage(String message, String playerName, TGroupChatInfo groupInfo) {

		ChatManipulation chatfix = new ChatManipulation();

		String messageFormat = MultiChat.configman.config.getString("groupchat.format");
		message = chatfix.replaceGroupChatVars(messageFormat, playerName, message, groupInfo.getName());

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if (((groupInfo.existsViewer(onlineplayer.getUniqueId())) && (onlineplayer.hasPermission("multichat.group"))) || ((MultiChat.allspy.contains(onlineplayer.getUniqueId())) && (onlineplayer.hasPermission("multichat.staff.spy")))) {
				onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
			}

		}

		String groupName = groupInfo.getName();

		System.out.println("\033[32m[MultiChat] /gc {" + groupName.toUpperCase() + "} {" + playerName + "}  " + message);
	}
}

