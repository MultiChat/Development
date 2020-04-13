package xyz.olivermartin.multichat.local.listeners;

import java.io.IOException;

public interface LocalBungeeObjectMessage extends LocalBungeeMessage {

	public Object readObject() throws ClassNotFoundException, IOException;
	
	public boolean readBoolean() throws IOException;
	
}
