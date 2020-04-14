package xyz.olivermartin.multichat.local.listeners.communication;

import java.io.IOException;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.listeners.LocalBungeeMessage;

public abstract class LocalCastListener {

	protected boolean handleMessage(LocalBungeeMessage message) {

		try {

			String castMessage = message.readUTF();
			broadcastRawMessageToChat(castMessage);
			return true;

		} catch (IOException e) {

			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred trying to read local cast message from Bungeecord, is the server lagging?");
			return false;

		}

	}

	protected abstract void broadcastRawMessageToChat(String message);

}
