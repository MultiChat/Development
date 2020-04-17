package xyz.olivermartin.multichat.local.sponge.listeners;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.spongepowered.api.network.ChannelBuf;

import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeObjectMessage;

public class SpongeBungeeObjectMessage implements LocalBungeeObjectMessage {

	private ObjectInputStream in;

	public SpongeBungeeObjectMessage(ChannelBuf data) throws IOException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		while (data.available() > 0) {
			try {
				outputStream.write(data.readBytes(1));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ByteArrayInputStream stream = new ByteArrayInputStream(outputStream.toByteArray());

		this.in = new ObjectInputStream(stream);

	}

	@Override
	public String readUTF() throws IOException {
		return in.readUTF();
	}

	@Override
	public Object readObject() throws ClassNotFoundException, IOException {
		return in.readObject();
	}

	@Override
	public boolean readBoolean() throws IOException {
		return in.readBoolean();
	}

}
