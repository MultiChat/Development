package xyz.olivermartin.multichat.velocity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ConsoleManager {

    public static void log(String message) {
        logToConsole(message);
    }

    public static void logDisplayMessage(String message) {
        logToConsole(MessageManager.getMessage("console_display_prefix") + message);
    }

    public static void logChat(String message) {

        logToConsole(MessageManager.getMessage("console_chat_prefix") + message);

    }

    public static void logModChat(String message) {

        if (!MultiChat.logStaffChat) {
            return;
        }

        logToConsole(MessageManager.getMessage("console_modchat_prefix") + message);

    }

    public static void logGroupChat(String message) {

        if (!MultiChat.logGroupChat) {
            return;
        }

        logToConsole(MessageManager.getMessage("console_groupchat_prefix") + message);

    }

    public static void logAdminChat(String message) {

        if (!MultiChat.logStaffChat) {
            return;
        }

        logToConsole(MessageManager.getMessage("console_adminchat_prefix") + message);

    }

    public static void logHelpMe(String message) {

        logToConsole(MessageManager.getMessage("console_helpme_prefix") + message);

    }


    public static void logBasicChat(String prefix, String message) {

        logToConsole(MessageManager.getMessage("console_chat_prefix") + prefix, message);

    }

    public static void logSocialSpy(String p1, String p2, String message) {

        if (!MultiChat.logPMs) {
            return;
        }

        logToConsole(MessageManager.getMessage("console_socialspy_prefix") + "(" + p1 + " -> " + p2 + ")  " + message);

    }

    private static void logToConsole(String message, String unformattedMessage) {
        Component first = LegacyComponentSerializer.legacyAmpersand().deserialize(MessageManager.getMessage("console_main_prefix") + MultiChatUtil.approximateHexCodes(MultiChatUtil.reformatRGB(message)));

        Component second = LegacyComponentSerializer.legacySection().deserialize(unformattedMessage);

        Component both = first.append(second);

        MultiChat.getInstance().getServer().getConsoleCommandSource().sendMessage(both);
    }

    private static void logToConsole(String message) {
        logToConsole(message, "");
    }
}
