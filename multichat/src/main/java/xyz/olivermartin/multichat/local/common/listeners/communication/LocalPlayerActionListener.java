package xyz.olivermartin.multichat.local.common.listeners.communication;

import java.io.IOException;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeMessage;

public abstract class LocalPlayerActionListener {

	protected abstract void executeCommandForPlayersMatchingRegex(String playerRegex, String command);

	protected boolean handleMessage(LocalBungeeMessage message) {

		try {

			String playerRegex = message.readUTF();
			String command = message.readUTF();

			executeCommandForPlayersMatchingRegex(playerRegex, command);

			return true;

		} catch (IOException e) {

			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred trying to read local player action message from Bungeecord, is the server lagging?");
			return false;

		}

	}

}
