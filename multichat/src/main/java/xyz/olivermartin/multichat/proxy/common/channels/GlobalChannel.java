package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.List;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;

public class GlobalChannel extends StaticNetworkChannel {

	public GlobalChannel(String desc, String format, List<String> aliases, ChannelManager manager) {
		super("global", new ChannelInfo(desc, format, false, MultiChatProxy.getInstance().getContextManager().getGlobalContext(), aliases), manager);
	}

}
