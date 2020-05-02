package xyz.olivermartin.multichat.local.common;

import java.util.UUID;

import xyz.olivermartin.multichat.local.common.commands.MultiChatLocalCommandSender;

public interface MultiChatLocalPlayer extends MultiChatLocalCommandSender {

	public UUID getUniqueId();
	
}
