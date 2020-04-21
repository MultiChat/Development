package xyz.olivermartin.multichat.proxy.common.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.List;

import xyz.olivermartin.multichat.common.config.ConfigStatus;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;

public abstract class ProxyMainConfig {

	// GENERAL
	private boolean fetchSpigotDisplayNames;
	private boolean setDisplayName;
	private String displayNameFormat;

	// PMs
	private boolean usePrivateMessaging;
	private List<String> noPrivateMessaging;
	private boolean allowPrivateMessagingToggle;
	private String privateMessagingOutFormat;
	private String privateMessagingInFormat;
	private String privateMessagingSpyFormat;
	private List<String> msgAliases;
	private List<String> rAliases;
	private List<String> socialSpyAliases;

	// Chat Channels
	private String defaultChannel;
	private boolean forceChannelOnJoin;
	private List<String> channelAliases;
	private List<String> globalAliases;
	private List<String> localAliases;

	// Global Chat
	private boolean useGlobalChat;
	private List<String> noGlobal;
	private String globalFormat;

	// Group Chats
	private String groupChatFormat;
	private String groupChatCCDefault;
	private String groupChatNCDefault;

	// Staff Chats
	private String modChatFormat;
	private String modChatCCDefault;
	private String modChatNCDefault;
	private String adminChatFormat;
	private String adminChatCCDefault;
	private String adminChatNCDefault;

	// Other
	private boolean allowStaffList;
	private List<String> multichatBypassAliases;
	private List<String> multichatExecuteAliases;

	// Privacy Controls
	private boolean logPrivateMessaging;
	private boolean logStaffChat;
	private boolean logGroupChat;

	// FILE SETTINGS
	private File configPath;
	private String fileName;
	protected boolean ready;
	private MultiChatProxyPlatform platform;

	public ProxyMainConfig(File configPath, String fileName, MultiChatProxyPlatform platform) {
		this.configPath = configPath;
		this.fileName = fileName;
		this.platform = platform;
		ConfigStatus startupStatus = startupConfig();
		ready = (startupStatus != ConfigStatus.FAILED);
	}

	public MultiChatProxyPlatform getPlatform() {
		return this.platform;
	}

	protected File getFile() {
		return new File(configPath, fileName);
	}

	protected ConfigStatus startupConfig() {

		boolean createdNew = false;

		try {

			File file = new File(configPath, fileName);

			if (!file.exists()) {
				saveDefaultConfig();
				createdNew = true;
			} 

			ConfigStatus loadStatus = loadConfig();
			if (loadStatus == ConfigStatus.FAILED) return ConfigStatus.FAILED;

			setMemberAttributes();

			if (createdNew) {
				return ConfigStatus.CREATED;
			} else {
				return ConfigStatus.LOADED;
			}

		} catch (IOException e) {
			return ConfigStatus.FAILED;
		}

	}

	protected void saveDefaultConfig() throws IOException {

		// Load default file into input stream
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

		// Copy to desired location
		Files.copy(inputStream, new File(configPath, fileName).toPath(), new CopyOption[0]);

	}

	/**
	 * This should physically load the config from the file system into some kind of Configuration class depending on the platform
	 * @return Status of loading config
	 */
	protected abstract ConfigStatus loadConfig();

