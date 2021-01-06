package xyz.olivermartin.multichat.local.common.listeners.communication;

import java.io.IOException;
import java.util.Optional;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeMessage;

public abstract class LocalPlayerMetaListener {

	protected abstract Optional<MultiChatLocalPlayer> getPlayerFromName(String playername);

	protected boolean handleMessage(LocalBungeeMessage message) {

		try {

			Optional<MultiChatLocalPlayer> opPlayer = getPlayerFromName(message.readUTF());

			if (!opPlayer.isPresent()) return true;

			MultiChatLocalPlayer player = opPlayer.get();

			MultiChatLocal.getInstance().getProxyCommunicationManager().updatePlayerMeta(player.getUniqueId());

			return true;

		} catch (IOException e) {

			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred trying to read local player meta message from Bungeecord, is the server lagging?");
			return false;

		}

	}

}
