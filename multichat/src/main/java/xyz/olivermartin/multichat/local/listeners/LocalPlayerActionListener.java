package xyz.olivermartin.multichat.local.listeners;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import xyz.olivermartin.multichat.local.MultiChatLocal;

public abstract class LocalPlayerActionListener {

	protected abstract void executeCommandForPlayersMatchingRegex(String playerRegex, String command);

	protected abstract void sendChatAsPlayer(String playerName, String rawMessage);

	protected boolean handleMessage(LocalBungeeMessage message) {

		try {

			String playerRegex = message.readUTF();
			String command = message.readUTF();

			// Handle the local global direct message hack
			if (isHackedMessage(command)) {
				handleHackedMessage(command, playerRegex);
				return true;
			}

			executeCommandForPlayersMatchingRegex(playerRegex, command);

			return true;

		} catch (IOException e) {

			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred trying to read local player action message from Bungeecord, is the server lagging?");
			return false;

		}

	}

	private boolean isHackedMessage(String command) {
		return (command.startsWith("!SINGLE L MESSAGE!") || command.startsWith("!SINGLE G MESSAGE!"));
	}

	private void handleHackedMessage(String command, String player) {

		String message = command.substring("!SINGLE X MESSAGE!".length(), command.length());

		if (MultiChatLocal.getInstance().getDataStore().chatQueues.containsKey(player.toLowerCase())) {

			Queue<String> chatQueue = MultiChatLocal.getInstance().getDataStore().chatQueues.get(player.toLowerCase());
			chatQueue.add(command);

		} else {

			Queue<String> chatQueue = new LinkedList<String>();
			chatQueue.add(command);
			MultiChatLocal.getInstance().getDataStore().chatQueues.put(player.toLowerCase(), chatQueue);

		}

		sendChatAsPlayer(player, message);

	}

}
