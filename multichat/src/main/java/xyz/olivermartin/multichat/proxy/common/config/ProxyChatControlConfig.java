package xyz.olivermartin.multichat.proxy.common.config;

import java.io.File;
import java.util.List;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlatform;
import xyz.olivermartin.multichat.proxy.common.RegexAction;
import xyz.olivermartin.multichat.proxy.common.RegexRule;

public abstract class ProxyChatControlConfig extends ProxyConfig {

	// VERSION
	private String version;

	// REGEX RULES
	private List<RegexRule> regexRules;
	private boolean applyRulesToGlobalChat;
	private boolean applyRulesToPrivateMessaging;
	private boolean applyRulesToGroupChat;
	private boolean applyRulesToStaffChat;
	private boolean applyRulesToDisplayCommand;
	private boolean applyRulesToAnnouncements;
	private boolean applyRulesToBulletins;
	private boolean applyRulesToCasts;
	private boolean applyRulesToHelpMe;

	// REGEX ACTIONS
	private List<RegexAction> regexActions;
	private boolean applyActionsToGlobalChat;
	private boolean applyActionsToPrivateMessaging;
	private boolean applyActionsToGroupChat;
	private boolean applyActionsToStaffChat;
	private boolean applyActionsToHelpMe;

	// ANTI SPAM
	private boolean useAntiSpam;
	private int antiSpamTime;
	private int spamSameMessage;
	private int antiSpamCooldown;
	private boolean antiSpamAction;
	private boolean antiSpamLocal;
	private int antiSpamTrigger;
	private String antiSpamCommand;
	private boolean applyAntiSpamToGlobalChat;
	private boolean applyAntiSpamToPrivateMessaging;
	private boolean applyAntiSpamToGroupChat;
	private boolean applyAntiSpamToHelpMe;

	// MUTE
	private boolean mute;
	private boolean applyMuteToGlobalChat;
	private boolean applyMuteToPrivateMessaging;
	private boolean applyMuteToGroupChat;
	private boolean applyMuteToHelpMe;
	private List<String> muteAliases;

	// IGNORE
	private boolean notifyIgnore;
	private boolean sessionIgnore;
	private boolean applyIgnoreToGlobalChat;
	private boolean applyIgnoreToPrivateMessaging;
	private boolean applyIgnoreToGroupChat;
	private List<String> ignoreAliases;

	// LINKS
	private boolean linkControl;
	private String linkRemovalMessage;
	private String linkControlRegex;

	public ProxyChatControlConfig(File configPath, String fileName, MultiChatProxyPlatform platform) {
		super(configPath, fileName, platform);
	}

	@Override
	public String getVersion() {
		if (version == null) throw new IllegalStateException("No version number found in MultiChatProxy's Chat Control Config!");
		return version;
	}

	protected abstract List<RegexRule> getRegexRulesFromConfig(String node);

	protected abstract List<RegexAction> getRegexActionsFromConfig(String node);

