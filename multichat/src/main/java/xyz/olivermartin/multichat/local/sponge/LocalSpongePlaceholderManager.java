package xyz.olivermartin.multichat.local.sponge;

import java.util.UUID;

import xyz.olivermartin.multichat.bungee.MultiChatUtil;
import xyz.olivermartin.multichat.local.common.LocalPlaceholderManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;

public class LocalSpongePlaceholderManager extends LocalPlaceholderManager {

	public LocalSpongePlaceholderManager() {
		super(MultiChatLocalPlatform.SPONGE);
	}

	@Override
	public String buildChatFormat(UUID uuid, String format) {
		// Reformat any hex codes in the format
		format = MultiChatUtil.reformatRGB(format);
		format = processMultiChatPlaceholders(uuid, format).replaceAll("&(?=[a-f,0-9,k-o,r,x])", "§");
		format = MultiChatUtil.approximateHexCodes(format);
		return format;
	}

}
