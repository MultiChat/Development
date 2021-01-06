package xyz.olivermartin.multichat.local.sponge.listeners.communication;

import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;

import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeMessage;
import xyz.olivermartin.multichat.local.common.listeners.communication.LocalPlayerActionListener;
import xyz.olivermartin.multichat.local.sponge.listeners.SpongeBungeeMessage;

public class LocalSpongePlayerActionListener extends LocalPlayerActionListener implements RawDataListener {

	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Type side) {

		LocalBungeeMessage lbm = new SpongeBungeeMessage(data);

		handleMessage(lbm);

	}

	@Override
	protected void executeCommandForPlayersMatchingRegex(String playerRegex, String command) {

		for (Player p : Sponge.getServer().getOnlinePlayers()) {

			if (p.getName().matches(playerRegex)) {

				Sponge.getCommandManager().process(p, command);

			}

		}

	}

}
