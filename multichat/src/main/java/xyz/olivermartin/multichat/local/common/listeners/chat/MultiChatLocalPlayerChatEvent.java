package xyz.olivermartin.multichat.local.common.listeners.chat;

import java.util.Set;
import java.util.UUID;

import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;

public interface MultiChatLocalPlayerChatEvent {

	public MultiChatLocalPlayer getPlayer();

	public String getMessage();

	public String getFormat();

	public void setMessage(String message);

	public void setFormat(String format);

	public boolean isCancelled();

	public void setCancelled(boolean cancelled);

	//public void removeIgnoredPlayersAndNonChansnelMembersFromRecipients(LocalPseudoChannel channel);

	public void removeOtherPlayers();

	public Set<UUID> getOtherRecipients();

}
