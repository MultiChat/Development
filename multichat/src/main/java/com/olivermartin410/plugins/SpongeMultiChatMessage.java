package com.olivermartin410.plugins;

import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.Message;

public class SpongeMultiChatMessage implements Message {

	public String displayName;
	public String name;
	
	@Override
	public void readFrom(ChannelBuf arg0) {
		//
		
	}

	@Override
	public void writeTo(ChannelBuf arg0) {
		arg0.writeUTF(displayName);
		arg0.writeUTF(name);
		
	}

	
	
}
