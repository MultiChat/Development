package xyz.olivermartin.multichat.local.common.config;

import xyz.olivermartin.multichat.common.MultiChatUtil;

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

		String testMessage = messageFormat;

		if (ignoreFormatCodes) {
			testMessage = MultiChatUtil.stripColorCodes(testMessage, true);
		} else {
			// This makes life easier when doing the config file as only have to use & style colour codes
			testMessage = testMessage.replace('§', '&');
		}

		return testMessage.matches(regex);

	}

	public String getChannel() {
		return this.channel;
	}

}
