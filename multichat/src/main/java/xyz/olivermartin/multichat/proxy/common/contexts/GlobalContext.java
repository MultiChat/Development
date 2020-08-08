package xyz.olivermartin.multichat.proxy.common.contexts;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

import java.util.ArrayList;

public class GlobalContext extends Context {

	public GlobalContext(String defaultChannel, boolean forceChannel, boolean blacklistServers) {
		super("global", 0, defaultChannel, forceChannel, blacklistServers, new ArrayList<>());
	}

	@Override
	public boolean contains(CommandSender sender) {
		if (!(sender instanceof ProxiedPlayer)) return true;

		ProxiedPlayer player = (ProxiedPlayer) sender;
		if (player.getServer() == null) return false;

		/*
		global    blacklist    needed
		--------------------------------
		0         0            0
		0         1            1
		1         0            1
		1         1            0

		This concludes that we can use XOR
		 */
		return ProxyConfigs.CONFIG.isGlobalServer(player.getServer().getInfo().getName()) ^ isBlacklistServers();
	}
}
