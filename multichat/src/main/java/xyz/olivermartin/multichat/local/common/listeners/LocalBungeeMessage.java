package xyz.olivermartin.multichat.local.common.listeners;

import java.io.IOException;

public interface LocalBungeeMessage {

	public String readUTF() throws IOException;
	
}
