package xyz.olivermartin.multichat.local.sponge.listeners;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.listeners.LocalLoginLogoutListener;
import xyz.olivermartin.multichat.local.sponge.MultiChatLocalSpongePlayer;

public class LocalSpongeLoginLogoutListener extends LocalLoginLogoutListener {

	@Listener(order=Order.POST)
	public void onJoin(ClientConnectionEvent.Join event, @Root Player player) {
		MultiChatLocalPlayer mclp = new MultiChatLocalSpongePlayer(player);
		handleLoginEvent(mclp);
	}

	@Listener(order=Order.POST)
	public void onLogout(ClientConnectionEvent.Disconnect event) {
		Optional<Player> playerOptional = event.getCause().<Player>first(Player.class);
		if (playerOptional.isPresent()) {
			MultiChatLocalPlayer mclp = new MultiChatLocalSpongePlayer(playerOptional.get());
			handleLogoutEvent(mclp);
		}
	}


	@Override
	protected boolean isPlayerStillOnline(MultiChatLocalPlayer player) {
		if (Sponge.getServer().getPlayer(player.getUniqueId()).isPresent()) {
			return true;
		} else {
			return false;
		}
	}

}
