package xyz.olivermartin.multichat.local.sponge;

import java.util.UUID;

import xyz.olivermartin.multichat.local.common.LocalPlaceholderManager;
import xyz.olivermartin.multichat.local.common.MultiChatLocalPlatform;

public class LocalSpongePlaceholderManager extends LocalPlaceholderManager {

	public LocalSpongePlaceholderManager() {
		super(MultiChatLocalPlatform.SPONGE);
	}

	@Override
	public String buildChatFormat(UUID uuid, String format) {
		format = processMultiChatPlaceholders(uuid, format);//.replaceAll("(?i)&(?=[a-f,0-9,k-o,r,x])", "§");
		//format = MultiChatUtil.approximateHexCodes(format);
		return format;
	}

}
