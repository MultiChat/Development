package xyz.olivermartin.multichat.local.common.listeners.communication;

import java.io.IOException;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeMessage;

public abstract class LocalServerActionListener {

	protected abstract void executeCommandAsConsole(String command);

	protected boolean handleMessage(LocalBungeeMessage message) {

		try {

			String command = message.readUTF();

			// HANDLE LEGACY SERVER HACK
			if (isHackedMessage(command)) {
				handleHackedMessage(command);
				return true;
			}

			executeCommandAsConsole(command);
			return true;

		} catch (IOException e) {

			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred trying to read local action message from Bungeecord, is the server lagging?");
			return false;

		}

	}

	private boolean isHackedMessage(String command) {
		return (command.equals("!!!LEGACYSERVER!!!") || command.equals("!!!NOTLEGACYSERVER!!!"));
	}

	private void handleHackedMessage(String command) {

		if (command.equals("!!!LEGACYSERVER!!!")) {
			MultiChatLocal.getInstance().getDataStore().setLegacy(true);
		} else {
			MultiChatLocal.getInstance().getDataStore().setLegacy(false);
		}

	}

}
