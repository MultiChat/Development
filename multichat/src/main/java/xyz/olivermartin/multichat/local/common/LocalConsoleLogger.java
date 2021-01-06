package xyz.olivermartin.multichat.local.common;

import xyz.olivermartin.multichat.common.MultiChatUtil;

public abstract class LocalConsoleLogger {

	private MultiChatLocalPlatform platform;

	protected static final String PREFIX = MultiChatUtil.translateColorCodes("&8[&2M&aC&3L&8]&7 ");
	protected static final String DEBUG_PREFIX = PREFIX + MultiChatUtil.translateColorCodes("&8[&4DEBUG&8]&7 ");

	private boolean debug;

	protected LocalConsoleLogger(MultiChatLocalPlatform platform) {
		this.platform = platform;
		debug = false;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean toggleDebug() {
		this.debug = !this.debug;
		return this.debug;
	}

	public MultiChatLocalPlatform getPlatform() {
		return this.platform;
	}

	protected abstract void displayMessageUsingLogger(String message);

	protected abstract void sendConsoleMessage(String message);

	public void log(String message) {
		sendConsoleMessage(PREFIX + message);
	}

	public void debug(String message) {
		debug("", message);
	}

	public void debug(String prefix, String message) {
		if (debug) sendConsoleMessage(DEBUG_PREFIX
				+ MultiChatUtil.approximateRGBColorCodes(MultiChatUtil.translateColorCodes(prefix))
				+ message);
	}

}
