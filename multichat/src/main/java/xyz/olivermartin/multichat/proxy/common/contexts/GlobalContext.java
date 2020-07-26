package xyz.olivermartin.multichat.proxy.common.contexts;

import java.util.List;

public class GlobalContext extends Context {

	public GlobalContext(String defaultChannel, boolean forceChannel, boolean blacklistServers, List<String> servers) {
		super("global", 0, defaultChannel, forceChannel, blacklistServers, servers);
	}

}
