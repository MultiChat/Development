package xyz.olivermartin.multichat.bungee.commands;

import java.util.Optional;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.BungeeComm;
import xyz.olivermartin.multichat.bungee.Channel;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ChatModeManager;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.DebugManager;
import xyz.olivermartin.multichat.bungee.Events;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.bungee.MultiChatUtil;

/**
 * Global Command
 * <p>Causes players to see messages sent from all servers in the global chat</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class GlobalCommand extends Command {

	public GlobalCommand() {
		super("global", "multichat.chat.mode", (String[]) ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("globalcommand").toArray(new String[0]));
	}

	public void execute(CommandSender sender, String[] args) {

		if ((sender instanceof ProxiedPlayer)) {

			if (args.length < 1) {

				ChatModeManager.getInstance().setGlobal(((ProxiedPlayer)sender).getUniqueId());

				MessageManager.sendMessage(sender, "command_global_enabled_1");
				MessageManager.sendMessage(sender, "command_global_enabled_2");

			} else {

				ProxiedPlayer player = (ProxiedPlayer)sender;
				String message = MultiChatUtil.getMessageFromArgs(args);

				if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean("global") == true) {

					if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_global").contains(player.getServer().getInfo().getName())) {

						if (ConfigManager.getInstance().getHandler("config.yml").getConfig().getBoolean("fetch_spigot_display_names") == true) {
							BungeeComm.sendMessage(player.getName(), player.getServer().getInfo());
						}

						if ((!MultiChat.frozen) || (player.hasPermission("multichat.chat.always"))) {

							if (ChatControl.isMuted(player.getUniqueId(), "global_chat")) {
								MessageManager.sendMessage(player, "mute_cannot_send_message");
								return;
							}

							DebugManager.log(player.getName() + "- about to check for spam");

							if (ChatControl.handleSpam(player, message, "global_chat")) {
								DebugManager.log(player.getName() + " - chat message being cancelled due to spam");
								return;
							}

							Optional<String> crm;

							crm = ChatControl.applyChatRules(message, "global_chat", player.getName());

							if (crm.isPresent()) {
								message = crm.get();
							} else {
								return;
							}

							if (!player.hasPermission("multichat.chat.link")) {
								message = ChatControl.replaceLinks(message);
							}
							
							// If they had this channel hidden, then unhide it...
							Channel global = Channel.getGlobalChannel();
							if (!global.isMember(player.getUniqueId())) {
								global.removeMember(player.getUniqueId());
								MessageManager.sendSpecialMessage(player, "command_channel_show", "GLOBAL");
							}

							// Let server know players channel preference
							BungeeComm.sendPlayerChannelMessage(player.getName(), Channel.getChannel(player.getUniqueId()).getName(), Channel.getChannel(player.getUniqueId()), player.getServer().getInfo(), (player.hasPermission("multichat.chat.colour")||player.hasPermission("multichat.chat.color")||player.hasPermission("multichat.chat.colour.simple")||player.hasPermission("multichat.chat.color.simple")), (player.hasPermission("multichat.chat.colour")||player.hasPermission("multichat.chat.color")||player.hasPermission("multichat.chat.colour.rgb")||player.hasPermission("multichat.chat.color.rgb")));

							// Message passes through to spigot here

							// Send message directly to global chat...

							BungeeComm.sendPlayerCommandMessage("!SINGLE G MESSAGE!" + message, sender.getName(), ((ProxiedPlayer)sender).getServer().getInfo());

							if (Events.hiddenStaff.contains(player.getUniqueId())) {
								Events.hiddenStaff.remove(player.getUniqueId());
							}

						} else {
							MessageManager.sendMessage(player, "freezechat_frozen");
						}

					}
				}

			}

		} else {
			MessageManager.sendMessage(sender, "command_global_only_players");
		}
	}
}
