package xyz.olivermartin.multichat.velocity;

public class DebugManager {

    private static boolean debug;

    static {
        debug = false;
    }

    public static void setDebug(boolean debug) {
        DebugManager.debug = debug;
    }

    public static void toggle() {
        debug = !debug;
    }

    public static void log(String message) {
        if (debug) ConsoleManager.log("[DEBUG] " + message);
    }

}
