package xyz.olivermartin.multichat.local.platform.sponge.listeners.communication;

import java.util.Optional;

import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;

import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.listeners.LocalBungeeMessage;
import xyz.olivermartin.multichat.local.listeners.communication.LocalPlayerMetaListener;
import xyz.olivermartin.multichat.local.platform.sponge.MultiChatLocalSpongePlayer;
import xyz.olivermartin.multichat.local.platform.sponge.listeners.SpongeBungeeMessage;

public class LocalSpongePlayerMetaListener extends LocalPlayerMetaListener implements RawDataListener {

	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Type side) {

		LocalBungeeMessage lbm = new SpongeBungeeMessage(data);

		handleMessage(lbm);

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
