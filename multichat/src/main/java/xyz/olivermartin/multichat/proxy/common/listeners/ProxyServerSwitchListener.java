package xyz.olivermartin.multichat.proxy.common.listeners;

import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;

public class ProxyServerSwitchListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onServerSwitch(ServerSwitchEvent event) {

		// Tell the new server the player's channel preference & if it is a legacy server
		ProxyServer.getInstance().getScheduler().schedule(MultiChatProxy.getInstance().getPlugin(), new Runnable() {

			public void run() {

				ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();
				String channelFormat;

				switch (channelManager.getChannel(event.getPlayer())) {

				case "global":
					channelFormat = channelManager.getGlobalChannel().getInfo().getFormat();
					break;
				case "local":
					channelFormat = channelManager.getLocalChannel().getFormat();
					break;
				default:
					if (channelManager.existsProxyChannel(channelManager.getChannel(event.getPlayer()))) {
						channelFormat = channelManager.getProxyChannel(channelManager.getChannel(event.getPlayer())).get().getInfo().getFormat();
					} else {
						channelFormat = channelManager.getGlobalChannel().getInfo().getFormat();
					}
					break;
				}

				ProxyLocalCommunicationManager.sendPlayerDataMessage(event.getPlayer().getName(), MultiChatProxy.getInstance().getChannelManager().getChannel(event.getPlayer()), channelFormat, event.getPlayer().getServer().getInfo(), (event.getPlayer().hasPermission("multichat.chat.colour")||event.getPlayer().hasPermission("multichat.chat.color")||event.getPlayer().hasPermission("multichat.chat.colour.simple")||event.getPlayer().hasPermission("multichat.chat.color.simple")), (event.getPlayer().hasPermission("multichat.chat.colour")||event.getPlayer().hasPermission("multichat.chat.color")||event.getPlayer().hasPermission("multichat.chat.colour.rgb")||event.getPlayer().hasPermission("multichat.chat.color.rgb")));
				ProxyLocalCommunicationManager.sendLegacyServerData(event.getPlayer().getServer().getInfo());

				if (ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getBoolean(ConfigValues.Config.FETCH_SPIGOT_DISPLAY_NAMES) == true) {

					ProxiedPlayer player = event.getPlayer();
					if (player.getServer() != null) {
						ProxyLocalCommunicationManager.sendUpdatePlayerMetaRequestMessage(player.getName(), player.getServer().getInfo());
					}

				}
			}

		}, 500L, TimeUnit.MILLISECONDS);

	}

}
