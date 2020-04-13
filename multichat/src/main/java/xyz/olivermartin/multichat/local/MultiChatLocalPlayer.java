package xyz.olivermartin.multichat.local;

import java.util.UUID;

import xyz.olivermartin.multichat.local.commands.MultiChatLocalCommandSender;

public interface MultiChatLocalPlayer extends MultiChatLocalCommandSender {

	public UUID getUniqueId();
	
}
