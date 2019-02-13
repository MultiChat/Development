package xyz.olivermartin.multichat.spongebridge.listeners;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import xyz.olivermartin.multichat.spongebridge.MultiChatSponge;

public class SpongeLoginListener {

	@Listener(order=Order.POST)
	public void onLogin(ClientConnectionEvent.Login event) {

		Optional<Player> playerOptional = event.getCause().<Player>first(Player.class);

		if (playerOptional.isPresent()) {

			Player p = playerOptional.get();

			if (!MultiChatSponge.playerChannels.containsKey(p)) {
				MultiChatSponge.playerChannels.put(p, "global");
			}

		}

	}

	@Listener(order=Order.POST)
	public void onLogout(ClientConnectionEvent.Disconnect event) {

		Optional<Player> playerOptional = event.getCause().<Player>first(Player.class);

		if (playerOptional.isPresent()) {

			Player p = playerOptional.get();
			MultiChatSponge.playerChannels.remove(p);

		}

	}

}
