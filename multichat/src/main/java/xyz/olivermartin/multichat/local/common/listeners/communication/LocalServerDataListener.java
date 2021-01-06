package xyz.olivermartin.multichat.local.common.listeners.communication;

import java.io.IOException;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeObjectMessage;

public abstract class LocalServerDataListener {

	protected boolean handleMessage(LocalBungeeObjectMessage message) {

		try {

			/*
			 * This is for the sdata channel
			 * 
			 * The ids are:
			 * - global = Global chat info
			 * - dn = display name info
			 * - legacy = legacy server info
			 */

			String id = message.readUTF();

			switch (id) {

			case "global":

				boolean globalServer;
				String globalChatFormat;

				globalServer = message.readBoolean();
				globalChatFormat = message.readUTF();

				MultiChatLocal.getInstance().getDataStore().setGlobalChatServer(globalServer);
				MultiChatLocal.getInstance().getDataStore().setGlobalChatFormat(globalChatFormat);

				break;

			case "dn":

				boolean setDisplayName;
				String displayNameFormat;

				setDisplayName = message.readBoolean();
				displayNameFormat = message.readUTF();

				MultiChatLocal.getInstance().getDataStore().setSetDisplayName(setDisplayName);
				MultiChatLocal.getInstance().getDataStore().setDisplayNameFormatLastVal(displayNameFormat);

				break;

			case "legacy":

				boolean isLegacy;

				isLegacy = message.readBoolean();

				MultiChatLocal.getInstance().getDataStore().setLegacy(isLegacy);

				break;

			default:

				// TODO No other ids exist at this point

				break;

			}

			return true;

		} catch (IOException e) {

			e.printStackTrace();
			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred trying to read local server data message from Bungeecord, is the server lagging?");
			return false;

		}

	}

}
