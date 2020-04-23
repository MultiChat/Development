package xyz.olivermartin.multichat.proxy.bungee;

import net.md_5.bungee.api.ProxyServer;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyCommandSender;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;
import xyz.olivermartin.multichat.proxy.common.ProxyConsoleLogger;
import xyz.olivermartin.multichat.proxy.common.ProxyMessageManager;

public class ProxyBungeeConsoleLogger extends ProxyConsoleLogger {

	public ProxyBungeeConsoleLogger(ProxyMessageManager messageManager) {
		super(messageManager, MultiChatProxyPlatform.BUNGEE);
	}

	@Override
	protected MultiChatProxyCommandSender getConsole() {
		return new MultiChatProxyBungeeCommandSender(ProxyServer.getInstance().getConsole());
	}

}
