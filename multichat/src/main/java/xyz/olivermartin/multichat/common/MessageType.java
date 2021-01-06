package xyz.olivermartin.multichat.common;

public enum MessageType {
    LOCAL_CHAT,
    GLOBAL_CHAT,
    PRIVATE_MESSAGES,
    GROUP_CHATS,
    STAFF_CHATS,
    DISPLAY_COMMAND,
    ANNOUNCEMENTS,
    BULLETINS,
    CASTS,
    HELPME;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}