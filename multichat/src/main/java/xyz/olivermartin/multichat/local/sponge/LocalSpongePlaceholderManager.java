package xyz.olivermartin.multichat.local.sponge;

import java.util.UUID;

import xyz.olivermartin.multichat.local.common.LocalPlaceholderManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;

public class LocalSpongePlaceholderManager extends LocalPlaceholderManager {

	public LocalSpongePlaceholderManager() {
		super(MultiChatLocalPlatform.SPONGE);
	}

	@Override
	public String buildChatFormat(UUID uuid, String format) {
		format = processMultiChatPlaceholders(uuid, format);
		format = MultiChatLocal.getInstance().getChatManager().translateColorCodes(format, true);
		return format;
	}

}
