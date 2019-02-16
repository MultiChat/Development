package xyz.olivermartin.multichat.spongebridge.listeners;

import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.network.ChannelBinding.RawDataChannel;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.text.serializer.TextSerializers;

public class BungeeChatListener implements RawDataListener {

	public BungeeChatListener(RawDataChannel channel) {
		super();
	}

	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Type side) {

		String message = data.readUTF();
		Sponge.getServer().getBroadcastChannel().send(TextSerializers.FORMATTING_CODE.deserialize(message));

	}

}
