package xyz.olivermartin.multichat.proxy.common.config;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.common.MessageType;
import xyz.olivermartin.multichat.proxy.common.ProxyLocalCommunicationManager;

import java.util.*;
import java.util.regex.Pattern;

public class ProxyChatControl extends AbstractProxyConfig {

    private String version, antiSpamCommand, linkRemovalMessage;
    private final Set<RegexRule> regexRules = new LinkedHashSet<>();
    private final Set<RegexAction> regexActions = new LinkedHashSet<>();
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

        version = getConfig().getString("version", "1.10");

        // Load regex rules
        getConfig().getList("regex_rules").forEach(listEntry -> {
            if (!(listEntry instanceof Map))
                return;
            Map<?, ?> regexRuleConfig = (Map<?, ?>) listEntry;
            String pattern = String.valueOf(regexRuleConfig.get("look_for"));
            if (pattern == null || pattern.isEmpty())
                return;

            regexRules.add(new RegexRule(pattern,
                    String.valueOf(regexRuleConfig.get("replace_with")),
                    String.valueOf(regexRuleConfig.get("permission")))
            );
        });
        for (MessageType messageType : MessageType.values())
            applyRulesTo.put(messageType, getConfig().getBoolean("apply_rules_to." + messageType.toString(), false));

        // Load regex actions
        getConfig().getList("regex_actions").forEach(listEntry -> {
            if (!(listEntry instanceof Map))
                return;
            Map<?, ?> regexActionConfig = (Map<?, ?>) listEntry;

            String pattern = String.valueOf(regexActionConfig.get("look_for"));
            if (pattern == null || pattern.isEmpty())
                return;

            Object cancelObject = regexActionConfig.get("cancel");
            boolean cancel = cancelObject instanceof Boolean && (boolean) cancelObject;

            Object spigotObject = regexActionConfig.get("spigot");
            boolean spigot = spigotObject instanceof Boolean && (boolean) spigotObject;

            regexActions.add(new RegexAction(pattern,
                            String.valueOf(regexActionConfig.get("command")),
                            String.valueOf(regexActionConfig.get("permission")),
                            cancel,
                            spigot
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

    // TODO: [2.0] Some of these #apply / #replace / #handle methods could be moved into a refactored ChatControl.class
    //  I just added them to not lose the logic for now
    // TODO: [2.0] Add events for regex rules and actions ?
    public String applyRegexRules(CommandSender commandSender, String message, MessageType messageType) {
        if (!applyRulesTo.get(messageType)) return message;

        for (RegexRule regexRule : regexRules)
            message = regexRule.apply(commandSender, message);
        return message;
    }

    public boolean regexActionsCancel(CommandSender commandSender, String message, MessageType messageType) {
        if (!applyActionsTo.get(messageType)) return false;

        // TODO: [ConfigRefactor] Personally I believe we should return after the first action cancels the message being sent
        boolean cancel = false;
        for (RegexAction regexAction : regexActions) {
            if (regexAction.cancels(commandSender, message))
                cancel = true;
        }

        return cancel;
    }

    public String replaceLinks(String message) {
        if (!linkControl)
            return message;

        return linkPattern.matcher(message).replaceAll(linkRemovalMessage);
    }

    public boolean applyMuteTo(MessageType messageType) {
        return applyMuteTo.get(messageType);
    }

    public boolean applyIgnoreTo(MessageType messageType) {
        return applyIgnoreTo.get(messageType);
    }

    public boolean applyAntiSpamTo(MessageType messageType) {
        return applyAntiSpamTo.get(messageType);
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

    // TODO: [ConfigRefactor] [2.0] Decide if we want to move these out of here. I don't think any other class needs access to these though.
    private static class RegexRule {
        private final Pattern pattern;
        private final String replaceWith, permission;

        RegexRule(String pattern, String replaceWith, String permission) {
            this.pattern = Pattern.compile(pattern);
            this.replaceWith = replaceWith.equals("null") ? "" : replaceWith;
            this.permission = permission.equals("null") ? "" : permission;
        }

        public String apply(CommandSender commandSender, String message) {
            if (commandSender != null && !permission.isEmpty() && !commandSender.hasPermission(permission))
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
            this.command = command.equals("null") ? "" : command;
            this.permission = permission.equals("null") ? "" : permission;
            this.cancel = cancel;
            this.spigot = spigot;
        }

        public boolean cancels(CommandSender commandSender, String message) {
            if (!permission.isEmpty() && !commandSender.hasPermission(permission))
                return false;
            if (!pattern.matcher(message).matches())
                return false;
            if (!command.isEmpty()) {
                String tempCommand = playerPattern.matcher(command).replaceAll(commandSender.getName());
                if (spigot && commandSender instanceof ProxiedPlayer)
                    ProxyLocalCommunicationManager.sendCommandMessage(tempCommand, ((ProxiedPlayer) commandSender).getServer().getInfo());
                else
                    ProxyServer.getInstance().getPluginManager().dispatchCommand(commandSender, tempCommand);
            }
            return cancel;
        }
    }
}
