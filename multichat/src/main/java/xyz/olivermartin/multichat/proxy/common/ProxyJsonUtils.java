package xyz.olivermartin.multichat.proxy.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonParser;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class ProxyJsonUtils {

	/**
	 * <p>Parses a raw string (which might be Json) and returns the BaseComponent[]</p>
	 * <p>If the string is not Json text, it is treated as legacy text</p>
	 * @param rawMessage The message (which might be Json) to parse
	 * @return the parsed BaseComponent[] ready for sending
	 */
	public static BaseComponent[] parse(String rawMessage) {

		if (isValidJson(rawMessage)) {
			return ComponentSerializer.parse(rawMessage);
		} else {
			return TextComponent.fromLegacyText(rawMessage);
		}

	}

	/**
	 * <p><b>PROTOTYPE ONLY</b></p>
	 * <p>Parses a raw string (which might be Json) and returns the BaseComponent[]</p>
	 * <p>The parseMultiple method is a prototype using a +++ separator between Json and legacy text</p>
	 * <p>If the string is not Json text, it is treated as legacy text</p>
	 * @param rawMessage The message (which might contains Json) to parse
	 * @return the parsed BaseComponent[] ready for sending
	 */
	public static BaseComponent[] parseMultiple(String rawMessage) {

		String[] split = rawMessage.split("\\+\\+\\+");
		List<BaseComponent[]> parsed = new ArrayList<BaseComponent[]>();

		int size = 0;

		for (String s : split) {
			BaseComponent[] next = parseCopies(s);
			parsed.add(next);
			size += next.length;
		}

		BaseComponent[] processed = new BaseComponent[size];

		int counter = 0;
		for (BaseComponent[] bca : parsed) {
			for (BaseComponent bc : bca) {
				processed[counter++] = bc;
			}
		}

		return processed;

	}

	/**
	 * <p><b>PROTOTYPE ONLY</b></p>
	 * <p>Parses a raw string (which might be Json) and returns the BaseComponent[]</p>
	 * <p>The parseMultiple method is a prototype using a +++ separator between Json and legacy text</p>
	 * <p>If the string is not Json text, it is treated as legacy text</p>
	 * @param rawMessage The message (which might contains Json) to parse
	 * @return the parsed BaseComponent[] ready for sending
	 */
	private static BaseComponent[] parseCopies(String rawMessage) {

		String[] split = rawMessage.split(">>>");
		List<BaseComponent[]> parsed = new ArrayList<BaseComponent[]>();
		int size = 0;

		for (String s : split) {

			BaseComponent[] next;

			if (isValidJson(s)) {
				next = ComponentSerializer.parse(s);
			} else {
				next = TextComponent.fromLegacyText(s);
			}

			parsed.add(next);
			size += next.length;

		}

		BaseComponent[] processed = new BaseComponent[size];
		BaseComponent last = null;

		int counter = 0;
		for (BaseComponent[] bca : parsed) {
			if (last != null) {
				bca[0].copyFormatting(last, false);
			}
			for (BaseComponent bc : bca) {
				processed[counter++] = bc;
				last = bc;
			}
		}

		return processed;

	}

	/**
	 * <p>Checks if a string is a valid json message or not</p>
	 * @param json The string to check
	 * @return true if is valid json
	 */
	public static boolean isValidJson(String json) {

		if (!isSafeMinecraftJson(json)) return false;

		try {

			return new JsonParser().parse(json).getAsJsonObject() != null;

		} catch (Throwable ignored) {

			try {
				return new JsonParser().parse(json).getAsJsonArray() != null;
			} catch (Throwable ignored2) {
				return false;
			}

		}

	}

	private static boolean isSafeMinecraftJson(String json) {
		try {
			return ComponentSerializer.parse(json) != null;
		} catch (Throwable ignored) {
			return false;
		}
	}

}
