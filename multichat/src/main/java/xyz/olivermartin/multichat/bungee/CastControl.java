package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.CommandSender;
import xyz.olivermartin.multichat.common.MessageType;
import xyz.olivermartin.multichat.proxy.common.channels.local.LocalChannel;
import xyz.olivermartin.multichat.proxy.common.channels.proxy.ProxyChannel;

/**
 * Cast Control
 * <p>Manages the creation, deletion and displaying of Custom broadcASTs (CASTs)</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class CastControl {

	public static Map<String,String> castList = new HashMap<String,String>();

	public static void sendCast(String castName, String castMessage, ProxyChannel channel, CommandSender sender) {
		castMessage = ChatControl.applyChatRules(sender, castMessage, MessageType.CASTS).get();
		channel.broadcastRawMessage(sender, castList.get(castName.toLowerCase()) + " " + castMessage);
	}

	public static void sendCast(String castName, String castMessage, LocalChannel channel, String server, CommandSender sender) {
		castMessage = ChatControl.applyChatRules(sender, castMessage, MessageType.CASTS).get();
		channel.broadcastRawMessage(sender, server, castList.get(castName.toLowerCase()) + " " + castMessage);
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
