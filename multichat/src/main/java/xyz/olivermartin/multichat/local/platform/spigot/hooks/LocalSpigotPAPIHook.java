package xyz.olivermartin.multichat.local.platform.spigot.hooks;

public class LocalSpigotPAPIHook {

	private static LocalSpigotPAPIHook instance;

	static {
		instance = new LocalSpigotPAPIHook();
	}

	public static LocalSpigotPAPIHook getInstance() {
		return instance;
	}

	// END STATIC

	private boolean hooked;

	private LocalSpigotPAPIHook() {
		hooked = false;
	}

	public boolean isHooked() {
		return hooked;
	}

	public void hook() {	
		hooked = true;
	}

	public void unHook() {
		hooked = false;
	}

}
