package xyz.olivermartin.multichat.proxy.common;

import java.util.Optional;

import xyz.olivermartin.multichat.proxy.common.modules.staffchat.StaffChatModule;

public class ProxyModuleManager {

	private StaffChatModule staffChat;

	public Optional<StaffChatModule> getStaffChatModule() {
		if (staffChat != null) return Optional.of(staffChat);
		return Optional.empty();
	}

	public void registerStaffChatModule(StaffChatModule staffChat) {
		this.staffChat = staffChat;
	}

	public void unregisterStaffChatModule() {
		this.staffChat = null;
	}

}
