package xyz.olivermartin.multichat.local.spigot.hooks;

import java.util.Optional;

import net.milkbowl.vault.chat.Chat;

public class LocalSpigotVaultHook {

	private static LocalSpigotVaultHook instance;

	static {
		instance = new LocalSpigotVaultHook();
	}

	public static LocalSpigotVaultHook getInstance() {
		return instance;
	}

	// END STATIC

	private boolean hooked;

	private Chat vaultChat;

	private LocalSpigotVaultHook() {
		hooked = false;
	}

	public boolean isHooked() {
		return hooked;
	}

	public void hook(Chat vaultChat) {	
		hooked = true;
		this.vaultChat = vaultChat;
	}

	public void unHook() {
		hooked = false;
		this.vaultChat = null;
	}

	public Optional<Chat> getHook() {
		if (!hooked) return Optional.empty();
		return Optional.of(vaultChat);
	}

}
