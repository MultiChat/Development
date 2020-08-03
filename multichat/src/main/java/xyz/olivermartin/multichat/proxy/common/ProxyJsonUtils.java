package xyz.olivermartin.multichat.proxy.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonParser;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class ProxyJsonUtils {

	public static final String WITH_DELIMITER = "((?<=(%1$s))|(?=(%1$s)))";

	/**
	 * <p>Parses a single node of a MultiChat message as legacy text and returns the BaseComponent[]</p>
	 * @param rawMessage The raw message node to parse
	 * @return the parsed BaseComponent[] ready for sending
	 */
	private static BaseComponent[] parseNodeAsLegacy(String rawMessage) {
		return TextComponent.fromLegacyText(rawMessage);
	}

	/**
	 * <p>Parses a single node of a MultiChat message (which might be Json) and returns the BaseComponent[]</p>
	 * <p>If the string is not Json text, it is treated as legacy text</p>
	 * @param rawMessage The raw message node (which might be Json) to parse
	 * @return the parsed BaseComponent[] ready for sending
	 */
	private static BaseComponent[] parseNode(String rawMessage) {

		if (isValidJson(rawMessage)) {
			return ComponentSerializer.parse(rawMessage);
		} else {
			return TextComponent.fromLegacyText(rawMessage);
		}

	}

	/**
	 * <p>Parses a single node of a MultiChat message (which might be Json) and returns the BaseComponent[]</p>
	 * <p>If the string is not Json text, it is treated as legacy text</p>
	 * <p>This method allows for a partially substituted message node</p>
	 * <p>You can specify a placeholder and its replacement which will be parsed as legacy text within the rest of the node</p>
	 * <p>This is useful for user %MESSAGE%s which you do not want to be parsed as JSON</p>
	 * @param rawMessage The raw message node (which might be Json) to parse
	 * @param placeholder The placeholder to be substituted
	 * @param replacement The replacement value for the placeholder (substituted as legacy text)
	 * @return the parsed BaseComponent[] ready for sending
	 */
	private static BaseComponent[] parsePartialNode(String rawMessage, String placeholder, String replacement) {

		if (placeholder == null || replacement == null) return parseNode(rawMessage);

		String[] split = rawMessage.split(String.format(WITH_DELIMITER, placeholder), -1);
		BaseComponent[][] result = new BaseComponent[split.length][];

		int counter = 0;
		for (String s : split) {

			if (s.equals(placeholder)) {
				result[counter++] = parseNodeAsLegacy(replacement);
			} else {
				result[counter++] = parseNode(s);
			}

		}

		return merge(true, result);

	}

	/**
	 * <p>Parses an entire MultiChat message (which might include Json) and returns the BaseComponent[]</p>
	 * <p>If the string is not Json text, it is treated as legacy text</p>
	 * <p>The concatenation operator +++, and injection operator >>>, will also be parsed</p>
	 * @param rawMessage The raw message (which might include Json) to parse
	 * @return the parsed BaseComponent[] ready for sending
	 */
	public static BaseComponent[] parseMessage(String rawMessage) {
		return parseConcatenations(rawMessage, null, null);
	}

	/**
	 * <p>Parses an entire MultiChat message (which might include Json) and returns the BaseComponent[]</p>
	 * <p>If the string is not Json text, it is treated as legacy text</p>
	 * <p>The concatenation operator +++, and injection operator >>>, will also be parsed</p>
	 * <p>This method allows for a partially substituted message node</p>
	 * <p>You can specify a placeholder and its replacement which will be parsed as legacy text within the rest of the node</p>
	 * <p>This is useful for user %MESSAGE%s which you do not want to be parsed as JSON</p>
	 * @param rawMessage The raw message (which might include Json) to parse
	 * @param placeholder The placeholder to be substituted
	 * @param replacement The replacement value for the placeholder (substituted as legacy text)
	 * @return the parsed BaseComponent[] ready for sending
	 */
	public static BaseComponent[] parseMessage(String rawMessage, String placeholder, String replacement) {
		return parseConcatenations(rawMessage, placeholder, replacement);
	}

	/*
	 * Parses the concatenations in a MultiChat message
	 */
	private static BaseComponent[] parseConcatenations(String rawMessage, String placeholder, String replacement) {

		String[] split = rawMessage.split("\\+\\+\\+");

		List<BaseComponent[]> parsed = new ArrayList<BaseComponent[]>();
		int size = 0;

		for (String s : split) {
			BaseComponent[] next = parseInjections(s, placeholder, replacement);
			parsed.add(next);
			size += next.length;
		}

		BaseComponent[][] bcaa = new BaseComponent[parsed.size()][];

		return merge(false, size, parsed.toArray(bcaa));

	}

	/*
	 * Parses the injections in a MultiChat message
	 */
	private static BaseComponent[] parseInjections(String rawMessage, String placeholder, String replacement) {

		String[] split = rawMessage.split(">>>");

		List<BaseComponent[]> parsed = new ArrayList<BaseComponent[]>();
		int size = 0;

		for (String s : split) {
			BaseComponent[] next = parsePartialNode(s, placeholder, replacement);
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
		boolean first = true;

		int counter = 0;
		for (BaseComponent[] bca : baseComponentArrays) {

			for (BaseComponent bc : bca) {

				if (!first && injectFormatting && previous != null) bc.copyFormatting(previous, false);

				merged[counter++] = bc;
				previous = bc;

			}

			first = false;

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

	/*
	 * Checks if the json can be safely parsed by Minecraft
	 */
	private static boolean isSafeMinecraftJson(String json) {
		try {
			return ComponentSerializer.parse(json) != null;
		} catch (Throwable ignored) {
			return false;
		}
	}

}
