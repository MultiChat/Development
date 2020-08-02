package xyz.olivermartin.multichat.proxy.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonParser;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class ProxyJsonUtils {

	static public final String WITH_DELIMITER = "((?<=(%1$s))|(?=(%1$s)))";

	/**
	 * <p>Parses a raw string as legacy text and returns the BaseComponent[]</p>
	 * @param rawMessage The message to parse
	 * @return the parsed BaseComponent[] ready for sending
	 */
	public static BaseComponent[] parseAsLegacy(String rawMessage) {
		return TextComponent.fromLegacyText(rawMessage);
	}

	/**
	 * <p>Parses a raw string (which might be Json) and returns the BaseComponent[]</p>
	 * <p>If the string is not Json text, it is treated as legacy text</p>
	 * @param rawMessage The message (which might be Json) to parse
	 * @return the parsed BaseComponent[] ready for sending
	 */
	public static BaseComponent[] parseSingle(String rawMessage) {

		if (isValidJson(rawMessage)) {
			return ComponentSerializer.parse(rawMessage);
		} else {
			return TextComponent.fromLegacyText(rawMessage);
		}

	}

	public static BaseComponent[] parsePartialSingle(String rawMessage, String placeholder, String replacement) {

		String[] split = rawMessage.split(String.format(WITH_DELIMITER, placeholder), -1);
		BaseComponent[][] result = new BaseComponent[split.length][];

		int counter = 0;
		for (String s : split) {

			if (s.equals(placeholder)) {
				result[counter++] = parseAsLegacy(replacement);
			} else {
				result[counter++] = parseSingle(s);
			}

		}

		return merge(true, result);

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

		BaseComponent[][] bcaa = new BaseComponent[parsed.size()][];

		return merge(false, size, parsed.toArray(bcaa));

	}

	public static BaseComponent[] parsePartialMultiple(String rawMessage, String placeholder, String replacement) {

		String[] split = rawMessage.split("\\+\\+\\+");

		List<BaseComponent[]> parsed = new ArrayList<BaseComponent[]>();
		int size = 0;

		for (String s : split) {
			BaseComponent[] next = parsePartialCopies(s, placeholder, replacement);
			parsed.add(next);
			size += next.length;
		}

		BaseComponent[][] bcaa = new BaseComponent[parsed.size()][];

		return merge(false, size, parsed.toArray(bcaa));

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
			BaseComponent[] next = parseSingle(s);
			parsed.add(next);
			size += next.length;
		}

		BaseComponent[][] bcaa = new BaseComponent[parsed.size()][];

		return merge(true, size, parsed.toArray(bcaa));

	}

	private static BaseComponent[] parsePartialCopies(String rawMessage, String placeholder, String replacement) {

		String[] split = rawMessage.split(">>>");

		List<BaseComponent[]> parsed = new ArrayList<BaseComponent[]>();
		int size = 0;

		for (String s : split) {
			BaseComponent[] next = parsePartialSingle(s, placeholder, replacement);
			parsed.add(next);
			size += next.length;
		}

		BaseComponent[][] bcaa = new BaseComponent[parsed.size()][];

		return merge(true, size, parsed.toArray(bcaa));

	}

	/**
	 * Merge together multiple entirely separate base component arrays
	 * @param injectFormatting If the formatting from the previous base component should be injected into the next
	 * @param baseComponentArrays The base component arrays to merge
	 * @return the concatenated array
	 */
	public static BaseComponent[] merge(boolean injectFormatting, BaseComponent[]... baseComponentArrays) {
		return merge(
				injectFormatting,
				Arrays.stream(baseComponentArrays).mapToInt(bca -> bca.length).sum(),
				baseComponentArrays);
	}

	/**
	 * Merge together multiple entirely separate base component arrays
	 * @param injectFormatting If the formatting from the previous base component should be injected into the next
	 * @param size The length of the new array
	 * @param baseComponentArrays The base component arrays to merge
	 * @return the concatenated array
	 */
	public static BaseComponent[] merge(boolean injectFormatting, int size, BaseComponent[]... baseComponentArrays) {

		BaseComponent[] merged = new BaseComponent[size];
		BaseComponent previous = null;

		int counter = 0;
		for (BaseComponent[] bca : baseComponentArrays) {

			for (BaseComponent bc : bca) {

				if (injectFormatting && previous != null) bc.copyFormatting(previous, false);

				merged[counter++] = bc;
				previous = bc;

			}

		}

		return merged;

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
