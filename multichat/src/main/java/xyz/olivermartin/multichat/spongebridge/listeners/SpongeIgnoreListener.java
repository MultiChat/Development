package xyz.olivermartin.multichat.spongebridge.listeners;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;

import xyz.olivermartin.multichat.spongebridge.MultiChatSponge;

public class SpongeIgnoreListener implements RawDataListener {

	@SuppressWarnings("unchecked")
	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Type side) {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

		while (data.available() > 0) {
			try {
				outputStream.write(data.readBytes(1));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ByteArrayInputStream stream = new ByteArrayInputStream(outputStream.toByteArray());
		try {
			ObjectInputStream oin = new ObjectInputStream(stream);

			MultiChatSponge.notifyIgnore = oin.readBoolean();
			MultiChatSponge.notifyIgnoreMessage = oin.readUTF();
			MultiChatSponge.ignoreMap = (Map<UUID, Set<UUID>>) oin.readObject();

		} catch (IOException e1) {
			System.err.println("[MultiChat] An error occurred getting player details, is the server lagging?");
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("[MultiChat] An error occurred getting player details, is the server lagging?");
			e.printStackTrace();
		}

	}

}
