package com.olivermartin410.plugins;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding.RawDataChannel;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
public class MultiChatRawDataListener implements RawDataListener {

	private RawDataChannel channel;
	
	public MultiChatRawDataListener(RawDataChannel channel) {
		super();
		this.channel = channel;
	}
	
	    @Override
	    public void handlePayload(ChannelBuf data, RemoteConnection connection, Platform.Type side) {
	    	
	        Optional<Player> player = Sponge.getServer().getPlayer(data.getUTF(0));
	        try {
	        Player p = player.get();
	        if (p.getOption("prefix").isPresent()) {
	        	if (p.getOption("suffix").isPresent()) {
	        		if (!p.getDisplayNameData().displayName().get().toPlain().contains(p.getOption("prefix").get())) {
	        			channel.sendTo(p,buffer -> buffer.writeUTF(p.getOption("prefix").get() + p.getDisplayNameData().displayName().get().toPlain() + p.getOption("suffix").get()).writeUTF(p.getName()));
	        		} else {
	        			channel.sendTo(p,buffer -> buffer.writeUTF(p.getDisplayNameData().displayName().get().toPlain()).writeUTF(p.getName()));
	        		}
	        	} else {
	        		if (!p.getDisplayNameData().displayName().get().toPlain().contains(p.getOption("prefix").get())) {
	        			channel.sendTo(p,buffer -> buffer.writeUTF(p.getOption("prefix").get() + p.getDisplayNameData().displayName().get().toPlain()).writeUTF(p.getName()));
	        		} else {
	        			channel.sendTo(p,buffer -> buffer.writeUTF(p.getDisplayNameData().displayName().get().toPlain()).writeUTF(p.getName()));
	        		}
	        	}
	        	
	        } else {
	        	channel.sendTo(p,buffer -> buffer.writeUTF(p.getDisplayNameData().displayName().get().toPlain()).writeUTF(p.getName()));
	        }
	        } catch (NoSuchElementException e) {
	        	System.err.println("[MultiChat] An error occurred getting player details, is the server lagging?");
	        }

	    }
}
