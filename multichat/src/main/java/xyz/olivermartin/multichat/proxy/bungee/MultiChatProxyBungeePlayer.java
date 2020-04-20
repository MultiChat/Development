package xyz.olivermartin.multichat.proxy.bungee;

import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlayer;

public class MultiChatProxyBungeePlayer extends MultiChatProxyBungeeCommandSender implements MultiChatProxyPlayer {

	private ProxiedPlayer player;

	public MultiChatProxyBungeePlayer(ProxiedPlayer player) {
		super(player);
	}

	@Override
	public UUID getUniqueId() {
		return player.getUniqueId();
	}

	@Override
	public String getPrefix() {
		return MultiChatProxy.getInstance().getPlayerMetaStore().getPrefix(getUniqueId());
	}

	@Override
	public String getSuffix() {
		return MultiChatProxy.getInstance().getPlayerMetaStore().getSuffix(getUniqueId());
	}

	@Override
	public String getDisplayName() {
		return MultiChatProxy.getInstance().getPlayerMetaStore().getDisplayName(getUniqueId());
	}

	@Override
	public String getWorld() {
		return MultiChatProxy.getInstance().getPlayerMetaStore().getWorld(getUniqueId());
	}

	@Override
	public String getServer() {
		Server server = player.getServer();
		if (server == null) return "";
		return server.getInfo().getName();
	}

	@Override
	public String getCurrentName() {
		return MultiChatProxy.getInstance().getPlayerMetaStore().getCurrentName(getUniqueId());
	}

}
