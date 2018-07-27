package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;

public class CastControl {

	public static Map<String,String> castList = new HashMap<String,String>();
	
	public static void sendCast(String castName, String castMessage, ChatStream chatStream) {
		chatStream.sendMessage(castList.get(castName.toLowerCase()) + " " + castMessage);
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
