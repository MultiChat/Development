package xyz.olivermartin.multichat.spongebridge.listeners;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;

import xyz.olivermartin.multichat.spigotbridge.PseudoChannel;
import xyz.olivermartin.multichat.spongebridge.MultiChatSponge;

public class PlayerChannelListener implements RawDataListener {

	@SuppressWarnings("unchecked")
	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Type side) {
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		
		while (data.available() > 0) {
			try {
				outputStream.write(data.readBytes(1));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ByteArrayInputStream stream = new ByteArrayInputStream(outputStream.toByteArray());
		//DataInputStream in = new DataInputStream(stream);

		try {

			ObjectInputStream oin = new ObjectInputStream(stream);


			String playername = oin.readUTF();

			Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(playername);

			if (!optionalPlayer.isPresent()) {
				return;
			}

			Player p = optionalPlayer.get();

			synchronized (p) {

				String channelName = oin.readUTF();
				MultiChatSponge.playerChannels.put(p, channelName);

				boolean colour = oin.readBoolean();
				MultiChatSponge.colourMap.put(p.getUniqueId(), colour);

				boolean whitelistMembers = oin.readBoolean();
				List<UUID> channelMembers = (List<UUID>) oin.readObject();

				PseudoChannel channelObject = new PseudoChannel(channelName, channelMembers, whitelistMembers);
				MultiChatSponge.channelObjects.put(channelName, channelObject);

			}

		} catch (IOException e) {

			Bukkit.getLogger().info("Error with connection to Bungeecord! Is the server lagging?");
			e.printStackTrace();

		} catch (ClassNotFoundException e) {

			Bukkit.getLogger().info("Could not read list of uuids from channel message");
			e.printStackTrace();
		}

	}

}
