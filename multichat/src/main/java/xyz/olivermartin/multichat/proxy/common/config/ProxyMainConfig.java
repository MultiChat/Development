package xyz.olivermartin.multichat.proxy.common.config;

import java.io.File;
import java.util.List;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;

public abstract class ProxyMainConfig extends ProxyConfig {

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

	public ProxyMainConfig(File configPath, String fileName, MultiChatProxyPlatform platform) {
		super(configPath, fileName, platform);
	}

	@Override
	protected void setMemberAttributes() {

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
