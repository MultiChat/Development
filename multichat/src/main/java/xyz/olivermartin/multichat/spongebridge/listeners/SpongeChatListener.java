package xyz.olivermartin.multichat.spongebridge.listeners;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import xyz.olivermartin.multichat.spigotbridge.PseudoChannel;
import xyz.olivermartin.multichat.spongebridge.MultiChatSponge;
import xyz.olivermartin.multichat.spongebridge.SpongePlaceholderManager;

public class SpongeChatListener {

	@Listener(order=Order.LAST)
	public void onChat(MessageChannelEvent.Chat event) {

		Optional<Player> playerOptional = event.getCause().<Player>first(Player.class);

		if (playerOptional.isPresent()) {

			Player player = playerOptional.get();

			Set<Player> onlinePlayers = new HashSet<Player>(Sponge.getServer().getOnlinePlayers());
			Iterator<Player> it = onlinePlayers.iterator();

			// Deal with ignores and channel members
			if (MultiChatSponge.playerChannels.containsKey(player)) {

				String channelName = MultiChatSponge.playerChannels.get(player);

				if (MultiChatSponge.channelObjects.containsKey(channelName)) {

					PseudoChannel channelObject = MultiChatSponge.channelObjects.get(channelName);

					Set<UUID> ignoredPlayers;

					while (it.hasNext()) {

						Player p = it.next();

						ignoredPlayers = MultiChatSponge.ignoreMap.get(p.getUniqueId());

						if ( (channelObject.whitelistMembers && channelObject.members.contains(p.getUniqueId())) || (!channelObject.whitelistMembers && !channelObject.members.contains(p.getUniqueId()))) {

							// Then this player is okay!
							if (ignoredPlayers != null) {

								if (ignoredPlayers.contains(player.getUniqueId())) {

									it.remove();

								}

							}

						} else {

							it.remove();

						}
					}

				}

			}

			String channel;
			String format;
			String message = event.getRawMessage().toPlain();

			if (MultiChatSponge.playerChannels.containsKey(player)) {
				channel = MultiChatSponge.playerChannels.get(player);
			} else {
				channel = "global";
			}

			if (channel.equals("local")) {

				// Local chat

				format = MultiChatSponge.localChatFormat;
				//format = SpigotPlaceholderManager.buildChatFormat(p, format);

			} else {

				// Global chat

				if (!MultiChatSponge.overrideGlobalFormat) {

					// If we aren't overriding then use the main global format
					format = MultiChatSponge.globalChatFormat;

				} else {

					// Otherwise use the locally defined one in the config file
					format = MultiChatSponge.overrideGlobalFormatFormat;

				}

			}

			// Build chat format
			format = SpongePlaceholderManager.buildChatFormat(player, format);

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

			Text toSend;

			// Deal with coloured chat
			if (MultiChatSponge.colourMap.containsKey(player.getUniqueId())) {

				boolean colour = MultiChatSponge.colourMap.get(player.getUniqueId());

				if (colour) {
					toSend = TextSerializers.FORMATTING_CODE.deserialize(format + message);
				} else {
					toSend = TextSerializers.FORMATTING_CODE.deserialize(format + TextSerializers.FORMATTING_CODE.stripCodes(message));
				}

			} else {
				toSend = TextSerializers.FORMATTING_CODE.deserialize(format + TextSerializers.FORMATTING_CODE.stripCodes(message));
			}

			// IF WE ARE MANAGING GLOBAL CHAT THEN WE NEED TO MANAGE IT!
			if (MultiChatSponge.globalChatServer) {

				event.setCancelled(true);

				// DISTRIBUTE THE MESSAGE TO THE RIGHT PLAYERS LOCALLY

				synchronized (MultiChatSponge.multichatChannel) {

					MultiChatSponge.multichatChannel.clearMembers();
					for (Player pl : onlinePlayers) {
						MultiChatSponge.multichatChannel.addMember(pl);
					}

					MultiChatSponge.multichatChannel.send(player, toSend);

				}


				// Lets send Bungee the latest info!
				MultiChatSponge.updatePlayerMeta(player.getName(), MultiChatSponge.setDisplayNameLastVal, MultiChatSponge.displayNameFormatLastVal);

				if (MultiChatSponge.playerChannels.containsKey(player)) {
					if (!MultiChatSponge.playerChannels.get(player).equals("local")) {

						// TODO Somehow use the Sponge format so that other plugins can edit it (instead of just the global format here and the .toPlain)
						// None of this is ideal, as event.getMessage() actually returns the WHOLE message that would be sent including name etc.
						MultiChatSponge.sendChatToBungee(player, message, format.replaceAll("%", "%%"));


					}
				}


			}

		}

	}

}
