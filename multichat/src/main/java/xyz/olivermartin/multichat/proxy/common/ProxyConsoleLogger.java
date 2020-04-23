package xyz.olivermartin.multichat.proxy.common;

public abstract class ProxyConsoleLogger {

	private ProxyMessageManager messageManager;
	private MultiChatProxyPlatform platform;
	private boolean debug;
	private String debugPrefix = "&4[DEBUG]&f ";

	public ProxyConsoleLogger(ProxyMessageManager messageManager, MultiChatProxyPlatform platform) {
		this.platform = platform;
		this.messageManager = messageManager;
		this.debug = false;
	}

	public MultiChatProxyPlatform getPlatform() {
		return this.platform;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean toggleDebug() {
		this.debug = !this.debug;
		return this.debug;
	}

	protected abstract MultiChatProxyCommandSender getConsole();

	private void logToConsole(String prefixPart, String messagePart) {
		getConsole().sendDualMessage(messageManager.getMessage("console_main_prefix") + prefixPart, messagePart);
	}

	private void logToConsole(String colouredMessage) {
		logToConsole(colouredMessage, "");
	}

	public void log(String message) {
		logToConsole(message);
	}

	public void debug(String message) {
		if (debug) logToConsole(debugPrefix, message);
	}

	public void logDisplayMessage(String message) {
		logToConsole(messageManager.getMessage("console_display_prefix") + message);
	}

	public void logChat(String message) {
		logToConsole(messageManager.getMessage("console_chat_prefix") + message);
	}

	public void logModChat(String message) {
		if (MultiChatProxy.getInstance().getConfigManager().getProxyMainConfig().isLogStaffChat())
			logToConsole(messageManager.getMessage("console_modchat_prefix") + message);
	}

	public void logGroupChat(String message) {
		if (MultiChatProxy.getInstance().getConfigManager().getProxyMainConfig().isLogGroupChat())
			logToConsole(messageManager.getMessage("console_groupchat_prefix") + message);
	}

	public void logAdminChat(String message) {
		if (MultiChatProxy.getInstance().getConfigManager().getProxyMainConfig().isLogStaffChat())
			logToConsole(messageManager.getMessage("console_adminchat_prefix") + message);
	}

	public void logHelpMe(String message) {
		logToConsole(messageManager.getMessage("console_helpme_prefix") + message);
	}


	public void logBasicChat(String prefix, String message) {
		logToConsole(messageManager.getMessage("console_chat_prefix") + prefix, message);
	}

	public void logSocialSpy(String p1, String p2, String message) {
		if (MultiChatProxy.getInstance().getConfigManager().getProxyMainConfig().isLogPrivateMessaging())
			logToConsole(messageManager.getMessage("console_socialspy_prefix") + "(" + p1 + " -> " + p2 + ")  " + message);
	}

}
