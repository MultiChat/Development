package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.channels.ProxyChannel;
import xyz.olivermartin.multichat.proxy.common.channels.GenericProxyChannel;

/**
 * Cast Control
 * <p>Manages the creation, deletion and displaying of Custom broadcASTs (CASTs)</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class CastControl {

	public static Map<String,String> castList = new HashMap<String,String>();

	public static void sendCast(String castName, String castMessage, ProxyChannel channel, ProxiedPlayer player) {
		MultiChatProxy
		castMessage = ChatControl.applyChatRules(castMessage, "casts", "").get();
		chatStream.sendMessage(sender, castList.get(castName.toLowerCase()) + " " + castMessage);
	}
	
	public static void sendCast(String castName, String castMessage, GenericProxyChannel proxyChannel, CommandSender sender) {
		castMessage = ChatControl.applyChatRules(castMessage, "casts", "").get();
		chatStream.sendMessage(sender, castList.get(castName.toLowerCase()) + " " + castMessage);
	}

	public static void addCast(String castName, String castFormat) {
		castList.put(castName.toLowerCase(), MultiChatUtil.reformatRGB(castFormat));
	}

	public static void removeCast(String castName) {
		castList.remove(castName.toLowerCase());
	}

	public static boolean existsCast(String castName) {
		return castList.containsKey(castName.toLowerCase());
	}

}
