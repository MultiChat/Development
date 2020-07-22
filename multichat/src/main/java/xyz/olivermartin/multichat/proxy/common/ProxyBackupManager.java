package xyz.olivermartin.multichat.proxy.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class ProxyBackupManager {

	private List<Runnable> backupTasks;
	private ScheduledTask scheduledTask;

	public ProxyBackupManager() {
		backupTasks = new ArrayList<Runnable>();
	}

	public void registerBackupTask(Runnable task) {
		backupTasks.add(task);
	}

	public void startBackup(long delay, long period, TimeUnit unit) {

		scheduledTask = ProxyServer.getInstance().getScheduler().schedule(MultiChatProxy.getInstance().getPlugin(), new Runnable() {

			public void run() {

				MultiChatProxy.getInstance().getPlugin().getLogger().info("Commencing backup!");

				for (Runnable r : backupTasks) {
					r.run();
				}

				MultiChatProxy.getInstance().getPlugin().getLogger().info("Backup complete. Any errors reported above.");

			}

		}, delay, period, unit);

	}

	public void stopBackup() {
		if (scheduledTask == null) return;
		scheduledTask.cancel();
	}

}
