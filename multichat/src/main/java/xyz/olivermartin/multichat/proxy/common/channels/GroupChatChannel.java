package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.UUID;

public class GroupChatChannel extends JoinableChannel {

	private UUID creatorId;

	public GroupChatChannel(String groupName, UUID creatorId) {
		super(groupName.toLowerCase(), groupName, false);
		this.creatorId = creatorId;
		joinChannel(creatorId);
	}

	public UUID getCreatorId() {
		return this.creatorId;
	}

}
