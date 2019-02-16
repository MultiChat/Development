package xyz.olivermartin.multichat.spongebridge.listeners;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding.RawDataChannel;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;

import xyz.olivermartin.multichat.spongebridge.MultiChatSponge;

/**
 * RAW DATA MESSAGING CHANNEL SPONGE LISTENER
 * Data Listener For SPONGE COMMUNICATION with BUNGEECORD
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class MetaListener implements RawDataListener {

	public MetaListener(RawDataChannel channel) {
		super();
	}

	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Platform.Type side) {

		Optional<Player> player = Sponge.getServer().getPlayer(data.readUTF());

		try {

			Player p = player.get();
			boolean setDisplayName = false;
			String displayNameFormat = "";
			boolean globalChat = false;

			synchronized (p) {
				if (p == null) {
					return;
				}

				if (data.readUTF().equals("T")) {
					setDisplayName = true;
				}

				displayNameFormat = data.readUTF();

				MultiChatSponge.setDisplayNameLastVal = setDisplayName;
				MultiChatSponge.displayNameFormatLastVal = displayNameFormat;

				MultiChatSponge.updatePlayerMeta(p.getName(), setDisplayName, displayNameFormat);

				if (data.readUTF().equals("T")) {
					globalChat = true;
				}

				MultiChatSponge.globalChatServer = globalChat;

				MultiChatSponge.globalChatFormat = data.readUTF();

			}

		} catch (NoSuchElementException e) {

			System.err.println("[MultiChat] An error occurred getting player details, is the server lagging?");

		}
	}
}
