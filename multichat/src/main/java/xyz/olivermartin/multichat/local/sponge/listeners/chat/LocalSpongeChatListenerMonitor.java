package xyz.olivermartin.multichat.local.sponge.listeners.chat;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.MessageChannelEvent;

import xyz.olivermartin.multichat.local.common.LocalChatManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.config.LocalConfig;
import xyz.olivermartin.multichat.local.common.listeners.chat.MultiChatLocalPlayerChatEvent;
import xyz.olivermartin.multichat.local.sponge.MultiChatLocalSpongePlayer;

public class LocalSpongeChatListenerMonitor {

	@Listener(order=Order.POST)
	public void onChat(MessageChannelEvent.Chat event) {

		LocalChatManager chatManager = MultiChatLocal.getInstance().getChatManager();

		Optional<Player> playerOptional = event.getCause().<Player>first(Player.class);

		if (!playerOptional.isPresent()) return;

		Player spongePlayer = playerOptional.get();

		MultiChatLocalPlayer player = new MultiChatLocalSpongePlayer(spongePlayer);

		MultiChatLocalPlayerChatEvent mcce = new MultiChatLocalSpongePlayerChatEvent(event, player);

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Okay less fun here, we are just the monitor...");

		LocalConfig config = MultiChatLocal.getInstance().getConfigManager().getLocalConfig();

		String channel = chatManager.pollChatChannel(mcce.getPlayer());

		// Deal with regex channel forcing...
		channel = chatManager.getRegexForcedChannel(channel, mcce.getFormat());

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - If the message is cancelled, then we will end here...");

		// IF ITS ALREADY CANCELLED WE CAN IGNORE IT
		if (event.isCancelled()) return;

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - The message isn't cancelled!");

		// IF ITS LOCAL CHAT WE CAN IGNORE IT
		if (!chatManager.isGlobalChatServer() || channel.equalsIgnoreCase("local")) {
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - We are speaking into local chat, so at this point we are returning! Bye!");
			return;
		}

		// IF WE ARE MANAGING GLOBAL CHAT THEN WE NEED TO MANAGE IT!

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - We are in global chat... SO TIME TO FORWARD TO PROXY!");

		MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - First we are sending their meta data...");
		MultiChatLocal.getInstance().getProxyCommunicationManager().updatePlayerMeta(mcce.getPlayer().getUniqueId());

		if (channel.equals("local")) return;

		if (!config.isOverrideAllMultiChatFormatting()) {

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - We were managing the format...");

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Currently it is " + mcce.getFormat());

			String proxyMessage = mcce.getMessage();
			String proxyFormat = mcce.getFormat();

			MultiChatLocal.getInstance().getConsoleLogger().debug("MESSAGE:" + proxyMessage);
			MultiChatLocal.getInstance().getConsoleLogger().debug("FORMAT: " + proxyFormat.replace("%", "%%") + "... followed by the message");

			MultiChatLocal.getInstance().getProxyCommunicationManager().sendChatMessage(player.getUniqueId(), proxyMessage, proxyFormat.replace("%", "%%"));

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Aaaaand we sent it to the proxy! ALL DONE.");
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - UUID: " + player.getUniqueId());
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - MESSAGE (please note this will be shown in colour here even if the player doesn't have colour permissions): " + proxyMessage);
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - FORMAT: " + proxyFormat);

		} else {

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Oh dear... we need to send it to the proxy... but we weren't managing the chat...");

			String proxyMessage = mcce.getMessage();
			String proxyFormat = mcce.getFormat();

			MultiChatLocal.getInstance().getConsoleLogger().debug("MESSAGE:" + proxyMessage);
			MultiChatLocal.getInstance().getConsoleLogger().debug("FORMAT: " + proxyFormat.replace("%", "%%") + "... followed by the message");

			MultiChatLocal.getInstance().getProxyCommunicationManager().sendChatMessage(player.getUniqueId(), proxyMessage, proxyFormat.replace("%", "%%"));

			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - Aaaaand we sent it to the proxy! ALL DONE.");
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - UUID: " + mcce.getPlayer().getUniqueId());
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - MESSAGE (please note this will be shown in colour here even if the player doesn't have colour permissions): " + proxyMessage);
			MultiChatLocal.getInstance().getConsoleLogger().debug("#CHAT@MONITOR - FORMAT: " + proxyFormat);

		}

	}

}
