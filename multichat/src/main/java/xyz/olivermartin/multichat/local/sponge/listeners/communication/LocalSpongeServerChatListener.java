package xyz.olivermartin.multichat.local.sponge.listeners.communication;

import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.text.serializer.TextSerializers;

import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeMessage;
import xyz.olivermartin.multichat.local.common.listeners.communication.LocalServerChatListener;
import xyz.olivermartin.multichat.local.sponge.listeners.SpongeBungeeMessage;

public class LocalSpongeServerChatListener extends LocalServerChatListener implements RawDataListener {

	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Type side) {

		LocalBungeeMessage lbm = new SpongeBungeeMessage(data);

		handleMessage(lbm);

	}

	@Override
	protected void broadcastRawMessageToChat(String message) {
		Sponge.getServer().getBroadcastChannel().send(TextSerializers.FORMATTING_CODE.deserialize(message));
	}

}
