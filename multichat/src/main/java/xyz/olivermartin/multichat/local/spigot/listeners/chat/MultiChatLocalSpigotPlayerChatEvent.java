package xyz.olivermartin.multichat.local.spigot.listeners.chat;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import xyz.olivermartin.multichat.local.common.LocalPseudoChannel;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;
import xyz.olivermartin.multichat.local.common.listeners.chat.MultiChatLocalPlayerChatEvent;
import xyz.olivermartin.multichat.local.spigot.MultiChatLocalSpigotPlayer;

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

		MultiChatLocal.getInstance().getConsoleLogger().debug("[MultiChatLocalSpigotChatEvent] Removing Ignored Players and Non Channel Members from recipients!");

		Set<UUID> ignoredPlayers;
		//LocalDataStore store = MultiChatLocal.getInstance().getDataStore();

		MultiChatLocal.getInstance().getConsoleLogger().debug("[MultiChatLocalSpigotChatEvent] Starting with " + event.getRecipients().size() + " recipients");

		Iterator<Player> it = event.getRecipients().iterator();

		while (it.hasNext()) {

			Player p = it.next();

			ignoredPlayers = MultiChatLocal.getInstance().getDataStore().ignoreMap.get(p.getUniqueId());

			if ( (channel.whitelistMembers && channel.members.contains(p.getUniqueId())) || (!channel.whitelistMembers && !channel.members.contains(p.getUniqueId()))) {

				// Then this player is okay!
				if (ignoredPlayers != null) {

					if (ignoredPlayers.contains(event.getPlayer().getUniqueId())) {

						MultiChatLocal.getInstance().getConsoleLogger().debug("[MultiChatLocalSpigotChatEvent] Removed a recipient due to ignore...");
						it.remove();

					}

				}

			} else {

				MultiChatLocal.getInstance().getConsoleLogger().debug("[MultiChatLocalSpigotChatEvent] Removed a recipient due to not being a channel member");
				it.remove();

			}
		}

	}

}
