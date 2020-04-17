package xyz.olivermartin.multichat.local.spigot.listeners;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeMessage;

public class SpigotBungeeMessage implements LocalBungeeMessage {
	
	DataInputStream in;
	
	public SpigotBungeeMessage(byte[] message) {
		ByteArrayInputStream stream = new ByteArrayInputStream(message);
		this.in = new DataInputStream(stream);
	}

	@Override
	public String readUTF() throws IOException {
		return in.readUTF();
	}
	
}
