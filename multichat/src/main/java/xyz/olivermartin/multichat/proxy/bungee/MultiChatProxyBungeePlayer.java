package xyz.olivermartin.multichat.proxy.bungee;

import java.util.Optional;
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
		Optional<String> opValue = MultiChatProxy.getInstance().getPlayerMetaStore().getPrefix(getUniqueId());
		if (opValue.isPresent())  {
			return opValue.get();
		} else {
			return "";
		}
	}

	@Override
	public String getSuffix() {
		Optional<String> opValue = MultiChatProxy.getInstance().getPlayerMetaStore().getSuffix(getUniqueId());
		if (opValue.isPresent())  {
			return opValue.get();
		} else {
			return "";
		}
	}

	@Override
	public String getDisplayName() {
		Optional<String> opValue = MultiChatProxy.getInstance().getPlayerMetaStore().getDisplayName(getUniqueId());
		if (opValue.isPresent())  {

			player.setDisplayName(opValue.get());

			return opValue.get();
		} else {
			return getCurrentName();
		}
	}

	@Override
	public String getWorld() {
		Optional<String> opValue = MultiChatProxy.getInstance().getPlayerMetaStore().getWorld(getUniqueId());
		if (opValue.isPresent())  {
			return opValue.get();
		} else {
			return "";
		}
	}

	@Override
	public String getServer() {
		Server server = player.getServer();
		if (server == null) return "";
		return server.getInfo().getName();
	}

	@Override
	public String getCurrentName() {
		Optional<String> opValue = MultiChatProxy.getInstance().getPlayerMetaStore().getNickname(getUniqueId());
		if (opValue.isPresent())  {
			return opValue.get();
		} else {
			return getName();
		}
	}

}
