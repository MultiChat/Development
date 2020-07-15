package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.BungeeComm;
import xyz.olivermartin.multichat.bungee.Channel;
import xyz.olivermartin.multichat.bungee.ChatModeManager;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;

/**
 * Chat Channel Command
 * <p>Players can use this command to switch channels, as well as show and hide specific channels</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ChannelCommand extends Command {

	public ChannelCommand() {
		super("channel", "multichat.chat.channel", (String[]) ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("channelcommand").toArray(new String[0]));
	}

	private void showHelp(CommandSender sender) {

		MessageManager.sendMessage(sender, "command_channel_help");

	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if ((sender instanceof ProxiedPlayer)) {

			if ((args.length < 1) || ((args.length == 1) && (args[0].toLowerCase().equals("help")))) {

				showHelp(sender);

			} else if (args.length == 1) {

				showHelp(sender);

			} else if (args.length == 2) {

				String subCommand = args[0].toLowerCase();
				String operand = args[1].toLowerCase();

				switch (subCommand) {

				case "switch":
					if (!sender.hasPermission("multichat.chat.channel.switch")) {
						MessageManager.sendMessage(sender, "command_channel_switch_no_permission");
						return;
					}
					if (operand.equals("local")) {
						ChatModeManager.getInstance().setLocal(((ProxiedPlayer)sender).getUniqueId());
						MessageManager.sendSpecialMessage(sender, "command_channel_switch", operand.toUpperCase());
					} else if (operand.equals("global")) {
						ChatModeManager.getInstance().setGlobal(((ProxiedPlayer)sender).getUniqueId());
						MessageManager.sendSpecialMessage(sender, "command_channel_switch", operand.toUpperCase());
					} else {
						MessageManager.sendMessage(sender, "command_channel_does_not_exist");
					}
					break;

				case "hide":
					if (!sender.hasPermission("multichat.chat.channel.hide")) {
						MessageManager.sendMessage(sender, "command_channel_hide_no_permission");
						return;
					}
					if (operand.equals("local")) {

						if (!ChatModeManager.getInstance().isGlobal(((ProxiedPlayer)sender).getUniqueId())) {
							MessageManager.sendMessage(sender, "command_channel_cannot_hide");
							return;
						}

						Channel local = Channel.getLocalChannel();
						if (local.isMember(((ProxiedPlayer)sender).getUniqueId())) {
							local.addMember(((ProxiedPlayer)sender).getUniqueId());
							MessageManager.sendSpecialMessage(sender, "command_channel_hide", operand.toUpperCase());
						} else {
							MessageManager.sendSpecialMessage(sender, "command_channel_already_hide", operand.toUpperCase());
						}

					} else if (operand.equals("global")) {

						if (ChatModeManager.getInstance().isGlobal(((ProxiedPlayer)sender).getUniqueId())) {
							MessageManager.sendMessage(sender, "command_channel_cannot_hide");
							return;
						}

						Channel global = Channel.getGlobalChannel();
						if (global.isMember(((ProxiedPlayer)sender).getUniqueId())) {
							global.addMember(((ProxiedPlayer)sender).getUniqueId());
							MessageManager.sendSpecialMessage(sender, "command_channel_hide", operand.toUpperCase());
						} else {
							MessageManager.sendSpecialMessage(sender, "command_channel_already_hide", operand.toUpperCase());
						}

					} else {
						MessageManager.sendMessage(sender, "command_channel_does_not_exist");
					}
					break;

				case "show":
					if (!sender.hasPermission("multichat.chat.channel.show")) {
						MessageManager.sendMessage(sender, "command_channel_show_no_permission");
						return;
					}
					if (operand.equals("local")) {

						Channel local = Channel.getLocalChannel();
						if (!local.isMember(((ProxiedPlayer)sender).getUniqueId())) {
							local.removeMember(((ProxiedPlayer)sender).getUniqueId());
							MessageManager.sendSpecialMessage(sender, "command_channel_show", operand.toUpperCase());
						} else {
							MessageManager.sendSpecialMessage(sender, "command_channel_already_show", operand.toUpperCase());
						}

					} else if (operand.equals("global")) {

						Channel global = Channel.getGlobalChannel();
						if (!global.isMember(((ProxiedPlayer)sender).getUniqueId())) {
							global.removeMember(((ProxiedPlayer)sender).getUniqueId());
							MessageManager.sendSpecialMessage(sender, "command_channel_show", operand.toUpperCase());
						} else {
							MessageManager.sendSpecialMessage(sender, "command_channel_already_show", operand.toUpperCase());
						}

					} else {
						MessageManager.sendMessage(sender, "command_channel_does_not_exist");
					}
					break;

				default:
					showHelp(sender);
					break;
				}

				// Update local channel info
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					BungeeComm.sendPlayerChannelMessage(p.getName(), Channel.getChannel(p.getUniqueId()).getName(), Channel.getChannel(p.getUniqueId()), p.getServer().getInfo(), (p.hasPermission("multichat.chat.colour")||p.hasPermission("multichat.chat.color")||p.hasPermission("multichat.chat.colour.simple")||p.hasPermission("multichat.chat.color.simple")), (p.hasPermission("multichat.chat.colour")||p.hasPermission("multichat.chat.color")||p.hasPermission("multichat.chat.colour.rgb")||p.hasPermission("multichat.chat.color.rgb")));
				}

			}

		} else {
			MessageManager.sendMessage(sender, "command_channel_only_players");
		}

	}

}
