package xyz.olivermartin.multichat.spongebridge.listeners;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.MessageChannelEvent;

import xyz.olivermartin.multichat.spongebridge.MultiChatSponge;

public class SpongeChatListener {

	@Listener(order=Order.LAST)
	public void onChat(MessageChannelEvent.Chat event) {

		Optional<Player> playerOptional = event.getCause().<Player>first(Player.class);

		if (playerOptional.isPresent()) {

			Player player = playerOptional.get();

			// IF WE ARE MANAGING GLOBAL CHAT THEN WE NEED TO MANAGE IT!
			if (MultiChatSponge.globalChatServer) {
				// Lets send Bungee the latest info!
				MultiChatSponge.updatePlayerMeta(player.getName(), MultiChatSponge.setDisplayNameLastVal, MultiChatSponge.displayNameFormatLastVal);
				event.setCancelled(true); //This is needed to stop the double message, but interferes with plugins like FactionsOne which for some reason use HIGHEST priority

				String format;

				if (!MultiChatSponge.overrideGlobalFormat) {

					// If we aren't overriding then use the main global format
					format = MultiChatSponge.globalChatFormat;

				} else {

					// Otherwise use the locally defined one in the config file
					format = MultiChatSponge.overrideGlobalFormatFormat;

				}

				if (MultiChatSponge.papi.isPresent()) {
					format = MultiChatSponge.papi.get().replaceSourcePlaceholders(format, event.getSource()).toPlain();

					// PAPI replaces unknown placeholders with {key}, so change them back to %key%!!
					format = format.replace("{NAME}", "%NAME%");
					format = format.replace("{DISPLAYNAME}", "%DISPLAYNAME%");
					format = format.replace("{PREFIX}", "%PREFIX%");
					format = format.replace("{SUFFIX}", "%SUFFIX%");
					format = format.replace("{NICK}", "%NICK%");
					format = format.replace("{SERVER}", "%SERVER%");
					format = format.replace("{WORLD}", "%WORLD%");
					format = format.replace("{MODE}", "%MODE%");

				}

				// TODO Somehow use the Sponge format so that other plugins can edit it (instead of just the global format here and the .toPlain)
				// None of this is ideal, as event.getMessage() actually returns the WHOLE message that would be sent including name etc.
				MultiChatSponge.sendChatToBungee(player, event.getRawMessage().toPlain(), format.replaceAll("%", "%%"), false, "");
			}

		}

	}

}