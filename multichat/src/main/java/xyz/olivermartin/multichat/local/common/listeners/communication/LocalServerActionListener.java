package xyz.olivermartin.multichat.local.common.listeners.communication;

import java.io.IOException;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeMessage;

public abstract class LocalServerActionListener {

	protected abstract void executeCommandAsConsole(String command);

	protected boolean handleMessage(LocalBungeeMessage message) {

		try {

			String command = message.readUTF();
			executeCommandAsConsole(command);
			return true;

		} catch (IOException e) {

			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred trying to read local action message from Bungeecord, is the server lagging?");
			return false;

		}

	}

}
