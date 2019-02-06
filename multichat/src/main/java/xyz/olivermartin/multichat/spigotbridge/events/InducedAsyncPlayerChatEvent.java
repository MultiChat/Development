package xyz.olivermartin.multichat.spigotbridge.events;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class InducedAsyncPlayerChatEvent extends AsyncPlayerChatEvent {

	public InducedAsyncPlayerChatEvent(boolean async, Player who, String message, Set<Player> players) {
		super(async, who, message, players);
	}

}
