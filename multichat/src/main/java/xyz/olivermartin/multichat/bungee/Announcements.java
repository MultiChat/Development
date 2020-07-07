package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import xyz.olivermartin.multichat.bungee.events.PostBroadcastEvent;

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

			ScheduledTask task = ProxyServer.getInstance().getScheduler().schedule(MultiChat.getInstance(), new Runnable() {

				@Override
				public void run() {
					String message = announcements.get(name.toLowerCase());

					message = ChatControl.applyChatRules(message, "announcements", "").get();

					for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
						onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',message)));
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

			announcements.put(name.toLowerCase(), MultiChatUtil.reformatRGB(message));
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

			message = ChatControl.applyChatRules(message, "announcements", "").get();

			for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
				onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',message)));
			}

			// Trigger PostBroadcastEvent
			ProxyServer.getInstance().getPluginManager().callEvent(new PostBroadcastEvent("announcement", message));

		}
	}
}
