package xyz.olivermartin.multichat.local.sponge.listeners;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;

public class SpongeGameReloadEvent {

	@Listener
	public void reload(GameReloadEvent event) {

		MultiChatLocal.getInstance().getConsoleLogger().log("Reloading MultiChatLocal config file...");
		MultiChatLocal.getInstance().getConfigManager().getLocalConfig().reload();
		MultiChatLocal.getInstance().getConsoleLogger().log("MultiChatLocal config file has been reloaded!");

	}

}
