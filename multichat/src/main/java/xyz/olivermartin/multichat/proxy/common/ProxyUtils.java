package xyz.olivermartin.multichat.proxy.common;

import java.util.Arrays;

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
	 * @param modes The TranslateModes to process
	 * @return the translated message
	 */
	public static String translateColourCodes(String rawMessage, TranslateMode... modes) {

		String translatedMessage = rawMessage;

		boolean rgb = Arrays.stream(modes).anyMatch(value -> value.equals(TranslateMode.ALL) || value.equals(TranslateMode.COLOUR_ALL));

		// If we are translating RGB codes, reformat these to the correct format
		if (rgb) translatedMessage = MultiChatUtil.reformatRGB(translatedMessage);

		// Process each of the translations
		for (TranslateMode mode : modes) {
			translatedMessage = mode.translate(translatedMessage);
		}

		return translatedMessage;

	}

}
