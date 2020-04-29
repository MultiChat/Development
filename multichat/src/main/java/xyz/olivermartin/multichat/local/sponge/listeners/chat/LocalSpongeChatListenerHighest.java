package xyz.olivermartin.multichat.local.sponge.listeners.chat;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.MessageChannelEvent;

import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.listeners.chat.LocalChatListenerHighest;
import xyz.olivermartin.multichat.local.common.listeners.chat.MultiChatLocalPlayerChatEvent;
import xyz.olivermartin.multichat.local.sponge.MultiChatLocalSpongePlayer;

public class LocalSpongeChatListenerHighest extends LocalChatListenerHighest {

	@Listener(order=Order.LAST)
	public void onChat(MessageChannelEvent.Chat event) {

		Optional<Player> playerOptional = event.getCause().<Player>first(Player.class);

		if (!playerOptional.isPresent()) return;

		MultiChatLocalPlayer player = new MultiChatLocalSpongePlayer(playerOptional.get());

		MultiChatLocalPlayerChatEvent mcce = new MultiChatLocalSpongePlayerChatEvent(event, player);

		handleChat(mcce);

	}

}
