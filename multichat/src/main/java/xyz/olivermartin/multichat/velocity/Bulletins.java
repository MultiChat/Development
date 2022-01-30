package xyz.olivermartin.multichat.velocity;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.olivermartin.multichat.velocity.events.PostBroadcastEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Bulletins Management
 * <p>The back-end code which manages the creation, deletion and scheduling of bulletins</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class Bulletins {
    private static final Object lock = new Object();

    private static ScheduledTask currentlyScheduled = null;
    private static int nextBulletin = -1;
    private static ArrayList<String> bulletin = new ArrayList<>();
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
        return nextBulletin != -1;
    }

    public static void stopBulletins() {

        if (nextBulletin != -1) {
            nextBulletin = -1;
        }

        try {
            currentlyScheduled.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void addBulletin(String message) {
        synchronized (lock) {
            bulletin.add(MultiChatUtil.reformatRGB(message));
        }
    }

    public static Iterator<String> getIterator() {
        synchronized (lock) {
            return bulletin.iterator();
        }
    }

    public static void removeBulletin(int index) {
        synchronized (lock) {
            try {
                bulletin.remove(index);
            } catch (Exception e) {
                System.err.println("Couldnt remove bulletin!");
            }
        }

    }

    private static void scheduleNextBulletin(final int minutes) {
        currentlyScheduled = MultiChat.getInstance().getServer().getScheduler().buildTask(MultiChat.getInstance(), () -> {
            String message;
            if (bulletin.size() >= 1) {
                message = bulletin.get(nextBulletin);

                message = ChatControl.applyChatRules(message, "bulletins", "").get();

                for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {
                    if (MultiChat.legacyServers.contains(onlineplayer.getCurrentServer().get().getServerInfo().getName())) {
                        onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(MultiChatUtil.approximateHexCodes(message)));
                    } else {
                        onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
                    }
                }

                // Trigger PostBroadcastEvent
                MultiChat.getInstance().getServer().getEventManager().fire(new PostBroadcastEvent("bulletin", message));

            }

            if (nextBulletin >= bulletin.size() - 1) {
                nextBulletin = 0;
            } else {
                nextBulletin++;
            }

            scheduleNextBulletin(minutes);
        }).delay(minutes, TimeUnit.MINUTES).schedule();
    }
}
