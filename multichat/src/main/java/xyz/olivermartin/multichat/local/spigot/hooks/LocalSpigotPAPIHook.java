package xyz.olivermartin.multichat.local.spigot.hooks;

import java.util.Optional;

import me.clip.placeholderapi.PlaceholderAPI;

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

	private PlaceholderAPI papi;

	private LocalSpigotPAPIHook() {
		hooked = false;
	}

	public boolean isHooked() {
		return hooked;
	}

	public void hook(PlaceholderAPI papi) {	
		hooked = true;
		this.papi = papi;
	}

	public void unHook() {
		hooked = false;
		this.papi = null;
	}

	public Optional<PlaceholderAPI> getHook() {
		if (!hooked) return Optional.empty();
		return Optional.of(papi);
	}

}
