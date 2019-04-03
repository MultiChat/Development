package xyz.olivermartin.multichat.spongebridge;

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
		if (debug) System.out.println("[DEBUG] " + message);
	}
	
	public static boolean isDebug() {
		return debug;
	}

}
