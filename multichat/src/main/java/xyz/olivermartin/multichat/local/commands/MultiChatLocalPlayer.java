package xyz.olivermartin.multichat.local.commands;

import java.util.UUID;

public interface MultiChatLocalPlayer extends MultiChatLocalCommandSender {

	public UUID getUniqueId();
	
}
