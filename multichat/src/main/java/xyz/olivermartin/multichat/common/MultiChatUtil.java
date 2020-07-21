package xyz.olivermartin.multichat.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiChatUtil {

	/**
	 * Reformat the RGB codes into Spigot Native version
	 * 
	 * @param message
	 * @return message reformatted
	 */
	public static String reformatRGB(String message) {
		// Translate RGB codes
		return message.replaceAll("(?i)\\&(x|#)([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])", "&x&$2&$3&$4&$5&$6&$7");
	}

	public static String approximateHexCodes(String message) {

		message = message.replaceAll("(?i)(\\&|§)x(\\&|§)([0-9A-F])(\\&|§)([0-9A-F])(\\&|§)([0-9A-F])(\\&|§)([0-9A-F])(\\&|§)([0-9A-F])(\\&|§)([0-9A-F])", "&#$3$5$7$9$11$13");

		List<String> allMatches = new ArrayList<String>();
		Matcher m = Pattern.compile("(?i)\\&(x|#)([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])([0-9A-F])")
				.matcher(message);
		while (m.find()) {
			allMatches.add(m.group());
		}

		for (String match : allMatches) {
			String hexonly = match.split("#")[1];
			String minecraftCode = hexToMinecraft(hexonly);
			message = message.replace(match,"§"+minecraftCode);
		}

		return message;

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

}
