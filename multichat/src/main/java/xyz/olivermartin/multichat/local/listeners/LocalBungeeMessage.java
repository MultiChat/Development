package xyz.olivermartin.multichat.local.listeners;

import java.io.IOException;

public interface LocalBungeeMessage {

	public String readUTF() throws IOException;
	
}