	@Override
	protected void setMemberAttributes() {

		// VERSION
		version = getString("version", null);

		// REGEX RULES
		regexRules = getRegexRulesFromConfig("regex_rules");
		applyRulesToGlobalChat = getBoolean("apply_rules_to.global_chat", true);
		applyRulesToPrivateMessaging = getBoolean("apply_rules_to.private_messages", false);
		applyRulesToGroupChat = getBoolean("apply_rules_to.group_chats", false);
		applyRulesToStaffChat = getBoolean("apply_rules_to.staff_chats", false);
		applyRulesToDisplayCommand = getBoolean("apply_rules_to.display_command", false);
		applyRulesToAnnouncements = getBoolean("apply_rules_to.announcements", false);
		applyRulesToBulletins = getBoolean("apply_rules_to.bulletins", false);
		applyRulesToCasts = getBoolean("apply_rules_to.casts", false);
		applyRulesToHelpMe = getBoolean("apply_rules_to.helpme", false);

		// REGEX ACTIONS
		regexActions = getRegexActionsFromConfig("regex_actions");
		applyActionsToGlobalChat = getBoolean("apply_actions_to.global_chat", true);
		applyActionsToPrivateMessaging = getBoolean("apply_actions_to.private_messages", false);
		applyActionsToGroupChat = getBoolean("apply_actions_to.group_chats", false);
		applyActionsToStaffChat = getBoolean("apply_actions_to.staff_chats", false);
		applyActionsToHelpMe = getBoolean("apply_actions_to.helpme", false);

		// ANTI SPAM
		useAntiSpam = getBoolean("anti_spam", true);
		antiSpamTime = getInt("anti_spam_time", 4);
		spamSameMessage = getInt("spam_same_message", 4);
		antiSpamCooldown = getInt("anti_spam_cooldown", 60);
		antiSpamAction = getBoolean("anti_spam_action", true);
		antiSpamLocal = getBoolean("anti_spam_spigot", true);
		antiSpamTrigger = getInt("anti_spam_trigger", 3);
		antiSpamCommand = getString("anti_spam_command", "kick %PLAYER% Spamming is not allowed");
		applyAntiSpamToGlobalChat = getBoolean("apply_anti_spam_to.global_chat", true);
		applyAntiSpamToPrivateMessaging = getBoolean("apply_anti_spam_to.private_messages", true);
		applyAntiSpamToGroupChat  = getBoolean("apply_anti_spam_to.group_chats", true);
		applyAntiSpamToHelpMe = getBoolean("apply_anti_spam_to.global_chat", true);

		// MUTE
		mute = getBoolean("mute", false);
		applyMuteToGlobalChat = getBoolean("apply_mute_to.global_chat", true);
		applyMuteToPrivateMessaging = getBoolean("apply_mute_to.private_messages", false);
		applyMuteToGroupChat = getBoolean("apply_mute_to.group_chats", false);
		applyMuteToHelpMe = getBoolean("apply_mute_to.helpme", false);
		muteAliases = getStringList("mutecommand");

		// IGNORE
		notifyIgnore = getBoolean("notify_ignore", false);
		sessionIgnore = getBoolean("session_ignore", false);
		applyIgnoreToGlobalChat = getBoolean("apply_ignore_to.global_chat", true);
		applyIgnoreToPrivateMessaging = getBoolean("apply_ignore_to.private_messages", true);
		applyIgnoreToGroupChat = getBoolean("apply_ignore_to.group_chats", false);
		ignoreAliases = getStringList("ignorecommand");

		// LINKS
		linkControl = getBoolean("link_control", false);
		linkRemovalMessage = getString("link_removal_message", "[LINK REMOVED]");
		linkControlRegex = getString("link_regex", "((https|http):\\/\\/)?(www\\.)?([-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.)+[a-zA-Z]{2,4}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)");

	}

	/**
	 * @return the regexRules
	 */
	public List<RegexRule> getRegexRules() {
		return regexRules;
	}

	/**
	 * @return the applyRulesToGlobalChat
	 */
	public boolean isApplyRulesToGlobalChat() {
		return applyRulesToGlobalChat;
	}

	/**
	 * @return the applyRulesToPrivateMessaging
	 */
	public boolean isApplyRulesToPrivateMessaging() {
		return applyRulesToPrivateMessaging;
	}

	/**
	 * @return the applyRulesToGroupChat
	 */
	public boolean isApplyRulesToGroupChat() {
		return applyRulesToGroupChat;
	}

	/**
	 * @return the applyRulesToStaffChat
	 */
	public boolean isApplyRulesToStaffChat() {
		return applyRulesToStaffChat;
	}

	/**
	 * @return the applyRulesToDisplayCommand
	 */
	public boolean isApplyRulesToDisplayCommand() {
		return applyRulesToDisplayCommand;
	}

	/**
	 * @return the applyRulesToAnnouncements
	 */
	public boolean isApplyRulesToAnnouncements() {
		return applyRulesToAnnouncements;
	}

	/**
	 * @return the applyRulesToBulletins
	 */
	public boolean isApplyRulesToBulletins() {
		return applyRulesToBulletins;
	}

	/**
	 * @return the applyRulesToCasts
	 */
	public boolean isApplyRulesToCasts() {
		return applyRulesToCasts;
	}

	/**
	 * @return the applyRulesToHelpMe
	 */
	public boolean isApplyRulesToHelpMe() {
		return applyRulesToHelpMe;
	}

	/**
	 * @return the regexActions
	 */
	public List<RegexAction> getRegexActions() {
		return regexActions;
	}

	/**
	 * @return the applyActionsToGlobalChat
	 */
	public boolean isApplyActionsToGlobalChat() {
		return applyActionsToGlobalChat;
	}

	/**
	 * @return the applyActionsToPrivateMessaging
	 */
	public boolean isApplyActionsToPrivateMessaging() {
		return applyActionsToPrivateMessaging;
	}

	/**
	 * @return the applyActionsToGroupChat
	 */
	public boolean isApplyActionsToGroupChat() {
		return applyActionsToGroupChat;
	}

