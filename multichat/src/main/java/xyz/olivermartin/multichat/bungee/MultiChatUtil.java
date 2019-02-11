package xyz.olivermartin.multichat.bungee;

import java.util.Collection;

public class MultiChatUtil {

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
				message = message + arg + " ";
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
