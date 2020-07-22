package xyz.olivermartin.multichat.proxy.common.listeners.communication;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import xyz.olivermartin.multichat.bungee.Channel;
import xyz.olivermartin.multichat.bungee.DebugManager;
import xyz.olivermartin.multichat.bungee.PlayerMeta;
import xyz.olivermartin.multichat.bungee.PlayerMetaManager;
import xyz.olivermartin.multichat.common.communication.CommChannels;

/**
 * Listener for communication over the Player Chat communication channel
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ProxyPlayerChatListener implements Listener {

	@EventHandler
	public static void onPluginMessage(PluginMessageEvent event) {

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
		DataInputStream in = new DataInputStream(stream);

		UUID uuid;
		String channel;
		String message;
		String format;

		try {
			uuid = UUID.fromString(in.readUTF());
			DebugManager.log("{multichat:pchat} UUID = " + uuid);
			channel = in.readUTF();
			DebugManager.log("{multichat:pchat} Channel = " + channel);
			message = in.readUTF();
			DebugManager.log("{multichat:pchat} Message = " + message);
			format = in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		DebugManager.log("{multichat:pchat} Format (before removal of double chars) = " + format);
		format = format.replace("%%","%");
		DebugManager.log("{multichat:pchat} Format = " + format);

		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

		// If the player UUID doesn't exist, then ignore it
		if (player == null) {
			DebugManager.log("{multichat:pchat} Could not get player! Abandoning chat message... (Is IP-Forwarding on?)");
			return;
		}

		DebugManager.log("{multichat:pchat} Got player successfully! Name = " + player.getName());

		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(uuid);

		if (opm.isPresent()) {

			switch (channel) {

			case "global":
				DebugManager.log("{multichat:pchat} Global Channel Available? = " + (Channel.getGlobalChannel() != null));
				Channel.getGlobalChannel().sendMessage(player, message, format);
				break;

			case "local":
				DebugManager.log("{multichat:pchat} LOCAL SPY MESSAGE - Not yet implemented...?");
				break;

			default:
				DebugManager.log("{multichat:pchat} Channel: " + channel + ", is not recognised");
				return;

			}

		}

	}

}
