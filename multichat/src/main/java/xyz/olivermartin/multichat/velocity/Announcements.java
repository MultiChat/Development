package xyz.olivermartin.multichat.velocity;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import xyz.olivermartin.multichat.velocity.events.PostBroadcastEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Announcements Management
 * <p>The back-end code which manages the creation, deletion and scheduling of announcements</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class Announcements {

    private static final Map<String, ScheduledTask> aKey = new HashMap<>();
    private static Map<String, String> announcements = new HashMap<>();

    public static boolean startAnnouncement(final String name, Integer minutes) {

        if (!(aKey.containsKey(name.toLowerCase())) && announcements.containsKey(name.toLowerCase())) {

            ScheduledTask task = MultiChat.getInstance().getServer().getScheduler().buildTask(MultiChat.getInstance(), () -> {
                String message = announcements.get(name.toLowerCase());

                message = ChatControl.applyChatRules(message, "announcements", "").get();

                for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {
                    if (MultiChat.legacyServers.contains(onlineplayer.getCurrentServer().get().getServerInfo().getName())) {
                        onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(MultiChatUtil.approximateHexCodes(message)));
                    } else {
                        onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
                    }
                }

                // Trigger PostBroadcastEvent
                MultiChat.getInstance().getServer().getEventManager().fire(new PostBroadcastEvent("announcement", message));
            }).repeat(minutes, TimeUnit.MINUTES).schedule();
            aKey.put(name.toLowerCase(), task);
            return true;

        } else {

            return false;

        }
    }

    public static HashMap<String, String> getAnnouncementList() {

        return (HashMap<String, String>) announcements;

    }

    public static void loadAnnouncementList(HashMap<String, String> loadedAnnouncements) {

        announcements = loadedAnnouncements;

    }

    public static boolean stopAnnouncement(String name) {

        if (aKey.containsKey(name.toLowerCase())) {
            aKey.get(name.toLowerCase()).cancel();
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
            aKey.get(name.toLowerCase()).cancel();
            aKey.remove(name.toLowerCase());
        }

        if (announcements.containsKey(name.toLowerCase())) {
            announcements.remove(name.toLowerCase());
            return true;
        } else {
            return false;
        }

    }

    public static boolean existsAnnouncemnt(String name) {
        return announcements.containsKey(name.toLowerCase());
    }

    public static void playAnnouncement(String name) {

        if (announcements.containsKey(name.toLowerCase())) {

            String message = announcements.get(name.toLowerCase());

            message = ChatControl.applyChatRules(message, "announcements", "").get();

            for (Player onlineplayer : MultiChat.getInstance().getServer().getAllPlayers()) {
                if (MultiChat.legacyServers.contains(onlineplayer.getCurrentServer().get().getServerInfo().getName())) {
                    onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(MultiChatUtil.approximateHexCodes(message)));
                } else {
                    onlineplayer.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
                }
            }

            // Trigger PostBroadcastEvent
            MultiChat.getInstance().getServer().getEventManager().fire(new PostBroadcastEvent("announcement", message));
        }
    }
}
