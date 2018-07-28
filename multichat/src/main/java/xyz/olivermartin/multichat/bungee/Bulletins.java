package xyz.olivermartin.multichat.bungee;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

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
		ScheduledTask task = ProxyServer.getInstance().getScheduler().schedule(MultiChat.getInstance(), new Runnable() {

			@Override
			public void run() {

				String message;

				if (bulletin.size() < 1) {
					/* EMPTY */
				} else {

					message = bulletin.get(nextBulletin);

					for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {
						onlineplayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',message)));
					}

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
