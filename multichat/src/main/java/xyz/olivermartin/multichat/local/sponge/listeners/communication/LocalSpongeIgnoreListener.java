package xyz.olivermartin.multichat.local.sponge.listeners.communication;

import java.io.IOException;

import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeObjectMessage;
import xyz.olivermartin.multichat.local.common.listeners.communication.LocalIgnoreListener;
import xyz.olivermartin.multichat.local.sponge.listeners.SpongeBungeeObjectMessage;

public class LocalSpongeIgnoreListener extends LocalIgnoreListener implements RawDataListener {

	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Type side) {

		try {
			LocalBungeeObjectMessage lbm = new SpongeBungeeObjectMessage(data);

			handleMessage(lbm);

		} catch (IOException e) {
			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred reading the object stream in the local ignore listener...");
			return;
		}

	}

}
