package com.olivermartin410.plugins;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.network.ChannelBinding.IndexedMessageChannel;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "multichat", name = "MultiChat - Sponge Bridge", version = "1.0")
public final class SpongeComm {

	IndexedMessageChannel c;
	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(stream);
	
	public void onServerStart(GameStartedServerEvent event) {
		//Sponge.getEventManager().registerListeners(this, this);
		c = Sponge.getChannelRegistrar().createChannel(this, "MultiChat");
    }
	
	public void onServerStop(GameStoppingServerEvent event) {
		Sponge.getChannelRegistrar().unbindChannel(c);
    }
	
	@Listener
	public void onPluginMessage(MessageChannelEvent e) {
		if (e.getChannel().toString().equals("MultiChat")) {
			String displayName;
			displayName = Sponge.getServer().getPlayer(e.getMessage().toString()).get().getDisplayNameData().displayName().get().toString();
		    try
		    {
		      out.writeUTF(displayName);
		      out.writeUTF(Sponge.getServer().getPlayer(e.getMessage().toString()).get().getName());
		    }
		    catch (IOException e1)
		    {
		      e1.printStackTrace();
		    }
		    SpongeMultiChatMessage m = new SpongeMultiChatMessage();
		    m.displayName = displayName;
		    m.name = Sponge.getServer().getPlayer(e.getMessage().toString()).get().getName();
			c.sendToAll(m);
		}
	}

}
