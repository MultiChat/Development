package xyz.olivermartin.multichat.local.sponge.listeners.communication;

import java.io.IOException;
import java.util.Optional;

import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeObjectMessage;
import xyz.olivermartin.multichat.local.common.listeners.communication.LocalPlayerDataListener;
import xyz.olivermartin.multichat.local.sponge.MultiChatLocalSpongePlayer;
import xyz.olivermartin.multichat.local.sponge.listeners.SpongeBungeeObjectMessage;

public class LocalSpongePlayerDataListener extends LocalPlayerDataListener implements RawDataListener {

	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Type side) {

		try {
			LocalBungeeObjectMessage lbm = new SpongeBungeeObjectMessage(data);

			handleMessage(lbm);

		} catch (IOException e) {
			MultiChatLocal.getInstance().getConsoleLogger().log("An error occurred reading the object stream in the local player data listener...");
			return;
		}

	}

	@Override
	protected Optional<MultiChatLocalPlayer> getPlayerFromName(String playername) {
		Optional<Player> player = Sponge.getServer().getPlayer(playername);
		if (player.isPresent()) {
			return Optional.of(new MultiChatLocalSpongePlayer(player.get()));
		} else {
			return Optional.empty();
		}
	}

}
