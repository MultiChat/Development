package xyz.olivermartin.multichat.local.sponge.listeners.chat;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.MessageChannelEvent;

import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.listeners.chat.MultiChatLocalPlayerChatEvent;
import xyz.olivermartin.multichat.local.sponge.MultiChatLocalSpongePlayer;

public class LocalSpongeChatListenerLowest {

	@Listener(order=Order.PRE)
	public void onChat(MessageChannelEvent.Chat event) {

		// IF ITS ALREADY CANCELLED THEN WE CAN IGNORE IT!
		if (event.isCancelled()) return;

		LocalChatManager chatManager = MultiChatLocal.getInstance().getChatManager();

		Optional<Player> playerOptional = event.getCause().<Player>first(Player.class);

		if (!playerOptional.isPresent()) return;

		Player spongePlayer = playerOptional.get();

		MultiChatLocalPlayer player = new MultiChatLocalSpongePlayer(spongePlayer);

		MultiChatLocalPlayerChatEvent mcce = new MultiChatLocalSpongePlayerChatEvent(event, player);

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Handling chat message...");

		String channel = chatManager.peekAtChatChannel(player);
		String format = mcce.getFormat();

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Channel for this message before forcing is " + channel);

		// Deal with regex channel forcing...
		channel = chatManager.getRegexForcedChannel(channel, format);

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Channel for this message after forcing is " + channel);

		if (!chatManager.isGlobalChatServer()) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Not a global chat server, so setting channel to local!");
			channel = "local";
		}

		if (channel.equals("local") && !chatManager.isSetLocalFormat()) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Its local chat and we aren't setting the format for that, so return now!");
			return;
		}
		
		if (chatManager.isOverrideMultiChatFormat()) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - We are overriding MultiChat's formatting... So abandon here...");
			return;
		}

		format = chatManager.getChannelFormat(channel);
		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Got the format for this channel as:" + format);

		// Build chat format
		format = MultiChatLocal.getInstance().getPlaceholderManager().buildChatFormat(player.getUniqueId(), format);

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Built to become: " + format);

		format = chatManager.processExternalPlaceholders(player, format);

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Processing external placeholders to become: " + format);

		format = format.replace("%", "%%");

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Did some magic formatting to end up as: " + format);

		mcce.setFormat(chatManager.translateColourCodes(format));

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@LOWEST - Set the format of the message. Finished processing at the lowest level!");

	}

}