	/**
	 * This should set all the member attributes of this file according to the real file (or to a default value if not present)
	 */
	private void setMemberAttributes() {

		// GENERAL
		fetchSpigotDisplayNames = getBoolean("fetch_spigot_display_names", true);
		setDisplayName = getBoolean("set_display_name", true);
		displayNameFormat = getString("display_name_format", "%PREFIX%%NICK%%SUFFIX%");

		// PMs
		usePrivateMessaging = getBoolean("pm", true);
		noPrivateMessaging = getStringList("no_pm");
		allowPrivateMessagingToggle = getBoolean("toggle_pm", true);
		privateMessagingOutFormat = getString("pmout", "&6[&cYou &6-> &c%DISPLAYNAMET%&6] &f%MESSAGE%");
		privateMessagingInFormat = getString("pmin", "&6[&c%DISPLAYNAME% &6-> &cMe&6] &f%MESSAGE%");
		privateMessagingSpyFormat = getString("pmspy", "&8&l<< &f%NAME% &7-> &f%NAMET%&8: &7%MESSAGE% &8&l>>");
		msgAliases = getStringList("msgcommand");
		rAliases = getStringList("rcommand");
		socialSpyAliases = getStringList("socialspycommand");

		// Chat Channels
		defaultChannel = getString("default_channel", "global");
		forceChannelOnJoin = getBoolean("force_channel_on_join", false);
		channelAliases = getStringList("channelcommand");
		globalAliases = getStringList("globalcommand");
		localAliases = getStringList("localcommand");

		// Global Chat
		useGlobalChat = getBoolean("global", true);
		noGlobal = getStringList("no_global");
		globalFormat = getString("globalformat", "&2[&aG&2] &f%DISPLAYNAME%&f: ");

		// Group Chats
		groupChatFormat = getString("groupchat.format", "%CC%(%NC%%GROUPNAME%%CC%)(%NC%%NAME%%CC%) %MESSAGE%");
		groupChatCCDefault = getString("groupchat.ccdefault", "a");
		groupChatNCDefault = getString("groupchat.ncdefault", "f");

		// Staff Chats
		modChatFormat = getString("modchat.format", "%CC%{%NC%%NAME%%CC%} %MESSAGE%");
		modChatCCDefault = getString("modchat.ccdefault", "b");
		modChatNCDefault = getString("modchat.ncdefault", "d");
		adminChatFormat = getString("adminchat.format", "%CC%{%NC%%NAME%%CC%} %MESSAGE%");
		adminChatCCDefault = getString("adminchat.ccdefault", "d");
		adminChatNCDefault = getString("adminchat.ncdefault", "b");

		// Other
		allowStaffList = getBoolean("staff_list", true);
		multichatBypassAliases = getStringList("multichatbypasscommand");
		multichatExecuteAliases = getStringList("multichatexecutecommand");

		// Privacy Controls
		logPrivateMessaging = getBoolean("privacy_settings.log_pms", true);
		logStaffChat = getBoolean("privacy_settings.log_staffchat", true);
		logGroupChat = getBoolean("privacy_settings.log_groupchat", true);

	}

	protected abstract String getString(String configNode, String defaultValue);

	protected abstract boolean getBoolean(String configNode, boolean defaultValue);

	protected abstract int getInt(String configNode, int defaultValue);

	protected abstract List<String> getStringList(String configNode);

	/**
	 * Reload this configuration file (and reload all the member attributes)
	 */
	public ConfigStatus reload() {

		ConfigStatus startupStatus = startupConfig();
		return startupStatus;

	}

	/**
	 * @return the fetchSpigotDisplayNames
	 */
	public boolean isFetchSpigotDisplayNames() {
		return fetchSpigotDisplayNames;
	}

	/**
	 * @return the setDisplayName
	 */
	public boolean isSetDisplayName() {
		return setDisplayName;
	}

	/**
	 * @return the displayNameFormat
	 */
	public String getDisplayNameFormat() {
		return displayNameFormat;
	}

	/**
	 * @return the usePrivateMessaging
	 */
	public boolean isUsePrivateMessaging() {
		return usePrivateMessaging;
	}

	/**
	 * @return the noPrivateMessaging
	 */
	public List<String> getNoPrivateMessaging() {
		return noPrivateMessaging;
	}

	/**
	 * @return the allowPrivateMessagingToggle
	 */
	public boolean isAllowPrivateMessagingToggle() {
		return allowPrivateMessagingToggle;
	}

