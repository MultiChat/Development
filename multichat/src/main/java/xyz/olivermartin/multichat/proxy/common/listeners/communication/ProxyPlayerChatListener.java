package xyz.olivermartin.multichat.proxy.common.listeners.communication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import xyz.olivermartin.multichat.bungee.DebugManager;
import xyz.olivermartin.multichat.bungee.PlayerMeta;
import xyz.olivermartin.multichat.bungee.PlayerMetaManager;
import xyz.olivermartin.multichat.common.communication.CommChannels;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;
import xyz.olivermartin.multichat.proxy.common.channels.proxy.ProxyChannel;

/**
 * Listener for communication over the Player Chat communication channel
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ProxyPlayerChatListener implements Listener {

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onPluginMessage(PluginMessageEvent event) {

		// Ignore if sent to a different channel
		if (!event.getTag().equals(CommChannels.PLAYER_CHAT)) return;

		event.setCancelled(true);

		/*
		 * Possible channels:
		 * - global
		 * - local (spy)
		 * 
		 * This range will expand in the future
		 */

		ByteArrayInputStream stream = new ByteArrayInputStream(event.getData());


		UUID uuid;
		String channel;
		String message;
		String format;
		Set<UUID> otherRecipients;

		try {

			ObjectInputStream in = new ObjectInputStream(stream);

			uuid = UUID.fromString(in.readUTF());
			DebugManager.log("{multichat:pchat} UUID = '" + uuid + "'");
			channel = in.readUTF();
			DebugManager.log("{multichat:pchat} Channel = '" + channel + "'");
			message = in.readUTF();
			DebugManager.log("{multichat:pchat} Message = '" + message + "'");
			format = in.readUTF();
			DebugManager.log("{multichat:pchat} Format = '" + format + "'");
			otherRecipients = (Set<UUID>) in.readObject();

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}

		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

		// If the player UUID doesn't exist, then ignore it
		if (player == null) {
			DebugManager.log("{multichat:pchat} Could not get player! Abandoning chat message... (Is IP-Forwarding on?)");
			return;
		}

		DebugManager.log("{multichat:pchat} Got player successfully! Name = '" + player.getName() + "'");

		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(uuid);
		ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();

		if (opm.isPresent()) {

			switch (channel) {

			case "global":
				channelManager.getGlobalChannel().distributeMessage(player, message, format, otherRecipients);
				break;

			case "local":
				channelManager.getLocalChannel().distributeMessage(player, message, format, otherRecipients);
				break;

			default:

				Optional<ProxyChannel> opProxyChannel = channelManager.getProxyChannel(channel);

				if (opProxyChannel.isPresent()) {

					ProxyChannel proxyChannel = opProxyChannel.get();
					proxyChannel.distributeMessage(player, message, format, otherRecipients);

				} else {
					DebugManager.log("{multichat:pchat} Channel: " + channel + ", is not recognised");
				}

				return;

			}

		}

	}

}
