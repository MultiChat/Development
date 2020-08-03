package xyz.olivermartin.multichat.bungee;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import xyz.olivermartin.multichat.bungee.events.PostBroadcastEvent;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyJsonUtils;
import xyz.olivermartin.multichat.proxy.common.ProxyUtils;

/**
 * Bulletins Management
 * <p>The back-end code which manages the creation, deletion and scheduling of bulletins</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class Bulletins {

	private static int currentlyScheduled = 0;
	private static int nextBulletin = -1;
	private static ArrayList<String> bulletin = new ArrayList<String>();
	private static int timeInbetween = 0;

	public static void setArrayList(ArrayList<String> bulletinList) {
		bulletin = bulletinList;
	}

	public static ArrayList<String> getArrayList() {
		return bulletin;
	}

	public static void startBulletins(int timeBetween) {

		timeInbetween = timeBetween;

		if (nextBulletin == -1 && bulletin.size() > 0) {
			nextBulletin = 0;
			scheduleNextBulletin(timeBetween);
		}

	}

	public static int getTimeBetween() {
		return timeInbetween;
	}

	public static boolean isEnabled() {
		if (nextBulletin == -1) { 
			return false;
		} else {
			return true;
		}
	}

	public static void stopBulletins() {

		if (nextBulletin != -1) {
			nextBulletin = -1;
		}

		try {
			ProxyServer.getInstance().getScheduler().cancel(currentlyScheduled);
		} catch (Exception e) {
			System.err.println(e);
		}

	}

	public static void addBulletin(String message) {
		synchronized (bulletin) {
			bulletin.add(message);
		}
	}

	public static Iterator<String> getIterator() {
		synchronized (bulletin) {
			return bulletin.iterator();
		}
	}

	public static void removeBulletin(int index) {

		synchronized (bulletin) {
			try {
				bulletin.remove(index);
			} catch (Exception e) {
				System.err.println("Couldnt remove bulletin!");
			}
		}

	}

	private static void scheduleNextBulletin(final int minutes) {
		ScheduledTask task = ProxyServer.getInstance().getScheduler().schedule(MultiChatProxy.getInstance().getPlugin(), new Runnable() {

			@Override
			public void run() {

				String message;

				if (bulletin.size() < 1) {
					/* EMPTY */
				} else {

					message = bulletin.get(nextBulletin);

					message = ChatControl.applyChatRules(message, "bulletins", "").get();

					message = ProxyUtils.translateColourCodes(message);

					for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
						if (MultiChat.legacyServers.contains(onlineplayer.getServer().getInfo().getName())) {
							onlineplayer.sendMessage(ProxyJsonUtils.parseMessage(MultiChatUtil.approximateHexCodes(message)));
						} else {
							onlineplayer.sendMessage(ProxyJsonUtils.parseMessage(message));
						}
					}

					// Trigger PostBroadcastEvent
					ProxyServer.getInstance().getPluginManager().callEvent(new PostBroadcastEvent("bulletin", message));

				}

				if (nextBulletin >= bulletin.size() - 1) {
					nextBulletin = 0;
				} else {
					nextBulletin++;
				}

				scheduleNextBulletin(minutes);
			}

		}, minutes, TimeUnit.MINUTES);

		currentlyScheduled = task.getId();
	}
}
