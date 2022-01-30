package xyz.olivermartin.multichat.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerInfo;
import ninja.leaping.configurate.ConfigurationNode;

import java.util.*;

public class ChatControl {

    static {
        mutedPlayers = new HashSet<>();
        ignoreMap = new HashMap<>();
        spamMap = new HashMap<>();
    }

    private static Set<UUID> mutedPlayers;
    private static Map<UUID, Set<UUID>> ignoreMap;
    private static final Map<UUID, PlayerSpamInfo> spamMap;

    public static boolean controlLinks = false;
    public static String linkRegex = "((https|http)://)?(www\\.)?([-a-zA-Z0-9@:%._+~#=]{2,256}\\.)+[a-zA-Z]{2,4}\\b([-a-zA-Z0-9@:%_+.~#?&/=]*)";
    public static String linkMessage = "[LINK REMOVED]";

    public static Set<UUID> getMutedPlayers() {
        return mutedPlayers;
    }

    public static void setMutedPlayers(Set<UUID> mutedPlayers) {
        ChatControl.mutedPlayers = mutedPlayers;
    }

    public static Map<UUID, Set<UUID>> getIgnoreMap() {
        return ignoreMap;
    }

    public static void setIgnoreMap(Map<UUID, Set<UUID>> ignoreMap) {
        ChatControl.ignoreMap = ignoreMap;
    }

    /**
     * @param input    The input message
     * @param chatType The type of chat the message was sent in
     * @return The message to send with rules applied, or empty if the chat message should be cancelled
     */
    @SuppressWarnings("rawtypes")
    public static Optional<String> applyChatRules(String input, String chatType, String playerName) {

        ConfigurationNode config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();
        boolean cancel = false;

        Player pp = MultiChat.getInstance().getServer().getPlayer(playerName).orElse(null);

        if (config.getNode("apply_rules_to").getChildrenMap().containsKey(chatType)) {
            if (config.getNode("apply_rules_to").getNode(chatType).getBoolean()) {

                List rules = config.getNode("regex_rules").getList(o -> o);

                for (Object rule : rules) {
                    Map dictionary = (Map) rule;

                    if (pp != null) {
                        if (dictionary.containsKey("permission")) {
                            String permission = String.valueOf(dictionary.get("permission"));
                            if (permission.startsWith("!")) {
                                permission = permission.substring(1);
                                if (pp.hasPermission(permission)) continue;
                            } else {
                                if (!pp.hasPermission(permission)) continue;
                            }
                        }
                    }

                    input = input.replaceAll(String.valueOf(dictionary.get("look_for")), String.valueOf(dictionary.get("replace_with")));
                }

            }
        }

        if (config.getNode("apply_actions_to").getChildrenMap().containsKey(chatType)) {
            if (config.getNode("apply_actions_to").getNode(chatType).getBoolean()) {

                List actions = config.getNode("regex_actions").getList(o -> o);

                for (Object action : actions) {
                    Map dictionary = (Map) action;

                    if (input.matches(String.valueOf(dictionary.get("look_for")))) {

                        if (pp != null) {
                            if (dictionary.containsKey("permission")) {
                                String permission = String.valueOf(dictionary.get("permission"));
                                if (permission.startsWith("!")) {
                                    permission = permission.substring(1);
                                    if (pp.hasPermission(permission)) continue;
                                } else {
                                    if (!pp.hasPermission(permission)) continue;
                                }
                            }
                        }

                        if ((Boolean) dictionary.get("cancel")) {
                            cancel = true;
                        }

                        if ((Boolean) dictionary.get("spigot")) {

                            ServerInfo server = MultiChat.getInstance().getServer().getPlayer(playerName).get().getCurrentServer().get().getServerInfo();
                            BungeeComm.sendCommandMessage(String.valueOf(dictionary.get("command")).replaceAll("%PLAYER%", playerName), server);

                        } else {
                            MultiChat.getInstance().getServer().getCommandManager().executeAsync(MultiChat.getInstance().getServer().getConsoleCommandSource(), String.valueOf(dictionary.get("command")).replaceAll("%PLAYER%", playerName));
                        }

                    }

                }

            }
        }

        if (cancel) {
            return Optional.empty();
        } else {
            return Optional.of(input);
        }

    }

    public static boolean isMuted(UUID uuid, String chatType) {

        ConfigurationNode config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

        if (!config.getNode("mute").getBoolean()) return false;

        if (!mutedPlayers.contains(uuid)) return false;

        if (!config.getNode("apply_mute_to").getChildrenMap().containsKey(chatType)) return false;

        return config.getNode("apply_mute_to").getNode(chatType).getBoolean();

    }

