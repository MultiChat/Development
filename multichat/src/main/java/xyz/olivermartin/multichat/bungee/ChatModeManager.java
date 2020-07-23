package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;

public class ChatModeManager {

	private static ChatModeManager instance;

	public static ChatModeManager getInstance() {
		return instance;
	}

	static {
		instance = new ChatModeManager();
	}

	/* END STATIC */

	private Map<UUID, Boolean> globalPlayers;

	private ChatModeManager() {
		globalPlayers = new HashMap<UUID, Boolean>();
	}

	public void setLocal(UUID uuid) {

		ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();

		globalPlayers.put(uuid, false);

		// TODO
		//LegacyChannel.setChannel(uuid, LegacyChannel.getLocalChannel());
		channelManager.select(uuid, "local");

		// TODO
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
		if (player == null) return;

		/*LegacyChannel local = LegacyChannel.getLocalChannel();
		if (!local.isMember(uuid)) {
			local.removeMember(uuid);
			MessageManager.sendSpecialMessage(player, "command_channel_show", "LOCAL");
		}*/

		if (channelManager.isHidden(uuid, "local")) {
			channelManager.show(uuid, "local");
			MessageManager.sendSpecialMessage(player, "command_channel_show", "LOCAL");
		}

		// TODO
		//ProxyLocalCommunicationManager.sendPlayerDataMessage(player.getName(), LegacyChannel.getChannel(uuid).getName(), LegacyChannel.getChannel(uuid), player.getServer().getInfo(), (player.hasPermission("multichat.chat.colour")||player.hasPermission("multichat.chat.color")||player.hasPermission("multichat.chat.colour.simple")||player.hasPermission("multichat.chat.color.simple")), (player.hasPermission("multichat.chat.colour")||player.hasPermission("multichat.chat.color")||player.hasPermission("multichat.chat.colour.rgb")||player.hasPermission("multichat.chat.color.rgb")));
		ProxyLocalCommunicationManager.sendPlayerDataMessage(player.getName(), "local", player.getServer().getInfo(), (player.hasPermission("multichat.chat.colour")||player.hasPermission("multichat.chat.color")||player.hasPermission("multichat.chat.colour.simple")||player.hasPermission("multichat.chat.color.simple")), (player.hasPermission("multichat.chat.colour")||player.hasPermission("multichat.chat.color")||player.hasPermission("multichat.chat.colour.rgb")||player.hasPermission("multichat.chat.color.rgb")));

	}

	public void setGlobal(UUID uuid) {

		ChannelManager channelManager = MultiChatProxy.getInstance().getChannelManager();

		globalPlayers.put(uuid, true);

		// TODO
		channelManager.select(uuid, "global");
		//LegacyChannel.setChannel(uuid, LegacyChannel.getGlobalChannel());

		// TODO
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
		if (player == null) return;

		/*LegacyChannel global = LegacyChannel.getGlobalChannel();
		if (!global.isMember(uuid)) {
			global.removeMember(uuid);
			MessageManager.sendSpecialMessage(player, "command_channel_show", "GLOBAL");
		}*/

		if (channelManager.isHidden(uuid, "global")) {
			channelManager.show(uuid, "global");
			MessageManager.sendSpecialMessage(player, "command_channel_show", "GLOBAL");
		}

		ProxyLocalCommunicationManager.sendPlayerDataMessage(player.getName(), "global", player.getServer().getInfo(), (player.hasPermission("multichat.chat.colour")||player.hasPermission("multichat.chat.color")||player.hasPermission("multichat.chat.colour.simple")||player.hasPermission("multichat.chat.color.simple")), (player.hasPermission("multichat.chat.colour")||player.hasPermission("multichat.chat.color")||player.hasPermission("multichat.chat.colour.rgb")||player.hasPermission("multichat.chat.color.rgb")));

	}

	public void registerPlayer(UUID uuid, boolean global) {

		globalPlayers.put(uuid, global);

	}

	public boolean existsPlayer(UUID uuid) {

		return globalPlayers.containsKey(uuid);

	}

	public Map<UUID, Boolean> getData() {
		return globalPlayers;
	}

	public void loadData(Map<UUID, Boolean> data) {
		this.globalPlayers = data;
		if (this.globalPlayers == null) {
			DebugManager.log("The global players data loaded was null... So made a new map!");
			globalPlayers = new HashMap<UUID, Boolean>();
		}
	}

	public boolean isGlobal(UUID uuid) {
		if (globalPlayers.containsKey(uuid)) {
			return globalPlayers.get(uuid);
		} else {
			return true;
		}
	}

}
