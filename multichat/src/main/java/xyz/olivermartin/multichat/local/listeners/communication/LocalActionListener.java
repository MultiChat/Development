package xyz.olivermartin.multichat.local.listeners.communication;

import java.io.IOException;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.listeners.LocalBungeeMessage;

public abstract class LocalActionListener {

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
