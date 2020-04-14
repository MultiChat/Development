package xyz.olivermartin.multichat.local;

public abstract class LocalConsoleLogger {

	private MultiChatLocalPlatform platform;

	protected String prefix;
	protected String debugPrefix;

	private boolean debug;

	protected LocalConsoleLogger(MultiChatLocalPlatform platform) {
		this.platform = platform;
		debug = false;
		prefix = "&8[&2M&aC&3L&8]&7 ";
		debugPrefix = "&8[&2M&aC&3L&8][&4DEBUG&8]&7 ";
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public MultiChatLocalPlatform getPlatform() {
		return this.platform;
	}

	protected abstract void displayMessageUsingLogger(String message);

	protected abstract void sendColouredMessageToConsoleSender(String message);

	public void log(String message) {
		sendColouredMessageToConsoleSender(prefix + message);
	}

	public void debug(String message) {
		if (debug) sendColouredMessageToConsoleSender(debugPrefix + message);
	}

}
