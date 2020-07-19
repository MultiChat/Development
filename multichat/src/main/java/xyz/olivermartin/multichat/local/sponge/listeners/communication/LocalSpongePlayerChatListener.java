package xyz.olivermartin.multichat.local.sponge.listeners.communication;

import java.util.Optional;

import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.text.Text;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.listeners.LocalBungeeMessage;
import xyz.olivermartin.multichat.local.common.listeners.communication.LocalPlayerChatListener;
import xyz.olivermartin.multichat.local.sponge.listeners.SpongeBungeeMessage;

public class LocalSpongePlayerChatListener extends LocalPlayerChatListener implements RawDataListener {

	@Override
	public void handlePayload(ChannelBuf data, RemoteConnection connection, Type side) {

		LocalBungeeMessage lbm = new SpongeBungeeMessage(data);

		handleMessage(lbm);

	}

	@Override
	protected void sendChatAsPlayer(String playerName, String rawMessage) {

		Optional<Player> opPlayer = Sponge.getServer().getPlayer(playerName);
		if (opPlayer.isPresent()) {
			EventContext context = EventContext.builder()
					.add(EventContextKeys.PLAYER_SIMULATED, opPlayer.get().getProfile())
					.add(EventContextKeys.PLUGIN, Sponge.getPluginManager().getPlugin(MultiChatLocal.getInstance().getPluginName()).get())
					.build();
			opPlayer.get().simulateChat(Text.of(rawMessage), Cause.builder().append(opPlayer.get()).append(Sponge.getPluginManager().getPlugin(MultiChatLocal.getInstance().getPluginName()).get()).build(context));
		}

	}

}
