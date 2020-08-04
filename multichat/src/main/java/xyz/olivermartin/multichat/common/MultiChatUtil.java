package xyz.olivermartin.multichat.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiChatUtil {

	private static final Pattern SHORT_UNTRANSLATED_RGB = Pattern.compile("(?i)\\&(x|#)([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])");
	private static final Pattern LONG_UNTRANSLATED_RGB = Pattern.compile("(?i)\\&x\\&([0-9A-F])\\&([0-9A-F])\\&([0-9A-F])\\&([0-9A-F])\\&([0-9A-F])\\&([0-9A-F])");
	private static final Pattern TRANSLATED_RGB = Pattern.compile("(?i)(§r)?§x§([0-9A-F])§([0-9A-F])§([0-9A-F])§([0-9A-F])§([0-9A-F])§([0-9A-F])");
	private static final Pattern ALL_FORMATTING_CHARS = Pattern.compile("(?i)([0-9A-FK-ORX])");
	private static final Pattern JSON_RGB = Pattern.compile("(?i)(\"color\":\")#([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])(\")");
	private static final Pattern APPROX_RGB_FORMAT = Pattern.compile("(?i)\\§(#)([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])");


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

		boolean rgb = Arrays.stream(modes).anyMatch(value -> TranslateMode.isRGB(value));

		// If we are translating RGB codes, reformat these to the correct format
		if (rgb) translatedMessage = MultiChatUtil.preProcessColourCodes(translatedMessage);

		// Process each of the translations
		for (TranslateMode mode : modes) {
			translatedMessage = mode.translate(translatedMessage);
		}

		return translatedMessage;

	}

	/**
	 * <p>Takes a raw string and strips any colour codes using the & symbol</p>
	 * <p>If stripTranslatedCodes is true then it will also strip any codes using the § symbol</p>
	 * @param rawMessage The raw message to strip
	 * @param stripTranslatedCodes If pre-translated codes (§) should also be stripped
	 * @return the stripped message
	 */
	public static String stripColourCodes(String rawMessage, boolean stripTranslatedCodes) {
		return stripColourCodes(rawMessage, stripTranslatedCodes, TranslateMode.ALL);
	}

	/**
	 * <p>Takes a raw string and strips formatting codes (&) according to the TranslateMode</p>
	 * <p>If stripTranslatedCodes is true then it will also strip any codes using the § symbol</p>
	 * @param rawMessage The raw message to strip
	 * @param stripTranslatedCodes If pre-translated codes (§) should also be stripped
	 * @param modes The TranslateModes to apply
	 * @return the stripped message
	 */
	public static String stripColourCodes(String rawMessage, boolean stripTranslatedCodes, TranslateMode... modes) {

		String strippedMessage = rawMessage;

		boolean rgb = Arrays.stream(modes).anyMatch(value -> TranslateMode.isRGB(value));

		// If we are stripping RGB codes, reformat these to the correct format
		if (rgb) strippedMessage = MultiChatUtil.preProcessColourCodes(strippedMessage);

		// Process each of the strips
		for (TranslateMode mode : modes) {
			if (stripTranslatedCodes) {
				strippedMessage = mode.stripAll(strippedMessage);
			} else {
				strippedMessage = mode.stripOrigin(strippedMessage);
			}
		}

		return strippedMessage;

	}

	/**
	 * <p>Takes a raw string and checks if it contains any codes using the & symbol</p>
	 * <p>Any RGB codes in the format &#abcdef, &xabcdef or &x&a&b&c&d&e&f will also be checked</p>
	 * @param rawMessage The raw message to check
	 * @param checkTranslatedCodes If pre-translated codes (§) should also be checked
	 * @return true if it contains format codes
	 */
	public static boolean containsColourCodes(String rawMessage, boolean checkTranslatedCodes) {
		return containsColourCodes(rawMessage, checkTranslatedCodes, TranslateMode.ALL);
	}

	/**
	 * <p>Takes a raw string and checks if it contains any formatting codes (&) according to the TranslateMode</p>
	 * @param rawMessage The raw message to check
	 * @param checkTranslatedCodes If pre-translated codes (§) should also be checked
	 * @param modes The TranslateModes to process
	 * @return true if it contains format codes
	 */
	public static boolean containsColourCodes(String rawMessage, boolean checkTranslatedCodes, TranslateMode... modes) {

		boolean rgb = Arrays.stream(modes).anyMatch(value -> TranslateMode.isRGB(value));

		// If we are checking RGB codes, reformat these to the correct format
		if (rgb) rawMessage = MultiChatUtil.preProcessColourCodes(rawMessage);

		// Process each of the checks
		for (TranslateMode mode : modes) {
			if (checkTranslatedCodes) {
				if (mode.containsAny(rawMessage)) return true;
			} else {
				if (mode.containsOrigin(rawMessage)) return true;
			}
		}

		return false;

	}

	/**
	 * Reformat the RGB codes into Spigot Native version
	 * 
	 * @param message
	 * @return message reformatted
	 */
	public static String preProcessColourCodes(String message) {

		Matcher longRgb = LONG_UNTRANSLATED_RGB.matcher(message);
		message = longRgb.replaceAll("&r&x&$1&$2&$3&$4&$5&$6");

		Matcher shortRgb = SHORT_UNTRANSLATED_RGB.matcher(message);
		message = shortRgb.replaceAll("&r&x&$2&$3&$4&$5&$6&$7");

		String transformedMessage = "";
		char lastChar = 'a';

		// Transform codes to lowercase for better compatibility with Essentials etc.
		for (char c : message.toCharArray()) {

			// If this could be a colour code
			if (lastChar == '&') {

				// If it is a colour code, set to be lowercase
				Matcher allFormattingChars = ALL_FORMATTING_CHARS.matcher(String.valueOf(c));
				if (allFormattingChars.matches()) {
					c = Character.toLowerCase(c);
				}

			}

			// Append to message
			transformedMessage = transformedMessage + c;
			lastChar = c;

		}

		return transformedMessage;

	}

	public static String approximateRGBColourCodes(String message) {

		Matcher rgbMatcher = TRANSLATED_RGB.matcher(message);
		message = rgbMatcher.replaceAll("§#$2$3$4$5$6$7");

		message = replaceRGBShortCodesWithApproximations(message, false);

		return approximateJsonRGBColourCodes(message);

	}

	private static String approximateJsonRGBColourCodes(String message) {

		Matcher jsonRgbMatcher = JSON_RGB.matcher(message);
		message = jsonRgbMatcher.replaceAll("$1§#$2$3$4$5$6$7$8");
		return replaceRGBShortCodesWithApproximations(message, true);

	}

	private static String replaceRGBShortCodesWithApproximations(String message, boolean useNameInsteadOfCode) {

		List<String> allMatches = new ArrayList<String>();
		Matcher m = APPROX_RGB_FORMAT.matcher(message);

		while (m.find()) {
			allMatches.add(m.group());
		}

		for (String match : allMatches) {

			String hexonly;
			if (match.contains("#")) {
				hexonly = match.split("#")[1];
			} else if (match.contains("x")) {
				hexonly = match.split("x")[1];
			} else {
				hexonly = match.split("X")[1];
			}
			String minecraftCode = hexToMinecraft(hexonly);

			if (useNameInsteadOfCode) {
				message = message.replace(match,getMinecraftCodeName(minecraftCode));
			} else {
				message = message.replace(match,"§"+minecraftCode);
			}

		}

		return message;

	}

	public static String getMinecraftCodeName(String code) {

		code = code.toLowerCase();

		switch (code) {
		case "0":
			return "black";
		case "1":
			return "dark_blue";
		case "2":
			return "dark_green";
		case "3":
			return "dark_aqua";
		case "4":
			return "dark_red";
		case "5":
			return "dark_purple";
		case "6":
			return "gold";
		case "7":
			return "gray";
		case "8":
			return "dark_gray";
		case "9":
			return "blue";
		case "a":
			return "green";
		case "b":
			return "aqua";
		case "c":
			return "red";
		case "d":
			return "light_purple";
		case "e":
			return "yellow";
		case "f":
			return "white";
		default:
			return "white";
		}

	}

	public static String hexToMinecraft(String hex) {

		String rcode = hex.substring(0,2);
		String gcode = hex.substring(2,4);
		String bcode = hex.substring(4,6);

		int rint = Integer.parseInt(rcode,16);
		int gint = Integer.parseInt(gcode,16);
		int bint = Integer.parseInt(bcode,16);

		String[] cga = {"000000","0000aa","00aa00","00aaaa","aa0000","aa00aa","ffaa00","aaaaaa","555555","5555ff","55ff55","55ffff","ff5555","ff55ff","ffff55","ffffff"};

		int diff = 999999999;
		int best = -1;

		for (int i = 0; i < 16; i++) {

			String current = cga[i];

			String rcode2 = current.substring(0,2);
			String gcode2 = current.substring(2,4);
			String bcode2 = current.substring(4,6);

			int rint2 = Integer.parseInt(rcode2,16);
			int gint2 = Integer.parseInt(gcode2,16);
			int bint2 = Integer.parseInt(bcode2,16);

			int val = Math.abs(rint-rint2) + Math.abs(gint-gint2) + Math.abs(bint-bint2);

			if (val < diff) {
				best = i;
				diff = val;
			}

		}

		return Integer.toHexString(best);

	}

	/**
	 * Concatenate the arguments together to get the message as a string
	 * 
	 * @param args The arguments of the command
	 * @param start The (zero-indexed) starting index of the message (inclusive)
	 * @param end The (zero-indexed) finishing index of the message (inclusive)
	 * @return The concatenated message
	 */
	public static String getMessageFromArgs(String[] args, int start, int end) {

		int counter = 0;
		String message = "";
		for (String arg : args) {
			if (counter >= start && counter <= end) {
				if (counter != end) {
					message = message + arg + " ";
				} else {
					message = message + arg;
				}
			}
			counter++;
		}

		return message;

	}

	/**
	 * Concatenate the arguments together to get the message as a string
	 * 
	 * @param args The arguments of the command
	 * @param start The (zero-indexed) starting index of the message (inclusive)
	 * @return The concatenated message
	 */
	public static String getMessageFromArgs(String[] args, int start) {

		return getMessageFromArgs(args, start, args.length - 1);

	}

	/**
	 * Concatenate the arguments together to get the message as a string
	 * 
	 * @param args The arguments of the command
	 * @return The concatenated message
	 */
	public static String getMessageFromArgs(String[] args) {

		return getMessageFromArgs(args, 0, args.length - 1);

	}

	public static String getStringFromCollection(Collection<String> collection) {

		String result = "";

		for (String item : collection) {
			if (result.equals("")) {
				result = result + item;
			} else {
				result = result + " " + item;
			}
		}

		return result;

	}

	public static String visualiseColourCodes(String message) {

		Matcher originMatcher = TranslateMode.ALL.getOriginPattern().matcher(message);
		Matcher translatedMatcher = TranslateMode.ALL.getTranslatedPattern().matcher(message);

		message = originMatcher.replaceAll("{Origin.$1}");
		message = translatedMatcher.replaceAll("{Transl.$1}");

		return message;

	}

}
