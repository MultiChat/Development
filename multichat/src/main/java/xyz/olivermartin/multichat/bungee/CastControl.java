package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.CommandSender;

/**
 * Cast Control
 * <p>Manages the creation, deletion and displaying of Custom broadcASTs (CASTs)</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class CastControl {

	public static Map<String,String> castList = new HashMap<String,String>();

	public static void sendCast(String castName, String castMessage, Channel chatStream, CommandSender sender) {
		castMessage = ChatControl.applyChatRules(castMessage, "casts", "").get();
		chatStream.sendMessage(castList.get(castName.toLowerCase()) + " " + castMessage, sender);
	}

	public static void addCast(String castName, String castFormat) {
		castList.put(castName.toLowerCase(), castFormat);
	}

	public static void removeCast(String castName) {
		castList.remove(castName.toLowerCase());
	}

	public static boolean existsCast(String castName) {
		return castList.containsKey(castName.toLowerCase());
	}

}