	/**
	 * @return the applyActionsToStaffChat
	 */
	public boolean isApplyActionsToStaffChat() {
		return applyActionsToStaffChat;
	}

	/**
	 * @return the applyActionsToHelpMe
	 */
	public boolean isApplyActionsToHelpMe() {
		return applyActionsToHelpMe;
	}

	/**
	 * @return the useAntiSpam
	 */
	public boolean isUseAntiSpam() {
		return useAntiSpam;
	}

	/**
	 * @return the antiSpamTime
	 */
	public int getAntiSpamTime() {
		return antiSpamTime;
	}

	/**
	 * @return the spamSameMessage
	 */
	public int getSpamSameMessage() {
		return spamSameMessage;
	}

	/**
	 * @return the antiSpamCooldown
	 */
	public int getAntiSpamCooldown() {
		return antiSpamCooldown;
	}

	/**
	 * @return the antiSpamAction
	 */
	public boolean isAntiSpamAction() {
		return antiSpamAction;
	}

	/**
	 * @return the antiSpamLocal
	 */
	public boolean isAntiSpamLocal() {
		return antiSpamLocal;
	}

	/**
	 * @return the antiSpamTrigger
	 */
	public int getAntiSpamTrigger() {
		return antiSpamTrigger;
	}

	/**
	 * @return the antiSpamCommand
	 */
	public String getAntiSpamCommand() {
		return antiSpamCommand;
	}

	/**
	 * @return the applyAntiSpamToGlobalChat
	 */
	public boolean isApplyAntiSpamToGlobalChat() {
		return applyAntiSpamToGlobalChat;
	}

	/**
	 * @return the applyAntiSpamToPrivateMessaging
	 */
	public boolean isApplyAntiSpamToPrivateMessaging() {
		return applyAntiSpamToPrivateMessaging;
	}

	/**
	 * @return the applyAntiSpamToGroupChat
	 */
	public boolean isApplyAntiSpamToGroupChat() {
		return applyAntiSpamToGroupChat;
	}

	/**
	 * @return the applyAntiSpamToHelpMe
	 */
	public boolean isApplyAntiSpamToHelpMe() {
		return applyAntiSpamToHelpMe;
	}

	/**
	 * @return the mute
	 */
	public boolean isMute() {
		return mute;
	}

	/**
	 * @return the applyMuteToGlobalChat
	 */
	public boolean isApplyMuteToGlobalChat() {
		return applyMuteToGlobalChat;
	}

	/**
	 * @return the applyMuteToPrivateMessaging
	 */
	public boolean isApplyMuteToPrivateMessaging() {
		return applyMuteToPrivateMessaging;
	}

	/**
	 * @return the applyMuteToGroupChat
	 */
	public boolean isApplyMuteToGroupChat() {
		return applyMuteToGroupChat;
	}

	/**
	 * @return the applyMuteToHelpMe
	 */
	public boolean isApplyMuteToHelpMe() {
		return applyMuteToHelpMe;
	}

	/**
	 * @return the muteAliases
	 */
	public List<String> getMuteAliases() {
		return muteAliases;
	}

	/**
	 * @return the notifyIgnore
	 */
	public boolean isNotifyIgnore() {
		return notifyIgnore;
	}

	/**
	 * @return the sessionIgnore
	 */
	public boolean isSessionIgnore() {
		return sessionIgnore;
	}

	/**
	 * @return the applyIgnoreToGlobalChat
	 */
	public boolean isApplyIgnoreToGlobalChat() {
		return applyIgnoreToGlobalChat;
	}

	/**
	 * @return the applyIgnoreToPrivateMessaging
	 */
	public boolean isApplyIgnoreToPrivateMessaging() {
		return applyIgnoreToPrivateMessaging;
	}

	/**
	 * @return the applyIgnoreToGroupChat
	 */
	public boolean isApplyIgnoreToGroupChat() {
		return applyIgnoreToGroupChat;
	}

	/**
	 * @return the ignoreAliases
	 */
	public List<String> getIgnoreAliases() {
		return ignoreAliases;
	}

	/**
	 * @return the linkControl
	 */
	public boolean isLinkControl() {
		return linkControl;
	}

	/**
	 * @return the linkRemovalMessage
	 */
	public String getLinkRemovalMessage() {
		return linkRemovalMessage;
	}

	/**
	 * @return the linkControlRegex
	 */
	public String getLinkControlRegex() {
		return linkControlRegex;
	}

}
