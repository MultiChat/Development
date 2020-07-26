package xyz.olivermartin.multichat.proxy.common.listeners;

import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;

public class ProxyServerSwitchListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onServerSwitch(ServerSwitchEvent event) {

		// Tell the new server the player's channel preference & if it is a legacy server
		ProxyServer.getInstance().getScheduler().schedule(MultiChatProxy.getInstance().getPlugin(), new Runnable() {

			public void run() {
				ProxyLocalCommunicationManager.sendPlayerDataMessage(event.getPlayer().getName(), MultiChatProxy.getInstance().getChannelManager().getChannel(event.getPlayer()), event.getPlayer().getServer().getInfo(), (event.getPlayer().hasPermission("multichat.chat.colour")||event.getPlayer().hasPermission("multichat.chat.color")||event.getPlayer().hasPermission("multichat.chat.colour.simple")||event.getPlayer().hasPermission("multichat.chat.color.simple")), (event.getPlayer().hasPermission("multichat.chat.colour")||event.getPlayer().hasPermission("multichat.chat.color")||event.getPlayer().hasPermission("multichat.chat.colour.rgb")||event.getPlayer().hasPermission("multichat.chat.color.rgb")));
				ProxyLocalCommunicationManager.sendLegacyServerData(event.getPlayer().getServer().getInfo());
			}

		}, 500L, TimeUnit.MILLISECONDS);

	}

}
