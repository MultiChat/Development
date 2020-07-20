package xyz.olivermartin.multichat.local.common.listeners.communication;

import java.io.IOException;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeMessage;

public abstract class LocalPlayerChatListener {

	protected abstract void sendChatAsPlayer(String playerName, String rawMessage);

	protected boolean handleMessage(LocalBungeeMessage message) {

		try {

			// This is used to handle the direct /local and /global messages
			// Previously used the old "direct hack" in action listeners

			String channel = message.readUTF(); // TODO THIS NEEDS WORK
			String player = message.readUTF();
			String chatMessage = message.readUTF();

			switch (channel) {

			case "local":
				MultiChatLocal.getInstance().getChatManager().queueChatChannel(player, "local");
				break;
			case "global":
				MultiChatLocal.getInstance().getChatManager().queueChatChannel(player, "global");
				break;
			default:
				// TODO No other channels exist at this point
				break;
			}

			sendChatAsPlayer(player, chatMessage);

			return true;

		} catch (IOException e) {

			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred trying to read local direct player chat message from Bungeecord, is the server lagging?");
			return false;

		}

	}

}
