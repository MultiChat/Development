package xyz.olivermartin.multichat.local.platform.spigot.listeners.chat;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.listeners.chat.MultiChatLocalPlayerChatEvent;
import xyz.olivermartin.multichat.local.platform.spigot.MultiChatLocalSpigotPlayer;

public class MultiChatLocalSpigotPlayerChatEvent implements MultiChatLocalPlayerChatEvent {

	private AsyncPlayerChatEvent event;
	private MultiChatLocalPlayer player;

	public MultiChatLocalSpigotPlayerChatEvent(AsyncPlayerChatEvent event) {

		this.event = event;
		this.player = new MultiChatLocalSpigotPlayer(event.getPlayer());

	}

	@Override
	public MultiChatLocalPlayer getPlayer() {
		return this.player;
	}

	@Override
	public String getMessage() {
		return event.getMessage();
	}

	@Override
	public String getFormat() {
		return event.getFormat();
	}

	@Override
	public void setMessage(String message) {
		event.setMessage(message);
	}

	@Override
	public void setFormat(String format) {
		event.setFormat(format);
	}

	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	@Override
	public void setCancelled(boolean cancelled) {
		event.setCancelled(cancelled);
	}

}
