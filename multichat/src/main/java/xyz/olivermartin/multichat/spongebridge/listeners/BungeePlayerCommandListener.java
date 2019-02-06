package xyz.olivermartin.multichat.spongebridge.listeners;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;

/**
 * Used to execute player specific commands sent from MultiChat on bungeecord
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class BungeePlayerCommandListener implements RawDataListener {

	public BungeePlayerCommandListener() {
		super();
	}

	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Platform.Type side) {

		String playerRegex = data.getUTF(0);
		String command = data.getUTF(0);

		for (Player p : Sponge.getServer().getOnlinePlayers()) {

			if (p.getName().matches(playerRegex)) {

				Sponge.getCommandManager().process(p, command);

			}

		}

	}
}
