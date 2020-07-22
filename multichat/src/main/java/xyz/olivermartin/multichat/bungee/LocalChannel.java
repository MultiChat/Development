package xyz.olivermartin.multichat.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;

public class LocalChannel extends LegacyChannel {

	public LocalChannel() {
		super("local", "", false, false);
	}

	/**
	 * This has no purpose as local chat for players is handled by the local servers
	 */
	@Override
	public void sendMessage(ProxiedPlayer sender, String message, String format) {
		/* EMPTY */
	}

	@Override
	public void sendMessage(String message, CommandSender sender) {

		DebugManager.log("LocalChannel wants to send a cast message!");

		// Use this to relay CASTS to local chat!
		if (sender instanceof ProxiedPlayer) {
			ProxyLocalCommunicationManager.sendServerChatMessage("local", message, ((ProxiedPlayer)sender).getServer().getInfo());
		}

	}

}
