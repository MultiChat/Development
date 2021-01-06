package xyz.olivermartin.multichat.local.common.listeners.communication;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeObjectMessage;

public abstract class LocalPlayerDataListener {

	protected abstract Optional<MultiChatLocalPlayer> getPlayerFromName(String playername);

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

			String channelFormat = message.readUTF();
			MultiChatLocal.getInstance().getDataStore().getChannelFormats().put(channelName, channelFormat);

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

			return true;

		} catch (IOException e) {

			e.printStackTrace();
			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred trying to read player data message from Bungeecord, is the server lagging?");
			return false;

		}

	}

}
