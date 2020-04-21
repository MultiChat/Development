package xyz.olivermartin.multichat.proxy.common.config;

import java.io.File;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;

public abstract class ProxyJoinMessagesConfig extends ProxyConfig {

	// Join Messages
	private boolean showJoinMessages;
	private boolean showQuitMessages;

	private String serverJoinFormat;
	private String silentJoinFormat;
	private String serverQuitFormat;
	private String silentQuitFormat;

	// Welcome messages
	private boolean welcome;
	private String welcomeMessage;
	private boolean privateWelcome;
	private String privateWelcomeMessage;

	public ProxyJoinMessagesConfig(File configPath, String fileName, MultiChatProxyPlatform platform) {
		super(configPath, fileName, platform);
	}

	@Override
	protected void setMemberAttributes() {

		// Join Messages
		showJoinMessages = getBoolean("showjoin", true);
		showQuitMessages = getBoolean("showquit", true);

		serverJoinFormat = getString("serverjoin", "&e%NAME% &ejoined the network");
		silentJoinFormat = getString("silentjoin", "&b&o%NAME% &b&ojoined the network silently");
		serverQuitFormat = getString("networkquit", "&e%NAME% left the network");
		silentQuitFormat = getString("silentquit", "&b&o%NAME% &b&oleft the network silently");

		// Welcome messages
		welcome = getBoolean("welcome", true);
		welcomeMessage = getString("welcome_message", "&dWelcome %NAME% to the network for the first time!");
		privateWelcome = getBoolean("private_welcome", false);
		privateWelcomeMessage = getString("private_welcome_message", "&5Hi there %NAME%, please make sure you read the /rules!");

	}

	/**
	 * @return the showJoinMessages
	 */
	public boolean isShowJoinMessages() {
		return showJoinMessages;
	}

	/**
	 * @return the showQuitMessages
	 */
	public boolean isShowQuitMessages() {
		return showQuitMessages;
	}

	/**
	 * @return the serverJoinFormat
	 */
	public String getServerJoinFormat() {
		return serverJoinFormat;
	}

	/**
	 * @return the silentJoinFormat
	 */
	public String getSilentJoinFormat() {
		return silentJoinFormat;
	}

	/**
	 * @return the serverQuitFormat
	 */
	public String getServerQuitFormat() {
		return serverQuitFormat;
	}

	/**
	 * @return the silentQuitFormat
	 */
	public String getSilentQuitFormat() {
		return silentQuitFormat;
	}

	/**
	 * @return the welcome
	 */
	public boolean isWelcome() {
		return welcome;
	}

	/**
	 * @return the welcomeMessage
	 */
	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	/**
	 * @return the privateWelcome
	 */
	public boolean isPrivateWelcome() {
		return privateWelcome;
	}

	/**
	 * @return the privateWelcomeMessage
	 */
	public String getPrivateWelcomeMessage() {
		return privateWelcomeMessage;
	}

}
