package xyz.olivermartin.multichat.local.common.listeners.chat;

import xyz.olivermartin.multichat.local.common.LocalPseudoChannel;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlayer;

public interface MultiChatLocalPlayerChatEvent {

	public MultiChatLocalPlayer getPlayer();

	public String getMessage();

	public String getFormat();

	public void setMessage(String message);

	public void setFormat(String format);

	public boolean isCancelled();

	public void setCancelled(boolean cancelled);

	public void removeIgnoredPlayersAndNonChannelMembersFromRecipients(LocalPseudoChannel channel);

}
