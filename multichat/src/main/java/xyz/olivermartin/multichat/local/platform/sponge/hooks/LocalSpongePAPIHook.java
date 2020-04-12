package xyz.olivermartin.multichat.local.platform.sponge.hooks;

import java.util.Optional;

import me.rojo8399.placeholderapi.PlaceholderService;

public class LocalSpongePAPIHook {

	private static LocalSpongePAPIHook instance;

	static {
		instance = new LocalSpongePAPIHook();
	}

	public static LocalSpongePAPIHook getInstance() {
		return instance;
	}

	// END STATIC

	private boolean hooked;

	private PlaceholderService papi;

	private LocalSpongePAPIHook() {
		hooked = false;
	}

	public boolean isHooked() {
		return hooked;
	}

	public void hook(PlaceholderService papi) {	
		hooked = true;
		this.papi = papi;
	}

	public void unHook() {
		hooked = false;
		this.papi = null;
	}

	public Optional<PlaceholderService> getHook() {
		if (!hooked) return Optional.empty();
		return Optional.of(papi);
	}

}
