package xyz.olivermartin.multichat.local.sponge;

import java.util.UUID;

import xyz.olivermartin.multichat.local.LocalPlaceholderManager;
import xyz.olivermartin.multichat.local.MultiChatLocalPlatform;

public class LocalSpongePlaceholderManager extends LocalPlaceholderManager {

	public LocalSpongePlaceholderManager() {
		super(MultiChatLocalPlatform.SPONGE);
	}

	@Override
	public String buildChatFormat(UUID uuid, String format) {
		return processMultiChatPlaceholders(uuid, format);
	}

}
