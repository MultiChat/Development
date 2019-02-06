package xyz.olivermartin.multichat.spongebridge.listeners;

import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.network.ChannelBinding.RawDataChannel;

public class BungeeChatListener implements RawDataListener {

	public BungeeChatListener(RawDataChannel channel) {
		super();
	}
	
	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Type side) {
		
		// TODO Auto-generated method stub

	}

}
