package xyz.olivermartin.multichat.bungee.events;

import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

/**
 * An event that could be used by IRC plugins to relay broadcast events from MultiChat
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class MultiChatBroadcastIRCEvent extends Event implements Cancellable {

	private boolean cancelled;
	private String message;
	private String type;

	public MultiChatBroadcastIRCEvent(String type, String message) {

		cancelled = false;
		this.message = message;
		this.type = type;

	}

	/**
	 * Can be "cast", "display", "bulletin" or "announcement"
	 */
	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

}