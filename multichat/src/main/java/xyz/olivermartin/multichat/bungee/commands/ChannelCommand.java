package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatModeManager;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;

/**
 * Chat Channel Command
 * <p>Players can use this command to switch channels, as well as show and hide specific channels</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ChannelCommand extends Command {

	public ChannelCommand() {
		super("mcchannel", "multichat.chat.channel", (String[]) ConfigManager.getInstance().getHandler("aliases.yml").getConfig().getStringList("channel").toArray(new String[0]));
	}

	private void showHelp(CommandSender sender) {

		MessageManager.sendMessage(sender, "command_channel_help");

	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();

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

						if (channelManager.existsProxyChannel(operand)) {
							ChatModeManager.getInstance().setGlobal(((ProxiedPlayer)sender).getUniqueId());
							channelManager.select(((ProxiedPlayer)sender).getUniqueId(), operand);
							MessageManager.sendSpecialMessage(sender, "command_channel_switch", operand.toUpperCase());
						} else {
							MessageManager.sendMessage(sender, "command_channel_does_not_exist");
						}

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

						if (!channelManager.isHidden(((ProxiedPlayer)sender).getUniqueId(), "local")) {
							channelManager.hide(((ProxiedPlayer)sender).getUniqueId(),"local");
							MessageManager.sendSpecialMessage(sender, "command_channel_hide", operand.toUpperCase());
						} else {
							MessageManager.sendSpecialMessage(sender, "command_channel_already_hide", operand.toUpperCase());
						}

					} else if (operand.equals("global")) {

						if (ChatModeManager.getInstance().isGlobal(((ProxiedPlayer)sender).getUniqueId())) {
							MessageManager.sendMessage(sender, "command_channel_cannot_hide");
							return;
						}

						if (!channelManager.isHidden(((ProxiedPlayer)sender).getUniqueId(), "global")) {
							channelManager.hide(((ProxiedPlayer)sender).getUniqueId(),"global");
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

						if (channelManager.isHidden(((ProxiedPlayer)sender).getUniqueId(), "local")) {
							channelManager.show(((ProxiedPlayer)sender).getUniqueId(),"local");
							MessageManager.sendSpecialMessage(sender, "command_channel_show", operand.toUpperCase());
						} else {
							MessageManager.sendSpecialMessage(sender, "command_channel_already_show", operand.toUpperCase());
						}

					} else if (operand.equals("global")) {

						if (channelManager.isHidden(((ProxiedPlayer)sender).getUniqueId(), "global")) {
							channelManager.show(((ProxiedPlayer)sender).getUniqueId(),"global");
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

				ProxiedPlayer player = (ProxiedPlayer) sender;
				String channelFormat;

				switch (channelManager.getChannel(player)) {

				case "global":
					channelFormat = channelManager.getGlobalChannel().getInfo().getFormat();
					break;
				case "local":
					channelFormat = channelManager.getLocalChannel().getFormat();
					break;
				default:
					if (channelManager.existsProxyChannel(channelManager.getChannel(player))) {
						channelFormat = channelManager.getProxyChannel(channelManager.getChannel(player)).get().getInfo().getFormat();
					} else {
						channelFormat = channelManager.getGlobalChannel().getInfo().getFormat();
					}
					break;
				}

				// Update local channel info
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					ProxyLocalCommunicationManager.sendPlayerDataMessage(p.getName(), channelManager.getChannel(p), channelFormat, p.getServer().getInfo(), (p.hasPermission("multichat.chat.colour")||p.hasPermission("multichat.chat.color")||p.hasPermission("multichat.chat.colour.simple")||p.hasPermission("multichat.chat.color.simple")), (p.hasPermission("multichat.chat.colour")||p.hasPermission("multichat.chat.color")||p.hasPermission("multichat.chat.colour.rgb")||p.hasPermission("multichat.chat.color.rgb")));
				}

			}

		} else {
			MessageManager.sendMessage(sender, "command_channel_only_players");
		}

	}

}
