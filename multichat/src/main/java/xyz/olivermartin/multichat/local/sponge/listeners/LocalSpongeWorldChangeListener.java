package xyz.olivermartin.multichat.local.sponge.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;

import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.listeners.LocalWorldChangeListener;
import xyz.olivermartin.multichat.local.sponge.MultiChatLocalSpongePlayer;

public class LocalSpongeWorldChangeListener extends LocalWorldChangeListener {

	@Listener(order=Order.POST)
	public void onWorldChange(MoveEntityEvent.Teleport event, @Root Player player) {

		String oldWorld = event.getFromTransform().getExtent().getName();
		String newWorld = event.getToTransform().getExtent().getName();

		if (oldWorld.equals(newWorld)) {
			return;
		}

		MultiChatLocalPlayer mclp = new MultiChatLocalSpongePlayer(player);
		updatePlayerWorld(mclp, newWorld);

	}

}
