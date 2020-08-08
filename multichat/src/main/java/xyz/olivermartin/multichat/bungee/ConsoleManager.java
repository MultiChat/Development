package xyz.olivermartin.multichat.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

import java.util.Arrays;
import java.util.stream.Stream;

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

		if (!ProxyConfigs.CONFIG.isLogStaffChat()) {
			return;
		}

		logToConsole(MessageManager.getMessage("console_modchat_prefix") + message);

	}

	public static void logGroupChat(String message) {

		if (!ProxyConfigs.CONFIG.isLogGroupChat()) {
			return;
		}

		logToConsole(MessageManager.getMessage("console_groupchat_prefix") + message);

	}

	public static void logAdminChat(String message) {

		if (!ProxyConfigs.CONFIG.isLogStaffChat()) {
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

		if (!ProxyConfigs.CONFIG.isLogPms()) {
			return;
		}

		logToConsole(MessageManager.getMessage("console_socialspy_prefix") + "(" + p1 + " -> " + p2 + ")  " + message);

	}

	private static void logToConsole(String message, String unformattedMessage) {
		BaseComponent[] first = TextComponent.fromLegacyText(
				MultiChatUtil.approximateRGBColorCodes(
						MultiChatUtil.translateColorCodes(
								MessageManager.getMessage("console_main_prefix") + message)));

		BaseComponent[] second = TextComponent.fromLegacyText(unformattedMessage);

		BaseComponent[] both = Stream.concat(Arrays.stream(first), Arrays.stream(second))
				.toArray(BaseComponent[]::new);

		ProxyServer.getInstance().getConsole().sendMessage(both);

	}

	private static void logToConsole(String message) {

		logToConsole(message, "");

	}

}
