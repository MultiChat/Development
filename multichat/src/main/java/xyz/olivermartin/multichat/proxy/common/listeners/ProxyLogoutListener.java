package xyz.olivermartin.multichat.proxy.common.listeners;

import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import xyz.olivermartin.multichat.bungee.ChatControl;
import xyz.olivermartin.multichat.bungee.ChatManipulation;
import xyz.olivermartin.multichat.bungee.ConfigManager;
import xyz.olivermartin.multichat.bungee.ConsoleManager;
import xyz.olivermartin.multichat.bungee.Events;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.bungee.PlayerMetaManager;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyJsonUtils;
import xyz.olivermartin.multichat.proxy.common.ProxyUtils;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

public class ProxyLogoutListener implements Listener {

	private void displayMessage(ProxiedPlayer player, String message) {

		if (player.getServer() == null) return;

		message = ProxyUtils.translateColourCodes(message);

		if (MultiChat.legacyServers.contains(player.getServer().getInfo().getName())) message = MultiChatUtil.approximateHexCodes(message);

		player.sendMessage(ProxyJsonUtils.parseMessage(message));

	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogout(PlayerDisconnectEvent event) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();
		ProxiedPlayer player = event.getPlayer();
		UUID uuid = event.getPlayer().getUniqueId();

		/*
		 * Remove volatile entries
		 */

		if (ds.getHiddenStaff().contains(uuid)) {
			ds.getHiddenStaff().remove(uuid);
		}

		if (Events.mcbPlayers.contains(uuid)) {
			Events.mcbPlayers.remove(uuid);
		}

		if (Events.MCToggle.contains(uuid)) {
			Events.MCToggle.remove(uuid);
		}
		if (Events.ACToggle.contains(uuid)) {
			Events.ACToggle.remove(uuid);
		}
		if (Events.GCToggle.contains(uuid)) {
			Events.GCToggle.remove(uuid);
		}

		Configuration config = ConfigManager.getInstance().getHandler(ConfigFile.CHAT_CONTROL).getConfig();

		// If using sessional ignore, then wipe ignores stored
		if (config.getBoolean("session_ignore")) {
			ChatControl.unignoreAll(uuid);
		}

		// Reset their spam data on logout (nothing is stored persistently)
		ChatControl.spamPardonPlayer(uuid);

		// Remove viewed group chat preferences
		if (ds.getViewedChats().containsKey(uuid)) {
			ds.getViewedChats().remove(uuid);
		}

		// Unregister player from volatile meta store
		PlayerMetaManager.getInstance().unregisterPlayer(uuid);

		ConsoleManager.log("Un-Registered player " + player.getName());

		// Remove player from the "joined network" list
		ds.getJoinedNetwork().remove(player.getUniqueId());

		// If we are handling the quit messages, then handle them...
		if ( ConfigManager.getInstance().getHandler(ConfigFile.JOIN_MESSAGES).getConfig().getBoolean("showquit") == true ) {

			// Get the formats
			String joinformat = ConfigManager.getInstance().getHandler(ConfigFile.JOIN_MESSAGES).getConfig().getString("networkquit");
			String silentformat = ConfigManager.getInstance().getHandler(ConfigFile.JOIN_MESSAGES).getConfig().getString("silentquit");

			// Replace the placeholders
			ChatManipulation chatman = new ChatManipulation();
			joinformat = chatman.replaceJoinMsgVars(joinformat, player.getName(), player.getServer().getInfo().getName());
			silentformat = chatman.replaceJoinMsgVars(silentformat, player.getName(), player.getServer().getInfo().getName());

			// Broadcast
			for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

				if (!player.hasPermission("multichat.staff.silentjoin")) {

					displayMessage(onlineplayer, joinformat);

				} else {

					if (onlineplayer.hasPermission("multichat.staff.silentjoin") ) {
						displayMessage(onlineplayer, silentformat);
					}

				}

			}

		}

	}

}
