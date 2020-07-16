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
import xyz.olivermartin.multichat.bungee.PlayerMeta;
import xyz.olivermartin.multichat.bungee.PlayerMetaManager;
import xyz.olivermartin.multichat.common.communication.CommChannels;

/**
 * Listener for communication over the Player Meta communication channel
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ProxyPlayerMetaListener implements Listener {

	@EventHandler
	public static void onPluginMessage(PluginMessageEvent event) {

		// Ignore if sent to a different channel
		if (!event.getTag().equals(CommChannels.getPlayerMeta())) return;

		event.setCancelled(true);

		/*
		 * Possible meta IDs:
		 * - prefix
		 * - suffix
		 * - dn
		 * - world
		 * - nick
		 */

		ByteArrayInputStream stream = new ByteArrayInputStream(event.getData());
		DataInputStream in = new DataInputStream(stream);

		UUID uuid;
		String metaId;
		String metaValue;

		try {
			uuid = UUID.fromString(in.readUTF());
			metaId = in.readUTF();
			metaValue = in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);

		// If the player UUID doesn't exist, then ignore it
		if (player == null) return;

		synchronized (player) {

			Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(uuid);

			if (opm.isPresent()) {

				switch (metaId) {

				case "prefix":
					opm.get().prefix = metaValue;
					PlayerMetaManager.getInstance().updateDisplayName(uuid); // TODO Do we need this?
					break;

				case "suffix":
					opm.get().suffix = metaValue;
					PlayerMetaManager.getInstance().updateDisplayName(uuid); // TODO Do we need this?
					break;

				case "dn":
					opm.get().localDisplayName = metaValue;
					PlayerMetaManager.getInstance().updateDisplayName(uuid); // TODO Do we need this?
					break;

				case "world":
					opm.get().world = metaValue;
					PlayerMetaManager.getInstance().updateDisplayName(uuid); // TODO Do we need this?
					break;

				case "nick":
					opm.get().nick = metaValue;
					PlayerMetaManager.getInstance().updateDisplayName(uuid); // TODO Do we need this?
					break;

				default:
					return;

				}

			}

		}

	}

}
