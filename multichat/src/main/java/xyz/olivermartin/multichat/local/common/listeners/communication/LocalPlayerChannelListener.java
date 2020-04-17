package xyz.olivermartin.multichat.local.common.listeners.communication;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import xyz.olivermartin.multichat.local.common.LocalPseudoChannel;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeObjectMessage;

public abstract class LocalPlayerChannelListener {

	protected abstract Optional<MultiChatLocalPlayer> getPlayerFromName(String playername);

	@SuppressWarnings("unchecked")
	protected boolean handleMessage(LocalBungeeObjectMessage message) {

		try {

			Optional<MultiChatLocalPlayer> opPlayer = getPlayerFromName(message.readUTF());

			if (!opPlayer.isPresent()) return true;

			MultiChatLocalPlayer player = opPlayer.get();

			String channelName = message.readUTF();
			MultiChatLocal.getInstance().getDataStore().playerChannels.put(player.getUniqueId(), channelName);

			boolean colour = message.readBoolean();
			MultiChatLocal.getInstance().getDataStore().colourMap.put(player.getUniqueId(), colour);

			boolean whitelistMembers = message.readBoolean();
			List<UUID> channelMembers = (List<UUID>) message.readObject();

			LocalPseudoChannel channelObject = new LocalPseudoChannel(channelName, channelMembers, whitelistMembers);
			MultiChatLocal.getInstance().getDataStore().channelObjects.put(channelName, channelObject);

			return true;

		} catch (IOException e) {

			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred trying to read local channel message from Bungeecord, is the server lagging?");
			return false;

		} catch (ClassNotFoundException e) {

			MultiChatLocal.getInstance().getConsoleLogger().log("Could not read List of UUIDs from local channel message...");
			return false;

		}

	}

}
