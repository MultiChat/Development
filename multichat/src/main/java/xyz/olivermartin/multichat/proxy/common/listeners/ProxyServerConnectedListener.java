package xyz.olivermartin.multichat.proxy.common.listeners;

import de.myzelyam.api.vanish.BungeeVanishAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import xyz.olivermartin.multichat.bungee.*;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyJsonUtils;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

import java.util.UUID;

public class ProxyServerConnectedListener implements Listener {

	private void displayMessage(ProxiedPlayer player, ProxiedPlayer sender, String senderServer, String message) {

		message = MultiChatUtil.translateColorCodes(message);

		if (player.getUniqueId().equals(sender.getUniqueId())) {
			if (ProxyConfigs.CONFIG.isLegacyServer(senderServer)) message = MultiChatUtil.approximateRGBColorCodes(message);
		} else {
			if (player.getServer() == null) return;
			if (ProxyConfigs.CONFIG.isLegacyServer(player.getServer().getInfo().getName())) message = MultiChatUtil.approximateRGBColorCodes(message);
		}

		player.sendMessage(ProxyJsonUtils.parseMessage(message));

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onServerConnected(ServerConnectedEvent event) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();
		ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();

		ProxiedPlayer player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		String defaultChannel = MultiChatProxy.getInstance().getContextManager().getGlobalContext().getDefaultChannel();
		boolean forceChannel = MultiChatProxy.getInstance().getContextManager().getGlobalContext().isForceChannel();
		boolean firstJoin = false;

		// Set up chat info
		if (!ChatModeManager.getInstance().existsPlayer(uuid)) {

			boolean globalMode;

			if (!defaultChannel.equalsIgnoreCase("local")) {
				globalMode = true;
			} else {
				globalMode = false;
			}
			ChatModeManager.getInstance().registerPlayer(uuid, globalMode);
			firstJoin = true;

		}

		// If we are forcing the channels, then force it
		if (forceChannel) {

			boolean globalMode;
			if (!defaultChannel.equalsIgnoreCase("local")) {
				globalMode = true;
			} else {
				globalMode = false;
			}
			ChatModeManager.getInstance().registerPlayer(uuid, globalMode);

		}

		// Set player to appropriate channels in NEW CHANNELS system
		if (ChatModeManager.getInstance().isGlobal(uuid)) {
			channelManager.select(uuid, "global");
		} else {
			channelManager.select(uuid, "local");
		}

		// Remove any old UUID - Name pairings
		if (UUIDNameManager.existsUUID(uuid)) {
			UUIDNameManager.removeUUID(uuid);
		}

		// Register updated entry in UUID - Name map
		UUIDNameManager.addNew(uuid, player.getName());
		ConsoleManager.log("Refreshed UUID-Name lookup: " + uuid.toString());

		// If player is only switching server (not joining for first time) then leave now
		if (ds.getJoinedNetwork().contains(player.getUniqueId())) return;
		ds.getJoinedNetwork().add(player.getUniqueId());

		// If MultiChat is handling join messages...
		if (ProxyConfigs.JOIN_MESSAGES.isShowJoin()
				|| ProxyConfigs.JOIN_MESSAGES.isWelcome()
				|| ProxyConfigs.JOIN_MESSAGES.isPrivateWelcome()) {

			// PremiumVanish support, return as early as possible to avoid loading unnecessary resources
			if (MultiChat.premiumVanish && ProxyConfigs.CONFIG.isPvSilenceJoin() && BungeeVanishAPI.isInvisible(player)) {
				return;
			}

			// Load join message formats from config
			String joinformat = ProxyConfigs.JOIN_MESSAGES.getServerJoin();
			String silentformat = ProxyConfigs.JOIN_MESSAGES.getSilentJoin();
			String welcomeMessage = ProxyConfigs.JOIN_MESSAGES.getWelcomeMessage();
			String privateWelcomeMessage = ProxyConfigs.JOIN_MESSAGES.getPrivateWelcomeMessage();

			// Replace the placeholders
			ChatManipulation chatman = new ChatManipulation(); // TODO Legacy
			joinformat = chatman.replaceJoinMsgVars(joinformat, player.getName(), event.getServer().getInfo().getName());
			silentformat = chatman.replaceJoinMsgVars(silentformat, player.getName(), event.getServer().getInfo().getName());
			welcomeMessage = chatman.replaceJoinMsgVars(welcomeMessage, player.getName(), event.getServer().getInfo().getName());
			privateWelcomeMessage = chatman.replaceJoinMsgVars(privateWelcomeMessage, player.getName(), event.getServer().getInfo().getName());

			// Check which messages should be broadcast
			boolean broadcastWelcome = ProxyConfigs.JOIN_MESSAGES.isWelcome();
			boolean privateWelcome = ProxyConfigs.JOIN_MESSAGES.isPrivateWelcome();
			boolean broadcastJoin = !player.hasPermission("multichat.staff.silentjoin");

			// Broadcast
			for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

				if (broadcastJoin) {

					if (firstJoin && broadcastWelcome) {
						displayMessage(onlineplayer, event.getPlayer(), event.getServer().getInfo().getName(), welcomeMessage);
					}

					if (firstJoin && privateWelcome
							&& onlineplayer.getName().equals(player.getName())) {

						displayMessage(onlineplayer, event.getPlayer(), event.getServer().getInfo().getName(), privateWelcomeMessage);

					}

					if (ProxyConfigs.JOIN_MESSAGES.isShowJoin()) {
						displayMessage(onlineplayer, event.getPlayer(), event.getServer().getInfo().getName(), joinformat);
					}

				} else {

					ds.getHiddenStaff().add(player.getUniqueId());

					if (ProxyConfigs.JOIN_MESSAGES.isShowJoin()) {
						if (onlineplayer.hasPermission("multichat.staff.silentjoin") ) {
							displayMessage(onlineplayer, event.getPlayer(), event.getServer().getInfo().getName(), silentformat);
						}
					}

				}

			}

		}

	}

}
