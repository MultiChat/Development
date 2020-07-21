package xyz.olivermartin.multichat.bungee.commands;

import java.util.Optional;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.bungee.MultiChatUtil;
import xyz.olivermartin.multichat.bungee.PrivateMessageManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyDataStore;

/**
 * Reply Command
 * <p>Used to quickly reply to your last private message</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ReplyCommand extends Command {

	public ReplyCommand() {
		super("mcr", "multichat.chat.msg", (String[])ConfigManager.getInstance().getHandler("aliases.yml").getConfig().getStringList("r").toArray(new String[0]));
	}

	public void execute(CommandSender sender, String[] args) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		if (args.length < 1) {

			MessageManager.sendMessage(sender, "command_reply_usage");
			MessageManager.sendMessage(sender, "command_reply_desc");

		} else if ((sender instanceof ProxiedPlayer)) {

			String message = MultiChatUtil.getMessageFromArgs(args);

			Optional<String> crm;

			if (ChatControl.isMuted(((ProxiedPlayer)sender).getUniqueId(), "private_messages")) {
				MessageManager.sendMessage(sender, "mute_cannot_send_message");
				return;
			}

			if (ChatControl.handleSpam(((ProxiedPlayer)sender), message, "private_messages")) {
				return;
			}

			crm = ChatControl.applyChatRules(message, "private_messages", sender.getName());

			if (crm.isPresent()) {
				message = crm.get();
			} else {
				return;
			}

			if (ds.getLastMsg().containsKey(((ProxiedPlayer)sender).getUniqueId())) {

				if (ProxyServer.getInstance().getPlayer((UUID)ds.getLastMsg().get(((ProxiedPlayer)sender).getUniqueId())) != null) {

					ProxiedPlayer target = ProxyServer.getInstance().getPlayer((UUID)ds.getLastMsg().get(((ProxiedPlayer)sender).getUniqueId()));

					if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_pm").contains(((ProxiedPlayer)sender).getServer().getInfo().getName())) {

						if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_pm").contains(target.getServer().getInfo().getName())) {

							if (ChatControl.ignores(((ProxiedPlayer)sender).getUniqueId(), target.getUniqueId(), "private_messages")) {
								ChatControl.sendIgnoreNotifications(target, sender, "private_messages");
								return;
							}

							PrivateMessageManager.getInstance().sendMessage(message, (ProxiedPlayer)sender, target);

						} else {
							MessageManager.sendMessage(sender, "command_msg_disabled_target");
						}

					} else {
						MessageManager.sendMessage(sender, "command_msg_disabled_sender");
					}

				} else if ( ds.getLastMsg().get( ((ProxiedPlayer)sender ).getUniqueId()).equals(new UUID(0L, 0L)) ) {

					// Console target stuff

					if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_pm").contains(((ProxiedPlayer)sender).getServer().getInfo().getName())) {

						PrivateMessageManager.getInstance().sendMessageConsoleTarget(message, (ProxiedPlayer)sender);

					} else {
						MessageManager.sendMessage(sender, "command_msg_disabled_sender");
					}

					// End console target stuff

				} else {
					MessageManager.sendMessage(sender, "command_reply_no_one_to_reply_to");
				}

			} else {
				MessageManager.sendMessage(sender, "command_reply_no_one_to_reply_to");
			}

		} else {

			// New console reply

			String message = MultiChatUtil.getMessageFromArgs(args);

			if (ds.getLastMsg().containsKey(new UUID(0L,0L))) {

				if (ProxyServer.getInstance().getPlayer((UUID)ds.getLastMsg().get((new UUID(0L,0L)))) != null) {

					ProxiedPlayer target = ProxyServer.getInstance().getPlayer((UUID)ds.getLastMsg().get((new UUID(0L,0L))));

					if (!ConfigManager.getInstance().getHandler("config.yml").getConfig().getStringList("no_pm").contains(target.getServer().getInfo().getName())) {

						PrivateMessageManager.getInstance().sendMessageConsoleSender(message, target);

					} else {
						MessageManager.sendMessage(sender, "command_msg_disabled_target");
					}

				} else {
					MessageManager.sendMessage(sender, "command_reply_no_one_to_reply_to");
				}

			} else {
				MessageManager.sendMessage(sender, "command_reply_no_one_to_reply_to");
			}

			// End new console stuff

		}
	}
}
