package xyz.olivermartin.multichat.proxy.common.channels.proxy;

import java.util.List;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.channels.ChannelManager;

public class GlobalStaticProxyChannel extends StaticProxyChannel {

	public GlobalStaticProxyChannel(String desc, String format, List<String> aliases, ChannelManager manager) {
		super("global", new ProxyChannelInfo(desc, format, false, MultiChatProxy.getInstance().getContextManager().getGlobalContext(), aliases), manager);
	}

}
