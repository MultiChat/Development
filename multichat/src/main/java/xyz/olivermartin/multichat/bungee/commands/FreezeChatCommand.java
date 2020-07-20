package xyz.olivermartin.multichat.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import xyz.olivermartin.multichat.bungee.MessageManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyDataStore;

/**
 * Freeze Chat Command
 * <p>Allows staff members to block all chat messages being sent</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class FreezeChatCommand extends Command {

	private static String[] aliases = new String[] {};

	public FreezeChatCommand() {
		super("freezechat", "multichat.chat.freeze", aliases);
	}

	public void execute(CommandSender sender, String[] args) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		if (ds.isChatFrozen()) {

			for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
				MessageManager.sendSpecialMessage(onlineplayer, "command_freezechat_thawed", sender.getName());
			}

			ds.setChatFrozen(false);

		} else {

			for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
				MessageManager.sendSpecialMessage(onlineplayer, "command_freezechat_frozen", sender.getName());
			}

			ds.setChatFrozen(true);

		}
	}
}