	/**
	 * @return the privateMessagingOutFormat
	 */
	public String getPrivateMessagingOutFormat() {
		return privateMessagingOutFormat;
	}

	/**
	 * @return the privateMessagingInFormat
	 */
	public String getPrivateMessagingInFormat() {
		return privateMessagingInFormat;
	}

	/**
	 * @return the privateMessagingSpyFormat
	 */
	public String getPrivateMessagingSpyFormat() {
		return privateMessagingSpyFormat;
	}

	/**
	 * @return the msgAliases
	 */
	public List<String> getMsgAliases() {
		return msgAliases;
	}

	/**
	 * @return the rAliases
	 */
	public List<String> getrAliases() {
		return rAliases;
	}

	/**
	 * @return the socialSpyAliases
	 */
	public List<String> getSocialSpyAliases() {
		return socialSpyAliases;
	}

	/**
	 * @return the defaultChannel
	 */
	public String getDefaultChannel() {
		return defaultChannel;
	}

	/**
	 * @return the forceChannelOnJoin
	 */
	public boolean isForceChannelOnJoin() {
		return forceChannelOnJoin;
	}

	/**
	 * @return the channelAliases
	 */
	public List<String> getChannelAliases() {
		return channelAliases;
	}

	/**
	 * @return the globalAliases
	 */
	public List<String> getGlobalAliases() {
		return globalAliases;
	}

	/**
	 * @return the localAliases
	 */
	public List<String> getLocalAliases() {
		return localAliases;
	}

	/**
	 * @return the useGlobalChat
	 */
	public boolean isUseGlobalChat() {
		return useGlobalChat;
	}

	/**
	 * @return the noGlobal
	 */
	public List<String> getNoGlobal() {
		return noGlobal;
	}

	/**
	 * @return the globalFormat
	 */
	public String getGlobalFormat() {
		return globalFormat;
	}

	/**
	 * @return the groupChatFormat
	 */
	public String getGroupChatFormat() {
		return groupChatFormat;
	}

	/**
	 * @return the groupChatCCDefault
	 */
	public String getGroupChatCCDefault() {
		return groupChatCCDefault;
	}

	/**
	 * @return the groupChatNCDefault
	 */
	public String getGroupChatNCDefault() {
		return groupChatNCDefault;
	}

	/**
	 * @return the modChatFormat
	 */
	public String getModChatFormat() {
		return modChatFormat;
	}

	/**
	 * @return the modChatCCDefault
	 */
	public String getModChatCCDefault() {
		return modChatCCDefault;
	}

	/**
	 * @return the modChatNCDefault
	 */
	public String getModChatNCDefault() {
		return modChatNCDefault;
	}

	/**
	 * @return the adminChatFormat
	 */
	public String getAdminChatFormat() {
		return adminChatFormat;
	}

	/**
	 * @return the adminChatCCDefault
	 */
	public String getAdminChatCCDefault() {
		return adminChatCCDefault;
	}

	/**
	 * @return the adminChatNCDefault
	 */
	public String getAdminChatNCDefault() {
		return adminChatNCDefault;
	}

	/**
	 * @return the allowStaffList
	 */
	public boolean isAllowStaffList() {
		return allowStaffList;
	}

	/**
	 * @return the multichatBypassAliases
	 */
	public List<String> getMultichatBypassAliases() {
		return multichatBypassAliases;
	}

	/**
	 * @return the multichatExecuteAliases
	 */
	public List<String> getMultichatExecuteAliases() {
		return multichatExecuteAliases;
	}

	/**
	 * @return the logPrivateMessaging
	 */
	public boolean isLogPrivateMessaging() {
		return logPrivateMessaging;
	}

	/**
	 * @return the logStaffChat
	 */
	public boolean isLogStaffChat() {
		return logStaffChat;
	}

	/**
	 * @return the logGroupChat
	 */
	public boolean isLogGroupChat() {
		return logGroupChat;
	}

}