    public static boolean isMutedAnywhere(UUID uuid) {
        ConfigurationNode config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

        if (!config.getNode("mute").getBoolean()) return false;

        return mutedPlayers.contains(uuid);

    }

    public static void mute(UUID uuid) {

        mutedPlayers.add(uuid);

    }

    public static void unmute(UUID uuid) {

        mutedPlayers.remove(uuid);

    }

    /**
     * Tests if the target is ignoring the sender, and hence should not receive the message
     *
     * @param sender The player trying to send a message
     * @param target The player who will see the message
     * @return TRUE if the target ignores the sender and the message should not be sent, FALSE otherwise
     */
    public static boolean ignores(UUID sender, UUID target, String chatType) {
        ConfigurationNode config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

        if (!ignoreMap.containsKey(target)) return false;

        Set<UUID> ignoredPlayers = ignoreMap.get(target);

        if (ignoredPlayers == null) return false;

        if (!ignoredPlayers.contains(sender)) return false;

        if (!config.getNode("apply_ignore_to").getChildrenMap().containsKey(chatType)) return false;

        return config.getNode("apply_ignore_to").getNode(chatType).getBoolean();
    }

    /**
     * Tests if the target is ignoring the sender, and hence should not receive the message
     *
     * @param sender The player trying to send a message
     * @param target The player who will see the message
     * @return TRUE if the target ignores the sender and the message should not be sent, FALSE otherwise
     */
    public static boolean ignoresAnywhere(UUID sender, UUID target) {

        if (!ignoreMap.containsKey(target)) return false;

        Set<UUID> ignoredPlayers = ignoreMap.get(target);

        if (ignoredPlayers == null) return false;

        return ignoredPlayers.contains(sender);
    }

    public static void ignore(UUID ignorer, UUID ignoree) {

        Set<UUID> ignoredPlayers;

        if (ignoreMap.containsKey(ignorer)) {

            ignoredPlayers = ignoreMap.get(ignorer);

        } else {

            ignoredPlayers = new HashSet<>();

        }

        ignoredPlayers.add(ignoree);
        ignoreMap.put(ignorer, ignoredPlayers);

    }

    public static void unignore(UUID ignorer, UUID ignoree) {

        Set<UUID> ignoredPlayers;

        if (ignoreMap.containsKey(ignorer)) {

            ignoredPlayers = ignoreMap.get(ignorer);

        } else {

            return;

        }

        ignoredPlayers.remove(ignoree);

        if (ignoredPlayers.size() < 1) {
            ignoreMap.remove(ignorer);
        } else {
            ignoreMap.put(ignorer, ignoredPlayers);
        }

    }

    public static void unignoreAll(UUID ignorer) {

        ignoreMap.remove(ignorer);

    }

    public static void sendIgnoreNotifications(CommandSource ignorer, CommandSource ignoree, String chatType) {

        ConfigurationNode config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

        if (config.getNode("notify_ignore").getBoolean()) {
            MessageManager.sendSpecialMessage(ignorer, "ignore_target", ignoree instanceof Player ? ((Player) ignoree).getUsername() : "CONSOLE");
        }

        if (!chatType.equals("private_messages")) return;

        MessageManager.sendMessage(ignoree, "ignore_sender");

    }

    /**
     * If sessional ignore is enabled, removes any offline players from the ignore map
     */
    public static void reload() {

        ConfigurationNode config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

        if (config.getNode("session_ignore").getBoolean()) {

            for (UUID uuid : ignoreMap.keySet()) {

                Player player = MultiChat.getInstance().getServer().getPlayer(uuid).orElse(null);

                if (player == null) ignoreMap.remove(uuid);

            }

        }

    }

    public static String replaceLinks(String message) {
        if (!controlLinks) return message;
        return message.replaceAll(linkRegex, linkMessage);
        //return message.replaceAll("((https|http):\\/\\/)?(www\\.)?([-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.)+[a-zA-Z]{2,4}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)", linkMessage);
    }

    public static void spamPardonPlayer(UUID uuid) {
        spamMap.remove(uuid);
    }

