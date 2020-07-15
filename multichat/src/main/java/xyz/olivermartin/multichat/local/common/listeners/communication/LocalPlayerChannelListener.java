package xyz.olivermartin.multichat.local.common.listeners.communication;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
			Map<UUID, String> playerChannels = MultiChatLocal.getInstance().getDataStore().getPlayerChannels();
			synchronized (playerChannels) {
				playerChannels.put(player.getUniqueId(), channelName);
			}

			boolean colour = message.readBoolean();
			boolean rgb = message.readBoolean();
			Map<UUID, Boolean> simpleColourMap = MultiChatLocal.getInstance().getDataStore().getSimpleColourMap();
			Map<UUID, Boolean> rgbColourMap = MultiChatLocal.getInstance().getDataStore().getRGBColourMap();
			synchronized (simpleColourMap) {
				simpleColourMap.put(player.getUniqueId(), colour);
			}
			synchronized (rgbColourMap) {
				rgbColourMap.put(player.getUniqueId(), rgb);
			}

			boolean whitelistMembers = message.readBoolean();
			List<UUID> channelMembers = (List<UUID>) message.readObject();

			LocalPseudoChannel channelObject = new LocalPseudoChannel(channelName, channelMembers, whitelistMembers);
			Map<String, LocalPseudoChannel> channelObjects = MultiChatLocal.getInstance().getDataStore().getChannelObjects();
			synchronized (channelObjects) {
				channelObjects.put(channelName, channelObject);
			}

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
