package xyz.olivermartin.multichat.local.listeners;

import java.io.IOException;
import java.util.Optional;

import xyz.olivermartin.multichat.local.MultiChatLocal;
import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;

public abstract class LocalPlayerMetaListener {

	protected abstract Optional<MultiChatLocalPlayer> getPlayerFromName(String playername);

	protected boolean handleMessage(LocalBungeeMessage message) {

		try {

			boolean setDisplayName = false;
			boolean globalChat = false;
			String displayNameFormat = "";

			Optional<MultiChatLocalPlayer> opPlayer = getPlayerFromName(message.readUTF());

			if (!opPlayer.isPresent()) return true;

			MultiChatLocalPlayer player = opPlayer.get();

			if (message.readUTF().equals("T")) {
				setDisplayName = true;
			}

			displayNameFormat = message.readUTF();

			MultiChatLocal.getInstance().getDataStore().setDisplayName = setDisplayName;
			MultiChatLocal.getInstance().getDataStore().displayNameFormatLastVal = displayNameFormat;

			MultiChatLocal.getInstance().getProxyCommunicationManager().updatePlayerMeta(player.getUniqueId());

			if (message.readUTF().equals("T")) {
				globalChat = true;
			}

			MultiChatLocal.getInstance().getDataStore().globalChatServer = globalChat;

			MultiChatLocal.getInstance().getDataStore().globalChatFormat = message.readUTF();

			return true;

		} catch (IOException e) {

			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred trying to read local player meta message from Bungeecord, is the server lagging?");
			return false;

		}

	}

}
