package xyz.olivermartin.multichat.local.common.config;

import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.local.common.MultiChatLocal;

public class RegexChannelForcer {

	private String regex;
	private boolean ignoreFormatCodes;
	private String channel;

	public RegexChannelForcer(String regex, boolean ignoreFormatCodes, String channel) {

		this.regex = regex;
		this.ignoreFormatCodes = ignoreFormatCodes;
		this.channel = channel;

		// Do not allow any other channel... YET...
		if (! (this.channel.equalsIgnoreCase("local")
				|| this.channel.equalsIgnoreCase("global")
				|| this.channel.equalsIgnoreCase("current")) ) {
			this.channel = "current";
		}

	}

	public boolean matchesRegex(String messageFormat) {

		MultiChatLocal.getInstance().getConsoleLogger().debug("[RegexChannelForcer] Testing format: " + messageFormat);
		MultiChatLocal.getInstance().getConsoleLogger().debug("[RegexChannelForcer] Testing format (visualised): " + messageFormat.replace("&", "(#d)").replace("§", "(#e)"));

		String testMessage = messageFormat;

		if (ignoreFormatCodes) {
			testMessage = MultiChatUtil.stripColourCodes(testMessage, true);
		} else {
			// This makes life easier when doing the config file as only have to use & style colour codes
			testMessage = testMessage.replace('§', '&');
		}

		MultiChatLocal.getInstance().getConsoleLogger().debug("[RegexChannelForcer] Processed format codes: " + testMessage);
		MultiChatLocal.getInstance().getConsoleLogger().debug("[RegexChannelForcer] Processed format codes (visualised): " + testMessage.replace("&", "(#d)").replace("§", "(#e)"));

		MultiChatLocal.getInstance().getConsoleLogger().debug("[RegexChannelForcer] Regex is: " + regex);
		MultiChatLocal.getInstance().getConsoleLogger().debug("[RegexChannelForcer] Regex is (visualised): " + regex.replace("&", "(#d)").replace("§", "(#e)"));

		return testMessage.matches(regex);

	}

	public String getChannel() {
		return this.channel;
	}

}