    /**
     * @return true if the player is spamming and the message should be blocked
     */
    public static boolean handleSpam(Player player, String message, String chatType) {

        DebugManager.log(player.getUsername() + " - checking for spam...");

        ConfigurationNode config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

        if (player.hasPermission("multichat.spam.bypass")) return false;

        DebugManager.log(player.getUsername() + " - does not have bypass perm...");

        if (!config.getNode("anti_spam").getBoolean()) return false;

        DebugManager.log(player.getUsername() + " - anti spam IS enabled...");

        if (!config.getNode("apply_anti_spam_to").getChildrenMap().containsKey(chatType)) return false;

        if (!config.getNode("apply_anti_spam_to").getNode(chatType).getBoolean()) return false;

        DebugManager.log(player.getUsername() + " - anti spam IS enabled for " + chatType + "...");

        if (!spamMap.containsKey(player.getUniqueId())) spamMap.put(player.getUniqueId(), new PlayerSpamInfo());

        PlayerSpamInfo spamInfo = spamMap.get(player.getUniqueId());

        boolean spam = spamInfo.checkSpam(message);

        if (spam) {

            DebugManager.log(player.getUsername() + " - PLAYER IS SPAMMING!");

            MessageManager.sendSpecialMessage(player, "anti_spam_cooldown", String.valueOf(spamInfo.getCooldownSeconds()));

            DebugManager.log(player.getUsername() + " - sent cooldown message to player...");

            if (spamInfo.getSpamTriggerCount() >= config.getNode("anti_spam_trigger").getInt()) {

                DebugManager.log(player.getUsername() + " - they have set off the trigger...");

                spamInfo.resetSpamTriggerCount();

                if (config.getNode("anti_spam_action").getBoolean()) {

                    DebugManager.log(player.getUsername() + " - trigger IS enabled...");

                    if (config.getNode("anti_spam_spigot").getBoolean()) {
                        ServerInfo server = player.getCurrentServer().get().getServerInfo();
                        BungeeComm.sendCommandMessage(config.getNode("anti_spam_command").getString().replaceAll("%PLAYER%", player.getUsername()), server);
                    } else {
                        MultiChat.getInstance().getServer().getCommandManager().executeAsync(MultiChat.getInstance().getServer().getConsoleCommandSource(), config.getNode("anti_spam_command").getString().replaceAll("%PLAYER%", player.getUsername()));
                    }

                }

            }

        }
        DebugManager.log(player.getUsername() + " - returning " + spam);

        return spam;

    }

    public static class PlayerSpamInfo {

        int spamTriggerCount = 0;
        long lastSpamTime = 0L;
        long[] messageTimeBuffer = {0L, 0L, 0L};
        int sameMessageCounter = 0;
        String lastMessage = "";

        /**
         * @return true if the user is spamming and message should be cancelled
         */
        public boolean checkSpam(String message) {

            boolean spam = false;
            long currentTime = System.currentTimeMillis();
            ConfigurationNode config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();

            // If the user triggered anti-spam, check if they are still on cooldown
            if (currentTime - lastSpamTime < (1000L * config.getNode("anti_spam_cooldown").getInt())) return true;

            long deltaTime = currentTime - messageTimeBuffer[2];

            if (lastMessage.equalsIgnoreCase(message)) {
                // This is a hard coded test. If the same message is sent 4 times in a row, it is spam...
                // However; this extra bit states that if it has been longer than 10 times the usual spam time
                // then this should not be considered spam. And hence the counter is reset.
                if ((currentTime - messageTimeBuffer[0]) < (1000L * config.getNode("anti_spam_time").getInt() * 10)) {
                    sameMessageCounter++;
                } else {
                    sameMessageCounter = 0;
                }
            } else {
                sameMessageCounter = 0;
                lastMessage = message;
            }

            rotateMessages(currentTime);

            // Max messages in time limit or same message in row check
            if (deltaTime < (1000L * config.getNode("anti_spam_time").getInt())
                    || !(sameMessageCounter + 1 < config.getNode("spam_same_message").getInt())) {
                spam = true;
                lastSpamTime = currentTime;
                spamTriggerCount++;
            }

            return spam;
        }

        private void rotateMessages(long currentTime) {
            messageTimeBuffer[2] = messageTimeBuffer[1];
            messageTimeBuffer[1] = messageTimeBuffer[0];
            messageTimeBuffer[0] = currentTime;
        }

        public int getSpamTriggerCount() {
            return spamTriggerCount;
        }

        public void resetSpamTriggerCount() {
            spamTriggerCount = spamTriggerCount - 1;
        }

        public long getCooldownSeconds() {
            ConfigurationNode config = ConfigManager.getInstance().getHandler("chatcontrol.yml").getConfig();
            return config.getNode("anti_spam_cooldown").getInt() - ((System.currentTimeMillis() - lastSpamTime) / 1000);
        }

    }

}
