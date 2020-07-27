package xyz.olivermartin.multichat.local.common.listeners.communication;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import xyz.olivermartin.multichat.local.common.LocalConsoleLogger;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeObjectMessage;

public abstract class LocalPlayerDataListener {

	protected abstract Optional<MultiChatLocalPlayer> getPlayerFromName(String playername);

	protected boolean handleMessage(LocalBungeeObjectMessage message) {

		LocalConsoleLogger logger = MultiChatLocal.getInstance().getConsoleLogger();

		try {

			logger.debug("Starting processing of pdata message");

			Optional<MultiChatLocalPlayer> opPlayer = getPlayerFromName(message.readUTF());

			if (!opPlayer.isPresent()) return true;

			MultiChatLocalPlayer player = opPlayer.get();

			logger.debug("Player is present: " + player.getName());

			String channelName = message.readUTF();

			logger.debug("Channel is present: " + channelName);

			Map<UUID, String> playerChannels = MultiChatLocal.getInstance().getDataStore().getPlayerChannels();
			synchronized (playerChannels) {
				playerChannels.put(player.getUniqueId(), channelName);
			}

			String channelFormat = message.readUTF();
			MultiChatLocal.getInstance().getDataStore().getChannelFormats().put(channelName, channelFormat);

			boolean colour = message.readBoolean();

			logger.debug("Colour: " + colour);

			boolean rgb = message.readBoolean();

			logger.debug("RGB: " + rgb);

			Map<UUID, Boolean> simpleColourMap = MultiChatLocal.getInstance().getDataStore().getSimpleColourMap();
			Map<UUID, Boolean> rgbColourMap = MultiChatLocal.getInstance().getDataStore().getRGBColourMap();
			synchronized (simpleColourMap) {
				simpleColourMap.put(player.getUniqueId(), colour);
			}
			synchronized (rgbColourMap) {
				rgbColourMap.put(player.getUniqueId(), rgb);
			}

			/*boolean whitelistMembers = message.readBoolean();
			List<UUID> channelMembers = (List<UUID>) message.readObject();

			LocalPseudoChannel channelObject = new LocalPseudoChannel(channelName, channelMembers, whitelistMembers);
			Map<String, LocalPseudoChannel> channelObjects = MultiChatLocal.getInstance().getDataStore().getChannelObjects();
			synchronized (channelObjects) {
				channelObjects.put(channelName, channelObject);
			}*/

			return true;

		} catch (IOException e) {

			e.printStackTrace();
			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred trying to read player data message from Bungeecord, is the server lagging?");
			return false;

		}

	}

}
