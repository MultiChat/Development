package xyz.olivermartin.multichat.proxy.common.channels;

public class StaffChatChannel extends PermissionChannel {

	public StaffChatChannel(String staffChannelID, String staffChannelName) {
		super(staffChannelID, staffChannelName, "multichat.staff." + staffChannelName);
	}

}
