package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.List;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;

public class GlobalChannel extends StaticNetworkChannel {

	public GlobalChannel(String id, String desc, String format, List<String> aliases, ChannelManager manager) {
		super(id, new ChannelInfo(desc, format, false, MultiChatProxy.getInstance().getContextManager().getGlobalContext(), aliases), manager);
	}

}
