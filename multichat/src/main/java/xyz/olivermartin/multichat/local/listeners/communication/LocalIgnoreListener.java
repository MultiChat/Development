package xyz.olivermartin.multichat.local.listeners.communication;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.listeners.LocalBungeeObjectMessage;

public abstract class LocalIgnoreListener {

	@SuppressWarnings("unchecked")
	protected boolean handleMessage(LocalBungeeObjectMessage message) {

		try {

			MultiChatLocal.getInstance().getConsoleLogger().debug("{multichat:ignore} Reading ignore map...");
			MultiChatLocal.getInstance().getDataStore().ignoreMap = (Map<UUID, Set<UUID>>) message.readObject();
			MultiChatLocal.getInstance().getConsoleLogger().debug("{multichat:ignore} Successfully read ignore map!");
			return true;

		} catch (IOException e) {

			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred trying to read local ignore message from Bungeecord, is the server lagging?");
			return false;

		} catch (ClassNotFoundException e) {

			MultiChatLocal.getInstance().getConsoleLogger().log("Could not read the ignore Map from local ignore message...");
			return false;
		}

	}

}
