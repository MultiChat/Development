package xyz.olivermartin.multichat.spongebridge.listeners;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;

import xyz.olivermartin.multichat.spongebridge.MultiChatSponge;

public class PlayerChannelListener implements RawDataListener {

	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Type side) {

		Optional<Player> player = Sponge.getServer().getPlayer(data.readUTF());

		try {

			Player p = player.get();
			String channel = data.readUTF();

			MultiChatSponge.playerChannels.put(p, channel);

		} catch (NoSuchElementException e) {

			System.err.println("[MultiChat] An error occurred getting player details, is the server lagging?");

		}

	}

}
