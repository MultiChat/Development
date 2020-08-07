package xyz.olivermartin.multichat.proxy.common.config;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import xyz.olivermartin.multichat.common.MessageType;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ProxyChatControl extends AbstractProxyConfig {

    private String version, antiSpamCommand, linkRemovalMessage;
    // List instead of Set, so owners can decide importance of rules via config order
    private final List<RegexRule> regexRules = new ArrayList<>();
    private final List<RegexAction> regexActions = new ArrayList<>();
    private final Map<MessageType, Boolean> applyRulesTo = new HashMap<>(), applyActionsTo = new HashMap<>(),
            applyAntiSpamTo = new HashMap<>(), applyMuteTo = new HashMap<>(), applyIgnoreTo = new HashMap<>();

    private boolean antiSpam, antiSpamAction, antiSpamSpigot, mute, notifyIgnore, sessionIgnore, linkControl;
    private int antiSpamTime, spamSameMessage, antiSpamCoolDown, antiSpamTrigger;
    private Pattern linkPattern;

    ProxyChatControl() {
        super("chatcontrol.yml");
    }

    @Override
    void reloadValues() {
        regexRules.clear();
        regexActions.clear();
        applyRulesTo.clear();
        applyActionsTo.clear();
        applyAntiSpamTo.clear();
        applyMuteTo.clear();
        applyIgnoreTo.clear();

        version = getConfig().getString("version");

        // Load regex rules
        getConfig().getList("regex_rules").forEach(listEntry -> {
            if (!(listEntry instanceof Configuration))
                return;
            Configuration regexRuleConfig = (Configuration) listEntry;

            String pattern = regexRuleConfig.getString("look_for", "");
            if (pattern.isEmpty())
                return;

            regexRules.add(new RegexRule(pattern,
                    regexRuleConfig.getString("replace_with", ""),
                    regexRuleConfig.getString("permission", ""))
            );
        });
        for (MessageType messageType : MessageType.values())
            applyRulesTo.put(messageType, getConfig().getBoolean("apply_rules_to." + messageType.toString(), false));

        // Load regex actions
        getConfig().getList("regex_actions").forEach(listEntry -> {
            if (!(listEntry instanceof Configuration))
                return;
            Configuration regexActionConfig = (Configuration) listEntry;

            String pattern = regexActionConfig.getString("look_for", "");
            if (pattern.isEmpty())
                return;

            regexActions.add(new RegexAction(pattern,
                            regexActionConfig.getString("command", ""),
                            regexActionConfig.getString("permission", ""),
                            regexActionConfig.getBoolean("cancel", false),
                            regexActionConfig.getBoolean("spigot", false)
                    )
            );
        });
        for (MessageType messageType : MessageType.values())
            applyActionsTo.put(messageType, getConfig().getBoolean("apply_actions_to." + messageType.toString(), false));

        // Load spam settings
        antiSpam = getConfig().getBoolean("anti_spam", true);
        antiSpamTime = getConfig().getInt("anti_spam_time", 4);
        spamSameMessage = getConfig().getInt("spam_same_message", 4);
        antiSpamCoolDown = getConfig().getInt("anti_spam_cooldown", 60);
        antiSpamAction = getConfig().getBoolean("anti_spam_action", true);
        antiSpamSpigot = getConfig().getBoolean("anti_spam_spigot", true);
        antiSpamTrigger = getConfig().getInt("anti_spam_trigger", 3);
        antiSpamCommand = getConfig().getString("anti_spam_command");
        for (MessageType messageType : MessageType.values())
            applyAntiSpamTo.put(messageType, getConfig().getBoolean("apply_anti_spam_to." + messageType.toString(), false));

        // Load mute settings
        mute = getConfig().getBoolean("mute", false);
        for (MessageType messageType : MessageType.values())
            applyMuteTo.put(messageType, getConfig().getBoolean("apply_mute_to." + messageType.toString(), false));

        // Load ignore settings
        notifyIgnore = getConfig().getBoolean("notify_ignore", false);
        sessionIgnore = getConfig().getBoolean("session_ignore", false);
        for (MessageType messageType : MessageType.values())
            applyIgnoreTo.put(messageType, getConfig().getBoolean("apply_ignore_to." + messageType.toString(), false));

        // Load link control settings
        linkControl = getConfig().getBoolean("link_control", false);
        linkPattern = Pattern.compile(getConfig().getString("link_regex",
                "((https|http):\\/\\/)?(www\\.)?([-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.)+[a-zA-Z]{2,4}\\b([-a-zA-Z0-9@:%_\\+.~#?&\\/\\/=]*)")
        );
        linkRemovalMessage = getConfig().getString("link_removal_message", "[LINK REMOVED]");
    }

    public String getVersion() {
        return version;
    }

    // TODO: Some of these #apply / #replace / #handle methods could be moved into a refactored ChatControl.class
    //  I just added them to not lose the logic for now
    public String applyRegexRules(String message, MessageType messageType) {
        return applyRegexRules(null, message, messageType);
    }

    // TODO: Add events for regex rules and actions ?
    public String applyRegexRules(ProxiedPlayer proxiedPlayer, String message, MessageType messageType) {
        if (!applyRulesTo.get(messageType)) return message;

        for (RegexRule regexRule : regexRules)
            message = regexRule.apply(proxiedPlayer, message);
        return message;
    }

    public boolean regexActionsCancel(ProxiedPlayer proxiedPlayer, String message, MessageType messageType) {
        if (!applyActionsTo.get(messageType)) return false;

        // TODO: Personally I believe we should return after the first action cancels the message being sent
        boolean cancel = false;
        for (RegexAction regexAction : regexActions) {
            if (regexAction.cancels(proxiedPlayer, message))
                cancel = true;
        }

        return cancel;
    }

    public String replaceLinks(String message) {
        if (!linkControl)
            return message;

        return linkPattern.matcher(message).replaceAll(linkRemovalMessage);
    }

    public boolean isAntiSpam() {
        return antiSpam;
    }

    public int getAntiSpamTime() {
        return antiSpamTime;
    }

    public int getSpamSameMessage() {
        return spamSameMessage;
    }

    public int getAntiSpamCoolDown() {
        return antiSpamCoolDown;
    }

    public boolean isAntiSpamAction() {
        return antiSpamAction;
    }

    public boolean isAntiSpamSpigot() {
        return antiSpamSpigot;
    }

    public int getAntiSpamTrigger() {
        return antiSpamTrigger;
    }

    public void setAntiSpamTrigger(int antiSpamTrigger) {
        this.antiSpamTrigger = antiSpamTrigger;
    }

    public String getAntiSpamCommand() {
        return antiSpamCommand;
    }

    public boolean isMute() {
        return mute;
    }

    public boolean isNotifyIgnore() {
        return notifyIgnore;
    }

    public boolean isSessionIgnore() {
        return sessionIgnore;
    }

    // TODO: Decide if we want to move these out of here. I don't think any other class needs access to these though.
    private static class RegexRule {
        private final Pattern pattern;
        private final String replaceWith, permission;

        RegexRule(String pattern, String replaceWith, String permission) {
            this.pattern = Pattern.compile(pattern);
            this.replaceWith = replaceWith;
            this.permission = permission;
        }

        public String apply(ProxiedPlayer proxiedPlayer, String message) {
            if (proxiedPlayer != null && !permission.isEmpty() && !proxiedPlayer.hasPermission(permission))
                return message;
            return pattern.matcher(message).replaceAll(replaceWith);
        }
    }

    private static class RegexAction {
        private final Pattern pattern, playerPattern = Pattern.compile("%PLAYER%");
        private final String command, permission;
        private final boolean cancel, spigot;

        RegexAction(String pattern, String command, String permission, boolean cancel, boolean spigot) {
            this.pattern = Pattern.compile(pattern);
            this.command = command;
            this.permission = permission;
            this.cancel = cancel;
            this.spigot = spigot;
        }

        public boolean cancels(ProxiedPlayer proxiedPlayer, String message) {
            if (!permission.isEmpty() && !proxiedPlayer.hasPermission(permission))
                return false;
            if (!pattern.matcher(message).matches())
                return false;
            if (!command.isEmpty()) {
                String tempCommand = playerPattern.matcher(command).replaceAll(proxiedPlayer.getName());
                if (spigot)
                    ProxyLocalCommunicationManager.sendCommandMessage(tempCommand, proxiedPlayer.getServer().getInfo());
                else
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(proxiedPlayer, tempCommand);
            }
            return cancel;
        }
    }
}
