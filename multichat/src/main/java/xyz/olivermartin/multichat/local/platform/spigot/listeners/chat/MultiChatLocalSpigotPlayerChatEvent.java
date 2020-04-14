package xyz.olivermartin.multichat.local.platform.spigot.listeners.chat;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import xyz.olivermartin.multichat.local.LocalPseudoChannel;
import xyz.olivermartin.multichat.local.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.listeners.chat.MultiChatLocalPlayerChatEvent;
import xyz.olivermartin.multichat.local.platform.spigot.MultiChatLocalSpigotPlayer;
import xyz.olivermartin.multichat.spigotbridge.MultiChatSpigot;

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

	@Override
	public void removeIgnoredPlayersAndNonChannelMembersFromRecipients(LocalPseudoChannel channel) {

		Set<UUID> ignoredPlayers;
		//LocalDataStore store = MultiChatLocal.getInstance().getDataStore();

		Iterator<Player> it = event.getRecipients().iterator();

		while (it.hasNext()) {

			Player p = it.next();

			ignoredPlayers = MultiChatSpigot.ignoreMap.get(p.getUniqueId());

			if ( (channel.whitelistMembers && channel.members.contains(p.getUniqueId())) || (!channel.whitelistMembers && !channel.members.contains(p.getUniqueId()))) {

				// Then this player is okay!
				if (ignoredPlayers != null) {

					if (ignoredPlayers.contains(event.getPlayer().getUniqueId())) {

						it.remove();

					}

				}

			} else {

				it.remove();

			}
		}

	}

}
