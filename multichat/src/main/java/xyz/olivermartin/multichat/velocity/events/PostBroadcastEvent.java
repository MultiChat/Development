package xyz.olivermartin.multichat.velocity.events;

import java.util.regex.Pattern;

/**
 * An event that triggers AFTER a broadcast message has been sent via MultiChat
 *
 * @author Oliver Martin (Revilo410)
 */
public class PostBroadcastEvent {

    private boolean cancelled;
    private String message; // The actual message that was broadcast
    private String type; // The type of broadcast

    /**
     * Trigger a new PostBroadcastEvent
     *
     * @param type    "cast", "display", "bulletin" or "announcement" depending on the type of broadcast
     * @param message The message that was broadcast
     */
    public PostBroadcastEvent(String type, String message) {

        cancelled = false;
        this.message = message;
        this.type = type;

    }

    /**
     * @return "cast", "display", "bulletin" or "announcement" depending on the type of broadcast
     */
    public String getType() {
        return type;
    }

    /**
     * @return The message that was broadcast including format codes in the '&' notation
     */
    public String getMessage() {
        return message;
    }

    /**
     * <p>Allows you to change the message in this PostBroadcastEvent ONLY</p>
     * <p>THIS WILL HAVE NO AFFECT ON WHAT MESSAGE MULTICHAT BROADCASTS!</p>
     * <p>This is a POST broadcast event, meaning the broadcast has already happened by the time this event triggers.</p>
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return The message that was broadcast EXCLUDING any format codes in the '&' notation
     */
    public String getRawMessage() {
        return stripAllFormattingCodes(message);
    }

    /**
     * <p>Returns true if this post broadcast event has been cancelled</p>
     * <p>THIS WILL HAVE NO AFFECT ON WHAT MESSAGE MULTICHAT BROADCASTS!</p>
     * <p>This is a POST broadcast event, meaning the broadcast has already happened by the time this event triggers.</p>
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * <p>Allows you to cancel this PostBroadcastEvent</p>
     * <p>THIS WILL HAVE NO AFFECT ON WHAT MESSAGE MULTICHAT BROADCASTS!</p>
     * <p>This is a POST broadcast event, meaning the broadcast has already happened by the time this event triggers.</p>
     */
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    // Remove all formatting codes from the text (&a, &l etc.)
    private static String stripAllFormattingCodes(String input) {

        char COLOR_CHAR = '&';
        Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");

        if (input == null) {
            return null;
        }

        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");

    }

}