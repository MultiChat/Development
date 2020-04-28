package xyz.olivermartin.multichat.local.sponge.listeners.chat;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.MessageChannelEvent;

import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.LocalPseudoChannel;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.listeners.chat.MultiChatLocalPlayerChatEvent;
import xyz.olivermartin.multichat.local.sponge.MultiChatLocalSpongePlayer;

public class LocalSpongeChatListenerHighest {

	@Listener(order=Order.LAST)
	public void onChat(MessageChannelEvent.Chat event) {

		// IF ITS ALREADY CANCELLED WE CAN IGNORE IT
		if (event.isCancelled()) return;

		LocalChatManager chatManager = MultiChatLocal.getInstance().getChatManager();

		Optional<Player> playerOptional = event.getCause().<Player>first(Player.class);

		if (!playerOptional.isPresent()) return;

		Player spongePlayer = playerOptional.get();

		MultiChatLocalPlayer player = new MultiChatLocalSpongePlayer(spongePlayer);

		MultiChatLocalPlayerChatEvent mcce = new MultiChatLocalSpongePlayerChatEvent(event, player);

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Now is where the fun starts... Welcome to the highest level!");

		if (chatManager.canChatInColour(mcce.getPlayer().getUniqueId())) {
			mcce.setMessage(chatManager.translateColourCodes(mcce.getMessage()));
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Translated their message to include the colours and set back in the event as: " + mcce.getMessage());
		}

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Now we will process MultiChat placeholders!");

		mcce.setFormat(chatManager.processMultiChatConfigPlaceholders(mcce.getPlayer(), mcce.getFormat()));

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - The resulting format was... " + mcce.getFormat());

		String channel = chatManager.peekAtChatChannel(mcce.getPlayer());

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Channel for this message before forcing is: " + channel);

		// Deal with regex channel forcing...
		channel = chatManager.getRegexForcedChannel(channel, mcce.getFormat());

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Channel for this message after forcing is: " + channel);

		// Deal with ignores and channel members

		Optional<LocalPseudoChannel> opChannelObject = chatManager.getChannelObject(channel);

		if (opChannelObject.isPresent()) {

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Do we have a channel object to match that name? Yes!");
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Now we are attempting to remove ignored players from the recipient list of the message, and making sure only people who are meant to see the channel (as specified in the channel object), can see it!");

			mcce.removeIgnoredPlayersAndNonChannelMembersFromRecipients(opChannelObject.get());

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - And BAM! That was handled by the local platform implementation!");

		} else {

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - We didn't find a channel object to match that name... Probably not good!");

		}

		if (!chatManager.isGlobalChatServer() || channel.equalsIgnoreCase("local")) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - We are speaking into local chat, so at this point we are returning! Bye!");
			return;
		}

		if (chatManager.isForceMultiChatFormat()) {

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - OKAYYY! We are forcing our format! All other plugins shall now crumble!");

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Currently it is starting out as... " + mcce.getFormat());

			String format;

			format = chatManager.getChannelFormat(channel);
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Got the format for this channel as:" + format);

			// Build chat format
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Rebuilding the chat format...");
			format = MultiChatLocal.getInstance().getPlaceholderManager().buildChatFormat(mcce.getPlayer().getUniqueId(), format);

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Now we have: " + format);

			format = chatManager.processExternalPlaceholders(mcce.getPlayer(), format);

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Processed external placeholders to get: " + format);

			format = format.replace("%", "%%");

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - Did some magic to get..." + format);

			mcce.setFormat(chatManager.translateColourCodes(format));
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@HIGHEST - FORMAT HAS BEEN SET AS: " + mcce.getFormat());

		}

	}

}
