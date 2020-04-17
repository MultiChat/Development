package xyz.olivermartin.multichat.local.sponge.listeners.chat;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import me.rojo8399.placeholderapi.PlaceholderService;
import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.sponge.MultiChatLocalSpongePlayer;
import xyz.olivermartin.multichat.local.sponge.hooks.LocalSpongePAPIHook;

public class LocalSpongeChatListenerHighest {

	protected String processExternalPlaceholders(Object source, String message) {

		if (LocalSpongePAPIHook.getInstance().isHooked()) {

			PlaceholderService papi = LocalSpongePAPIHook.getInstance().getHook().get();

			MultiChatLocal.getInstance().getConsoleLogger().debug("Going into PAPI we have: " + message);
			MultiChatLocal.getInstance().getConsoleLogger().debug("Going into PAPI we have (visualised): " + message.replace("&", "(#d)").replace("§", "(#e)"));

			message = TextSerializers.FORMATTING_CODE.serialize(papi.replaceSourcePlaceholders(message+"#", source));

			MultiChatLocal.getInstance().getConsoleLogger().debug("Serialised we have: " + message);
			MultiChatLocal.getInstance().getConsoleLogger().debug("Serialised we have (visualised): " + message.replace("&", "(#d)").replace("§", "(#e)"));

			// PAPI replaces unknown placeholders with {key}, so change them back to %key%!!
			message = message.substring(0,message.length()-1);
			message = message.replace("{NAME}", "%NAME%");
			message = message.replace("{DISPLAYNAME}", "%DISPLAYNAME%");
			message = message.replace("{PREFIX}", "%PREFIX%");
			message = message.replace("{SUFFIX}", "%SUFFIX%");
			message = message.replace("{NICK}", "%NICK%");
			message = message.replace("{SERVER}", "%SERVER%");
			message = message.replace("{WORLD}", "%WORLD%");
			message = message.replace("{MODE}", "%MODE%");

			MultiChatLocal.getInstance().getConsoleLogger().debug("After PAPI we have: " + message);
			MultiChatLocal.getInstance().getConsoleLogger().debug("After PAPI we have (visualised): " + message.replace("&", "(#d)").replace("§", "(#e)"));

		}

		return message;

	}

	@Listener(order=Order.LAST)
	public void onChat(MessageChannelEvent.Chat event) {

		if (event.isCancelled()) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("Chat event cancelled!");
			return;
		}

		LocalChatManager chatManager = MultiChatLocal.getInstance().getChatManager();

		Optional<Player> playerOptional = event.getCause().<Player>first(Player.class);

		if (!playerOptional.isPresent()) return;

		Player player = playerOptional.get();

		MultiChatLocalPlayer mclp = new MultiChatLocalSpongePlayer(player);

		MultiChatMessageChannel messageChannel = new MultiChatMessageChannel(mclp);

		event.setChannel(messageChannel); // TODO Is this how we do it? Seems about right! (Can also set default message channel for a player too)

		String channel = messageChannel.getMultiChatChannelName();
		String message = event.getRawMessage().toPlain();
		String format = chatManager.getChannelFormat(channel);

		// Build chat format
		format = MultiChatLocal.getInstance().getPlaceholderManager().buildChatFormat(player.getUniqueId(), format);

		format = processExternalPlaceholders(event.getSource(), format);

		Text toSendMessage;
		Text toSendFormat;

		// Deal with coloured chat
		if (chatManager.canChatInColour(mclp.getUniqueId())) {
			toSendMessage = TextSerializers.FORMATTING_CODE.deserialize(message);
		} else {
			toSendMessage = Text.of(message);
		}

		toSendFormat = TextSerializers.FORMATTING_CODE.deserialize(format);

		event.getFormatter().setBody(toSendMessage);
		event.getFormatter().setHeader(toSendFormat);
		// event.setMessage(toSend)...

		// IF WE ARE MANAGING GLOBAL CHAT THEN WE NEED TO MANAGE IT!
		if (chatManager.isGlobalChatServer()) {

			// Lets send Bungee the latest info!
			MultiChatLocal.getInstance().getProxyCommunicationManager().updatePlayerMeta(mclp.getUniqueId());

			if (channel.equals("local")) return;

			// TODO Somehow use the Sponge format so that other plugins can edit it (instead of just the global format here and the .toPlain)
			// None of this is ideal, as event.getMessage() actually returns the WHOLE message that would be sent including name etc.
			MultiChatLocal.getInstance().getConsoleLogger().debug("We need to send the message to bungeecord!");
			MultiChatLocal.getInstance().getConsoleLogger().debug("Data to send is: ");
			MultiChatLocal.getInstance().getConsoleLogger().debug("PLAYER:" + player.getName());

			String proxyMessage = TextSerializers.formattingCode('§').serialize(event.getFormatter().getBody().toText());
			String proxyFormat = TextSerializers.formattingCode('§').serialize(event.getFormatter().getHeader().toText());

			MultiChatLocal.getInstance().getConsoleLogger().debug("MESSAGE:" + proxyMessage);
			MultiChatLocal.getInstance().getConsoleLogger().debug("FORMAT: " + proxyFormat.replace("%", "%%") + "... followed by the message");

			MultiChatLocal.getInstance().getProxyCommunicationManager().sendChatMessage(player.getUniqueId(), proxyMessage, proxyFormat.replace("%", "%%"));

		}

	}

}
