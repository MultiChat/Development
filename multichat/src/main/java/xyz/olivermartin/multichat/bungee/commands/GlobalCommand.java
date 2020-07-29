package xyz.olivermartin.multichat.bungee.commands;

import java.util.Optional;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatModeManager;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyChatManager;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;

/**
 * Global Command
 * <p>Causes players to see messages sent from all servers in the global chat</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class GlobalCommand extends Command {

	public GlobalCommand() {
		super("mcglobal", "multichat.chat.mode", (String[]) ConfigManager.getInstance().getHandler(ConfigFile.ALIASES).getConfig().getStringList("global").toArray(new String[0]));
	}

	public void execute(CommandSender sender, String[] args) {

		ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();
		ProxyChatManager chatManager = MultiChatProxy.getInstance().getChatManager();

		if ((sender instanceof ProxiedPlayer)) {

			if (args.length < 1) {

				ChatModeManager.getInstance().setGlobal(((ProxiedPlayer)sender).getUniqueId());

				MessageManager.sendMessage(sender, "command_global_enabled_1");
				MessageManager.sendMessage(sender, "command_global_enabled_2");

			} else {

				ProxiedPlayer player = (ProxiedPlayer)sender;
				String message = MultiChatUtil.getMessageFromArgs(args);

				if (ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getBoolean(ConfigValues.Config.GLOBAL) == true) {

					if (!ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getStringList(ConfigValues.Config.NO_GLOBAL).contains(player.getServer().getInfo().getName())) {

						if (ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getBoolean(ConfigValues.Config.FETCH_SPIGOT_DISPLAY_NAMES) == true) {
							ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(player.getName(), player.getServer().getInfo());
						}

						Optional<String> optionalMessage = chatManager.handleChatMessage(player, message); // Processed message

						if (!optionalMessage.isPresent()) {
							// Player not permitted to send this message, so cancel it
							return;
						}

						message = optionalMessage.get();

						// If they had this channel hidden, then unhide it...
						if (channelManager.isHidden(player.getUniqueId(), "global")) {
							channelManager.show(player.getUniqueId(), "global");
							MessageManager.sendSpecialMessage(player, "command_channel_show", "GLOBAL");
						}

						// Let server know players channel preference

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

						ProxyLocalCommunicationManager.sendPlayerDataMessage(player.getName(), channelManager.getChannel(player), channelFormat, player.getServer().getInfo(), (player.hasPermission("multichat.chat.colour")||player.hasPermission("multichat.chat.color")||player.hasPermission("multichat.chat.colour.simple")||player.hasPermission("multichat.chat.color.simple")), (player.hasPermission("multichat.chat.colour")||player.hasPermission("multichat.chat.color")||player.hasPermission("multichat.chat.colour.rgb")||player.hasPermission("multichat.chat.color.rgb")));

						// Message passes through to spigot here

						// Send message directly to global chat...
						ProxyLocalCommunicationManager.sendPlayerDirectChatMessage("global", sender.getName(), message, ((ProxiedPlayer)sender).getServer().getInfo());

						if (MultiChatProxy.getInstance().getDataStore().getHiddenStaff().contains(player.getUniqueId())) {
							MultiChatProxy.getInstance().getDataStore().getHiddenStaff().remove(player.getUniqueId());
						}

					}
				}

			}

		} else {
			MessageManager.sendMessage(sender, "command_global_only_players");
		}
	}
}
