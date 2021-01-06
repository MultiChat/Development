package xyz.olivermartin.multichat.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import xyz.olivermartin.multichat.bungee.events.PostBroadcastEvent;
import xyz.olivermartin.multichat.common.MessageType;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyJsonUtils;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Announcements Management
 * <p>The back-end code which manages the creation, deletion and scheduling of announcements</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class Announcements {

	private static Map<String, Integer> aKey = new HashMap<String, Integer>();
	private static Map<String, String> announcements = new HashMap<String, String>();

	public static boolean startAnnouncement(final String name, Integer minutes) {

		if(!(aKey.containsKey(name.toLowerCase())) && announcements.containsKey(name.toLowerCase())) {

			Integer ID;

			ScheduledTask task = ProxyServer.getInstance().getScheduler().schedule(MultiChatProxy.getInstance().getPlugin(), new Runnable() {

				@Override
				public void run() {
					String message = announcements.get(name.toLowerCase());

					message = ChatControl.applyChatRules(null, message, MessageType.ANNOUNCEMENTS).get();

					message = MultiChatUtil.translateColorCodes(message);

					for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
						if (ProxyConfigs.CONFIG.isLegacyServer(onlineplayer.getServer().getInfo().getName())) {
							onlineplayer.sendMessage(ProxyJsonUtils.parseMessage(MultiChatUtil.approximateRGBColorCodes(message)));
						} else {
							onlineplayer.sendMessage(ProxyJsonUtils.parseMessage(message));
						}
					}

					// Trigger PostBroadcastEvent
					ProxyServer.getInstance().getPluginManager().callEvent(new PostBroadcastEvent("announcement", message));
				}

			}, 0, minutes, TimeUnit.MINUTES);

			ID = task.getId();

			aKey.put(name.toLowerCase(), ID);

			return true;

		} else {

			return false;

		}
	}

	public static HashMap<String,String> getAnnouncementList() {

		return (HashMap<String, String>) announcements;

	}

	public static void loadAnnouncementList(HashMap<String, String> loadedAnnouncements) {

		announcements = loadedAnnouncements;

	}

	public static boolean stopAnnouncement(String name) {

		if (aKey.containsKey(name.toLowerCase())) {
			ProxyServer.getInstance().getScheduler().cancel(aKey.get(name.toLowerCase()));
			aKey.remove(name.toLowerCase());
			return true;
		} else {
			return false;
		}

	}

	public static boolean addAnnouncement(String name, String message) {

		if (!announcements.containsKey(name.toLowerCase())) {

			announcements.put(name.toLowerCase(), message);
			return true;

		} else {
			return false;
		}

	}

	public static boolean removeAnnouncement(String name) {

		if (aKey.containsKey(name.toLowerCase())) {
			ProxyServer.getInstance().getScheduler().cancel(aKey.get(name.toLowerCase()));
			aKey.remove(name.toLowerCase());
		}

		if(announcements.containsKey(name.toLowerCase())) {
			announcements.remove(name.toLowerCase());
			return true;
		} else {
			return false;
		}

	}

	public static boolean existsAnnouncemnt(String name) {

		if ( announcements.containsKey(name.toLowerCase() ) ) {
			return true;
		} else {
			return false;
		}

	}

	public static void playAnnouncement(String name) {

		if (announcements.containsKey(name.toLowerCase())) {

			String message = announcements.get(name.toLowerCase());

			message = ChatControl.applyChatRules(null, message, MessageType.ANNOUNCEMENTS).get();

			message = MultiChatUtil.translateColorCodes(message);

			for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
				if (ProxyConfigs.CONFIG.isLegacyServer(onlineplayer.getServer().getInfo().getName())) {
					onlineplayer.sendMessage(ProxyJsonUtils.parseMessage(MultiChatUtil.approximateRGBColorCodes(message)));
				} else {
					onlineplayer.sendMessage(ProxyJsonUtils.parseMessage(message));
				}
			}

			// Trigger PostBroadcastEvent
			ProxyServer.getInstance().getPluginManager().callEvent(new PostBroadcastEvent("announcement", message));

		}
	}
}
