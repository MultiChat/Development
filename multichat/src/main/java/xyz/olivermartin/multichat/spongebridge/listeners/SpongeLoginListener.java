package xyz.olivermartin.multichat.spongebridge.listeners;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import xyz.olivermartin.multichat.spongebridge.DebugManager;
import xyz.olivermartin.multichat.spongebridge.MultiChatSponge;
import xyz.olivermartin.multichat.spongebridge.SpongeNameManager;

public class SpongeLoginListener {

	/*@Listener(order=Order.POST)
	public void onLogin(ClientConnectionEvent.Login event, @Root Player player) {

		DebugManager.log("Login event!");

		//Optional<Player> playerOptional = event.getCause().first(Player.class);

		//if (playerOptional.isPresent()) {

		//Player p = playerOptional.get();

		DebugManager.log("Player: " + player.getName());

		SpongeNameManager.getInstance().registerPlayer(player);

		if (!MultiChatSponge.playerChannels.containsKey(player)) {
			MultiChatSponge.playerChannels.put(player, "global");
		}

		//}

	}*/

	@Listener(order=Order.POST)
	public void onJoin(ClientConnectionEvent.Join event, @Root Player player) {

		DebugManager.log("Join event!");

		DebugManager.log("Player: " + player.getName());

		SpongeNameManager.getInstance().registerPlayer(player);

		if (!MultiChatSponge.playerChannels.containsKey(player)) {
			MultiChatSponge.playerChannels.put(player, "global");
		}

	}

	@Listener(order=Order.POST)
	public void onLogout(ClientConnectionEvent.Disconnect event) {

		Optional<Player> playerOptional = event.getCause().<Player>first(Player.class);

		if (playerOptional.isPresent()) {
			
			DebugManager.log("Disconnecting player...");

			Player p = playerOptional.get();
			SpongeNameManager.getInstance().unregisterPlayer(p);
			MultiChatSponge.playerChannels.remove(p);

		}

	}

}
