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
import xyz.olivermartin.multichat.spongebridge.DebugManager;
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

				DebugManager.log("We are in local chat!");
				format = MultiChatSponge.localChatFormat;
				DebugManager.log("The chat format is: " + format);
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

				DebugManager.log("We are in global chat!");
				DebugManager.log("The chat format is: " + format);

			}

			DebugManager.log("The chat format contains the § symbol?" + format.contains("§"));

			// Build chat format
			format = SpongePlaceholderManager.buildChatFormat(player, format);

			DebugManager.log("THE FORMAT HAS NOW BEEN BUILT BY THE PLACEHOLDER MANAGER");
			DebugManager.log("We received: " + format);

			if (MultiChatSponge.papi.isPresent()) {

				DebugManager.log("PlaceholderAPI is present");

				
				format = TextSerializers.FORMATTING_CODE.serialize(MultiChatSponge.papi.get().replaceSourcePlaceholders(format+"#", event.getSource()));
				// PAPI replaces unknown placeholders with {key}, so change them back to %key%!!
				format = format.substring(0,format.length()-1);
				format = format.replace("{NAME}", "%NAME%");
				format = format.replace("{DISPLAYNAME}", "%DISPLAYNAME%");
				format = format.replace("{PREFIX}", "%PREFIX%");
				format = format.replace("{SUFFIX}", "%SUFFIX%");
				format = format.replace("{NICK}", "%NICK%");
				format = format.replace("{SERVER}", "%SERVER%");
				format = format.replace("{WORLD}", "%WORLD%");
				format = format.replace("{MODE}", "%MODE%");

				DebugManager.log("After PAPI replacements we have: " + format);

			}

			Text toSend;

			// Deal with coloured chat
			DebugManager.log("Now we are dealing with coloured chat");
			if (MultiChatSponge.colourMap.containsKey(player.getUniqueId())) {

				DebugManager.log("We have an entry for this player in the colour map!");

				boolean colour = MultiChatSponge.colourMap.get(player.getUniqueId());

				DebugManager.log("Can they use colour codes?: " + colour);

				if (colour) {
					toSend = TextSerializers.FORMATTING_CODE.deserialize(format + message);
				} else {
					toSend = TextSerializers.FORMATTING_CODE.deserialize(format + TextSerializers.FORMATTING_CODE.stripCodes(message));
				}

				DebugManager.log("The text has now been formatted by decoding & to the real colour codes!");

				if (DebugManager.isDebug()) {
					DebugManager.log("Here is exactly how the message is formatted:");
					Sponge.getGame().getServer().getConsole().sendMessage(toSend);
				}
				
				DebugManager.log("The text in plaintext form is: " + toSend.toString());

			} else {
				DebugManager.log("We dont have an entry for the player in the colour map, we dont know if they can format or not!");
				toSend = TextSerializers.FORMATTING_CODE.deserialize(format + TextSerializers.FORMATTING_CODE.stripCodes(message));
				if (DebugManager.isDebug()) {
					DebugManager.log("Here is exactly how the message is formatted:");
					Sponge.getGame().getServer().getConsole().sendMessage(toSend);
				}
			}

			// IF WE ARE MANAGING GLOBAL CHAT THEN WE NEED TO MANAGE IT!
			if (MultiChatSponge.globalChatServer) {
				
				DebugManager.log("This server is linked to the global chat!");

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
						DebugManager.log("We need to send the message to bungeecord!");
						DebugManager.log("Data to send is: ");
						DebugManager.log("PLAYER:" + player.getName());
						DebugManager.log("MESSAGE:" + message);
						DebugManager.log("FORMAT: " + format.replace("%", "%%"));
						MultiChatSponge.sendChatToBungee(player, message, format.replace("%", "%%"));

					}
				}

			}

		}

	}

}
