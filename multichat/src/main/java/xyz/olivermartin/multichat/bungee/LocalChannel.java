package xyz.olivermartin.multichat.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LocalChannel extends Channel {

	public LocalChannel() {
		super("local", "", false, false);
	}

	@Override
	public void sendMessage(ProxiedPlayer sender, String message, String format, boolean local, String playerList) {
		/* EMPTY */
	}

	@Override
	public void sendMessage(String message) {
		/* EMPTY */
	}

}
