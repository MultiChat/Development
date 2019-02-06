package xyz.olivermartin.multichat.spongebridge.listeners;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding.RawDataChannel;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import xyz.olivermartin.multichat.spongebridge.MultiChatSponge;

public class BungeeChatListener implements RawDataListener {

	public BungeeChatListener(RawDataChannel channel) {
		super();
	}

	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Type side) {

		Optional<Player> player = Sponge.getServer().getPlayer(data.getUTF(0));

		try {

			Player p = player.get();

			String format = data.readUTF();
			String message = data.readUTF();

			boolean colour = data.readBoolean();

			String playerString = data.readUTF();

			Set<String> playerNames = new HashSet<String>(Arrays.asList(playerString.split(" ")));
			Set<Player> players = new HashSet<Player>(Sponge.getServer().getOnlinePlayers());
			Iterator<Player> it = players.iterator();

			while(it.hasNext()) {
				if (!playerNames.contains(it.next().getName())) {
					it.remove();
				}
			}

			synchronized (p) {
				if (p == null) {
					return;
				}

				// Manage colour permissions
				Text toSend;
				if (colour) {
					toSend = TextSerializers.FORMATTING_CODE.deserialize(format.replace("%MESSAGE%", message));
				} else {
					toSend = TextSerializers.FORMATTING_CODE.deserialize(format.replace("%MESSAGE%", "")).concat(Text.of(message));
				}

				synchronized (MultiChatSponge.multichatChannel) {

					MultiChatSponge.multichatChannel.clearMembers();
					for (Player pl : players) {
						MultiChatSponge.multichatChannel.addMember(pl);
					}

					MultiChatSponge.multichatChannel.send(p, toSend);

				}

			}

		} catch (NoSuchElementException e) {

			System.err.println("[MultiChat] An error occurred getting player details, is the server lagging?");

		}

	}

}
