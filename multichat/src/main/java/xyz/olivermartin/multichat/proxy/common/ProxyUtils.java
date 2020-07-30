package xyz.olivermartin.multichat.proxy.common;

import java.util.HashSet;
import java.util.Set;

import xyz.olivermartin.multichat.common.MultiChatUtil;

public class ProxyUtils {

	/**
	 * <p>Takes a raw string and translates any colour codes using the & symbol</p>
	 * <p>Any RGB codes in the format &#abcdef, &xabcdef or &x&a&b&c&d&e&f will also be translated</p>
	 * @param rawMessage The raw message to translate
	 * @return the translated message
	 */
	public static String translateColourCodes(String rawMessage) {
		return translateColourCodes(rawMessage, TranslateMode.ALL);
	}

	/**
	 * <p>Takes a raw string and translates formatting codes according to the TranslateMode</p>
	 * @param rawMessage The raw message to translate
	 * @param mode The TranslateMode to process
	 * @return the translated message
	 */
	public static String translateColourCodes(String rawMessage, TranslateMode mode) {
		Set<TranslateMode> modes = new HashSet<TranslateMode>();
		modes.add(mode);
		return translateColourCodes(rawMessage, modes);
	}

	/**
	 * <p>Takes a raw string and translates formatting codes according to the TranslateMode</p>
	 * @param rawMessage The raw message to translate
	 * @param modes The TranslateModes to process
	 * @return the translated message
	 */
	public static String translateColourCodes(String rawMessage, Set<TranslateMode> modes) {

		String translatedMessage = rawMessage;
		boolean rgb = modes.contains(TranslateMode.ALL) || modes.contains(TranslateMode.COLOUR_ALL);

		// If we are translating RGB codes, reformat these to the correct format
		if (rgb) translatedMessage = MultiChatUtil.reformatRGB(translatedMessage);

		// Process each of the translations
		for (TranslateMode mode : modes) {
			translatedMessage = mode.translate(translatedMessage);
		}

		return translatedMessage;

	}

}
