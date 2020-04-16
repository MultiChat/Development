package xyz.olivermartin.multichat.local.platform.sponge.listeners;

import java.io.IOException;

import org.spongepowered.api.network.ChannelBuf;

import xyz.olivermartin.multichat.local.listeners.LocalBungeeMessage;

public class SpongeBungeeMessage implements LocalBungeeMessage {

	private ChannelBuf data;

	public SpongeBungeeMessage(ChannelBuf data) {
		this.data = data;
	}

	@Override
	public String readUTF() throws IOException {
		return data.readUTF();
	}

}
