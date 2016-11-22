package com.olivermartin410.plugins;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.network.ChannelBinding;
import org.spongepowered.api.network.ChannelBinding.RawDataChannel;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "multichat", name = "MultiChat - Sponge Bridge", version = "1.0")
public final class SpongeComm {
	
	ChannelRegistrar channelRegistrar;
	RawDataChannel channel;
	
	@Listener
	public void onServerStart(GameStartedServerEvent event) {
		 channelRegistrar = Sponge.getGame().getChannelRegistrar();
	     ChannelBinding.RawDataChannel channel = Sponge.getGame().getChannelRegistrar().createRawChannel(this, "MultiChat");
	     channel.addListener(Platform.Type.SERVER, new MultiChatRawDataListener(channel));
	     this.channel = channel;
	}
	
	@Listener
	public void onServerStop(GameStoppingServerEvent event) {
		Sponge.getChannelRegistrar().unbindChannel(channel);
    }
	
	/*@Listener
	public void onPluginMessage(DropItemEvent e) {
		System.out.println("MESSAGE IN");
		//if (e.getChannel().toString().equals("MultiChat")) {
			System.out.println("ITS MULTICHAT!");
			String displayName;
			displayName = Sponge.getServer().getPlayer("Revilo410".toString()).get().getDisplayNameData().displayName().get().toString();
		    SpongeMultiChatMessage m = new SpongeMultiChatMessage();
		    m.displayName = displayName;
		    m.name = Sponge.getServer().getPlayer("Revilo410".toString()).get().getName();
			c.sendToAll(m);
			e.setCancelled(true);
		//}
	}*/

}
