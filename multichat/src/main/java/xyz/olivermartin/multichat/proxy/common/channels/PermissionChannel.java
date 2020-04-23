package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.Collection;
import java.util.Iterator;

import xyz.olivermartin.multichat.proxy.common.MultiChatProxyPlayer;

public class PermissionChannel extends AbstractChannel {

	private String talkPermission;
	private String viewPermission;

	public PermissionChannel(String id, String name, String permission) {
		super(id, name, ChannelType.PERMISSION);
		this.talkPermission = permission;
		this.viewPermission = permission;
	}

	/**
	 * Does the player have permission to talk in this chat?
	 * @param player
	 * @return True if they have permission to TALK
	 */
	public boolean hasTalkPermission(MultiChatProxyPlayer player) {
		return player.hasProxyPermission(talkPermission);
	}

	/**
	 * Does the player have permission to view this chat?
	 * @param player
	 * @return True if they have permission to VIEW
	 */
	public boolean hasViewPermission(MultiChatProxyPlayer player) {
		return player.hasProxyPermission(viewPermission);
	}

	/**
	 * Does the player have permission to view and talk in this chat?
	 * @param player
	 * @return True if they have permission to TALK and VIEW
	 */
	public boolean hasPermission(MultiChatProxyPlayer player) {
		return (hasTalkPermission(player) && hasViewPermission(player));
	}

	@Override
	protected boolean isPermittedToSendMessage(MultiChatProxyPlayer sender) {
		return hasTalkPermission(sender);
	}

	@Override
	protected Collection<MultiChatProxyPlayer> removeNonPermittedViewers(
			Collection<MultiChatProxyPlayer> currentViewers) {

		Iterator<MultiChatProxyPlayer> it = currentViewers.iterator();

		while (it.hasNext()) {
			MultiChatProxyPlayer viewer = it.next();
			if (!hasViewPermission(viewer)) {
				it.remove();
			}
		}

		return currentViewers;
	}

	@Override
	protected boolean canAlwaysChat(MultiChatProxyPlayer sender) {
		return (sender.hasProxyPermission(talkPermission+".always") || sender.hasProxyPermission("multichat.chat.always"));
	}

}
