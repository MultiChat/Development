package com.olivermartin410.plugins;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBinding.RawDataChannel;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.text.Text;
public class MultiChatRawDataListener implements RawDataListener {

	private RawDataChannel channel;

	public MultiChatRawDataListener(RawDataChannel channel) {
		super();
		this.channel = channel;
	}

	private Text getDisplayName(Player player) {
		synchronized (player) {
			//Optional<Text> data;
			if ((SpongeComm.displayNames.containsKey(player.getUniqueId()))) {
				//data = player.get(Keys.DISPLAY_NAME);
				//System.out.println(data.get().toPlain());
				return Text.of(SpongeComm.displayNames.get(player.getUniqueId()));
			} else {
				String nickname;
				if (SpongeComm.nicknames.containsKey(player.getUniqueId())) {
					nickname = SpongeComm.nicknames.get(player.getUniqueId());
					//System.out.println("nick: " + nickname);
				} else {
					//System.out.println("else: " + player.getName());
					nickname = player.getName();
				}
				return Text.of(nickname);
			}
		}
	}

	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Platform.Type side) {

		Optional<Player> player = Sponge.getServer().getPlayer(data.getUTF(0));
		try {
			Player p = player.get();
			synchronized (p) {
				if (p == null) {
					return;
				}


				/*if (p.getOption("prefix").isPresent()) {
					if (p.getOption("suffix").isPresent()) {
						if (!getDisplayName(p).toPlain().contains(p.getOption("prefix").get())) {
							channel.sendTo(p,buffer -> buffer.writeUTF(p.getOption("prefix").get() + nickname + p.getOption("suffix").get()).writeUTF(p.getName()));
						} else {
							channel.sendTo(p,buffer -> buffer.writeUTF(nickname).writeUTF(p.getName()));
						}
					} else {
						if (!getDisplayName(p).toPlain().contains(p.getOption("prefix").get())) {
							channel.sendTo(p,buffer -> buffer.writeUTF(p.getOption("prefix").get() + nickname).writeUTF(p.getName()));
						} else {
							channel.sendTo(p,buffer -> buffer.writeUTF(nickname).writeUTF(p.getName()));
						}
					}

				} else {
					channel.sendTo(p,buffer -> buffer.writeUTF(nickname).writeUTF(p.getName()));
				}*/

				channel.sendTo(p,buffer -> buffer.writeUTF(getDisplayName(p).toPlain()).writeUTF(p.getName()));
			}
		} catch (NoSuchElementException e) {
			System.err.println("[MultiChat] An error occurred getting player details, is the server lagging?");
		}

	}
}
